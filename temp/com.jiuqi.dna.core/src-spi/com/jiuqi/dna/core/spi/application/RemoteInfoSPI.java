package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.RemoteInfo;

public interface RemoteInfoSPI extends RemoteInfo {
	/**
	 * �ͻ��˵�������
	 */
	public void setRemoteHost(String host);

	/**
	 * �ͻ��˵ĵ�ַ
	 */
	public void setRemoteAddr(String addr);
	
	/**
	 * �ͻ��˵�Mac��ַ
	 */
	public void setRemoteMac(String mac);
	
}
