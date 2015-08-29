package com.jiuqi.dna.core.impl;

/**
 * ����IO������
 * 
 * @author gaojingxin
 * 
 * @param <TPackageStub>���Ĵ��
 */
public interface DataPackageReceiver {

	/**
	 * ���ݽ���������
	 * 
	 * @author gaojingxin
	 * 
	 */
	public interface NetPackageReceivingStarter {
		/**
		 * ��������package�������package����ȡ������
		 * 
		 * @param <TAttachment>
		 *            ���ݽ��մ������ĸ�������
		 * @param handler
		 *            ���ݽ��մ�����
		 * @param attachment
		 *            ���ݽ��մ������ĸ���
		 * @return �����첽���ƾ��
		 */
		public <TAttachment> AsyncIOStub<TAttachment> startReceivingPackage(
				DataFragmentResolver<? super TAttachment> handler,
				TAttachment attachment);
	}

	/**
	 * 
	 * �����ܵ������ݺ�����buffer
	 * 
	 * @param channel
	 *            ���Ӷ���
	 * @param starter
	 *            ���ݽ���������
	 * @throws Throwable
	 *             ���׳��쳣
	 */
	public void packageArriving(NetChannelImpl channel,
			DataInputFragment fragment, NetPackageReceivingStarter starter)
			throws Throwable;

	public void channelDisabled(NetChannelImpl channel);
}
