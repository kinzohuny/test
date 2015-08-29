package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * ��ɫʵ�����
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthRoleEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthRole extends TableDeclarator {

	/**
	 * ��ɫ�����ֶζ���
	 */
	public final TableFieldDefine f_name;

	/**
	 * ��ɫ�����ֶζ���
	 */
	public final TableFieldDefine f_title;

	/**
	 * ��ɫ״̬�ֶζ���
	 */
	public final TableFieldDefine f_state;

	/**
	 * ��ɫ������Ϣ�ζ���
	 */
	public final TableFieldDefine f_description;

	/**
	 * ��������
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