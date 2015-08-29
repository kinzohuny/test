package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DBTableDefineImpl;
import com.jiuqi.dna.core.impl.GUIDType;
import com.jiuqi.dna.core.impl.IllegalStatementDefineException;
import com.jiuqi.dna.core.impl.MoRootTableRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableUsage;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl.FieldAssign;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlConditionBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlCursorLoopBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSegmentBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlTableRefBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateMultiBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlUpdateMultiCommandFactory;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlPredicate;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

/**
 * ��������Sql
 * 
 * <p>
 * dna-sql��Update�﷨��Ҫ�ο���SQLServer���﷨����.���SQLServer�﷨,ʡ��from�ؼ���,��join��ǰ,
 * ����ֻ�ǶԵ��߼���ĸ���,���ҵȼ���ǿ��SQLServer��from�Ӿ�ĵ�һ�������ñ���ָ�����Ŀ���.
 * 
 * <blockquote>
 * 
 * <pre>
 * update [Target] [left|right|full]
 * 	join [Reference] [,n..]
 * 	set [...]
 * 	where [...]</prev></blockquote>
 * 
 * <p>sqlserver�Դ��﷨��ʵ��ִ�мƻ�Ϊ:����from�Ӿ�Ĺ�ϵ:���������Ŀ���ϵ,����Ŀ���ϵ��ͶӰ,
 * ��ÿ����Ч��ͶӰ�н���Update����;���������Ŀ���ϵ,��ֱ��ѭ��Ŀ���ϵ,��from��ϵ���ۺϺ������²���.
 * ע�⵽���ӵĲο���ʵ���ṩ��:�й���,���������������;where�����Ĳο�ֵ;set�Ӿ�Ĳο�ֵ.
 * 
 * <p>sqlserver�������﷨��ʽ�������ǲ����Ͻ���.
 * ����,�й�ϵA(id,fid)ʵ��Ϊ((0,a),(1,b),(2,c))���ϵB(id,k)ʵ��Ϊ((a,10),(a,20),(c,30)),������¹�ϵA,
 * from�Ӿ�����"A.fid = B.id"��������.���ڹ�ϵB��id����Ψһ��,���¶Թ�ϵA��ÿһȷ��Ԫ��,�����Ӧ�����B��Ԫ����.
 * ��ʱ�ο���ϵ�������ṩwhere�ο�ֵ����set�ο�ֵ���ǲ����е�.�����������,sqlserver���Թ�ϵB�����ۺ�����,ʵ��ΪANY����.
 * ��ȡ�������Ԫ��,�ṩ�Թ�ϵA���µĲο�.����ζ��,���½���ǲ��ȶ���.
 * 
 * <p>
 * oracle��Update���,��ʹ�ñ�׼sql�ṩ����ʵ��,�й�����where�ο���������װ��"�Ӳ�ѯ",setֵ�ο���������װ��"������ͼ".
 * ��������ѯ��ִ�мƻ����Ƿֿ����е�.�����ӽ������ӱ��ʵ��﷨:update(select ... from Ŀ���ϵ join �ο���ϵ)set [...],
 * �����﷨,Oracleǿ��Ҫ��ο���ϵ����ͨ��1��1����1��n�Ĺ�ϵ���ӵ�Ŀ���ϵ��,��ǿ��Ҫ��(hint��ȡ��)�ο���ϵ�ĵ�ֵ������Ϊ��������Ψһ����(���Ψһ����ͬ��).
 * ���ִ���ʽ�����Ͻ���.
 * 
 * <p>
 * �������ṩ��join����,��ʵ����ֻ�ܲο�sqlserver���߼�.
 * 
 * <p>
 * ����dna-sql��update����Ե��߼����,��������Ҫһ�θ��¶�������.���ȹ涨:
 * <ul>
 * <li>updateָ�������߼���ĸ���.
 * <li>dbUpdateָ�Ե�һ�����ĸ���.
 * </ul>
 * 
 * <p>�ڶ�������updateʱ,�������:dbUpdate�ƻ���update������������dbUpdate�ĸ�ֵ��Դ.
 * �����߼���update�Ĳ�������,�����dbUpdate�Ĳο�ֵ������� ����update��ǰֵ���Ǻ����.
 * 
 * <p>
 * �ٶ�������:
 * <ol>
 * <li><strong>������ͻ(ConditonConflict)</strong>:update��Ŀ���ֶ�,��Ϊ��update������,�Ƹ�Ŀ���ֶ����ڵ���������������ͻ.
 * <li><strong>��ֵ����(AssignDependOn)</strong>:Ŀ��dbUpdate�ĸ�ֵ(setֵ)�ο�������dbUpdate�лᱻ���µ��ֶ�,��Ŀ�������ڲο�,ʵ��Ϊ:Ŀ���ֶα������ڲο��ֶν���.
 * <li><strong>������Դ(ConditionFrom)</strong>:��update��������,ʹ�õ�������Ŀ���Ĳο��ֶ������ڵ������.
 * </ol>
 * 
 * <p>
 * �ڲ�����join�������,���۶����¿��ܵ�����.��CCΪ1ʱ,���������Ҫ������.��CC����1ʱ,��һdbUpdate�����ƻ�ʣ��dbUpdate������.
 * ��ֵ��������Ϊ�����ֶ�˳����ϵ�����,������˳���ǿ����н��,�������ֶμ���,Ҳ������dbUpdate����.ĿǰΪ�˼򻯱���,��dbUpdate������.
 * ��ADOת��ΪdbUpdate˳����������,������Ҫ����CC�����.
 */
