package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.TypeFactory;

/**
 * 元数据合并历史
 * 
 * @author houchunlei
 */
final class CoreMetadataMerge extends TableDeclarator {

	public final TableFieldDefine f_appid;
	public final TableFieldDefine f_timestamp;
	public final TableFieldDefine f_kind;
	public final TableFieldDefine f_name;
	public final TableFieldDefine f_static_def;
	public final TableFieldDefine f_dynamic_def;
	public final TableFieldDefine f_merged;

	static final String NAME = "core_metadata_mh";

	CoreMetadataMerge() {
		super(NAME);
		this.f_appid = this.table.newField("appid", TypeFactory.GUID);
		this.f_timestamp = this.table.newField("timestamp", TypeFactory.DATE);
		this.f_kind = this.table.newField("kind", TypeFactory.NVARCHAR(64));
		this.f_name = this.table.newField("name", TypeFactory.NVARCHAR(64));
		this.f_static_def = this.table.newField("static_def", TypeFactory.TEXT);
		this.f_dynamic_def = this.table.newField("dynamic_def", TypeFactory.TEXT);
		this.f_merged = this.table.newField("merged", TypeFactory.TEXT);
	}
}