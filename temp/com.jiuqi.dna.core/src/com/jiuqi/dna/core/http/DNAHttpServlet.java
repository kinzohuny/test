package com.jiuqi.dna.core.http;

import javax.servlet.http.HttpServlet;

import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.spi.application.Application;

@SuppressWarnings("serial")
public abstract class DNAHttpServlet extends HttpServlet {
	private Application app;

	/**
	 * 获得DNA应用
	 * 
	 * @return
	 */
	public Application getApplication() {
		Application app = this.app;
		if (app == null) {
			this.app = app = (Application) this.getServletContext()
					.getAttribute(Application.servlet_context_attr_application);
			if (app == null) {
				throw new MissingObjectException("未找到DNA-Application");
			}
		}
		return app;
	}
}