// took too much time here...=.=
public final class DeprecatedSimpleUpdateSql extends SimpleModifySql {

	public DeprecatedSimpleUpdateSql(DbMetadata dbMetadata,
			final UpdateStatementImpl update) {
		if (update.assigns.size() == 0) {
			throw new IllegalStatementDefineException(update, "������䶨��[" + update.name + "]δ�����κθ����С�");
		}
		final ISqlCommandFactory factory = dbMetadata.sqlbuffers();
		final ISqlUpdateMultiCommandFactory umf = factory.getFeature(ISqlUpdateMultiCommandFactory.class);
		final UpdateStatementStatusVisitor visitor = new UpdateStatementStatusVisitor(update);
		if (update.moTableRef.target.dbTables.size() == 1) {
			updateSingle(update, visitor, factory, this);
		} else if (umf != null) {
			updateMultipleOnce(update, visitor, umf, this);
		} else {
			UpdateMultipleResolver resolver = new UpdateMultipleResolver(update);
			if (resolver.dbTables.size() == 1) {
				UpdateSingleDbTable single = resolver.dbTables.get(0);
				final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
				ISqlUpdateBuffer buffer = factory.update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
				multiple(update, visitor, single, buffer, alias);
				this.build(buffer);
			} else if (resolver.tryResolveSequence()) {
				ISqlSegmentBuffer segment = factory.segment();
				for (UpdateSingleDbTable single : resolver.dbTables) {
					final String alias = Render.aliasOf(update.moTableRef, single.dbTable);
					ISqlUpdateBuffer buffer = segment.update(single.dbTable.namedb(), alias, single.assignValueFromJoin());
					multiple(update, visitor, single, buffer, alias);
				}
				this.build(segment);
			} else {
				ISqlSegmentBuffer segment = factory.segment();
				cursor(update, visitor, resolver, segment);
				this.build(segment);
			}
		}
		// else if (resolver.directResolvable()) {
		// ISqlSegmentBuffer segment = factory.segment();
		// for (Single single : resolver.dbTables) {
		// final String alias = Render.aliasOf(update.moTableRef,
		// single.dbTable);
		// ISqlUpdateBuffer buffer = segment.update(
		// single.dbTable.namedb(), alias,
		// single.assignValueFromJoin());
		// multiple(update, visitor, single, buffer, alias);
		// }
		// this.build(segment);
		// }
		// throw new IllegalStatementDefineException(update,
		// "��֧��������Դ�������ĸ�����䡣");

		// final ISqlUpdateMultiCommandFactory umf = factory
		// .getFeature(ISqlUpdateMultiCommandFactory.class);
		// if (umf != null) {
		// updateMultipleOnce(update, visitor, umf, this);
		// } else {
		// MultipleResolver resolver = new MultipleResolver(update);
		// if (resolver.dbTables.size() == 1) {
		// Single single = resolver.dbTables.get(0);
		// final String alias = Render.aliasOf(update.moTableRef,
		// single.dbTable);
		// ISqlUpdateBuffer buffer = factory.update(
		// single.dbTable.namedb(), alias,
		// single.assignValueFromJoin());
		// multiple(update, visitor, single, buffer, alias);
		// this.build(buffer);
		// } else if (resolver.tryResolveSequence()) {
		// ISqlSegmentBuffer segment = factory.segment();
		// for (Single single : resolver.dbTables) {
		// final String alias = Render.aliasOf(update.moTableRef,
		// single.dbTable);
		// ISqlUpdateBuffer buffer = segment.update(
		// single.dbTable.namedb(), alias,
		// single.assignValueFromJoin());
		// multiple(update, visitor, single, buffer, alias);
		// }
		// this.build(segment);
		// } else {
		// ISqlSegmentBuffer segment = factory.segment();
		// cursor(update, visitor, resolver, segment);
		// this.build(segment);
		// }
		// }
	}

