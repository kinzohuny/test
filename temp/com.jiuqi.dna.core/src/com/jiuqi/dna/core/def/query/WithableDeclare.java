package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;

/**
 * ֧��with�����
 * 
 * @author houchunlei
 * 
 */
public interface WithableDeclare extends WithableDefine {

	public ModifiableNamedElementContainer<? extends DerivedQueryDeclare> getWiths();

	/**
	 * ʹ��with�Ӿ� ��������ʱ�����
	 * 
	 * @param name
	 *            ��ʱ����������ƣ�������������ʱ����������ظ���
	 * @return
	 */
	public DerivedQueryDeclare newWith(String name);
}
