package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * ���ζ���
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public interface HierarchyDefine extends NamedDefine {

	/**
	 * ��������
	 */
	public TableDefine getOwner();

	/**
	 * ������֧�ֵļ������
	 * 
	 * @return ������󼶴�
	 */
	public int getMaxLevel();
}