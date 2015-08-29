package com.jiuqi.dna.core.impl;

import java.net.URL;
import java.util.ArrayList;

import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.GUID;

final class DistributedEnvironment {

	final Type type;

	/**
	 * 当前节点是否参数节点
	 */
	final boolean param;

	enum Type {

		SSO, APP
	}

	DistributedEnvironment(SXElement element) {
		final String type = element.getString("type");
		if (type.equals("sso")) {
			this.type = Type.SSO;
		} else if (type.equals("app")) {
			this.type = Type.APP;
		} else {
			throw new RuntimeException();
		}
		this.param = element.getBoolean("param", false);
		for (SXElement e = element.firstChild(); e != null; e = e.nextSibling()) {
			if (e.name.equals("sso")) {
				this.ssos.add(readNode(e));
			} else if (e.name.equals("node")) {
				Node n = readNode(e);
				App app = new App();
				app.filter = readFilter(e);
				app.nodes.add(n);
				this.apps.add(app);
			} else {
				App app = new App();
				app.filter = readFilter(e);
				for (SXElement x = e.firstChild("node"); x != null; x = x.nextSibling("node")) {
					app.nodes.add(readNode(x));
				}
				if (app.nodes.size() == 0) {
					throw new IllegalArgumentException("初始化分布式环境错误：集群服务没有配置任何节点。");
				}
				this.apps.add(app);
			}
		}
	}

	static final Node readNode(SXElement e) {
		final String host = e.getString("host");
		final int port = e.getInt("port");
		final GUID id = e.getGUID("id");
		final boolean self = e.getBoolean("self", false);
		return new Node(host, port, id, self);
	}

	static final CacheFilter readFilter(SXElement e) {
		SXElement fe = e.firstChild("dispatch_rule");
		if (fe == null) {
			return null;
		}
		return new CacheFilter(fe.getString("template"), fe.getCDATA());
	}

	final ArrayList<Node> ssos = new ArrayList<Node>();
	final ArrayList<App> apps = new ArrayList<App>();

	static final class Node {

		final String host;
		final int port;
		final GUID id;
		final boolean self;

		private Node(String host, int port, GUID id, boolean self) {
			if (host == null || host.length() == 0) {
				throw new IllegalArgumentException("初始化分布式环境错误：非法的主机地址。");
			}
			this.host = host;
			if (port <= 0) {
				throw new IllegalArgumentException("初始化分布式环境错误：非法的端口号。");
			}
			this.port = port;
			if (port <= 0) {
				throw new IllegalArgumentException("初始化分布式环境错误：非法的分布式节点标识。");
			}
			this.id = id;
			this.self = self;
			this.space = this.host + ":" + this.port;
			try {
				this.url = new URL("http", host, port, "");
			} catch (Throwable e) {
				throw new IllegalArgumentException("初始化分布式环境错误：非法URL地址。", e);
			}
		}

		final String space;
		final URL url;
	}

	static final class CacheFilter {

		final String template;
		final String conf;

		CacheFilter(String template, String conf) {
			if (template == null || template.length() == 0) {
				throw new IllegalArgumentException("初始化分布式环境错误：非法的过滤器模板名称。");
			}
			this.template = template;
			this.conf = conf;
		}
	}

	static final class App {

		CacheFilter filter;
		final ArrayList<Node> nodes = new ArrayList<Node>();

		final boolean isCluster() {
			return this.nodes.size() > 1;
		}

		final String space() {
			return this.nodes.get(0).space;
		}

		final boolean containSelf() {
			for (Node n : this.nodes) {
				if (n.self) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 静态配置是否启用复制
	 * 
	 * @return
	 */
	final boolean enableRepl() {
		return this.param && this.type == Type.APP;
	}
}