package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;

/**
 * ֧��with�����
 * 
 * @author houchunlei
 * 
 */
public interface WithableDefine {

	/**
	 * ��ȡ��ʱ��������б�
	 * 
	 * @return δ�����򷵻�null
	 */
	public ModifiableNamedElementContainer<? extends DerivedQueryDefine> getWiths();
}
