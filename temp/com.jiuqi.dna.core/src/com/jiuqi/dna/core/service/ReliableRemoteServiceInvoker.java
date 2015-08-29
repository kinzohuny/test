package com.jiuqi.dna.core.service;

import java.net.Proxy;
import java.net.URL;

public interface ReliableRemoteServiceInvoker extends SyncServiceInvoker {

	public URL getURL();

	public Proxy getProxy();

	public String getUserName();

}
