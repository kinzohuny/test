package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * 角色分配实体表定义
 * 
 * <p>
 * 依次按操作者ID和优先级索引。
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthRAEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthRA extends TableDeclarator {

	/**
	 * 操作者ID字段定义
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * 身份ID字段定义
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * 角色ID字段定义
	 */
	public final TableFieldDefine f_roleID;

	/**
	 * 优先级字段定义
	 */
	public final TableFieldDefine f_priority;

	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthRA";

	public TD_CoreAuthRA() {
		super(NAME);
		this.f_actorID = AuthorityDataBaseConstant.TABLE_RA_FIELD_ACTORID.toTableField(this.table);
		this.f_orgID = AuthorityDataBaseConstant.TABLE_RA_FIELD_ORGID.toTableField(this.table);
		this.f_roleID = AuthorityDataBaseConstant.TABLE_RA_FIELD_ROLEID.toTableField(this.table);
		this.f_priority = AuthorityDataBaseConstant.TABLE_RA_FIELD_PRIORITY.toTableField(this.table);
		this.table.newIndex("IDX_CoreAuthRA_ActorID", this.f_actorID);
		this.table.newIndex("IDX_CoreAuthRA_Priority", this.f_priority);
	}
}