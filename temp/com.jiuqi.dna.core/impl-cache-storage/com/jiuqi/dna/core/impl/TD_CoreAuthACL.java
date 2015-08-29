package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * ACLʵ�����
 * 
 * <p>
 * ���ΰ�������ID����֯����ID����Դ���ID����ԴID������
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthACLEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthACL extends TableDeclarator {

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * ��֯����ID�ֶζ���
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * ��Դ���ID�ֶζ���
	 */
	public final TableFieldDefine f_resCategoryID;

	/**
	 * ��ԴID�ֶζ���
	 */
	public final TableFieldDefine f_resourceID;

	/**
	 * ��Ȩ�����ֶζ���
	 */
	public final TableFieldDefine f_authorityCode;

	/**
	 * ��������
	 */
	public static final String NAME = "Core_AuthACL";

	public TD_CoreAuthACL() {
		super(NAME);
		this.f_actorID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_ACTORID.toTableField(this.table);
		this.f_orgID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_ORGID.toTableField(this.table);
		this.f_resCategoryID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_GROUPID.toTableField(this.table);
		this.f_resourceID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_RESOURCEID.toTableField(this.table);
		this.f_authorityCode = AuthorityDataBaseConstant.TABLE_ACL_FIELD_CODE.toTableField(this.table);
		this.table.newIndex("IDX_CoreAuthACL_ActorID", this.f_actorID);
		this.table.newIndex("IDX_CoreAuthACL_OrgID", this.f_orgID);
		this.table.newIndex("IDX_CoreAuthACL_ResCategoryID", this.f_resCategoryID);
		this.table.newIndex("IDX_CoreAuthACL_ResourceID", this.f_resourceID);
	}
}