package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.info.InfoParameterDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * 信息参数定义实现
 * 
 * @author gaojingxin
 * 
 */
final class InfoParameterDefineImpl extends FieldDefineImpl implements
		InfoParameterDeclare {
	public InfoParameterDefineImpl(InfoDefineImpl owner, String name,
			DataType type) {
		super(name, type);
		if (owner == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
	}

	final InfoDefineImpl owner;

	public final InfoDefineImpl getOwner() {
		return this.owner;
	}

	// ////////////////////////////
	// /// XML
	// ////////////////////////////
	static final String xml_element_name_param = "param";

	@Override
	public final String getXMLTagName() {
		return xml_element_name_param;
	}

}
