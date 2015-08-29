package com.jiuqi.dna.core.resource;

import com.jiuqi.dna.core.exception.DisposedException;

/**
 * ��Դ���
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 *            ��Դ���
 */
public interface ResourceStub<TFacade> {

	/**
	 * �����Դģʽ
	 */
	public ResourceKind getKind();

	/**
	 * ͬһ�����Դ����һ���
	 */
	public Object getCategory();

	/**
	 * ��������
	 */
	public Class<TFacade> getFacadeClass();

	/**
	 * ��ø���Դ����۶���
	 * 
	 * @return ������Դ��ָ�Ķ��󣬼�ʵ��Ҫʹ�õĶ���
	 */
	public TFacade getFacade() throws DisposedException;

	/**
	 * ���Ի�ø���Դ����۶���
	 * ��Чʱ����null
	 */
	public TFacade tryGetFacade();
}
