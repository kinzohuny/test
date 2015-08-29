package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.HierarchyOperateExpression;
import com.jiuqi.dna.core.def.exp.HierarchyOperator;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.table.HierarchyDefine;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 级次函数
 * 
 * @author houchunlei
 * 
 */
@SuppressWarnings("deprecation")
public class HierarchyOperateExpr extends ValueExpr implements
		HierarchyOperateExpression {

	public final ValueExpression getLevel() {
		return this.level;
	}

	public final HierarchyOperator getOperator() {
		return this.operator;
	}

	public final QuTableRef getSource() {
		return (QuTableRef) this.tableRef;
	}

	@Override
	public final DataTypeBase getType() {
		return this.operator.getType();
	}

	@Override
	public final String getXMLTagName() {
		return HierarchyOperateExpr.xml_name_hierarchy_operate;
	}

	@Override
	public final void render(SXElement element) {
		element.setAttribute(xml_attr_reference, this.tableRef.getName());
		element.setAttribute(HierarchyOperateExpr.xml_attr_hierarchy, this.hierarchy.name);
		element.setEnum(HierarchyOperateExpr.xml_attr_op_type, this.operator);
	}

	static final String xml_name_hierarchy_operate = "hierarchy-operate";
	static final String xml_attr_reference = "reference";
	static final String xml_attr_hierarchy = "hierarchy";
	static final String xml_attr_op_type = "type";
	static final String xml_attr_level = "level";

	final TableRef tableRef;

	final HierarchyDefineImpl hierarchy;

	final HierarchyOperatorImpl operator;

	final ValueExpr level;

	HierarchyOperateExpr(RelationRef relationRef, HierarchyDefine hierarchy,
			HierarchyOperatorImpl operator, ValueExpr level) {
		this.tableRef = StandaloneTableRef.ensureHierarchyForTableRef(relationRef, hierarchy);
		this.hierarchy = (HierarchyDefineImpl) hierarchy;
		this.operator = operator;
		this.level = level;
	}

	@Override
	protected final ValueExpr clone(RelationRefDomain domain,
			ArgumentableDefine args) {
		RelationRef relationRef = domain.getRelationRefRecursively(this.tableRef.getName());
		try {
			QuTableRef tableRef = (QuTableRef) relationRef;
			return new HierarchyOperateExpr(tableRef, tableRef.getTarget().hierarchies.get(this.hierarchy.name), this.operator, this.level == null ? null : this.level.clone(domain, args));
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("关系引用类型错误");
		}
	}

	@Override
	protected final HierarchyOperateExpr clone(RelationRef fromSample,
			RelationRef from, RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitHierarchyOperateExpr(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		// HCL Auto-generated method stub
		throw Utils.notImplemented();
	}
}