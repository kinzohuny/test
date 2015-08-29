package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;

import com.jiuqi.dna.core.def.exp.Predicate;
import com.jiuqi.dna.core.impl.ArgumentRefExpr;
import com.jiuqi.dna.core.impl.CombinedExpr;
import com.jiuqi.dna.core.impl.ConditionalExpr;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.MoRelationRef;
import com.jiuqi.dna.core.impl.OperateExpr;
import com.jiuqi.dna.core.impl.PredicateExpr;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.impl.TraversedExprVisitor;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.impl.ValueExpr;

/**
 * 多物理表更新的分析器
 * 
 * <p>
 * 同时用作更新条件以及连接条件的检查器，检查条件来源及条件冲突
 * 
 * @author houchunlei
 * 
 */
public final class UpdateMultipleResolver extends TraversedExprVisitor<Object> {

	public final ArrayList<UpdateSingleDbTable> dbTables = new ArrayList<UpdateSingleDbTable>();

	final UpdateStatementImpl update;

	final TableDefineImpl table;

	boolean assignValueDependOn;

	ValueExpr recidValue;
	
	ValueExpr recverValue;
	
	final BitSet conditionConflict = new BitSet();

	public UpdateMultipleResolver(UpdateStatementImpl update) {
		this.update = update;
		this.table = update.moTableRef.target;
		UpdateSingleDbTable single = null;
		for (int i = 0; i < update.assigns.size(); i++) {
			FieldAssign fa = update.assigns.get(i);
			DBTableDefineImpl dbTable = fa.field.dbTable;
			if (single != null && single.dbTable == dbTable) {
				single.assigns.add(fa);
			} else {
				ensure: {
					for (int ti = 0, c = this.dbTables.size(); ti < c; ti++) {
						single = this.dbTables.get(ti);
						if (single.dbTable == dbTable) {
							single.assigns.add(fa);
							break ensure;
						}
					}
					single = new UpdateSingleDbTable(this, dbTable);
					this.dbTables.add(single);
					single.assigns.add(fa);
				}
			}
		}
		// 检查更新条件的条件冲突
		if (this.update.getCondition() != null) {
			this.update.getCondition().visit(this, null);
		}
		// 检查连接条件的条件冲突
		if (this.update.moTableRef.getJoins() != null) {
			for (MoRelationRef relationRef : this.update.moTableRef) {
				relationRef.visit(this, null);
			}
		}
		// 检查各物理表更新的赋值来源及赋值冲突
		for (int i = 0, c = this.dbTables.size(); i < c; i++) {
			this.dbTables.get(i).visitAssignValue();
		}
		// 检查更新条件是否是乐观锁更新
		if (this.update.getCondition() != null) {
			searchForRecidAndRecver(this.update.getCondition());
		}
	}

	private final UpdateSingleDbTable get(int dbTableIndex) {
		for (int i = 0, c = this.dbTables.size(); i < c; i++) {
			UpdateSingleDbTable single = this.dbTables.get(i);
			if (single.dbTableIndex == dbTableIndex) {
				return single;
			}
		}
		throw new NullPointerException();
	}

	boolean conditionNonDeterministic;

	@Override
	public void visitOperateExpr(OperateExpr expr, Object context) {
		super.visitOperateExpr(expr, context);
		if (expr.isNonDeterministic()) {
			this.conditionNonDeterministic = true;
		}
	}
	
	private void checkPredicateValue(ValueExpr value0, ValueExpr value1) {
		if (value0 instanceof TableFieldRefImpl) {
			TableFieldRefImpl fieldRef = (TableFieldRefImpl) value0;
			if (fieldRef.tableRef.getTarget() != table) {
				return;
			}
			if (value1 instanceof ConstExpr || value1 instanceof ArgumentRefExpr) {
				if(fieldRef.field.isRECID()) {
					recidValue = value1;
				} else if(fieldRef.field.isRECVER()) {
					recverValue = value1;
				}
			}
		}
	}

	/**
	 * 在where条件的and条件中查找recid和recver相等比较。
	 * @param where
	 */
	private void searchForRecidAndRecver(ConditionalExpr where) {
		if (where.isNot()) {
			return;
		}
		if (where instanceof PredicateExpr) {
			PredicateExpr pExpr = (PredicateExpr) where;
			if (pExpr.predicate == Predicate.EQUAL_TO) {
				checkPredicateValue(pExpr.values[0], pExpr.values[1]);
				checkPredicateValue(pExpr.values[1], pExpr.values[0]);
			}
		} else if (where instanceof CombinedExpr) {
			CombinedExpr combinedExpr = (CombinedExpr) where;
			if (combinedExpr.isAnd()) {
				for (int i = 0; i < combinedExpr.getCount(); i++) {
					ConditionalExpr condition = combinedExpr.get(i);
					searchForRecidAndRecver(condition);
				}
			}
		}
	}

	final boolean updateByRecidAndRecver() {
		return this.recidValue != null && this.recverValue != null;
	}
	
