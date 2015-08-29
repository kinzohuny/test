package com.jiuqi.dna.core.resource;

/**
 * ��Դ��ʶ
 * 
 * 
 * @param <TFacade>
 */
public interface ResourceToken<TFacade> extends ResourceStub<TFacade> {

	/**
	 * �ձ�ʶ���޷���λ��Դֵ��
	 */
	@SuppressWarnings("unchecked")
	public static final ResourceToken MISSING = ResourceService.MISSTOKEN;

	/**
	 * ��ñ�����Դ�ĸ��ڵ�(��Դ���ϼ�)
	 * 
	 * @return ���ظ���ʶ����null
	 */
	public ResourceToken<TFacade> getParent();

	/**
	 * ���ر�����Դ��ֱ���¼�����(��Դ���¼�)
	 * 
	 * @return ���ص�һ�������ڵ㣬����null��ʾû�к���
	 */
	public ResourceTokenLink<TFacade> getChildren();

	/**
	 * ������õ�ǰ��Դ����Դ
	 * 
	 * @param <TSuperFacade>
	 *            ������Դ�������
	 * @param superTokenFacadeClass
	 *            ������Դ�����
	 * @return �������õ�ǰ��Դ����Դ��null��ʾ��ǰ��Դû�б�����
	 * @throws IllegalArgumentException
	 *             ��������������Դ���ö������׳��쳣
	 */
	public <TSuperFacade> ResourceToken<TSuperFacade> getSuperToken(
			Class<TSuperFacade> superTokenFacadeClass)
			throws IllegalArgumentException;

	/**
	 * ��õ�ǰ��Դ���õ�ĳ����Դ������
	 * 
	 * @param <TSubFacade>
	 *            ��������Դ�����
	 * @param subTokenFacadeClass
	 *            ��������Դ�������
	 * @return ���ص�һ�������ڵ㣬����null��ʾû�е�ǰ��Դû�������κ���Դ
	 * @throws IllegalArgumentException
	 *             ��������������Դ���ö������׳��쳣
	 */
	public <TSubFacade> ResourceTokenLink<TSubFacade> getSubTokens(
			Class<TSubFacade> subTokenFacadeClass)
			throws IllegalArgumentException;

}
