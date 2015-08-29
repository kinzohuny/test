package com.jiuqi.dna.core.internal.db.datasource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.jiuqi.dna.core.impl.ResolveHelper;

enum WebContainer {

	Tomcat() {

		@Override
		DataSource lookup(ComboDataSourceConf config) throws NamingException {
			Context initCtx = new InitialContext();
			try {
				return (DataSource) initCtx.lookup(config.location);
			} catch (NamingException e) {
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				return (DataSource) envCtx.lookup(config.location);
			}
		}
	},

	WebSphere {

		@Override
		DataSource lookup(ComboDataSourceConf config) throws NamingException {
			Context initCtx = new InitialContext();
			try {
				return (DataSource) initCtx.lookup(config.location);
			} catch (NamingException e) {
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				return (DataSource) envCtx.lookup(config.location);
			}
		}
	};

	static final DataSource lookupEach(ComboDataSourceConf config,
			ClassLoader context) {
		final Thread thread = Thread.currentThread();
		final ClassLoader save = thread.getContextClassLoader();
		thread.setContextClassLoader(context);
		try {
			try {
				Context initCtx = new InitialContext();
				try {
					return (DataSource) initCtx.lookup(config.location);
				} catch (NamingException e1) {
					try {
						return (DataSource) initCtx.lookup("jndi/" + config.location);
					} catch (NamingException e2) {
						try {
							return (DataSource) initCtx.lookup("jdbc/" + config.location);
						} catch (NamingException e3) {
							Context envCtx = (Context) initCtx.lookup("java:comp/env");
							try {
								return (DataSource) envCtx.lookup(config.location);
							} catch (NamingException e4) {
								envCtx = (Context) initCtx.lookup("java:comp/env/jdbc");
								return (DataSource) envCtx.lookup(config.location);
							}
						}
					}
				}
			} catch (Throwable e) {
				ResolveHelper.logStartInfo("无法获取Web容器管理的JNDI数据源。"+config.location);
			}
			// boolean catchedException = false;
			// for (WebContainer wc : WebContainer.values()) {
			// try {
			// DataSource ds = wc.lookup(config);
			// if (ds != null) {
			// ResolveHelper.logStartInfo("成功获取[" + wc.name()
			// + "]管理的JNDI数据源[" + config.location + "]。");
			// return ds;
			// }
			// } catch (Throwable e) {
			// catchedException = true;
			// continue;
			// }
			// }
			// if (catchedException) {
			// ResolveHelper.logStartInfo("无法获取Web容器管理的JNDI数据源。");
			// }
		} finally {
			thread.setContextClassLoader(save);
		}
		return null;
	}

	abstract DataSource lookup(ComboDataSourceConf config)
			throws NamingException;
}