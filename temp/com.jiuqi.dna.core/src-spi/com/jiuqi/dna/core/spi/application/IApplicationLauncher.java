/**
 * 
 */
package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.spi.http.AbstractHttpServer;

/**
 * @author linfangchao
 *
 */
public interface IApplicationLauncher {
	Application launch(AbstractHttpServer server);

	boolean shutdown(Application application);
}