	private static final void updateSingle(UpdateStatementImpl update,
			UpdateStatementStatusVisitor status, ISqlCommandFactory factory,
			DeprecatedSimpleUpdateSql sql) {
		ISqlUpdateBuffer buffer = factory.update(update.moTableRef.target.primary.namedb(), update.moTableRef.name, status.assignValueFromJoinedRef());
		update.moTableRef.render(buffer.target(), status);
		for (int i = 0; i < update.assigns.size(); i++) {
			FieldAssign fa = update.assigns.get(i);
			ISqlExprBuffer value = buffer.newValue(fa.field.namedb());
			fa.value().render(value, status);
		}
		if (update.getCondition() != null) {
			update.getCondition().render(buffer.where(), status);
		}
		sql.build(buffer);
	}

	private static final void multiple(UpdateStatementImpl update,
			UpdateStatementStatusVisitor status, UpdateSingleDbTable single,
			ISqlUpdateBuffer buffer, String alias) {
		join(buffer.target(), alias, update.moTableRef, status, single.dbTable);
		for (FieldAssign fa : single.assigns) {
			fa.value().render(buffer.newValue(fa.field.namedb()), status);
		}
		if (update.getCondition() != null) {
			update.getCondition().render(buffer.where(), status);
		}
	}

	/**
	 * mysql����,һ��update�����¶��ű�.
	 * 
	 * @param update
	 * @param visitor
	 * @param umf
	 * @param sql
	 */
	private static final void updateMultipleOnce(UpdateStatementImpl update,
			UpdateStatementStatusVisitor visitor,
			ISqlUpdateMultiCommandFactory umf, DeprecatedSimpleUpdateSql sql) {
		final MoRootTableRef tableRef = update.moTableRef;
		TableUsage usage = visitor.ensureUsageOf(tableRef);
		for (int i = 0; i < update.assigns.size(); i++) {
			usage.use(update.assigns.get(i).field.dbTable);
		}
		ISqlUpdateMultiBuffer buffer = null;
		String alias = null;
		for (DBTableDefineImpl dbTable : usage.tables()) {
			if (buffer == null) {
				alias = Render.aliasOf(tableRef, dbTable);
				buffer = umf.updateMultiple(dbTable.namedb(), alias);
			} else {
				if (alias == null) {
					throw new IllegalStateException();
				}
				String ja = Render.aliasOf(tableRef, dbTable);
				Render.renderLeftJoinOnRecidEq(buffer.target(), alias, dbTable.namedb(), ja);
			}
		}
		if (buffer == null) {
			throw new IllegalStateException();
		}
		tableRef.render(buffer.target(), visitor);
		for (int i = 0; i < update.assigns.size(); i++) {
			FieldAssign fa = update.assigns.get(i);
			ISqlExprBuffer value = buffer.newValue(Render.aliasOf(tableRef, fa.field.dbTable), fa.field.namedb());
			fa.value().render(value, visitor);
		}
		if (update.getCondition() != null) {
			update.getCondition().render(buffer.where(), visitor);
		}
		sql.build(buffer);
	}

