package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.internal.da.sql.render.Render;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * ���ֶ����ñ��ʽ
 * 
 * @author houchunlei
 * 
 */
public final class TableFieldRefImpl extends RelationColumnRefImpl implements
		TableFieldRefExpr {

	@Override
	public final TableFieldDefineImpl getColumn() {
		return this.field;
	}

	@Override
	public final TableRef getReference() {
		return this.tableRef;
	}

	@Override
	public final DataTypeInternal getType() {
		return this.field.getType();
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_fieldref;
	}

	@Override
	public final void render(SXElement element) {
		element.setString(xml_attr_tableref, this.tableRef.getName());
		element.setString(xml_attr_field, this.field.name);
	}

	@Override
	public final String toString() {
		return this.getReference().getName().concat(".").concat(this.field.name);
	}

	static final String xml_name_fieldref = "field-ref";
	static final String xml_attr_tableref = "reference";
	static final String xml_attr_field = "field";

	/**
	 * ����������
	 */
	public final TableRef tableRef;
	/**
	 * ���ֶζ���
	 */
	public final TableFieldDefineImpl field;

	public TableFieldRefImpl(TableRef tableRef, TableFieldDefineImpl field) {
		if (tableRef.getTarget() != field.owner) {
			throw new IllegalArgumentException("�ֶζ���[" + field.name + "]�����ڱ����õ�Ŀ���߼���[" + tableRef.getTarget().name + "]");
		}
		this.tableRef = tableRef;
		this.field = field;
	}

	/**
	 * ��xml�����й����ֶ����ö���
	 * 
	 * @param sample
	 * @param owner
	 */
	TableFieldRefImpl(SXElement sample, RelationRefOwner owner) {
		RelationRef relationRef = owner.findRelationRef(sample.getAttribute(xml_attr_tableref));
		if (relationRef == null || !(relationRef instanceof TableRef)) {
			throw new IllegalArgumentException();
		}
		this.tableRef = (TableRef) relationRef;
		this.field = this.tableRef.getTarget().fields.get(sample.getAttribute(xml_attr_field));
	}

	@Override
	protected final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		try {
			// HCL ����Ҫ������߼�,�ر��ǿ�¡�������ж�
			if (this.tableRef == fromSample) {
				// ���õ�Ŀ������ǿ�¡�ĸ���!!
				if (!from.getTarget().getName().equals(fromSample.getTarget().getName())) {
					throw new IllegalArgumentException();
				}
				return ((TableRef) from).expOf(this.field.name);
			} else if (this.tableRef == toSample) {
				// ���õ�Ŀ������ǿ�¡�ĸ���!!
				if (!to.getTarget().getName().equals(toSample.getTarget().getName())) {
					throw new IllegalArgumentException();
				}
				return ((TableRef) to).expOf(this.field.name);
			}
		} catch (ClassCastException e) {
			throw new IllegalArgumentException();
		}
		throw new IllegalArgumentException();
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitTableFieldRef(this, context);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadColumnRef(Render.aliasOf(this.tableRef, this.field.getDBTable()), this.field.namedb());
	}
}