package com.jiuqi.dna.core.impl;

/**
 * ��Ҫ׼���Ľӿ�
 * 
 * @author gaojingxin
 * 
 */
interface Prepareble {
	/**
	 * ������ݿ�û��׼���þͲ�׼��
	 */
	public boolean ignorePrepareIfDBInvalid();

	/**
	 * ����Ƿ��Ѿ�׼������
	 */
	public boolean isPrepared();

	/**
	 * ȷ��׼��
	 * 
	 * @param context
	 *            ������
	 * @param rePrepared
	 *            �Ƿ�����׼��
	 */
	public void ensurePrepared(ContextImpl<?, ?, ?> context, boolean rePrepared);
}