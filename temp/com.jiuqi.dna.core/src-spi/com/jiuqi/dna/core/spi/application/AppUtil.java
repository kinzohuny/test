package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.impl.ApplicationImpl;
import com.jiuqi.dna.core.spi.application.HttpConfig.HttpListenConfig;

/**
 * 应用工具类
 * 
 * @author gaojingxin
 * 
 */
public final class AppUtil {

	public static Application tryGetDefaultApp() {
		return ApplicationImpl.tryGetDefaultApp();
	}
	public static Application getDefaultApp() {
		return ApplicationImpl.getDefaultApp();
	}
	public static boolean startAppServerWithConf(HttpConfig config) {
		ApplicationImpl app = ApplicationImpl.tryGetDefaultApp();
		if (app == null) {
			return false;
		}
		return app.tryStartServer(config.getSXElement());
	}
	public static boolean startAppServerWithConf(HttpListenConfig[] listens) {
		
		HttpConfig config = new HttpConfig();
		config.addListenAll(listens);
		
		return startAppServerWithConf(config);
	}

	@Deprecated
	public static void initTypes() {
	}

	private AppUtil() {
	}
}
