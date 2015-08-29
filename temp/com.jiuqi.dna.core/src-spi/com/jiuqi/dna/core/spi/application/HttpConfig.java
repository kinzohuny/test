package com.jiuqi.dna.core.spi.application;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.misc.SXElement;
/**
 * 
 * @author jiangqifan
 * 用于dna启动之后配置httpSever
 */
public class HttpConfig {

	static final String xml_element_http = "http";
	static final String xml_attr_app_context = "context";
	static final String xml_attr_http_sessions = "http-sessions";
	
	private List<HttpListenConfig> confs =	new ArrayList<HttpConfig.HttpListenConfig>();
	
	private boolean supportSession;
	private String context;
	
	public HttpConfig() {
		this(null,true);
	}
	public HttpConfig(String context,boolean supportSession) {
		this.context = context;
		this.supportSession = supportSession;
	}
	public void addListen(HttpListenConfig conf) {
		confs.add(conf);
	}
	public void addListenAll(HttpListenConfig[] confs) {
		if (null == confs) {
			return;
		}
		for (HttpListenConfig conf : confs) {
			this.addListen(conf);
		}
	}
	public SXElement getSXElement() {
		
		SXElement element = SXElement.newElement(xml_element_http);
		
		element.setAttribute(xml_attr_app_context, context);
		element.setAttribute(xml_attr_http_sessions, String.valueOf(supportSession));
		
		for (HttpListenConfig conf : confs) {
			element.append(conf.getSXElement());
		}
		return element;
	}
	
	public static class HttpListenConfig {

		static final String xml_element_listen = "listen";
		static final String xml_attr_host = "host";
		static final String xml_attr_port = "port";
		static final String xml_attr_max_thread = "max-threads";
		static final String xml_attr_min_thread = "min-threads";
		static final String xml_attr_max_idle_ms = "max-idle-ms";
		static final String xml_attr_accept_queue_size = "accept-queue-size";
		static final String xml_attr_acceptor_threads = "acceptors";
		
		private String host;
		private int port = 9797;
		private int maxThreads = 500;
		private int minThreads = 2;
		private int maxIdleMs = 10000;
		private int acceptQueueSize = 500;
		private int acceptors = 2;
		
		public HttpListenConfig(String host,int port) {
			this.host = host;
			this.port = port;
		}
		public String getHost() {
			return host;
		}
		public int getPort() {
			return port;
		}
		public int getMaxIdleMs() {
			return maxIdleMs;
		}
		public void setMaxIdleMs(int maxIdleMs) {
			this.maxIdleMs = maxIdleMs;
		}
		public int getMaxThreads() {
			return maxThreads;
		}
		public void setMaxThreads(int maxThreads) {
			this.maxThreads = maxThreads;
		}
		public int getMinThreads() {
			return minThreads;
		}
		public void setMinThreads(int minThreads) {
			this.minThreads = minThreads;
		}
		public int getAcceptors() {
			return acceptors;
		}
		public void setAcceptors(int acceptors) {
			this.acceptors = acceptors;
		}
		public int getAcceptQueueSize() {
			return acceptQueueSize;
		}
		public void setAcceptQueueSize(int acceptQueueSize) {
			this.acceptQueueSize = acceptQueueSize;
		}
		
		public SXElement getSXElement() {
			SXElement element = SXElement.newElement(xml_element_listen);
			
			element.setAttrWithEmptyStr(xml_attr_host, host);
			element.setAttrWithEmptyStr(xml_attr_port, String.valueOf(port));
			element.setAttribute(xml_attr_max_thread, String.valueOf(maxThreads));
			element.setAttribute(xml_attr_min_thread, String.valueOf(minThreads));
			element.setAttribute(xml_attr_max_idle_ms, String.valueOf(maxIdleMs));
			element.setAttribute(xml_attr_accept_queue_size, String.valueOf(acceptQueueSize));
			element.setAttribute(xml_attr_acceptor_threads, String.valueOf(acceptors));
			
			return element;
		}
	}
	
	public static class APJ13HttpListenConfig extends HttpListenConfig{

		static final String xml_attr_ajp13 = "ajp13";
		
		public APJ13HttpListenConfig(String host, int port) {
			super(host, port);
		}
		@Override
		public SXElement getSXElement() {
			SXElement element = super.getSXElement();
			element.setAttribute(xml_attr_ajp13, "true");
			
			return element;
		}
		
	}
	public static class SSLHttpListenConfig extends HttpListenConfig{

		static final String xml_attr_ssl = "ssl";
		static final String xml_attr_ssl_keystore = "ssl-keystore";
		static final String xml_attr_ssl_keystore_type = "ssl-keystore-type";
		static final String xml_attr_ssl_key_password = "ssl-key-password";
		static final String xml_attr_ssl_password = "ssl-password";
		
		private String keyStore;
		private String keyStoreType;
		private String keyPassword;
		private String password;
		
		public SSLHttpListenConfig(String host, int port) {
			super(host, port);
		}
		
		public String getKeyPassword() {
			return keyPassword;
		}
		public void setKeyPassword(String keyPassword) {
			this.keyPassword = keyPassword;
		}
		public String getKeyStore() {
			return keyStore;
		}
		public void setKeyStore(String keyStore) {
			this.keyStore = keyStore;
		}
		public String getKeyStoreType() {
			return keyStoreType;
		}
		public void setKeyStoreType(String keyStoreType) {
			this.keyStoreType = keyStoreType;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		@Override
		public SXElement getSXElement() {
			SXElement element = super.getSXElement();
			element.setAttribute(xml_attr_ssl, "true");
			
			element.setAttribute(xml_attr_ssl_keystore, keyStore);
			element.setAttribute(xml_attr_ssl_keystore_type, keyStoreType);
			element.setAttribute(xml_attr_ssl_key_password, keyPassword);
			element.setAttribute(xml_attr_ssl_password, password);
			
			return element;
		}
	}
}
