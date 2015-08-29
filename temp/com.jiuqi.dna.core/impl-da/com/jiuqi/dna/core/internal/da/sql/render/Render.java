package com.jiuqi.dna.core.internal.da.sql.render;

import static com.jiuqi.dna.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

import java.util.ArrayList;

import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.def.table.DBTableDefine;
import com.jiuqi.dna.core.def.table.TableJoinType;
import com.jiuqi.dna.core.impl.ConditionalExpr;
import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.JoinedTableRef;
import com.jiuqi.dna.core.impl.PredicateExpr;
import com.jiuqi.dna.core.impl.QuTableRef;
import com.jiuqi.dna.core.impl.QueryColumnImpl;
import com.jiuqi.dna.core.impl.QueryStatementBase;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldRefImpl;
import com.jiuqi.dna.core.impl.TableRef;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.impl.TableUsages;
import com.jiuqi.dna.core.impl.TraversedExprVisitor;
import com.jiuqi.dna.core.impl.ValueExpr;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlJoinedTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlRelationRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;

public final class Render {

	private Render() {
	}

	/**
	 * �����߼������õ������ʹ��״̬,renderʹ�õ��������,��������Ӧ������.
	 * 
	 * <ul>
	 * <li>ֻʹ������,ֱ��render����.
	 * <li>ͬʱʹ��������͸���,�������������left����.
	 * </ul>
	 * 
	 * @param tableRef
	 *            �߼�������
	 * @param buffer
	 * @param usages
	 *            �߼���ʹ�����
	 * @return
	 */
	public static final ISqlTableRefBuffer renderTableRef(TableRef tableRef,
			ISqlSelectBuffer buffer, TableUsages usages) {
		if (tableRef.getTarget() == TableDefineImpl.DUMMY) {
			buffer.fromDummy();
			return null;
		}
		TableUsage usage = usages.usageOf(tableRef);
		if (usage == null || usage.tableCount() == 0) {
			return buffer.newTableRef(tableRef.getTarget().primary.namedb(), tableRef.getName());
		} else if (usage.tableCount() == 1) {
			DBTableDefineImpl used = usage.firstTable();
			return buffer.newTableRef(used.namedb(), Render.aliasOf(tableRef, used));
		} else {
			ISqlTableRefBuffer trb = null;
			String left = null;
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (trb == null) {
					trb = buffer.newTableRef(dbTable.namedb(), left = Render.aliasOf(tableRef, dbTable));
				} else {
					renderLeftJoinOnRecidEq(trb, left, dbTable.namedb(), aliasOf(tableRef, dbTable));
				}
			}
			return trb;
		}
	}

	public static final ISqlJoinedTableRefBuffer renderJoinedTableRef(
			JoinedTableRef tableRef, ISqlRelationRefBuffer buffer,
			ConditionalExpr condition, TableUsages usages) {
		if (tableRef.getTarget() == TableDefineImpl.DUMMY) {
			throw new UnsupportedOperationException("DUMMY��������");
		}
		TableUsage usage = usages.usageOf(tableRef);
		if (usage == null || usage.tableCount() == 0) {
			ISqlJoinedTableRefBuffer trb = buffer.joinTable(tableRef.getTarget().primary.namedb(), tableRef.getName(), tableRef.getJoinType());
			condition.render(trb.onCondition(), usages);
			return trb;
		} else if (usage.tableCount() == 1) {
			ISqlJoinedTableRefBuffer trb = buffer.joinTable(usage.firstTable().namedb(), tableRef.getName(), tableRef.getJoinType());
			condition.render(trb.onCondition(), usages);
			return trb;
		} else if(hasSlaveColumn(condition, usage)){
			ISqlJoinedTableRefBuffer trb = null;
			String left = null;
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (trb == null) {
					trb = buffer.joinTable(dbTable.namedb(), left = Render.aliasOf(tableRef, dbTable), TableJoinType.INNER);
				} else {
					renderLeftJoinOnRecidEq(trb, left, dbTable.namedb(), aliasOf(tableRef, dbTable));
				}
			}
			condition.render(trb.onCondition(), usages);
			return trb;
		} else {  //��������ֻ���������ֶΣ���˳�����join
			ISqlJoinedTableRefBuffer join = null;
			String left = null;
			for (DBTableDefineImpl dbTable : usage.tables()) {
				if (join == null) {
					left = Render.aliasOf(tableRef, dbTable);
					join = buffer.joinTable(dbTable.namedb(), left, TableJoinType.INNER);
					condition.render(join.onCondition(), usages);
				} else {
					String right = aliasOf(tableRef, dbTable);
					join = buffer.joinTable(dbTable.namedb(), right, TableJoinType.LEFT);
					ISqlExprBuffer cond = join.onCondition();
					cond.loadColumnRef(left, FIELD_DBNAME_RECID);
					cond.loadColumnRef(right, FIELD_DBNAME_RECID);
					cond.eq();
				}
			}
			return join;
		}
	}
	
	private static boolean hasSlaveColumn(ConditionalExpr where, TableUsage usage) {
		TableFieldRefDetector detector = new TableFieldRefDetector();
		detector.visitSelectWhere(where, null);
		TableDefineImpl tableDefine = usage.firstTable().getOwner();
		for (TableFieldRefExpr fieldRef : detector.fields) {
			if (fieldRef.getColumn().getOwner().equals(tableDefine)) {
				DBTableDefine dbTable = fieldRef.getColumn().getDBTable();
				if(!dbTable.equals(usage.firstTable())) {
					return true;	
				}
			}
		}
		return false;
	}
	
	static final class TableFieldRefDetector extends TraversedExprVisitor<Object> {
		
		final ArrayList<TableFieldRefExpr> fields = new ArrayList<TableFieldRefExpr>(); 
		
		@Override
		public void visitPredicateExpr(PredicateExpr expr, Object context) {
			for (ValueExpr value : expr.values) {
				if (value instanceof TableFieldRefExpr) {
					TableFieldRefExpr fieldRef = (TableFieldRefExpr) value;
					fields.add(fieldRef);
				}
			}
		}

	}

	/**
	 * ����recid��ֵ��������.������������ӷ�ʽ.
	 * 
	 * @param left
	 *            ���
	 * @param leftAlias
	 *            ������
	 * @param rightTable
	 *            �ұ�����
	 * @param rightAlias
	 *            �ұ����
	 */
	public static final void renderLeftJoinOnRecidEq(ISqlTableRefBuffer left,
			String leftAlias, String rightTable, String rightAlias) {
		ISqlJoinedTableRefBuffer join = left.joinTable(rightTable, rightAlias, TableJoinType.LEFT);
		ISqlExprBuffer condition = join.onCondition();
		condition.loadColumnRef(leftAlias, FIELD_DBNAME_RECID);
		condition.loadColumnRef(rightAlias, FIELD_DBNAME_RECID);
		condition.eq();
	}

	public static final String aliasOf(TableRef tableRef,
			DBTableDefineImpl dbTable) {
		return dbTable.isPrimary() ? tableRef.getName() : tableRef.getName() + "_" + dbTable.index();
	}

	public static final IllegalStatementDefineException noRecidColumnForTable(
			QueryStatementBase statement, TableDefineImpl table) {
		return new IllegalStatementDefineException(statement, "��ѯ��䶨��[" + statement.name + "]û�������[" + table.name + "]��RECID�С�");
	}

	// static final IllegalStatementDefineException duplicateModifyColumn() {
	// return new IllegalStatementDefineException("�ظ����޸���ֵ");
	// }

	public static final IllegalStatementDefineException duplicateModifyTable(
			QueryStatementBase statement, TableDefineImpl table) {
		return new IllegalStatementDefineException(statement, "��ѯ��䶨��[" + statement.name + "]�ظ����±�[" + table.name + "]��");
	}

	public static final String rowModifyAlias(DBTableDefineImpl dbTable) {
		return dbTable.name;
	}

	public static final IllegalStatementDefineException modifyTableNotSupport(
			QueryStatementBase statement) {
		throw new IllegalStatementDefineException(statement, "��ѯ����[" + statement.name + "]����������û�ж����κοɸ��µ������");
	}

	public static final TableFieldDefineImpl tryGetUpdateFieldFor(
			QueryColumnImpl qc, QuTableRef tableRef, DBTableDefineImpl dbTable) {
		if (tableRef.getTarget() != dbTable.owner) {
			throw new IllegalArgumentException();
		}
		if (qc.value() instanceof TableFieldRefImpl) {
			TableFieldRefImpl fieldRef = (TableFieldRefImpl) qc.value();
			TableFieldDefineImpl field = fieldRef.field;
			if (fieldRef.tableRef == tableRef && field.dbTable == dbTable && !field.isRECID()) {
				return field;
			}
		}
		return null;
	}

	public static final TableFieldDefineImpl tryGetInsertFieldFor(
			QueryColumnImpl qc, QuTableRef tableRef, DBTableDefineImpl dbTable) {
		if (tableRef.getTarget() != dbTable.owner) {
			throw new IllegalArgumentException();
		}
		if (qc.value() instanceof TableFieldRefImpl) {
			TableFieldRefImpl fieldRef = (TableFieldRefImpl) qc.value();
			if (fieldRef.tableRef == tableRef && (fieldRef.field.dbTable == dbTable || fieldRef.field.isRECID())) {
				return fieldRef.field;
			}
		}
		return null;
	}
}
