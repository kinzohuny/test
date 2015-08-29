package com.jiuqi.dna.core.resource;

/**
 * ��Դ���<br>
 * ���԰�װ��Դ�����Լ�����Ϣ
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 *            ��Դ�Ķ�ȡ���棨�ӿڻ���󣩸����͵����з���Ӧ��ֻ��������Դ��ֻ�����ʷ���
 */
public interface ResourceHandle<TFacade> extends ResourceStub<TFacade> {

	/**
	 * �������Դ��ѯ��
	 */
	public ResourceQuerier getOwnedResourceQuerier();

	/**
	 * �����Դ��ʶ
	 */
	public ResourceToken<TFacade> getToken();

	/**
	 * �رվ�����ͷ���
	 */
	public void closeHandle();

}
