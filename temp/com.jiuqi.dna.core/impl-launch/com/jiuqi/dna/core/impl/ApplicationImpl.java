package com.jiuqi.dna.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.CodeSource;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.SessionKind;
import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.exception.AbortException;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.exception.SessionDisposedException;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceManager;
import com.jiuqi.dna.core.internal.db.datasource.DataSourceRef;
import com.jiuqi.dna.core.internal.db.datasource.JdbcDriverManager;
import com.jiuqi.dna.core.internal.db.datasource.JdbcDriverProvider;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXElementBuilder;
import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.spi.application.Session;
import com.jiuqi.dna.core.spi.application.SessionIniter;
import com.jiuqi.dna.core.spi.http.AbstractHttpServer;
import com.jiuqi.dna.core.spi.metadata.LoadAllMetaDataTask;
import com.jiuqi.dna.core.spi.work.WorkingManager;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.DateParser;
import com.jiuqi.dna.core.type.GUID;

/**
 * Ӧ��ʵ��
 * 
 * @author gaojingxin
 * 
 */
public final class ApplicationImpl implements Application {

	private static long start_nanoTime = System.nanoTime();
	private static long start_time = System.currentTimeMillis();
	boolean isInCluster;
	/**
	 * �Ƿ�Ϊ�Ǽ�Ⱥ�ڵ����Ϊ��Ⱥ�е�һ�������Ľڵ�
	 */
	boolean isFirstInCluster;

	final static void printDateTime(PrintStream stream) {
		final long time = (System.nanoTime() - start_nanoTime) / 1000000 + start_time;
		stream.print(DateParser.format(time, DateParser.FORMAT_DATE_TIME_MS));
	}

	final NetChannelManagerImpl netChannelManager;

	final NetNodeManagerImpl netNodeManager;

	/**
	 * ���ڵ������ID��ÿ������ʱ�������
	 */
	final GUID localNodeID = GUID.randomID();

	public SiteState getDefaultSiteState() {
		return this.rootSite.state;
	}

	public long getBornTime() {
		return start_time;
	}

	public final int getNormalSessionCount() {
		return this.sessionManager.getNormalSessionCount(false);
	}

	/**
	 * �����ͨ�Ự����������Ự��
	 * 
	 * @param excludeBuildInUser
	 *            �Ƿ��޳��ڽ��û����Ƿ��������¼��ĻỰ��
	 */
	public final int getNormalSessionCount(boolean excludeBuildInUser) {
		return this.sessionManager.getNormalSessionCount(excludeBuildInUser);
	}

	public List<? extends Session> getNormalSessions(GUID byUserID) {
		return this.sessionManager.getNormalSessions(byUserID);
	}

	/**
	 * ���������ͨ�Ự�б�
	 */
	public final List<? extends Session> getNormalSessions() {
		return this.sessionManager.getNormalSessions(null);
	}

	public final long getHTTPRequestBytes() {
		return 0L;
	}

	public long getDBTimeused() {
		return 0L;
	}

	public final long getHTTPRequestTicks() {
		return 0L;
	}

	public final long getHTTPResponseBytes() {
		return 0L;
	}

	public final long getHTTPRequestTotalProcessTime() {
		return 0L;
	}

	private HashMap<String, LanguagePackage> infoGroupLanguages;

	final void regInfoGroupLanguage(String infoGroupFullName, int localeKey,
			String[] infoNameMessages) {
		if (this.infoGroupLanguages == null) {
			this.infoGroupLanguages = new HashMap<String, LanguagePackage>();
		}
		final LanguagePackage newLp = new LanguagePackage(localeKey, infoNameMessages);
		newLp.next = this.infoGroupLanguages.put(infoGroupFullName, newLp);
	}

	final LanguagePackage findInfoGroupLanguages(String infoGroupFullName) {
		return this.infoGroupLanguages != null ? this.infoGroupLanguages.get(infoGroupFullName) : null;
	}

	public final <TUserData> SessionImpl newSession(
			SessionIniter<TUserData> sessionIniter, TUserData userData) {
		return this.sessionManager.newSession(SessionKind.NORMAL, BuildInUser.anonym, sessionIniter, userData);
	}

	public final SessionImpl getSession(long sessionID)
			throws SessionDisposedException {
		return this.sessionManager.getOrFindSession(sessionID, true);
	}

	public final SessionImpl getSystemSession() {
		return this.sessionManager.getSystemSession();
	}

	private final Random random = new Random();

	/**
	 * ���ȫ��Ψһ���������������GUID����
	 */
	final GUID newRECID() {
		return GUID.valueOf(this.timeRelatedSequence.next(), this.random.nextLong());
	}

