/**
 * 
 */
package com.jiuqi.dna.core.impl;

import org.osgi.framework.BundleContext;

import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.spi.application.IApplicationLauncher;
import com.jiuqi.dna.core.spi.http.AbstractHttpServer;

/**
 * @author linfangchao
 *
 */
public class ApplicationLauncherImpl implements IApplicationLauncher {
	
	private BundleContext context;
	
	public ApplicationLauncherImpl(BundleContext context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see com.jiuqi.dna.core.spi.application.IApplicationLauncher#launch(com.jiuqi.dna.core.spi.http.AbstractHttpServer)
	 */
	public ApplicationImpl launch(AbstractHttpServer server) {
		return ApplicationImpl.startApp(context, server);
	}

	public boolean shutdown(Application application) {
//		((ApplicationImpl)application).doDispose();
		ApplicationImpl.stopApp();
		return true;
	}

}
