package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * ����������֯����ӳ���ϵʵ���
 * 
 * <p>
 * ��������ID������
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthUOMEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthUOM extends TableDeclarator {

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * ��������
	 */
	public static final String NAME = "Core_AuthUOM";

	public TD_CoreAuthUOM() {
		super(NAME);
		this.f_actorID = AuthorityDataBaseConstant.TABLE_UOM_FIELD_ACTORID.toTableField(this.table);
		this.f_orgID = AuthorityDataBaseConstant.TABLE_UOM_FIELD_ORGID.toTableField(this.table);
		this.table.newIndex("IDX_CoreAuthUOM_ActorID", this.f_actorID);
	}
}