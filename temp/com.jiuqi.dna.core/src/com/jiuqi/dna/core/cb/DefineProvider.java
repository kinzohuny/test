package com.jiuqi.dna.core.cb;

import com.jiuqi.dna.core.def.MetaElementType;

/**
 * Ԫ�����ṩ��
 * 
 * <p>
 * �ص��ӿ�
 * 
 * @author houchunlei
 * 
 */
public interface DefineProvider {

	/**
	 * �������Ԫ���ݶ��嵽������
	 * 
	 * @param demander
	 * @param type
	 * @param name
	 */
	public void demand(DefineHolder demander, MetaElementType type, String name);
}
