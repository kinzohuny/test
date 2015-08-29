package com.jiuqi.dna.core.internal.da.sql.render;

import java.util.ArrayList;
import java.util.BitSet;

import com.jiuqi.dna.core.impl.ActiveChangable;
import com.jiuqi.dna.core.impl.DBAdapterImpl;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.MoJoinedQueryRef;
import com.jiuqi.dna.core.impl.MoJoinedTableRef;
import com.jiuqi.dna.core.impl.MoRelationRef;
import com.jiuqi.dna.core.impl.MoRootTableRef;
import com.jiuqi.dna.core.impl.PlainSql;
import com.jiuqi.dna.core.impl.SelectColumnRefImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.impl.TraversedExprVisitor;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sql.execute.KingbaseMultipleSqlModifier;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public class KingbaseMultiUpdateSql extends MultiSql implements ModifySql {

	public final KingbaseMultipleSqlModifier newExecutor(DBAdapterImpl adapter,
			ActiveChangable notify) {
		return new KingbaseMultipleSqlModifier(adapter, this, notify);
	}

	public KingbaseMultiUpdateSql(DbMetadata dbMetadata,
			final UpdateStatementImpl update) {
		if (update.assigns.size() == 0) {
			throw new IllegalStatementDefineException(update, "更新语句定义[" + update.name + "]未定义任何更新列。");
		}
		final ISqlCommandFactory factory = dbMetadata.sqlbuffers();
		final UpdateStatus status = new UpdateStatus();
		// status.usageOf(tableRef);
		// DBTableDefineImpl last = status.usage.firstTable();
		update.visit(status, null);
		MultipleResolver resolver = new MultipleResolver(update);
		for (Single single : resolver.list) {
			final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
			ISqlUpdateBuffer buffer = factory.update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
			multiple(update, status, single, buffer, alias);
			PlainSql si = new PlainSql();
			si.build(buffer);
			this.sqls.add(si);
		}
	}

	private static final void multiple(UpdateStatementImpl update,
			UpdateStatus status, Single single, ISqlUpdateBuffer buffer,
			String alias) {
		join(buffer.target(), alias, update.moTableRef, status, single.dbTable);
		for (FieldAssign fa : single.list) {
			fa.value().render(buffer.newValue(fa.field.namedb()), status);
		}
		if (update.getCondition() != null) {
			update.getCondition().render(buffer.where(), status);
		}
	}

	private static final class UpdateStatus extends TableUsages {

		private boolean visitingAssignValue;

		private boolean assignValueFromJoinedRef;

		final boolean assignValueFromJoinedRef() {
			return this.assignValueFromJoinedRef;
		}

		@Override
		public void visitUpdateAssign(FieldAssign assign, Object context) {
			this.visitingAssignValue = true;
			super.visitUpdateAssign(assign, context);
			this.visitingAssignValue = false;
		}

		@Override
		public void visitSelectColumnRef(SelectColumnRefImpl expr,
				Object context) {
			super.visitSelectColumnRef(expr, context);
			if (this.visitingAssignValue && expr.queryRef instanceof MoJoinedQueryRef) {
				this.assignValueFromJoinedRef = true;
			}
		}

		@Override
		public void visitTableFieldRef(TableFieldRefImpl expr, Object context) {
			super.visitTableFieldRef(expr, context);
			if (this.visitingAssignValue && expr.tableRef instanceof MoJoinedTableRef) {
				this.assignValueFromJoinedRef = true;
			}
		}
	}

	/**
	 * 物理表的更新信息
	 * 
	 * <p>
	 * 同时是赋值依赖,赋值来源的检查器
	 * 
	 * @author houchunlei
	 */
	private static final class Single extends TraversedExprVisitor<Object> {

		final ArrayList<FieldAssign> list = new ArrayList<FieldAssign>();
		final MultipleResolver resolver;
		final DBTableDefineImpl dbTable;
		final int dbTableIndex;
		final BitSet assignValueUsingTables = new BitSet();

		Single(MultipleResolver resolver, DBTableDefineImpl dbTable) {
			this.resolver = resolver;
			this.dbTable = dbTable;
			this.dbTableIndex = dbTable.index();
		}

		final void visitAssignValue() {
			for (int i = 0, c = this.list.size(); i < c; i++) {
				FieldAssign fa = this.list.get(i);
				fa.value().visit(this, null);
			}
		}

		private boolean assignValueFromJoinedRef;

		private boolean assignValueFromTargetRefOnSlave;

		boolean assignValueFromJoin() {
			return this.assignValueFromJoinedRef || this.assignValueFromTargetRefOnSlave;
		}

		@Override
		public void visitSelectColumnRef(SelectColumnRefImpl expr,
				Object context) {
			if (expr.queryRef instanceof MoJoinedQueryRef) {
				this.assignValueFromJoinedRef = true;
			}
		}

		@Override
		public void visitTableFieldRef(TableFieldRefImpl fieldRef,
				Object context) {
			if (fieldRef.tableRef instanceof MoJoinedTableRef) {
				this.assignValueFromJoinedRef = true;
			}
			DBTableDefineImpl dbTable = fieldRef.field.getDBTable();
			if (fieldRef.field.owner == this.resolver.table && dbTable != this.dbTable) {
				if (fieldRef.tableRef == this.resolver.update.moTableRef) {
					this.assignValueFromTargetRefOnSlave = true;
				}
				if (this.resolver.update.assigns.contains(fieldRef.field)) {
					this.assignValueUsingTables.set(dbTable.index());
					this.resolver.assignValueDependOn = true;
				}
			}
		}

		@Override
		public final String toString() {
			return "dbUpdate[" + this.dbTableIndex + "]";
		}
	}

	/**
	 * 逻辑表的更新信息记录
	 * 
	 * <p>
	 * 同时用作更新条件以及连接条件的检查器,检查条件来源及条件冲突
	 * 
	 * @author houchunlei
	 * 
	 */
	private static final class MultipleResolver extends
			TraversedExprVisitor<Object> {

		final ArrayList<Single> list = new ArrayList<Single>();

		final UpdateStatementImpl update;

		final TableDefineImpl table;

		private boolean assignValueDependOn;

		final BitSet conditionConflict = new BitSet();

		MultipleResolver(UpdateStatementImpl update) {
			this.update = update;
			this.table = update.moTableRef.target;
			Single single = null;
			for (int i = 0; i < update.assigns.size(); i++) {
				FieldAssign fa = update.assigns.get(i);
				DBTableDefineImpl dbTable = fa.field.dbTable;
				if (single != null && single.dbTable == dbTable) {
					single.list.add(fa);
				} else {
					ensure: {
						for (int ti = 0, c = this.list.size(); ti < c; ti++) {
							single = this.list.get(ti);
							if (single.dbTable == dbTable) {
								single.list.add(fa);
								break ensure;
							}
						}
						single = new Single(this, dbTable);
						this.list.add(single);
						single.list.add(fa);
					}
				}
			}
			if (this.update.getCondition() != null) {
				this.update.getCondition().visit(this, null);
			}
			if (this.update.moTableRef.getJoins() != null) {
				for (MoRelationRef relationRef : this.update.moTableRef) {
					relationRef.visit(this, null);
				}
			}
			for (int i = 0, c = this.list.size(); i < c; i++) {
				this.list.get(i).visitAssignValue();
			}
		}

		// private final Single get(int dbTableIndex) {
		// for (int i = 0, c = this.list.size(); i < c; i++) {
		// Single single = this.list.get(i);
		// if (single.dbTableIndex == dbTableIndex) {
		// return single;
		// }
		// }
		// throw new NullPointerException();
		// }
		//
		// boolean conditionNonDeterministic;
		//
		// @Override
		// public void visitOperateExpr(OperateExpr expr, Object context) {
		// super.visitOperateExpr(expr, context);
		// if (expr.isNonDeterministic()) {
		// this.conditionNonDeterministic = true;
		// }
		// }

		@Override
		public void visitTableFieldRef(TableFieldRefImpl fieldRef,
				Object context) {
			if (fieldRef.field.owner == this.table) {
				DBTableDefineImpl dbTable = fieldRef.field.dbTable;
				if (this.update.assigns.contains(fieldRef.field)) {
					this.conditionConflict.set(dbTable.index());
				}
			}
		}
	}

	private static final void join(ISqlTableRefBuffer from, String alias,
			MoRootTableRef tableRef, UpdateStatus status,
			DBTableDefineImpl except) {
		TableUsage usage = status.usageOf(tableRef);
		if (usage != null) {
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (dbTable == except) {
					continue;
				}
				String ja = Render.aliasOf(tableRef, dbTable);
				Render.renderLeftJoinOnRecidEq(from, alias, dbTable.name, ja);
			}
		}
		tableRef.render(from, status);
	}
}