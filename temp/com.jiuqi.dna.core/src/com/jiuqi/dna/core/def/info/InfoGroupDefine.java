package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.def.NamedElementContainer;

/**
 * ��Ϣ�鶨��
 * 
 * @author gaojingxin
 * 
 */
public interface InfoGroupDefine extends MetaElement {
	/**
	 * �õ���������
	 * 
	 * @return ���ز�������
	 */
	public NamedElementContainer<? extends InfoDefine> getInfos();
}
