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
 * Ӧ�ö���ӿ�
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
	 * ��ȡָ��ID�ĻỰ
	 * 
	 * @param sessionID
	 *            �ỰID
	 * @throws SessionDisposedException
	 *             ָ��ID�ĻỰ������ʱ
	 */
	public Session getSession(long sessionID) throws SessionDisposedException;

	/**
	 * ��ȡϵͳ�Ự
	 */
	public Session getSystemSession();

	/**
	 * �����ͨ�Ự����������Ự��
	 */
	public int getNormalSessionCount();

	/**
	 * �����ͨ�Ự����������Ự��
	 * 
	 * @param excludeBuildInUser
	 *            �Ƿ��޳��ڽ��û����Ƿ��������¼��ĻỰ��
	 */
	public int getNormalSessionCount(boolean excludeBuildInUser);

	/**
	 * ���������ͨ�Ự�б�
	 */
	public List<? extends Session> getNormalSessions();

	/**
	 * ���������ͨ�Ự�б�
	 * 
	 * @param byUserID
	 *            ����UserID���ˣ�����null��GUID.emptyID��ʾ������
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
	 * ���DNA�ĸ�Ŀ¼
	 */
	public File getDNARoot();

	/**
	 * ���DNA-Server.xml�е�������
	 */
	public SXElement getDNAConfig(String name);

	/**
	 * ���DNA-Server.xml�е�������
	 */
	public SXElement getDNAConfig(String name1, String name2);

	/**
	 * ���DNA-Server.xml�е�������
	 */
	public SXElement getDNAConfig(String name1, String name2, String... names);

	/**
	 * ��ȡӦ�ó�������ʱ���(���� )
	 * 
	 * @return
	 */
	public long getBornTime();

	/**
	 * ��ȡĬ��վ���״̬
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