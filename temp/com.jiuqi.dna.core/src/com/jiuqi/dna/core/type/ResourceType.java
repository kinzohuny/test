package com.jiuqi.dna.core.type;

/**
 * ��Դ��������
 * 
 * @author gaojingxin
 * 
 */
@Deprecated
public interface ResourceType<TFacade> extends Type {
	/**
	 * ��Դ���������
	 */
	public Class<TFacade> getFacadeClass();

	/**
	 * ��Դ�����
	 */
	public Object getCategory();
}
