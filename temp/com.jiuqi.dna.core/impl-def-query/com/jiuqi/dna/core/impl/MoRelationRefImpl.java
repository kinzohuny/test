package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Filter;
import com.jiuqi.dna.core.def.query.DerivedQueryDefine;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableRelationDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 更新语句使用的关系引用
 * 
 * @author houchunlei
 * 
 * @param <TRelation>
 *            目标关系类型
 * @param <TLink>
 *            自身及链表类型
 * @param <TItrNode>
 *            迭代类型
 */
public abstract class MoRelationRefImpl<TRelation extends Relation, TLink extends MoRelationRef, TItrNode extends MoRelationRef>
		extends
		NodableRelationRefImpl<TRelation, TLink, MoJoinedRelationRef, TItrNode>
		implements MoRelationRef {

	public final ModifyStatementImpl getOwner() {
		return this.owner;
	}

	public final MoJoinedTableRef newJoin(TableDefine target) {
		if (target == null) {
			throw new NullArgumentException("表定义");
		}
		return this.newJoin(target.getName(), (TableDefineImpl) target);
	}

	public final MoJoinedTableRef newJoin(TableDefine target, String name) {
		if (target == null) {
			throw new NullArgumentException("表定义");
		}
		return this.newJoin(name, (TableDefineImpl) target);
	}

	public final MoJoinedTableRef newJoin(TableDeclarator target) {
		if (target == null) {
			throw new NullArgumentException("表定义");
		}
		TableDefineImpl table = (TableDefineImpl) target.getDefine();
		return this.newJoin(table.name, table);
	}

	public final MoJoinedTableRef newJoin(TableDeclarator target, String name) {
		if (target == null) {
			throw new NullArgumentException("表定义");
		}
		TableDefineImpl table = (TableDefineImpl) target.getDefine();
		return this.newJoin(name, table);
	}

	public final MoJoinedTableRef newJoin(TableRelationDefine sample) {
		if (sample == null) {
			throw new NullArgumentException("表关系定义");
		}
		return this.newJoin(sample.getName(), (TableRelationDefineImpl) sample);
	}

	public final MoJoinedTableRef newJoin(TableRelationDefine sample,
			String name) {
		if (sample == null) {
			throw new NullArgumentException("表关系定义");
		}
		return this.newJoin(name, (TableRelationDefineImpl) sample);
	}

	public final MoJoinedQueryRef newJoin(DerivedQueryDefine query) {
		if (query == null) {
			throw new NullArgumentException("查询定义");
		}
		return this.newJoin(query.getName(), (DerivedQueryImpl) query);
	}

	public final MoJoinedQueryRef newJoin(DerivedQueryDefine query, String name) {
		if (query == null) {
			throw new NullArgumentException("查询定义");
		}
		return this.newJoin(name, (DerivedQueryImpl) query);
	}

	final ModifyStatementImpl owner;

	MoRelationRefImpl(ModifyStatementImpl owner, String name, TRelation target) {
		super(name, target);
		this.owner = owner;
	}

	final MoJoinedTableRef newJoin(String name, TableRelationDefineImpl sample) {
		if (sample == null) {
			throw new NullArgumentException("表关系");
		}
		MoJoinedTableRef join = this.newJoin(name, sample.target);
		join.setJoinCondition(sample.condition.clone(sample.owner.selfRef, this, sample, join));
		return join;
	}

	final MoJoinedTableRef newJoin(String name, TableDefineImpl table) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("引用名称");
		}
		if (table == null) {
			throw new NullArgumentException("表定义");
		}
		if (this.owner.refs.containsKey(name)) {
			name = NameUtl.buildIdentityName(name, 0, new Filter<String>() {
				public boolean accept(String item) {
					return MoRelationRefImpl.this.owner.refs.containsKey(item);
				}
			});
		}
		MoJoinedTableRef join = new MoJoinedTableRef(this.owner, name, table);
		this.addJoinNoCheck(join);
		this.owner.refs.put(name, join, true);
		return join;
	}

	final MoJoinedQueryRef newJoin(String name, DerivedQueryImpl query) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("引用名称");
		}
		if (query == null) {
			throw new NullArgumentException("查询定义");
		}
		if (ContextVariableIntl.isStrictExprDomain()) {
			query.checkDomain(this.owner);
		}
		if (this.owner.refs.find(name) != null) {
			name = NameUtl.buildIdentityName(name, 0, new Filter<String>() {
				public boolean accept(String item) {
					return MoRelationRefImpl.this.owner.refs.containsKey(item);
				}
			});
		}
		MoJoinedQueryRef join = new MoJoinedQueryRef(this.owner, name, query);
		this.addJoinNoCheck(join);
		this.owner.refs.put(name, join, true);
		return join;
	}

	final MoJoinedRelationRef newJoinOnly(String name, Relation target) {
		if (target instanceof TableDefineImpl) {
			return new MoJoinedTableRef(this.owner, name, (TableDefineImpl) target);
		} else if (target instanceof DerivedQueryImpl) {
			return new MoJoinedQueryRef(this.owner, name, (DerivedQueryImpl) target);
		}
		throw new UnsupportedOperationException("不支持的关系类型.");
	}

}