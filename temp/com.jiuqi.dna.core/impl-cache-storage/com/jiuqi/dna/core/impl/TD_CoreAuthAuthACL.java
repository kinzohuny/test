package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * ACL实体表定义
 * 
 * <p>
 * 依次按操作者ID、组织机构ID、资源类别ID和资源ID索引。
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthACLEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthAuthACL extends TableDeclarator {

	/**
	 * 操作者ID字段定义
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * 组织机构ID字段定义
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * 资源类别ID字段定义
	 */
	public final TableFieldDefine f_resCategoryID;

	/**
	 * 资源ID字段定义
	 */
	public final TableFieldDefine f_resourceID;

	/**
	 * 授权编码字段定义
	 */
	public final TableFieldDefine f_authorityCode;

	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthAuthACL";

	public TD_CoreAuthAuthACL() {
		super(NAME);
		this.f_actorID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_ACTORID.toTableField(this.table);
		this.f_orgID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_ORGID.toTableField(this.table);
		this.f_resCategoryID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_GROUPID.toTableField(this.table);
		this.f_resourceID = AuthorityDataBaseConstant.TABLE_ACL_FIELD_RESOURCEID.toTableField(this.table);
		this.f_authorityCode = AuthorityDataBaseConstant.TABLE_ACL_FIELD_CODE.toTableField(this.table);
		// this.table.newIndex("IDX_CoreAuthAuthACL_ActorID", this.f_actorID);
		this.table.newIndex("IDX_CoreAuthAuthACL_ActorIDOrgID", this.f_actorID, this.f_orgID);
		this.table.newIndex("IDX_CoreAuthAuthACL_OrgID", this.f_orgID);
		this.table.newIndex("IDX_CoreAuthAuthACL_ResCatID", this.f_resCategoryID);
		this.table.newIndex("IDX_CoreAuthAuthACL_ResourceID", this.f_resourceID);
	}
}