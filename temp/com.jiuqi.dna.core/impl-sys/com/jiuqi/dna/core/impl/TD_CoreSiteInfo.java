package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.table.EntityTableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

public class TD_CoreSiteInfo extends EntityTableDeclarator<CoreSiteInfo> {

	/**
	 * ԭ����XML�ı����Ѿ�����������Ϊ��
	 */
	public final TableFieldDefine f_xml;

	public static final String NAME = "core_siteinfo";

	public TD_CoreSiteInfo() {
		super(NAME);
		final ModifiableNamedElementContainer<? extends TableFieldDeclare> fields = this.table.getFields();
		this.f_xml = fields.get("xml");
	}

}
