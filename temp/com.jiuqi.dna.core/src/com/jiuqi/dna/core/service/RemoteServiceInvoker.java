package com.jiuqi.dna.core.service;

import java.net.Proxy;
import java.net.URL;

/**
 * Զ�̷���������ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface RemoteServiceInvoker extends ServiceInvoker {

	public URL getURL();

	public Proxy getProxy();

	public String getUserName();

	/**
	 * ����ʹ�ø÷��������������һ��ʹ�ú�Զ�������Ż���������<br>
	 * ʹ�����ǿ�ҽ�����ã���û�бط���finally����<br>
	 * ���ø÷������ᵼ�¶��󲻿��ã�ֻ����ʱ�ͷ����ݿ���Դ
	 */
	public void unuse();
}
