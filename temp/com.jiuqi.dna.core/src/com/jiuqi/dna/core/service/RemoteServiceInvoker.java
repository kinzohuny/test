package com.jiuqi.dna.core.service;

import java.net.Proxy;
import java.net.URL;

/**
 * 远程服务调用器接口
 * 
 * @author gaojingxin
 * 
 */
public interface RemoteServiceInvoker extends ServiceInvoker {

	public URL getURL();

	public Proxy getProxy();

	public String getUserName();

	/**
	 * 不再使用该访问器，或距离下一次使用很远，用于优化网络连接<br>
	 * 使用完后强烈建议调用，但没有必放在finally块中<br>
	 * 调用该方法不会导致对象不可用，只是暂时释放数据库资源
	 */
	public void unuse();
}