	private final AtomicLong resourceItemID;

	final long newResourceItemID() {
		return this.resourceItemID.incrementAndGet();
	}

	/**
	 * ������������İ汾��
	 */
	final long newRECVER() {
		return this.timeRelatedSequence.next();
	}

	final TimeRelatedSequenceImpl timeRelatedSequence;

	final void doDispose() {
		if (this.httpServer != null) {
			this.httpServer.dispose();
		}
		if (this.netManager != null) {
			this.netManager.doDispose();
		}
		if (this.sessionManager != null) {
			this.sessionManager.doDispose();
		}
		if (this.overlappedManager != null) {
			this.overlappedManager.doDispose();
		}
		if (this.dataSourceManager != null) {
			this.dataSourceManager.doDispose();
		}
	}

	final static String file_dna_server = "work/dna-server.xml";
	final static String xml_element_dna = "dna";
	final static String xml_element_sites = "sites";

	final DataSourceRef newSiteConnectionInfo2(SXElement siteElement) {
		if (this.dataSourceManager.isEmpty()) {
			return null;
		}
		if (siteElement != null) {
			for (SXElement datasourcerefE : siteElement.getChildren(Site.xml_element_datasourcerefs, DataSourceRef.xml_element_datasourceref)) {
				if (datasourcerefE.getAttribute(DataSourceRef.xml_attr_space).length() == 0) {
					try {
						return new DataSourceRef(this.dataSourceManager, datasourcerefE);
					} catch (Throwable e) {
						this.catcher.catchException(e, this.dataSourceManager);
						return null;
					}
				}
			}
		}
		try {
			return new DataSourceRef(this.dataSourceManager.getDefaultSource());
		} catch (Throwable e) {
			this.catcher.catchException(e, this.dataSourceManager);
			return null;
		}
	}

	private final SXElement dnaServerConfig;

	public final SXElement getDNAConfig(String name) {
		return this.dnaServerConfig.firstChild(name);
	}

	public final SXElement getDNAConfig(String name1, String name2) {
		return this.dnaServerConfig.firstChild(name1, name2);
	}

	public final SXElement getDNAConfig(String name1, String name2,
			String... names) {
		return this.dnaServerConfig.firstChild(name1, name2, names);
	}

	private final SXElement getDefaultSiteConfig() {
		return this.getDNAConfig(xml_element_sites, Site.xml_element_site);
	}

	private static final SXElement getDNAServerConfig(File dnaRoot,
			SXElementBuilder builder, ExceptionCatcher catcher) {
		if (dnaRoot != null) {
			try {
				final File fDNAServer = new File(dnaRoot, file_dna_server);
				if (fDNAServer.isFile()) {
					final SXElement dna = builder.build(fDNAServer).firstChild(xml_element_dna);
					if (dna != null) {
						return dna;
					}
				}
			} catch (Throwable e) {
				catcher.catchException(e, null);
			}
		}
		return SXElement.newElement(xml_element_dna);
	}

	public final ClassLoader contextFinder;

