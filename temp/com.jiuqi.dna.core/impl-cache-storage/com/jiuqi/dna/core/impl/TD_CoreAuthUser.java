package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * 用户实体表定义
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthUserEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthUser extends TableDeclarator {

	/**
	 * 用户名称字段定义
	 */
	public final TableFieldDefine f_name;

	/**
	 * 用户标题字段定义
	 */
	public final TableFieldDefine f_title;

	/**
	 * 用户状态字段定义
	 */
	public final TableFieldDefine f_state;

	/**
	 * 用户描述信息字段定义
	 */
	public final TableFieldDefine f_description;
	
	/**
	 * 用户级别信息字段定义
	 */
	public final TableFieldDefine f_level;

	/**
	 * 用户登陆密码字段定义
	 */
	public final TableFieldDefine f_password;

	/**
	 * 用户优先级信息字段定义
	 */
	public final TableFieldDefine f_priorityInfo;

	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthUser";

	public static final String INDEX_COREAUTHUSER_NAME_NAME = "IDX_CoreAuthUser_Name";

	public TD_CoreAuthUser() {
		super(NAME);
		this.f_name = AuthorityDataBaseConstant.TABLE_USER_FIELD_NAME.toTableField(this.table);
		this.f_title = AuthorityDataBaseConstant.TABLE_USER_FIELD_TITLE.toTableField(this.table);
		this.f_state = AuthorityDataBaseConstant.TABLE_USER_FIELD_STATE.toTableField(this.table);
		this.f_description = AuthorityDataBaseConstant.TABLE_USER_FIELD_DESCRIPTION.toTableField(this.table);
		this.f_level = AuthorityDataBaseConstant.TABLE_USER_FIELD_LEVEL.toTableField(this.table);
		this.f_password = AuthorityDataBaseConstant.TABLE_USER_FIELD_PASSWORD.toTableField(this.table);
		this.f_priorityInfo = AuthorityDataBaseConstant.TABLE_USER_FIELD_PRIORITY.toTableField(this.table);
		this.table.newIndex(INDEX_COREAUTHUSER_NAME_NAME, this.f_name);
	}
}