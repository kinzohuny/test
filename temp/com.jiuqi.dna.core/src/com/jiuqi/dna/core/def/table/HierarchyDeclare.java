package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * ���ζ���
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public interface HierarchyDeclare extends HierarchyDefine, NamedDeclare {

	/**
	 * ��������
	 */
	public TableDeclare getOwner();

	/**
	 * �������֧�ֵļ���
	 */
	public void setMaxLevel(int maxLevel);
}