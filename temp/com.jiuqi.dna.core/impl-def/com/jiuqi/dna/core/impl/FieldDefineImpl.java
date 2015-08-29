package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.FieldDeclare;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.ReadableValue;

/**
 * �ֶζ���ӿ�ʵ��
 * 
 * @author gaojingxin
 * 
 */
abstract class FieldDefineImpl extends NamedDefineImpl implements FieldDeclare {

	final DataType type;

	ValueExpr defaultValue;

	boolean isKeepValid;

	boolean isReadonly;

	/**
	 * ת������Ӧ�����ͣ�Ϊ������ʹ��Ĭ��ֵ
	 */
	final Object convertWithDefault(Object value) {
		if (value == null) {
			if (this.defaultValue instanceof ReadableValue) {
				value = ((ReadableValue) this.defaultValue).getObject();
			} else {
				return null;
			}
		}
		return this.type.convert(value);
	}

	public FieldDefineImpl(String name, DataType type) {
		super(name);
		if (type == null) {
			throw new NullPointerException();
		}
		this.type = type;
	}

	public final ValueExpr getDefault() {
		return this.defaultValue;
	}

	public final void setDefault(ValueExpression value) {
		if (value == null || value == NullExpr.NULL) {
			this.defaultValue = null;
		} else {
			this.defaultValue = (ValueExpr) value;
		}
	}

	public final boolean isKeepValid() {
		return this.isKeepValid;
	}

	public final boolean isReadonly() {
		return this.isReadonly;
	}

	public final DataType getType() {
		return this.type;
	}

	public final void setKeepValid(boolean value) {
		this.isKeepValid = value;

	}

	public final void setReadonly(boolean value) {
		this.isReadonly = value;
	}

	// ------------------------- xml��ʽ�� -------------------------

	static final String xml_attr_type = "type";
	static final String xml_attr_isKeepValid = "keep-valid";
	static final String xml_attr_isReadOnly = "read-only";
	static final String xml_element_default = "default";

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setAsType(xml_attr_type, this.type);
		element.maskTrue(xml_attr_isKeepValid, this.isKeepValid);
		element.maskTrue(xml_attr_isReadOnly, this.isReadonly);
		if (this.defaultValue != null) {
			this.defaultValue.renderInto(element.append(xml_element_default));
		}
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.isKeepValid = element.getBoolean(xml_attr_isKeepValid, this.isKeepValid);
		this.isReadonly = element.getBoolean(xml_attr_isReadOnly, this.isReadonly);
		SXElement defElement = element.firstChild(xml_element_default);
		if (defElement != null) {
			this.defaultValue = ConstExpr.loadConst(defElement.firstChild());
		}
	}
}
