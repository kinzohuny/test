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
 * 更新语句的Sql
 * 
 * <p>
 * dna-sql的Update语法主要参考了SQLServer的语法规则.相比SQLServer语法,省略from关键字,将join提前,
 * 但仍只是对单逻辑表的更新,并且等价于强制SQLServer的from子句的第一个表引用必须指向更新目标表.
 * 
 * <blockquote>
 * 
 * <pre>
 * update [Target] [left|right|full]
 * 	join [Reference] [,n..]
 * 	set [...]
 * 	where [...]</prev></blockquote>
 * 
 * <p>sqlserver对此语法的实际执行计划为:计算from子句的关系:如果包含了目标关系,则在目标关系上投影,
 * 对每个有效的投影行进行Update操作;如果不包含目标关系,则直接循环目标关系,对from关系流聚合后做更新操作.
 * 注意到连接的参考表实际提供了:行过滤,尤其内连接情况下;where条件的参考值;set子句的参考值.
 * 
 * <p>sqlserver的这种语法形式本质上是不够严谨的.
 * 例如,有关系A(id,fid)实例为((0,a),(1,b),(2,c))与关系B(id,k)实例为((a,10),(a,20),(c,30)),假设更新关系A,
 * from子句内做"A.fid = B.id"的内连接.由于关系B的id不是唯一的,导致对关系A的每一确定元祖,将会对应到多个B的元祖上.
 * 此时参考关系无论是提供where参考值或是set参考值都是不可行的.在这种情况下,sqlserver将对关系B做流聚合运算,实质为ANY运算.
 * 即取任意随机元祖,提供对关系A更新的参考.即意味着,更新结果是不稳定的.
 * 
 * <p>
 * oracle的Update语句,如使用标准sql提供类似实现,行过滤与where参考将单独组装成"子查询",set值参考将单独组装成"内联视图".
 * 这两个查询在执行计划中是分开进行的.而更接近于连接本质的语法:update(select ... from 目标关系 join 参考关系)set [...],
 * 这种语法,Oracle强制要求参考关系必须通过1对1或者1对n的关系连接到目标关系上,即强制要求(hint可取消)参考关系的等值连接列为主键或有唯一索引(多键唯一索引同理).
 * 这种处理方式才是严谨的.
 * 
 * <p>
 * 但由于提供了join连接,在实现上只能参考sqlserver的逻辑.
 * 
 * <p>
 * 由于dna-sql的update是针对单逻辑表的,即可能需要一次更新多个物理表.首先规定:
 * <ul>
 * <li>update指对整个逻辑表的更新.
 * <li>dbUpdate指对单一物理表的更新.
 * </ul>
 * 
 * <p>在多物理表的update时,存在情况:dbUpdate破坏了update的条件或其他dbUpdate的赋值来源.
 * 而从逻辑表update的层面来看,必须各dbUpdate的参考值都是针对 整个update的前值才是合理的.
 * 
 * <p>
 * 再定义术语:
 * <ol>
 * <li><strong>条件冲突(ConditonConflict)</strong>:update的目标字段,作为了update的条件,称该目标字段所在的物理表存在条件冲突.
 * <li><strong>赋值依赖(AssignDependOn)</strong>:目标dbUpdate的赋值(set值)参考了其他dbUpdate中会被更新的字段,称目标依赖于参考,实质为:目标字段必须先于参考字段进行.
 * <li><strong>条件来源(ConditionFrom)</strong>:在update的条件中,使用到的所有目标表的参考字段所属于的物理表.
 * </ol>
 * 
 * <p>
 * 在不考虑join的情况下,讨论多表更新可能的问题.当CC为1时,该物理表需要最后更新.当CC大于1时,任一dbUpdate都会破坏剩余dbUpdate的条件.
 * 赋值依赖本质为更新字段顺序的上的依赖,该依赖顺序是可能有解的,可以在字段级别,也可以在dbUpdate级别.目前为了简化编译,在dbUpdate级别处理.
 * 即ADO转换为dbUpdate顺序求解的问题,并且需要考虑CC来求解.
 */
// took too much time here...=.=
public final class DeprecatedSimpleUpdateSql extends SimpleModifySql {

	public DeprecatedSimpleUpdateSql(DbMetadata dbMetadata,
			final UpdateStatementImpl update) {
		if (update.assigns.size() == 0) {
			throw new IllegalStatementDefineException(update, "更新语句定义[" + update.name + "]未定义任何更新列。");
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
		// "不支持条件来源多物理表的更新语句。");

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
	 * mysql特性,一个update语句更新多张表.
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