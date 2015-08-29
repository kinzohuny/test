package com.jiuqi.dna.core.resource;

/**
 * ��Դ������
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 *            ��Դ���
 * @param <TImpl>
 *            ��Դ�޸���
 * @param <TKeysHolder>
 *            ��Դ��Դ
 * @param <TResourceMetaData>
 *            ��Դԭ����
 */
public interface ResourceInserter<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourcePutter<TFacade, TImpl, TKeysHolder> {

	/**
	 * ���������Դ�Ŀ���
	 * 
	 * @param <TOwnerFacade>������Դ�ľ��
	 * @param ownerFacadeClass
	 *            ������Դ��ȡ�ӿ���
	 * @return ���������Դ�Ŀ���
	 */
	@Deprecated
	public <TOwnerFacade> ResourceToken<TOwnerFacade> getOwnerResource(
			Class<TOwnerFacade> ownerFacadeClass);

}