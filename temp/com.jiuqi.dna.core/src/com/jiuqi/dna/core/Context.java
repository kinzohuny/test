package com.jiuqi.dna.core;

import java.net.Proxy;
import java.net.URL;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.auth.RoleAuthorityChecker;
import com.jiuqi.dna.core.auth.UserAuthorityChecker;
import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.info.InfoReporter;
import com.jiuqi.dna.core.license.LicenseEntry;
import com.jiuqi.dna.core.resource.CategorialResourceQuerier;
import com.jiuqi.dna.core.resource.ResourceQuerier;
import com.jiuqi.dna.core.resource.ResourceStub;
import com.jiuqi.dna.core.service.ReliableRemoteServiceInvoker;
import com.jiuqi.dna.core.service.RemoteServiceInvoker;
import com.jiuqi.dna.core.service.ServiceInvoker;
import com.jiuqi.dna.core.type.GUID;

/**
 * ���������Ľӿڣ���������Դ��������ģ���������ģ�Ͷ�λ���ͽ������ýӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface Context extends ServiceInvoker, ResourceQuerier, DBAdapter,
		InfoReporter, Localizer {
	/**
	 * �����Ƿ���Ч
	 */
	public boolean isValid();

	/**
	 * ����Ƿ���Ч
	 */
	public void checkValid();

	/**
	 * ��������ĵ�����
	 */
	public ContextKind getKind();

	/**
	 * ��õ�ǰվ���״̬
	 */
	public SiteState getSiteState();

	/**
	 * ��������
	 */
	public <TObject> TObject newObject(Class<TObject> clazz,
			Object... aditionalArgs);

	/**
	 * ���վ���ΨһID����IDֻ��վ������ݿ��й�
	 */
	public GUID getSiteID();

	/**
	 * ���վ��ļ�ΨһID����IDֻ��վ������ݿ��й�
	 */
	public int getSiteSimpleID();

	/**
	 * �׳����쳣����<br>
	 * ��Ϊ�˱�֤�쳣ͨ������˻���Ҫ���������﷨<br>
	 * 
	 * <pre>
	 * public void hasException()throws XException{
	 *     ...
	 * }
	 * public void foo(Context context){//���ߴ������ط����context
	 *    try{
	 *        hasException();
	 *    }catch(XException e){ //���Լ򻯳ɣ�catch(Throwable e)
	 *        throw context.throwThrowable(e);
	 *    }
	 * }
	 * </pre>
	 * 
	 * @param throwable
	 *            ��Ҫ�׳����쳣����
	 */
	public RuntimeException throwThrowable(Throwable throwable);

	/**
	 * ����һ����ָ�������в�����Դ�Ĳ�ѯ����
	 * 
	 * @param category
	 *            ָ������Դ����
	 * @return ����һ����ָ�������в�����Դ�Ĳ�ѯ����
	 */
	public CategorialResourceQuerier usingResourceCategory(Object category);

	// ////////////�û�����½���//////////////////////
	/**
	 * ���������½
	 */
	public Login getLogin();

	/**
	 * �л���½�û�
	 * 
	 * @param user
	 *            ���л����û�
	 * @return �����л�ǰ�ľ��û�
	 */
	public User changeLoginUser(User user);

	public void setUserCurrentOrg(GUID orgID);

	public GUID getUserCurrentOrg();

	// //////////// ��Ȩ��Ϣ��� //////////////////
	/**
	 * �������Ʒ�����Ȩ��Ϣ
	 * 
	 * @param licenseEntryName
	 *            ��Ȩ��ȫ������Сд����
	 * @return �ҵ��򷵻���Ȩ����򷵻�null
	 */
	public LicenseEntry findLicenseEntry(String licenseEntryName);

	// ////////////Ȩ�����//////////////////////
	/**
	 * ����Ƿ��ĳ��Դӵ��ĳ��Ȩ��<br>
	 * �벻Ҫʹ�ø÷�������Ϊ������Ȩ�޵���Դ�<br>
	 * ��ʹ��getXXX(Operation<? super TFacade> operation,...)<br>
	 * ��findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            ����
	 * @param resource
	 *            ��Դ
	 * @return ������Ȩ����Ϣ
	 */
	public <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * ����Ƿ��ĳ��Դӵ��ĳ��Ȩ�ޣ�<br>
	 * �벻Ҫʹ�ø÷�������Ϊ������Ȩ�޵���Դ�<br>
	 * ��ʹ��getXXX(Operation<? super TFacade> operation,...)<br>
	 * ��findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            ����
	 * @param resource
	 *            ��Դ
	 * @return �����Ƿ�ӵ��Ȩ��
	 */
	public <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceStub<TFacade> resource);

	/**
	 * ���ڸ÷���ִ��Ч�ʽϵͣ�������Ƶ���������ø÷�����
	 */
	public <TFacade> Authority getAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * ���ڸ÷���ִ��Ч�ʽϵͣ�������Ƶ���������ø÷�����
	 */
	public <TFacade> boolean hasAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	public UserAuthorityChecker newUserAuthorityChecker(User user, GUID orgID,
			boolean operationAuthority);

	public RoleAuthorityChecker newRoleAuthorityChecker(Role role, GUID orgID,
			boolean operationAuthority);

	public UserAuthorityChecker newUserAuthorityChecker(GUID userID,
			GUID orgID, boolean operationAuthority);

	public RoleAuthorityChecker newRoleAuthorityChecker(GUID roleID,
			GUID orgID, boolean operationAuthority);

	// /////////////Զ�̵������/////////////////
	@Deprecated
	public RemoteServiceInvoker newRemoteServiceInvoker(URL url);

	@Deprecated
	public RemoteServiceInvoker newRemoteServiceInvoker(URL url, Proxy proxy);

	@Deprecated
	public RemoteServiceInvoker newRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5);

	@Deprecated
	public RemoteServiceInvoker newRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5, Proxy proxy);

	/**
	 * �½�һ����Ч��Զ�̵������������ڲ��𻷾��򵥣���������ڣ���Զ�̵���
	 * 
	 * @param url
	 * @return
	 */
	public RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url);

	/**
	 * �½�һ����Ч��Զ�̵������������ڲ��𻷾��򵥣���������ڣ���Զ�̵���
	 * 
	 * @param url
	 * @param proxy
	 * @return
	 */
	public RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			Proxy proxy);

	/**
	 * �½�һ����Ч��Զ�̵������������ڲ��𻷾��򵥣���������ڣ���Զ�̵���
	 * 
	 * @param url
	 * @param userName
	 * @param passwordMD5
	 * @return
	 */
	public RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5);

	/**
	 * �½�һ����Ч��Զ�̵������������ڲ��𻷾��򵥣���������ڣ���Զ�̵���
	 * 
	 * @param url
	 * @param userName
	 * @param passwordMD5
	 * @param proxy
	 * @return
	 */
	public RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5, Proxy proxy);

	/**
	 * �½�һ����Ӧ�������绷�����ɿ��Ըߵ�Զ�̵������� ͨ�����ص�Զ�̵�������ֻ�ܽ���ͬ����Զ�̵��ã����ù�����ֻ����һ��������Ӧ��
	 * 
	 * @param url
	 * @return
	 */
	public ReliableRemoteServiceInvoker newReliableRemoteServiceInvoker(URL url);

	/**
	 * �½�һ����Ӧ�������绷�����ɿ��Ըߵ�Զ�̵������� ͨ�����ص�Զ�̵�������ֻ�ܽ���ͬ����Զ�̵��ã����ù�����ֻ����һ��������Ӧ��
	 * 
	 * @param url
	 * @param proxy
	 * @return
	 */
	public ReliableRemoteServiceInvoker newReliableRemoteServiceInvoker(
			URL url, Proxy proxy);

	/**
	 * �½�һ����Ӧ�������绷�����ɿ��Ըߵ�Զ�̵������� ͨ�����ص�Զ�̵�������ֻ�ܽ���ͬ����Զ�̵��ã����ù�����ֻ����һ��������Ӧ��
	 * 
	 * @param url
	 * @param userName
	 * @param passwordMD5
	 * @return
	 */
	public ReliableRemoteServiceInvoker newReliableRemoteServiceInvoker(
			URL url, String userName, GUID passwordMD5);

	/**
	 * �½�һ����Ӧ�������绷�����ɿ��Ըߵ�Զ�̵������� ͨ�����ص�Զ�̵�������ֻ�ܽ���ͬ����Զ�̵��ã����ù�����ֻ����һ��������Ӧ��
	 * 
	 * @param url
	 * @param userName
	 * @param passwordMD5
	 * @param proxy
	 * @return
	 */
	public ReliableRemoteServiceInvoker newReliableRemoteServiceInvoker(
			URL url, String userName, GUID passwordMD5, Proxy proxy);

	// //////////////////Deprecated//////////////////////////
	/**
	 * ��ȡԶ��������½��Ϣ���Ѿ���������ʹ��newRemoteServiceInvoker����
	 * 
	 * @param host
	 *            Զ����������IP
	 * @param port
	 *            Զ�̶˿ں�
	 * @return ����Զ��������½��Ϣ
	 */
	@Deprecated
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port);

	/**
	 * ��ȡԶ�̵�½��Ϣ���Ѿ���������ʹ��newRemoteServiceInvoker����
	 * 
	 * @param host
	 *            Զ����������IP
	 * @param port
	 *            Զ�̶˿ں�
	 * @param user
	 *            ��½������
	 * @param password
	 *            ��½������
	 * @return ����Զ�̵�½��Ϣ
	 */
	@Deprecated
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password);

	/**
	 * ��ȡԶ�̵�½��Ϣ���Ѿ���������ʹ��newRemoteServiceInvoker����
	 * 
	 * @param host
	 *            Զ����������IP
	 * @param port
	 *            Զ�̶˿ں�
	 * @param user
	 *            ��½������
	 * @param password
	 *            ��½������
	 * @param life
	 *            ��½������������
	 * @return ����Զ�̵�½��Ϣ
	 */
	@Deprecated
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password, RemoteLoginLife life);

	/**
	 * ʹ��Զ�̷�����������Ѿ���������ʹ��newRemoteServiceInvoker����
	 * 
	 * @param remoteLoginInfo
	 *            Զ�̵�½��Ϣ
	 * @return ���ض�Ӧ��Զ�̷��������
	 */
	@Deprecated
	public ServiceInvoker usingRemoteInvoker(RemoteLoginInfo remoteLoginInfo);

}