	@Override
	public void visitTableFieldRef(TableFieldRefImpl fieldRef, Object context) {
		if (fieldRef.field.owner == this.table) {
			DBTableDefineImpl dbTable = fieldRef.field.dbTable;
			if (this.update.assigns.contains(fieldRef.field)) {
				this.conditionConflict.set(dbTable.index());
			}
		}
	}

	final boolean directResolvable() {
		if (this.conditionNonDeterministic) {
			return false;
		}
		// 辅表按照recid更新
		if (updateByRecidAndRecver()) {
			return true;
		}
		if (this.conditionConflict.cardinality() > 1) {
			return false;
		}
		if (this.conditionConflict.cardinality() == 0 && !this.assignValueDependOn) {
			return true;
		}
		return false;
	}

	/**
	 * 尝试计算多物理表的更新顺序
	 * 
	 * <p>
	 * 
	 * @return 是否可解
	 */
	public final boolean tryResolveSequence() {
		if (this.conditionNonDeterministic) {
			return false;
		}
		if (this.conditionConflict.cardinality() > 1) {
			return false;
		}
		if (this.conditionConflict.cardinality() == 0 && !this.assignValueDependOn) {
			return true;
		}
		// 总物理表个数
		final int a = this.table.dbTables.size();
		// 需要更新表的个数
		final int c = this.dbTables.size();
		// 需要更新表的index列表
		final ArrayList<Integer> targets = new ArrayList<Integer>(c);
		for (int i = 0; i < c; i++) {
			targets.add(this.dbTables.get(i).dbTable.index());
		}
		// 依赖矩阵,每行代表每个需要更新的物理表的依赖,
		final boolean[][] deponOns = new boolean[a][a];
		// 填充依赖矩阵
		for (int i = 0; i < a; i++) {
			if (targets.contains(i)) {
				UpdateSingleDbTable single = this.get(i);
				int from = 0;
				// 当前物理表所依赖的物理表
				int dependOn;
				while ((dependOn = single.assignValueUsingTables.nextSetBit(from)) >= 0) {
					// 被依赖的物理表可能不需要更新
					if (targets.contains(dependOn)) {
						deponOns[single.dbTableIndex][dependOn] = true;
					}
					from = dependOn + 1;
				}
			}
		}
		// 考虑条件冲突
		switch (this.conditionConflict.cardinality()) {
		case 0:
			break;
		case 1:
			// 条件冲突不为空,表示此物理表上有更新字段被用作条件,此物理表必须最后更新
			// 即该表依赖所有其他物理表
			int column = this.conditionConflict.nextSetBit(0);
			for (int row = 0; row < a; row++) {
				if (targets.contains(row)) {
					deponOns[row][column] = true;
				}
			}
			break;
		default:
			// unreachable
			return false;
		}
		// 存储已经排序的结果,按照更新的顺序,存储物理表的序号
		final int[] sequenced = new int[c];
		// 初始全为-1,否则影响contain和columnContainTrue方法,不能正确处理第0行
		Arrays.fill(sequenced, -1);
		// 已经排序的个数
		int sequencedCount = 0;
		// 每次从序号1开始,找到一个未排序的,检查是否能加入队列
		sorting: while (sequencedCount < c) {
			next: for (int i = 0; i < a; i++) {
				// 不需要更新的物理表
				if (!targets.contains(i)) {
					continue next;
				}
				if (sequencedCount != 0) {
					// 已排序的队列的不为空
					if (contain(sequenced, i)) {
						// 物理表已经加入已排序队列
						continue next;
					}
				}
				// 某列不包含true(排除自身和已经排序的),即意味着没有其他表依赖该表,该表可加入队列
				if (columnContainTrue(deponOns, i, sequenced, i)) {
					continue next;
				} else {
					// 该序号没有对其他序号的依赖,加入队列
					sequenced[sequencedCount++] = i;
					continue sorting;
				}
			}
			// 无法找到被依赖为0的表
			return false;
		}
		Collections.sort(this.dbTables, new Comparator<UpdateSingleDbTable>() {
			public int compare(UpdateSingleDbTable o1, UpdateSingleDbTable o2) {
				return search(sequenced, o1.dbTableIndex) - search(sequenced, o2.dbTableIndex);
			}
		});
		return true;
	}

	/**
	 * 数组中遍历查找键值序号
	 */
	private static final int search(int[] a, int key) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == key) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 数组中是否存在某键值
	 */
	private static final boolean contain(int[] a, int key) {
		return search(a, key) >= 0;
	}

	/**
	 * 矩阵中某列是否包含true值
	 * 
	 * @param m
	 *            矩阵
	 * @param column
	 *            目标检查列
	 * @param excepts
	 *            排除行
	 * @param except
	 *            排除行
	 * @return
	 */
	private static final boolean columnContainTrue(boolean[][] m, int column,
			int[] excepts, int except) {
		for (int row = 0; row < m.length; row++) {
			if (contain(excepts, row)) {
				continue;
			}
			if (row == except) {
				continue;
			}
			if (m[row][column]) {
				return true;
			}
		}
		return false;
	}
}