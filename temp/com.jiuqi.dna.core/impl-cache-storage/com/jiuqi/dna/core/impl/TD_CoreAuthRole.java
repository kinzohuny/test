package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * 角色实体表定义
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthRoleEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthRole extends TableDeclarator {

	/**
	 * 角色名称字段定义
	 */
	public final TableFieldDefine f_name;

	/**
	 * 角色标题字段定义
	 */
	public final TableFieldDefine f_title;

	/**
	 * 角色状态字段定义
	 */
	public final TableFieldDefine f_state;

	/**
	 * 角色描述信息段定义
	 */
	public final TableFieldDefine f_description;

	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthRole";

	public TD_CoreAuthRole() {
		super(NAME);
		this.f_name = AuthorityDataBaseConstant.TABLE_ROLE_FIELD_NAME.toTableField(this.table);
		this.f_title = AuthorityDataBaseConstant.TABLE_ROLE_FIELD_TITLE.toTableField(this.table);
		this.f_state = AuthorityDataBaseConstant.TABLE_ROLE_FIELD_STATE.toTableField(this.table);
		this.f_description = AuthorityDataBaseConstant.TABLE_ROLE_FIELD_DESCRIPTION.toTableField(this.table);
		this.table.newIndex("IDX_CoreAuthRole_Name", this.f_name);
	}
}