package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.query.ModifyStatementDeclare;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;

/**
 * 更新语句
 * 
 * @author houchunlei
 * 
 */
abstract class ModifyStatementImpl extends StatementImpl implements
		ModifyStatementDeclare, RelationRefDomain, OMVisitable {

	public final DerivedQueryImpl newDerivedQuery() {
		return new DerivedQueryImpl(this);
	}

	public final SubQueryImpl newSubQuery() {
		return new SubQueryImpl(this);
	}

	public final TableFieldRefImpl expOf(RelationColumnDefine column) {
		if (column instanceof TableFieldDefineImpl) {
			TableFieldDefineImpl f = (TableFieldDefineImpl) column;
			return new TableFieldRefImpl(this.moTableRef, f);
		}
		throw RelationRefImpl.notSupportedRelationColumnRefException(this.moTableRef, column);
	}

	public final TableFieldRefImpl expOf(String columnName) {
		return this.moTableRef.expOf(this.moTableRef.getTarget().getColumn(columnName));
	}

	static final ExistingDetector<StringKeyMap<MoRelationRef>, MoRelationRef, String> detector = new ExistingDetector<StringKeyMap<MoRelationRef>, MoRelationRef, String>() {

		public boolean exists(StringKeyMap<MoRelationRef> container,
				String key, MoRelationRef ignore) {
			MoRelationRef relationRef = container.find(key);
			return relationRef != null && (ignore == null || relationRef != ignore);
		}

	};

	/**
	 * 更新表引用
	 */
	public final MoRootTableRef moTableRef;

	final StringKeyMap<MoRelationRef> refs = new StringKeyMap<MoRelationRef>(false);

	ModifyStatementImpl(String name, String alias, TableDefineImpl table) {
		super(name);
		if (table == null) {
			throw new NullPointerException();
		}
		this.moTableRef = new MoRootTableRef(this, alias, table);
		this.refs.put(alias, this.moTableRef);
	}

	ModifyStatementImpl(String name, String alias, TableDefineImpl table,
			StructDefineImpl arguments) {
		super(name, arguments);
		if (table == null) {
			throw new NullPointerException();
		}
		this.moTableRef = new MoRootTableRef(this, alias, table);
		this.refs.put(alias, this.moTableRef);
	}

	final TableFieldRefImpl exprOf(TableFieldDefineImpl field) {
		if (field == null) {
			throw new NullArgumentException("字段定义");
		}
		return new TableFieldRefImpl(this.moTableRef, field);
	}

	final TableFieldDefineImpl checkOwner(TableFieldDefine field) {
		TableFieldDefineImpl f = (TableFieldDefineImpl) field;
		if (f.owner != this.moTableRef.target) {
			throw new IllegalArgumentException("字段定义[" + f.name + "]不属于当前更新语句的目标表定义[" + this.moTableRef.target.name + "].");
		}
		return f;
	}

	public final MoRelationRef findRelationRef(String name) {
		return this.refs.find(name);
	}

	public final MoRelationRef getRelationRef(String name) {
		MoRelationRef relationRef = this.findRelationRef(name);
		if (relationRef != null) {
			return relationRef;
		}
		throw missingRelationRef(name);
	}

	public final MoRelationRef findRelationRefRecursively(String name) {
		return this.findRelationRef(name);
	}

	public final MoRelationRef getRelationRefRecursively(String name) {
		return this.getRelationRef(name);
	}

	public final DerivedQueryImpl getWith(String name) {
		throw new UnsupportedOperationException();
	}

	public final RelationRefDomain getDomain() {
		return null;
	}

	static final MissingDefineException missingRelationRef(String name) {
		return new MissingDefineException("不存在名称为[" + name + "]的关系引用定义");
	}

	@Override
	public abstract ModifySql getSql(DBAdapterImpl dbAdapter);
}