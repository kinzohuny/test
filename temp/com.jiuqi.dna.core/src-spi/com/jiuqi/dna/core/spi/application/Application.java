package com.jiuqi.dna.core.spi.application;

import java.io.File;
import java.sql.Driver;
import java.util.List;

import com.jiuqi.dna.core.SiteState;
import com.jiuqi.dna.core.exception.SessionDisposedException;
import com.jiuqi.dna.core.impl.LoginController;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.spi.work.WorkingManager;
import com.jiuqi.dna.core.type.GUID;

/**
 * 应用对象接口
 * 
 * @author gaojingxin
 * 
 */
public interface Application {

	public final static boolean IN_DEBUG_MODE = Boolean.getBoolean("com.jiuqi.dna.debug");

	public final static boolean DEBUG_LAUNCH = Boolean.getBoolean("com.jiuqi.dna.debug.launch");

	public <TUserData> Session newSession(
			SessionIniter<TUserData> sessionIniter, TUserData userData);

	/**
	 * 获取指定ID的会话
	 * 
	 * @param sessionID
	 *            会话ID
	 * @throws SessionDisposedException
	 *             指定ID的会话不存在时
	 */
	public Session getSession(long sessionID) throws SessionDisposedException;

	/**
	 * 获取系统会话
	 */
	public Session getSystemSession();

	/**
	 * 获得普通会话个数（界面会话）
	 */
	public int getNormalSessionCount();

	/**
	 * 获得普通会话个数（界面会话）
	 * 
	 * @param excludeBuildInUser
	 *            是否剔除内建用户（是否仅包括登录后的会话）
	 */
	public int getNormalSessionCount(boolean excludeBuildInUser);

	/**
	 * 获得所有普通会话列表
	 */
	public List<? extends Session> getNormalSessions();

	/**
	 * 获得所有普通会话列表
	 * 
	 * @param byUserID
	 *            根据UserID过滤，传入null或GUID.emptyID表示不过滤
	 */
	public List<? extends Session> getNormalSessions(GUID byUserID);

	@Deprecated
	public long getDBTimeused();

	@Deprecated
	public long getHTTPRequestBytes();

	@Deprecated
	public long getHTTPResponseBytes();

	@Deprecated
	public long getHTTPRequestTicks();

	@Deprecated
	public long getHTTPRequestTotalProcessTime();

	/**
	 * 获得DNA的根目录
	 */
	public File getDNARoot();

	/**
	 * 获得DNA-Server.xml中的配置项
	 */
	public SXElement getDNAConfig(String name);

	/**
	 * 获得DNA-Server.xml中的配置项
	 */
	public SXElement getDNAConfig(String name1, String name2);

	/**
	 * 获得DNA-Server.xml中的配置项
	 */
	public SXElement getDNAConfig(String name1, String name2, String... names);

	/**
	 * 获取应用程序启动时间戳(毫秒 )
	 * 
	 * @return
	 */
	public long getBornTime();

	/**
	 * 获取默认站点的状态
	 * 
	 * @return
	 */
	public SiteState getDefaultSiteState();

	public static final String servlet_context_attr_application = "dna-application";
	public static final String ARGUMENT_HTTP_SERVER = "http-server";

	public Iterable<Driver> getJdbcDrivers();

	public ExceptionCatcher getExceptionCatcher();
	public Class<?> loadClass(String className) throws ClassNotFoundException ;
	public File getDNAWork();
	public LoginController getLoginController();
	public WorkingManager getWorkingManager();
	public boolean isMultiNodes();
	public int getIndexinCluster();	public GUID getNodeID();}