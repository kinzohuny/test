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
 * ���������µķ�����
 * 
 * <p>
 * ͬʱ�������������Լ����������ļ���������������Դ��������ͻ
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
		// ������������������ͻ
		if (this.update.getCondition() != null) {
			this.update.getCondition().visit(this, null);
		}
		// �������������������ͻ
		if (this.update.moTableRef.getJoins() != null) {
			for (MoRelationRef relationRef : this.update.moTableRef) {
				relationRef.visit(this, null);
			}
		}
		// �����������µĸ�ֵ��Դ����ֵ��ͻ
		for (int i = 0, c = this.dbTables.size(); i < c; i++) {
			this.dbTables.get(i).visitAssignValue();
		}
		// �����������Ƿ����ֹ�������
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
	 * ��where������and�����в���recid��recver��ȱȽϡ�
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
		// ������recid����
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
	 * ���Լ���������ĸ���˳��
	 * 
	 * <p>
	 * 
	 * @return �Ƿ�ɽ�
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
		// ����������
		final int a = this.table.dbTables.size();
		// ��Ҫ���±�ĸ���
		final int c = this.dbTables.size();
		// ��Ҫ���±��index�б�
		final ArrayList<Integer> targets = new ArrayList<Integer>(c);
		for (int i = 0; i < c; i++) {
			targets.add(this.dbTables.get(i).dbTable.index());
		}
		// ��������,ÿ�д���ÿ����Ҫ���µ�����������,
		final boolean[][] deponOns = new boolean[a][a];
		// �����������
		for (int i = 0; i < a; i++) {
			if (targets.contains(i)) {
				UpdateSingleDbTable single = this.get(i);
				int from = 0;
				// ��ǰ������������������
				int dependOn;
				while ((dependOn = single.assignValueUsingTables.nextSetBit(from)) >= 0) {
					// ���������������ܲ���Ҫ����
					if (targets.contains(dependOn)) {
						deponOns[single.dbTableIndex][dependOn] = true;
					}
					from = dependOn + 1;
				}
			}
		}
		// ����������ͻ
		switch (this.conditionConflict.cardinality()) {
		case 0:
			break;
		case 1:
			// ������ͻ��Ϊ��,��ʾ����������и����ֶα���������,����������������
			// ���ñ������������������
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
		// �洢�Ѿ�����Ľ��,���ո��µ�˳��,�洢���������
		final int[] sequenced = new int[c];
		// ��ʼȫΪ-1,����Ӱ��contain��columnContainTrue����,������ȷ�����0��
		Arrays.fill(sequenced, -1);
		// �Ѿ�����ĸ���
		int sequencedCount = 0;
		// ÿ�δ����1��ʼ,�ҵ�һ��δ�����,����Ƿ��ܼ������
		sorting: while (sequencedCount < c) {
			next: for (int i = 0; i < a; i++) {
				// ����Ҫ���µ������
				if (!targets.contains(i)) {
					continue next;
				}
				if (sequencedCount != 0) {
					// ������Ķ��еĲ�Ϊ��
					if (contain(sequenced, i)) {
						// ������Ѿ��������������
						continue next;
					}
				}
				// ĳ�в�����true(�ų�������Ѿ������),����ζ��û�������������ñ�,�ñ�ɼ������
				if (columnContainTrue(deponOns, i, sequenced, i)) {
					continue next;
				} else {
					// �����û�ж�������ŵ�����,�������
					sequenced[sequencedCount++] = i;
					continue sorting;
				}
			}
			// �޷��ҵ�������Ϊ0�ı�
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
	 * �����б������Ҽ�ֵ���
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
	 * �������Ƿ����ĳ��ֵ
	 */
	private static final boolean contain(int[] a, int key) {
		return search(a, key) >= 0;
	}

	/**
	 * ������ĳ���Ƿ����trueֵ
	 * 
	 * @param m
	 *            ����
	 * @param column
	 *            Ŀ������
	 * @param excepts
	 *            �ų���
	 * @param except
	 *            �ų���
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