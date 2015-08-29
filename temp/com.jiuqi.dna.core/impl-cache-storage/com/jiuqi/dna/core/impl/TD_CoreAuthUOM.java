package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * 访问者与组织机构映射关系实体表
 * 
 * <p>
 * 按访问者ID索引。
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthUOMEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthUOM extends TableDeclarator {

	/**
	 * 访问者ID字段定义
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * 访问者ID字段定义
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * 表名定义
	 */
	public static final String NAME = "Core_AuthUOM";

	public TD_CoreAuthUOM() {
		super(NAME);
		this.f_actorID = AuthorityDataBaseConstant.TABLE_UOM_FIELD_ACTORID.toTableField(this.table);
		this.f_orgID = AuthorityDataBaseConstant.TABLE_UOM_FIELD_ORGID.toTableField(this.table);
		this.table.newIndex("IDX_CoreAuthUOM_ActorID", this.f_actorID);
	}
}