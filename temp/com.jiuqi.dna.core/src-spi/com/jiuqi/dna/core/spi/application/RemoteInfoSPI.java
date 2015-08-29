package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.RemoteInfo;

public interface RemoteInfoSPI extends RemoteInfo {
	/**
	 * 客户端的主机名
	 */
	public void setRemoteHost(String host);

	/**
	 * 客户端的地址
	 */
	public void setRemoteAddr(String addr);
	
	/**
	 * 客户端的Mac地址
	 */
	public void setRemoteMac(String mac);
	
}