	private static final void join(ISqlTableRefBuffer from, String alias,
			MoRootTableRef tableRef, UpdateStatementStatusVisitor status,
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

	private static final void cursor(UpdateStatementImpl update,
			UpdateStatementStatusVisitor visitor,
			UpdateMultipleResolver resolver, ISqlSegmentBuffer segment) {
		segment.declare(VAR_LAST_RECID, GUIDType.TYPE);
		ISqlCursorLoopBuffer cursor = defineCursor(segment, update, visitor);
		ISqlConditionBuffer ifs = cursor.ifThenElse();
		whenNotLastRecid(ifs.newWhen());
		updateCurrent(ifs.newThen(), resolver);
	}

	private static final String VAR_LAST_RECID = "LAST_RECID";
	private static final String CUR_NAME = "CUR";
	private static final String RECID_OUTPUT_ALIAS = "RECID_OUTPUT_ALIAS";
	private static final String VAR_RECID_OUTPUT = "RECID_OUTPUT";

	private static final ISqlCursorLoopBuffer defineCursor(
			ISqlSegmentBuffer segment, UpdateStatementImpl update,
			UpdateStatementStatusVisitor visitor) {
		MoRootTableRef tableRef = update.moTableRef;
		TableDefineImpl table = tableRef.target;
		ISqlCursorLoopBuffer cursor = segment.cursorLoop(CUR_NAME, true);
		ISqlSelectBuffer select = cursor.query().select();
		String alias = Render.aliasOf(tableRef, table.primary);
		ISqlTableRefBuffer from = select.newTableRef(table.primary.name, alias);
		join(from, alias, tableRef, visitor, table.primary);
		select.newColumn(RECID_OUTPUT_ALIAS).loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		cursor.declare(VAR_RECID_OUTPUT, GUIDType.TYPE);
		for (int i = 0, c = update.assigns.size(); i < c; i++) {
			FieldAssign fa = update.assigns.get(i);
			fa.value().render(select.newColumn(fa.field.name), visitor);
			cursor.declare(fa.field.name, fa.field.getType());
		}
		cursor.query().newOrder(false).loadColumnRef(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		return cursor;
	}

	private static final void whenNotLastRecid(ISqlExprBuffer when) {
		when.loadVar(VAR_LAST_RECID).predicate(SqlPredicate.IS_NULL, 1);
		when.loadVar(VAR_LAST_RECID).loadVar(VAR_RECID_OUTPUT).ne();
		when.or(2);
	}

	private static final void updateCurrent(ISqlSegmentBuffer segment,
			UpdateMultipleResolver resolver) {
		segment.assign(VAR_LAST_RECID).loadVar(VAR_RECID_OUTPUT);
		for (UpdateSingleDbTable single : resolver.dbTables) {
			ISqlUpdateBuffer update = segment.update(single.dbTable.namedb(), "T", false);
			update.whereCurrentOf(CUR_NAME);
			for (int i = 0, c = single.assigns.size(); i < c; i++) {
				FieldAssign fa = single.assigns.get(i);
				update.newValue(fa.field.namedb()).loadVar(fa.field.name);
			}
		}
	}
}