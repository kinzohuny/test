package com.jiuqi.dna.core.resource;

/**
 * ��Դ��������
 * 
 * @author gaojingxin
 * 
 */
public interface ResourceTokenLink<TFacade> {

	/**
	 * �ڵ��ϵ���Դ��ʶ
	 */
	public ResourceToken<TFacade> getToken();

	/**
	 * ��һ�����ڣ�����null��
	 */
	public ResourceTokenLink<TFacade> next();

}
