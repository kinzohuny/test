package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * ��ɫ����ʵ�����
 * 
 * <p>
 * ���ΰ�������ID�����ȼ�������
 * 
 * @see com.jiuqi.dna.core.impl.CoreAuthRAEntity
 * @author LiuZhi 2009-12
 */
final class TD_CoreAuthRA extends TableDeclarator {

	/**
	 * ������ID�ֶζ���
	 */
	public final TableFieldDefine f_actorID;

	/**
	 * ���ID�ֶζ���
	 */
	public final TableFieldDefine f_orgID;

	/**
	 * ��ɫID�ֶζ���
	 */
	public final TableFieldDefine f_roleID;

	/**
	 * ���ȼ��ֶζ���
	 */
	public final TableFieldDefine f_priority;

	/**
	 * ��������
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