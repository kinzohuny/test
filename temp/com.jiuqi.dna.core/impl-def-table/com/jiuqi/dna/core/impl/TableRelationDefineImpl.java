package com.jiuqi.dna.core.impl;

import java.util.Arrays;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.exp.ConditionalExpression;
import com.jiuqi.dna.core.def.table.TableRelationDeclare;
import com.jiuqi.dna.core.def.table.TableRelationType;

/**
 * 表关系定义实现类
 * 
 * @author houchunlei
 * 
 */
class TableRelationDefineImpl extends StandaloneTableRef implements
		TableRelationDeclare {

	public final TableDefineImpl getOwner() {
		return this.owner;
	}

	public final ConditionalExpr getJoinCondition() {
		return this.condition;
	}

	public final void setJoinCondition(ConditionalExpression condition) {
		if (condition == null) {
			throw new NullPointerException();
		}
		this.resetEquiRelation();
		this.condition = (ConditionalExpr) condition;
	}

	public final TableRelationType getRelationType() {
		return this.type;
	}

	public final void setRelationType(TableRelationType type) {
		if (type == null) {
			throw new NullPointerException();
		}
		this.type = type;
	}

	public boolean isEquiRelation() {
		this.ensureCheckEquiRelation();
		return this.isEquiRelation;
	}

	public TableFieldDefineImpl getEquiRelationSelfField() {
		this.ensureCheckEquiRelation();
		if (this.isEquiRelation) {
			return this.selfs[0];
		}
		throw new NotEquiRelationException(this);
	}

	public TableFieldDefineImpl getEquiRelationTargetField() {
		this.ensureCheckEquiRelation();
		if (this.isEquiRelation) {
			return this.targets[0];
		}
		throw new NotEquiRelationException(this);
	}

	public TableEquiRelationDefineImpl castAsEquiRelation() {
		this.ensureCheckEquiRelation();
		if (this.isEquiRelation) {
			return this.equiRelation;
		}
		throw new NotEquiRelationException(this);
	}

	/**
	 * 所属逻辑表定义
	 */
	final TableDefineImpl owner;
	/**
	 * 关系类型
	 */
	TableRelationType type;
	/**
	 * 关系条件
	 */
	ConditionalExpr condition;

	TableRelationDefineImpl(TableDefineImpl owner, String name,
			TableDefineImpl target) {
		super(name, target);
		if (owner == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
		this.type = TableRelationType.REFERENCE;
	}

	/**
	 * 使用clone方法
	 */
	TableRelationDefineImpl(TableDefineImpl owner,
			TableRelationDefineImpl sample, ObjectQuerier querier) {
		super(sample, querier);
		this.owner = owner;
		this.type = sample.type;
		// HCL 有问题，不能支持复杂的表关系。
		this.condition = sample.condition.clone(sample, this, sample.owner.selfRef, this.owner.selfRef);
	}

	private volatile boolean hasCheckedEquiRelation;

	private final void ensureCheckEquiRelation() {
		if (!this.hasCheckedEquiRelation) {
			synchronized (this) {
				if (!this.hasCheckedEquiRelation) {
					if (this.condition.isEqualsPredicate()) {
						PredicateExpr expr = (PredicateExpr) this.condition;
						try {
							TableFieldRefImpl tfr0 = (TableFieldRefImpl) expr.values[0];
							TableFieldRefImpl tfr1 = (TableFieldRefImpl) expr.values[1];
							if (tfr0.tableRef == this && tfr1.tableRef == this.owner.selfRef) {
								this.selfs[0] = tfr1.field;
								this.targets[0] = tfr0.field;
								this.equiRelation = new TableEquiRelationDefineImpl(this.owner, this.name, tfr1.field, this.target, tfr0.field);
								this.isEquiRelation = true;
							} else if (tfr0.tableRef == this.owner.selfRef && tfr1.tableRef == this) {
								this.selfs[0] = tfr0.field;
								this.targets[0] = tfr1.field;
								this.equiRelation = new TableEquiRelationDefineImpl(this.owner, this.name, tfr0.field, this.target, tfr1.field);
								this.isEquiRelation = true;
							} else {
								this.isEquiRelation = false;
							}
						} catch (ClassCastException e) {
							this.isEquiRelation = false;
						}
					}
					this.hasCheckedEquiRelation = true;
				}
			}
		}
	}

	private final synchronized void resetEquiRelation() {
		this.hasCheckedEquiRelation = false;
		this.isEquiRelation = false;
		this.equiRelation = null;
		Arrays.fill(this.selfs, null);
		Arrays.fill(this.targets, null);
	}

	private volatile boolean isEquiRelation;

	private TableFieldDefineImpl[] selfs = new TableFieldDefineImpl[3];
	private TableFieldDefineImpl[] targets = new TableFieldDefineImpl[3];

	private TableEquiRelationDefineImpl equiRelation;

	@Override
	public String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "relation";

	@Override
	final void assignFrom(Object sample) {
		super.assignFrom(sample);
		TableRelationDefineImpl relation = (TableRelationDefineImpl) sample;
		this.type = relation.type;
		this.condition = relation.condition.clone(relation, this, relation.owner.selfRef, this.owner.selfRef);
	}

	TableRelationDefineImpl clone(TableDefineImpl owner, ObjectQuerier querier) {
		return new TableRelationDefineImpl(owner, this, querier);
	}

}