	/**
	 * ���캯��
	 */
	private ApplicationImpl(BundleContext coreBundleContext,
			ClassLoader contextFinder, AbstractHttpServer server) {
		if (coreBundleContext == null) {
			throw new NullArgumentException("coreBundleContext");
		}
		this.contextFinder = contextFinder;
		ResolveHelper.logStartInfo("��ʼ");
		ResolveHelper.javaVersion();
		ResolveHelper.logStartInfo("Ӧ��ID��" + this.localNodeID);
		File rootPath = getDNARootPath(coreBundleContext.getProperty(ROOT_PATH_2));
		if (rootPath != null) {
			this.dnaRoot = rootPath;
		} else {
			this.dnaRoot = getDNARootPath(coreBundleContext.getProperty(ROOT_PATH));
		}
		this.dnaWork = this.dnaRoot != null ? new File(this.dnaRoot, folder_work) : null;
		final SXElementBuilder sxBuilder;
		try {
			sxBuilder = new SXElementBuilder();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
		this.dnaServerConfig = getDNAServerConfig(this.dnaRoot, sxBuilder, this.catcher);
		
		this.initializeDistEnv();
		try {
			// �����ģ���ʵ��������������Ϣ
			final NetSelfClusterImpl selfCluster = new NetSelfClusterImpl(this, this.getDNAConfig(NetSelfClusterImpl.xml_element_cluster));
			this.timeRelatedSequence = new TimeRelatedSequenceImpl(selfCluster.thisClusterNodeIndex);
			this.resourceItemID = new AtomicLong(((long) selfCluster.thisClusterNodeIndex) << 60);
			this.overlappedManager = new WorkingManager(this);
			this.netChannelManager = new NetChannelManagerImpl(this);
			this.netNodeManager = new NetNodeManagerImpl(this.netChannelManager, selfCluster, this.getDNAConfig(NetNodeManagerImpl.xml_element_net));
			
			this.httpServer = server;
			server.setApplication(this);
//				coreBundleContext.registerService(ServletContainer.class.getName(), this.httpServer, null);
//			Class httpServerClass = Activator.getHttpServerClass();
//			if(httpServerClass!=null){
//				Constructor c = httpServerClass.getDeclaredConstructor(Application.class);
//				if(c!=null){
//					this.httpServer = (AbstractHttpServer) c.newInstance(this);
//				}
//				coreBundleContext.registerService(ServletContainer.class.getName(), this.httpServer, null);
//			}
			this.coreBundle = this.initBundleStubs(coreBundleContext, sxBuilder);
			this.mseManager = new ModelScriptEngineManager();
			this.sessionManager = new SessionManager(this, this.getDNAConfig(SessionManager.xml_element_session));
			this.dataSourceManager = new DataSourceManager(this, this.getDNAConfig(DataSourceManager.XML_EL_DATASOURCES));
			this.netManager = new NetManager(this);
			this.netManager.config(this.getDNAConfig(NetManager.xml_e_cluster));
			this.LDAPValidator = new LDAPValidator(this.getDNAConfig(com.jiuqi.dna.core.impl.LDAPValidator.xml_element_ldap));
			
			this.loginController = new LoginController(this.dnaServerConfig);
			
			// ����jetty������
			tryStartServer(this.getDNAConfig("http"));
			// ����վ��ʵ��
			this.rootSite = new Site(this, this.getDefaultSiteConfig(), true);
			defaultApp = this;
			// ������ģ��
			this.netNodeManager.active();
			this.rootSite.active(true);
		} catch (Throwable e) {
			defaultApp = null;
			this.doDispose();
			throw Utils.tryThrowException(e);
		}
	}

	// //////////////////////////////////////////////////////////////////////
	// ////
	// //////////////////�������ڲ�����//////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////

	/**
	 * ��վ��
	 */
	private Site rootSite;

	/**
	 * ��ȡĬ�ϵ�վ��
	 */
	final Site getDefaultSite() {
		return this.rootSite;
	}

	final Site findSite(GUID id) {
		return this.getDefaultSite();
	}

	// �쳣�ռ���
	public final ExceptionCatcher catcher = new ExceptionCatcher() {
		public void catchException(Throwable e, Object sender) {
			if (!(e instanceof AbortException)) {
				e.printStackTrace();
				if (e instanceof InterruptedException) {
					throw Utils.tryThrowException(e);
				}
			}
		}
	};

	/**
	 * �첽�����̳߳�
	 */
	public final WorkingManager overlappedManager;
	/**
	 * ����Դ����
	 */
	public final DataSourceManager dataSourceManager;
	/**
	 * http������
	 */
	private AbstractHttpServer httpServer;
	/**
	 * �Ự��
	 */
	final SessionManager sessionManager;
	/**
	 * ��ǰ�̵߳�������
	 */
	final ThreadLocal<ContextImpl<?, ?, ?>> contextLocal = new ThreadLocal<ContextImpl<?, ?, ?>>();
	/**
	 * LDAP�����֤��
	 */
	final LDAPValidator LDAPValidator;
	
	final LoginController loginController;
	public LoginController getLoginController() {
		return this.loginController;
	}

	// /////////////////////////////////////////////////////////////
	// /////����
	// /////////////////////////////////////////////////////////////
	final static String xml_file_site = "site.xml";
	final static String folder_site_root = "work/com.jiuqi.dna";
	final static String folder_work = "work";
	private final File dnaRoot;
	private final File dnaWork;

	public final File getDNARoot() {
		final File dnaRoot = this.dnaRoot;
		if (dnaRoot == null) {
			throw new IllegalStateException("DNA��Ŀ¼�޷�ȷ��");
		}
		return dnaRoot;
	}

	public final File getDNAWork() {
		final File dnaWork = this.dnaWork;
		if (dnaWork == null) {
			throw new IllegalStateException("DNA����Ŀ¼�޷�ȷ��");
		}
		return dnaWork;
	}

	private static final File getDNARootPath(String bundleRootPath) {
		if (bundleRootPath != null && bundleRootPath.length() > 0) {
			File root = new File(bundleRootPath);
			if (root.isDirectory()) {
				return root;
			}
		}
		return null;
	}

	final BundleStub coreBundle;
	final Map<String, BundleStub> bundles = new HashMap<String, BundleStub>();

	private final void putBundle(BundleStub bundle) {

		BundleStub exist = this.bundles.get(bundle.name);
		BundleStub last = null;
		while (exist != null && exist.version.compareTo(bundle.version) >= 0) {
			last = exist;
			exist = exist.next;
		}
		if (last != null) {
			bundle.next = last.next;
			last.next = bundle;
		} else {
			bundle.next = exist;
			this.bundles.put(bundle.name, bundle);
		}
	}

	final Class<?> loadClass(String className, String bundleName)
			throws ClassNotFoundException {
		BundleStub b = this.bundles.get(bundleName);
		if (b == null) {
			throw new ClassNotFoundException("bundle[" + bundleName + "]�����ڣ���λ������[" + className + "]");
		}
		return b.loadClass(className, null);
	}

	final Class<?> tryLoadClass(String className, String bundleName) {
		try {
			return this.loadClass(className, bundleName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public final Class<?> loadClass(String className) throws ClassNotFoundException {
		try {
			return this.coreBundle.loadClass(className, null);
		} catch (ClassNotFoundException e) {
			for (BundleStub b : this.bundles.values()) {
				if (b != this.coreBundle) {
					try {
						return b.loadClass(className, null);
					} catch (ClassNotFoundException e2) {
					}
				}
			}
			throw e;
		}
	}

	final Class<?> tryLoadClass(String className) {
		try {
			return this.loadClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	final DataType findDataType(GUID typeID) {
		return DataTypeBase.findDataType(typeID);
	}

	static {
		DataTypeBase.ensureStaticInited();
	}

	private final void loadGather(BundleStub bundle, SXElement gatherE) {
		String groupTag = gatherE.getAttribute(PublishedElementGatherer.xml_attr_group);
		if (groupTag == null || groupTag.length() == 0) {
			return;
		}
		PublishedElementGatherer<?> gather;
		try {
			Class<?> gatherClass = bundle.loadClass(gatherE.getAttribute(PublishedElementGatherer.xml_attr_class), PublishedElementGatherer.class);
			Constructor<?> c1, c2;
			try {
				c1 = gatherClass.getDeclaredConstructor(SXElement.class);
				c2 = null;
			} catch (Throwable e) {
				c2 = gatherClass.getDeclaredConstructor();
				c1 = null;
			}
			if (c1 != null) {
				Utils.publicAccessibleObject(c1);
				gather = (PublishedElementGatherer<?>) c1.newInstance(gatherE);
			} else {
				Utils.publicAccessibleObject(c2);
				gather = (PublishedElementGatherer<?>) c2.newInstance();
			}
		} catch (Throwable e) {
			this.catcher.catchException(e, bundle);
			return;
		}
		PublishedElementGathererGroup gathererGroup = this.gathererGroupMap.get(groupTag);
		if (gathererGroup == null) {
			gathererGroup = new PublishedElementGathererGroup();
			this.gathererGroupMap.put(groupTag, gathererGroup);
		}
		String elementTag = gatherE.getAttribute(PublishedElementGatherer.xml_attr_element);
		gathererGroup.putGather(elementTag, gather);
	}

	/**
	 * ��ȡbundle���dna.xml�Ļ�������.
	 * 
	 * <ul>
	 * <li>��ȡservlet,filter�����ṩ��jetty.
	 * <li>��ȡgather����,ʵ����gather
	 * <li>��ȡtype����.
	 * </ul>
	 * 
	 * @param bundle
	 *            Ŀ��bundle���
	 * @param dna
	 *            Ŀ��bundle�洢��dna.xml����
	 */
	final void loadBaseConfigs(BundleStub bundle, SXElement dna) {
		for (SXElement base = dna.firstChild(); base != null; base = base.nextSibling()) {
			if (base.name.equals(PublishedElementGatherer.xml_element_gathering)) {
				for (SXElement gatherE = base.firstChild(PublishedElementGatherer.xml_element_gatherer); gatherE != null; gatherE = gatherE.nextSibling(PublishedElementGatherer.xml_element_gatherer)) {
					this.loadGather(bundle, gatherE);
				}
			} else if (this.httpServer!=null&&(base.name.equals("servlets") || base.name.equals("filters"))) {
				ClassLoader classLoader = bundle.asClassLoader();
				for (SXElement servletE = base.firstChild(); servletE != null; servletE = servletE.nextSibling()) {
					try {
						this.httpServer.loadServletOrFilter(classLoader, servletE);
					} catch (Throwable e) {
						this.catcher.catchException(e, bundle);
					}
				}
			} else if (base.name.equals(ObjectDataTypeBase.xml_element_types)) {
				for (SXElement typeE = base.firstChild(ObjectDataTypeBase.xml_element_type); typeE != null; typeE = typeE.nextSibling(ObjectDataTypeBase.xml_element_type)) {
					try {
						ObjectDataTypeBase.loadCustomType(bundle, typeE);
					} catch (Throwable e) {
						this.catcher.catchException(e, bundle);
					}
				}
			} else if (base.name.equals(JdbcDriverProvider.xml_element_jdbc_drivers)) {
				for (SXElement providerElement = base.firstChild(JdbcDriverProvider.xml_element_jdbc_driver); providerElement != null; providerElement = providerElement.nextSibling(JdbcDriverProvider.xml_element_jdbc_driver)) {
					try {
						JdbcDriverManager.INSTANCE.register(bundle, providerElement);
					} catch (Throwable e) {
						this.catcher.catchException(e, bundle);
					}
				}
			}
		}
	}

	private final BundleStub initBundleStubs(BundleContext coreBundleContext,
			SXElementBuilder sxBuilder) {
		BundleStub coreBundleStub = new BundleStub(coreBundleContext.getBundle(), sxBuilder, this);
		this.putBundle(coreBundleStub);
		for (Bundle bundle : coreBundleContext.getBundles()) {
			if (!coreBundleStub.sameBundle(bundle)) {
				BundleStub bundleStub = new BundleStub(bundle, sxBuilder, this);
				this.putBundle(bundleStub);
			}
		}
		return coreBundleStub;
	}

	final Map<String, PublishedElementGathererGroup> gathererGroupMap = new HashMap<String, PublishedElementGathererGroup>();

	private static ApplicationImpl defaultApp;
	public static final String ROOT_PATH = "com.jiuqi.dna.rootpath";
	/**
	 * ���ڽ��launcher����ROOT_PATH����������
	 */
	public static final String ROOT_PATH_2 = "com.jiuqi.dna.rootpath2";

	static ApplicationImpl startApp(BundleContext context, AbstractHttpServer server) {
		if (ApplicationImpl.defaultApp != null) {
			throw new IllegalStateException("Ӧ���Ѿ�����");
		}
		final Thread thread = Thread.currentThread();
		final ClassLoader contextFinder = thread.getContextClassLoader();
		
//		if (WorkingThread.cxfBundleClassLoader != contextFinder) {
//			thread.setContextClassLoader(WorkingThread.cxfBundleClassLoader);
//		}
//		try {
			return new ApplicationImpl(context, contextFinder, server);
//		} finally {
//			if (WorkingThread.cxfBundleClassLoader != contextFinder) {
//				thread.setContextClassLoader(contextFinder);
//			}
//		}
	}

	static void stopApp() {
		if (ApplicationImpl.defaultApp != null) {
			try {
				ApplicationImpl.defaultApp.doDispose();
			} finally {
				ApplicationImpl.defaultApp = null;
			}
		}
	}
	
	public static ApplicationImpl tryGetDefaultApp() {
		return ApplicationImpl.defaultApp;
	}
	
	public static ApplicationImpl getDefaultApp() {
		if (ApplicationImpl.defaultApp == null) {
			throw new IllegalStateException("DNA��������δ�������");
		}
		return ApplicationImpl.defaultApp;
	}

	// //////////////////
	// //// remote
	// //////////////////
	private NetManager netManager;

	final NetNodeInfo getNetNodeInfo(String host, int port) {
		if (this.netManager == null) {
			throw new UnsupportedOperationException("��֧��Զ�̵���");
		}
		try {
			return this.netManager.ensureGet(host, port);
		} catch (UnknownHostException e) {
			throw Utils.tryThrowException(e);
		}
	}

	// ////////////////////////////
	// ///// scripting
	// ////////////////////////////
	final ModelScriptEngineManager mseManager;

	// /////////////////////////////////////
	// // �����ϲ�
	// /////////////////////////////////////

	private final static class RestartHook extends Thread {
		final static String DEFAULT_REBOOT_PATH = "dna/core/com.jiuqi.dna.reboot_1.0.0.jar";
		final static String DEFAULT_MAIN_CLASS = "com.jiuqi.dna.reboot.Main";
		final static String ARG_CONSOLE = "reboot.console";
		final static String ARG_PID = "reboot.pid";
		final static String ARG_DEBUG = "reboot.debug";
		private final int pid;
		private final String classPath;

		public RestartHook(ObjectQuerier querier) {
			this.pid = getPid();
			String path = getClassPath(querier, DEFAULT_MAIN_CLASS);
			if (path == null) {
				throw new IllegalStateException("�Ҳ���ģ��com.jiuqi.dna.reboot");
			}
			if (!path.toLowerCase().endsWith(".jar")) {
				throw new UnsupportedOperationException("��֧����jar����");
			}
			this.classPath = path;
		}

		private static final int getPid() {
			RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
			String name = runtime.getName(); // format: "pid@hostname"
			return Integer.parseInt(name.substring(0, name.indexOf('@')));
		}

		private final static String getClassPath(ObjectQuerier querier,
				String className) {
			try {
				// ��ȡosgi�������
				Class<?> clz = querier.find(Class.class, className);
				if (clz == null) {
					try {
						clz = Class.forName(className);
					} catch (Throwable e) {
						if (Application.IN_DEBUG_MODE) {
							e.printStackTrace();
						}
					}
				}
				if (clz != null) {
					try {
						// ��ȡ����������ڵ��ֽ�����Ϣ
						CodeSource cs = clz.getProtectionDomain().getCodeSource();
						if (cs != null) {
							// ��ȡjar����·��
							String url = cs.getLocation().toString();
							// URI��֧�ֿո�����Ҫ�Ƚ��ո�ת��
							if (url.startsWith("file:/")) {
								url = "file:/" + URLEncoder.encode(url.substring("file:/".length()), "UTF-8");
							}
							return new File(new URI(url)).toString();
						}
					} catch (Throwable e) {
						if (Application.IN_DEBUG_MODE) {
							e.printStackTrace();
						}
					}
				}
			} catch (Throwable e) {
				if (Application.IN_DEBUG_MODE) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private static boolean detectSysWin() {
			return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
		}

		private static boolean detectConsole() {
			try {
				java.lang.reflect.Method m = System.class.getDeclaredMethod("console");
				try {
					return m.invoke(null) != null;
				} catch (Throwable e) {
				}
			} catch (NoSuchMethodException e) {
			}
			return true;
		}

		@Override
		public void run() {
			ArrayList<String> cmds = new ArrayList<String>();
			// ��鵱ǰ�����Ƿ�������console��
			boolean console = detectConsole();
			// ������ϵͳ�Ƿ�Ϊlinux
			if (!detectSysWin()) {
				// linux�²���ʾ���ڣ�������nohup����
				console = false;
				cmds.add("nohup");
			}
			// ��ȡjava������
			String java = System.getProperty("java.home");
			if (java == null) {
				java = "java";
			} else {
				StringBuilder sb = new StringBuilder(java);
				if (!java.endsWith(File.separator)) {
					sb.append(File.separator);
				}
				sb.append("bin").append(File.separator).append("java");
				java = sb.toString();
			}
			cmds.add(java);
			// reboot����ģʽ
			if (Application.IN_DEBUG_MODE) {
				cmds.add("-D" + ARG_DEBUG + "=true");
			}
			// �����Ƿ���ʾconsole
			if (console) {
				cmds.add("-D" + ARG_CONSOLE + "=true");
			}
			// ���ý���PID
			cmds.add("-D" + ARG_PID + "=" + this.pid);
			// ���reboot���
			if (this.classPath.toLowerCase().endsWith(".jar")) {
				cmds.add("-jar");
				// ·���д��пո�ʱ��win���޷����У�����ʹ�����·���������·��
				cmds.add(DEFAULT_REBOOT_PATH);
			} else {
				throw new UnsupportedOperationException("��֧����jar����");
			}
			// ���jvm����
			String[] props;
			if (Unsf.jvm_ibm) {
				props = new String[] { "-Xjcl", // -Xjcl:jclscar_23
				"-Dconsole.encoding", // -Dconsole.encoding=
				"-Dcom.ibm.", // -Dcom.ibm.oti.vm.bootstrap.library.path=
				"-Dsun.boot.", // -Dsun.boot.library.path=
				"-Djava.library.path", // -Djava.library.path=
				"-Djava.home", // -Djava.home=
				"-Djava.ext.dirs", // -Djava.ext.dirs=
				"-Djava.class.path", // -Djava.class.path=
				"-Duser.dir", // -Duser.dir=
				"-Dinvokedviajava", // -Dinvokedviajava
				"-Xdump", // -Xdump
				"_", // _j2se_j9 _port_library
				"vfprintf", // vfprintf
				"-Dosgi.noShutdown", // -Dosgi.noShutdown=
				"-Dosgi.console.blockOnReady", // -Dosgi.console.blockOnReady=
				"-Dout.redirect" // -Dout.redirect=
				};

			} else {
				props = new String[] { "-Dosgi.noShutdown", // -Dosgi.noShutdown=
				"-Dosgi.console.blockOnReady", // -Dosgi.console.blockOnReady=
				"-Dout.redirect" // -Dout.redirect=
				};
			}
			StringBuilder sb = new StringBuilder("(");
			for (String prop : props) {
				sb.append(prop.replace(".", "\\.")).append('|');
			}
			sb.replace(sb.length() - 1, sb.length(), ").*");
			Pattern p = Pattern.compile(sb.toString());
			for (String s : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
				if (!p.matcher(s).matches() && !cmds.contains(s)) {
					cmds.add(s);
				}
			}
			// ��Ӳ�������ֹ��̨������Ϊ��׼������������������˳�
			if (console) {
				if (Boolean.getBoolean("osgi.noShutdown")) {
					cmds.add("-Dosgi.noShutdown=true");
				}
				if (Boolean.getBoolean("out.redirect")) {
					cmds.add("-Dout.redirect=true");
				}
			} else {
				// -Dosgi.noShutdown=true
				// -Dosgi.console.blockOnReady=false
				// -Dout.redirect=true
				cmds.add("-Dosgi.noShutdown=true");
				cmds.add("-Dosgi.console.blockOnReady=false");
				cmds.add("-Dout.redirect=true");
			}
			// ��ʾ������
			sb.setLength(0);
			sb.append("cmd:");
			for (String s : cmds) {
				sb.append(' ').append(s);
			}
			System.out.println(sb);
			// �����½���
			try {
				Runtime.getRuntime().exec(cmds.toArray(new String[cmds.size()]));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	final void shutdown(ContextImpl<?, ?, ?> context, boolean restart)
			throws Throwable {
		final Site oldRootSite = this.rootSite;
		synchronized (oldRootSite) {
			switch (oldRootSite.state) {
			case ACTIVE:
			case DISPOSING:
				break;
			default:
				throw new IllegalStateException("վ����봦�ڼ��������������״̬��������������");
			}
		}
		final Exception stack = new Exception();
		final PrintStream out = System.err;
		synchronized (out) {
			printDateTime(out);
			out.println(": DNA Ӧ�ó���" + (restart ? "��������" : "�ر�") + "������ջ��");
			for (StackTraceElement trace : stack.getStackTrace()) {
				out.println("\t->" + trace);
			}
		}
		// ע�ṳ�ӣ���������Ӧ�ó���
		// Runtime.getRuntime().addShutdownHook(new RestartHook(context));
		// ���Է���shutdownHook��ʱ�򲻱����ã�
		RestartHook hook = restart ? new RestartHook(context) : null;
		try {
			this.sessionManager.doReset();
		} finally {
			try {
				oldRootSite.doDispose(context);
			} finally {
				// ֹͣSystemBundle
				for (Bundle bundle : this.coreBundle.getBundleContext().getBundles()) {
					if ("org.eclipse.osgi.framework.internal.core.SystemBundle".equals(bundle.getClass().getName())) {
						try {
							bundle.stop();
						} catch (Throwable e) {
							e.printStackTrace();
						}
						break;
					}
				}
				if (hook != null) {
					hook.run();
				}
				// �˳�����
				System.exit(1);
			}
		}
	}

	/**
	 * ������վ��
	 */
	@Deprecated
	final void restartRootSite(ContextImpl<?, ?, ?> context) throws Throwable {
		final Site oldRootSite = this.rootSite;
		synchronized (oldRootSite) {
			if (oldRootSite.state != SiteState.ACTIVE) {
				throw new IllegalStateException("վ����봦�ڼ���״̬��������������");
			}
			oldRootSite.state = SiteState.DISPOSING;
		}
		final Exception stack = new Exception();
		final PrintStream out = System.err;
		synchronized (out) {
			printDateTime(out);
			out.println(": DNA վ����������������ջ��");
			for (StackTraceElement trace : stack.getStackTrace()) {
				out.println("\t->" + trace);
			}
		}
		final Site newSite;
		try {
			this.sessionManager.doReset();
			newSite = new Site(this, this.getDefaultSiteConfig(), true);
		} catch (Throwable e) {
			oldRootSite.state = SiteState.ACTIVE;
			throw e;
		}
		// ����
		this.rootSite = newSite;
		try {
			newSite.active(false);
		} finally {
			// �����������֤�ӳٶ�
			oldRootSite.doDispose(context);
		}
	}

	/**
	 * ����װ��վ��
	 */
	final void reLoadRootSite(ContextImpl<?, ?, ?> context,
			LoadAllMetaDataTask task) throws Throwable {
		final Site oldRootSite = this.rootSite;
		synchronized (oldRootSite) {
			if (oldRootSite.state != SiteState.ACTIVE) {
				throw new IllegalStateException("վ����봦�ڼ���״̬������װ�ز���");
			}
			oldRootSite.state = SiteState.WAITING_LOAD_METADATA;
		}
		Site tmpSite = null;
		try {
			tmpSite = new Site(this, this.getDefaultSiteConfig(), false);
			tmpSite.load(context, task);
			context.resolveTrans();
			tmpSite.state = SiteState.DISPOSING;
		} finally {
			try {
				if (tmpSite != null) {
					// �����������֤�ӳٶ�
					tmpSite.doDispose(context);
				}
			} finally {
				context.lockResourceU(context.getResourceToken(SynClusterClock.class));
				oldRootSite.state = SiteState.DISPOSING;
				oldRootSite.shutdown(context, true);
			}
		}
	}

	public final Iterable<Driver> getJdbcDrivers() {
		return JdbcDriverManager.INSTANCE.itr();
	}

	DistributedEnvironment distenv;

	final void initializeDistEnv() {
		File folder = new File(this.dnaWork, "dist");
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		File file = new File(folder, "distributed.xml");
		if (!file.exists() || !file.isFile()) {
			return;
		}
		final String text;
		try {
			text = read(file, "UTF8");
		} catch (Throwable e) {
			this.catcher.catchException(e, this);
			return;
		}
		SXElement element = null;
		try {
			element = new SXElementBuilder().build(text);
		} catch (Throwable e) {
			this.catcher.catchException(e, this);
			return;
		}
		if (element == null) {
			return;
		}
		SXElement dist = element.firstChild("distributed");
		if (dist == null) {
			return;
		}
		if (dist.getBoolean("enable", false)) {
			try {
				this.distenv = new DistributedEnvironment(dist);
			} catch (Throwable e) {
				this.catcher.catchException(e, this);
			}
		}
		if (this.distenv != null) {
			ContextVariableIntl.ENABLE_CACHE_MODIFY_EVENT = this.distenv.enableRepl();
		}
	}

	static final String readString(InputStream is, Charset charset)
			throws IOException {
		final InputStreamReader isr = new InputStreamReader(is, charset);
		try {
			char[] str = new char[500];
			int strl = 0;
			for (int start = 0;;) {
				int l = str.length;
				int r = isr.read(str, start, l - start);
				if (r >= 0) {
					strl += r;
					if (strl == l) {
						char[] newstr = new char[l * 2];
						System.arraycopy(str, 0, newstr, 0, l);
						str = newstr;
					}
					start = strl;
				} else {
					break;
				}
			}
			return new String(str, 0, strl);
		} finally {
			isr.close();
		}
	}

	static final String read(File file, String charset) throws IOException {
		final FileInputStream fis = new FileInputStream(file);
		try {
			return readString(fis, Charset.forName(charset));
		} finally {
			fis.close();
		}
	}



	public ExceptionCatcher getExceptionCatcher() {
		// TODO Auto-generated method stub
		return this.catcher;
	}

	public WorkingManager getWorkingManager() {
		// TODO Auto-generated method stub
		return this.overlappedManager;
	}

	public boolean isMultiNodes() {
		// TODO Auto-generated method stub
		return  this.netNodeManager.thisCluster.multiNodes;
	}

	public int getIndexinCluster() {
		// TODO Auto-generated method stub
		return this.netNodeManager.thisCluster.thisClusterNodeIndex;
	}
	public GUID getNodeID() {
		return this.localNodeID;
	}
	
	
	// /////////////////////////////////////
	// // httpServer
	// /////////////////////////////////////

	public boolean tryStartServer(SXElement httpConfig) {
		if(this.httpServer!=null){
			if (null != httpConfig) {
				this.httpServer.configure(httpConfig);
				if (this.httpServer.tryStart()) {
					startNetManager: {
						int port = this.httpServer.getHttpPort();
						if (port < 0) {
							port = this.httpServer.getSslPort();
							if (port < 0) {
								break startNetManager;
							}
						}
						this.netManager.setPort(port, true);
						this.netManager.start();
					}
					return true;
				} else {
					System.exit(-1);
				}
			}
		}
		return false;
	}
}