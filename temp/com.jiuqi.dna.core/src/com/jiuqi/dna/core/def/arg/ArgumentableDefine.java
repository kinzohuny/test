package com.jiuqi.dna.core.def.arg;

import com.jiuqi.dna.core.def.DefineBase;
import com.jiuqi.dna.core.def.NamedElementContainer;

/**
 * �������ģ���������
 * 
 * @author gaojingxin
 * 
 */
public interface ArgumentableDefine extends DefineBase {

	/**
	 * ��ò�������
	 * 
	 * @return ���ز�������
	 */
	public NamedElementContainer<? extends ArgumentDefine> getArguments();

	/**
	 * ��ȡ�����������
	 * 
	 * @return ���ز���ʵ��������
	 */
	public Class<?> getAOClass();

	/**
	 * ������������
	 * 
	 * @return �����½��Ĳ�������
	 */
	public Object newAO();

	/**
	 * ��ֵ�б�ת���ɶ�Ӧ�Ĳ�������
	 */
	public Object newAO(Object... args);
}