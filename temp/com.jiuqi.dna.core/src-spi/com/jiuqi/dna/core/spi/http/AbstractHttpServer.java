/**
 * 
 */
package com.jiuqi.dna.core.spi.http;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.application.Application;

/**
 * @author renbaogang
 *
 */
public abstract class AbstractHttpServer /*implements ServletContainer*/ {

	private Application application;
	
	public AbstractHttpServer() {
		//
	}
	
	public void setApplication(Application application) {
		this.application = application;
	}
	
	public Application getApplication() {
		return application;
	}
	
	public abstract void configure(SXElement httpConfig);
	public abstract boolean tryStart();
	public abstract int getHttpPort();
	public abstract int getSslPort();
	public abstract void loadServletOrFilter(ClassLoader classLoader, SXElement servletOrFilterE)throws Throwable;
	public abstract void dispose();
}
