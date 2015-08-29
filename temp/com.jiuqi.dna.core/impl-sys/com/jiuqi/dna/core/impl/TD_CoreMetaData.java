package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.TypeFactory;

/**
 * 核心框架元数据存储
 * 
 * @author gaojingxin
 * 
 */
final class TD_CoreMetaData extends TableDeclarator {

	public final TableFieldDefine f_kind;

	public final TableFieldDefine f_name;

	public final TableFieldDefine f_space;

	public final TableFieldDefine f_xml;

	public final TableFieldDefine f_md5;

	public static final String NAME = "core_metadata";

	private TD_CoreMetaData() {
		super(NAME);
		this.f_kind = this.table.newPrimaryField("kind", TypeFactory.VARCHAR16);
		this.f_name = this.table.newPrimaryField("name", TypeFactory.VARCHAR64);
		this.f_space = this.table.newField("space", TypeFactory.VARCHAR256);
		this.f_xml = this.table.newField("xml", TypeFactory.TEXT);
		this.f_md5 = this.table.newField("md5", TypeFactory.GUID);
	}
}