package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDefine;

/**
 * ��ϵ�ж���
 * 
 * @author houchunlei
 * 
 */
public interface RelationColumnDefine extends NamedDefine {

	/**
	 * ��ȡ�����Ĺ�ϵ����
	 * 
	 * @return ��ϵ����
	 */
	public RelationDefine getOwner();
}
