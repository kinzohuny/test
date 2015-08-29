package com.jiuqi.dna.core.resource;

import com.jiuqi.dna.core.auth.Operation;

/**
 * ��Դ�����������ӿڡ�
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 *            ��Դ��ۣ�����Դʵ���ṩ��ֻ���ӿ�
 * @param <TImpl>
 *            ��Դʵ�����ͣ��ȿ��������޸���Դ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 * @param <TKeysHolder>
 *            ��Դ��Դ���ȿ��Դ��еõ���Դ�ļ���ֵ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 */
public interface ResourceReferencePutter<TFacade, TImpl extends TFacade, TKeysHolder> {
	/**
	 * �����Դ���
	 */
	public Object getCategory();

	/**
	 * ������Դ�����е���Դ����֮������ù�ϵ��
	 * <p>
	 * ������������Դ����<code>reference</code>�ŵ�ָ����Դ����<code>holder</code>�С�
	 * <p>
	 * �����������Ӱ��<code>reference</code>���������ã�Ҳ����˵�����<code>reference</code>
	 * �Ѿ�����������holder�У�����Ҳ���Ὣ<code>reference</code>����Щholder���Ƴ���
	 * 
	 * @param <THolderFacade>
	 *            ����<code>reference</code>���õ���Դ���������
	 * @param holder
	 *            ���õı�����
	 * @param reference
	 *            ���ö���
	 */
	<THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<THolderFacade> void putResourceReferenceBy(ResourceToken<TFacade> holder,
			ResourceToken<THolderFacade> reference);

	/**
	 * �Ƴ���Դ�����е���Դ����֮������ù�ϵ��
	 * <p>
	 * �������ֻ�����Դ����֮������ù�ϵ����������Դ������ɾ����Դ����
	 * 
	 * @param <THolderFacade>
	 *            ����<code>reference</code>���õ���Դ���������
	 * @param holder
	 *            ���õı�����
	 * @param reference
	 *            ���ö���
	 */
	<THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);

	/**
	 * �Ƴ���Դ�����е���Դ����֮������ù�ϵ��
	 * <p>
	 * �������ֻ�����Դ����֮������ù�ϵ����������Դ������ɾ����Դ����
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param <THolderFacade>
	 *            ����<code>reference</code>���õ���Դ���������
	 * @param holder
	 *            ���õı�����
	 * @param reference
	 *            ���ö���
	 */
	<THolderFacade> void removeResourceReference(
			Operation<? super TFacade> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);
}
