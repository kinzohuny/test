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
 * 调用上下文接口，整合了资源请求器，模块调用器，模型定位器和进度设置接口
 * 
 * @author gaojingxin
 * 
 */
public interface Context extends ServiceInvoker, ResourceQuerier, DBAdapter,
		InfoReporter, Localizer {
	/**
	 * 返回是否有效
	 */
	public boolean isValid();

	/**
	 * 检查是否有效
	 */
	public void checkValid();

	/**
	 * 获得上下文的类型
	 */
	public ContextKind getKind();

	/**
	 * 获得当前站点的状态
	 */
	public SiteState getSiteState();

	/**
	 * 创建对象
	 */
	public <TObject> TObject newObject(Class<TObject> clazz,
			Object... aditionalArgs);

	/**
	 * 获得站点的唯一ID，该ID只和站点的数据库有关
	 */
	public GUID getSiteID();

	/**
	 * 获得站点的简单唯一ID，该ID只和站点的数据库有关
	 */
	public int getSiteSimpleID();

	/**
	 * 抛出的异常对象<br>
	 * 但为了保证异常通道，因此还需要沿用如下语法<br>
	 * 
	 * <pre>
	 * public void hasException()throws XException{
	 *     ...
	 * }
	 * public void foo(Context context){//或者从其他地方获得context
	 *    try{
	 *        hasException();
	 *    }catch(XException e){ //可以简化成：catch(Throwable e)
	 *        throw context.throwThrowable(e);
	 *    }
	 * }
	 * </pre>
	 * 
	 * @param throwable
	 *            需要抛出的异常对象
	 */
	public RuntimeException throwThrowable(Throwable throwable);

	/**
	 * 返回一个在指定分类中查找资源的查询对象
	 * 
	 * @param category
	 *            指定的资源分类
	 * @return 返回一个在指定分类中查找资源的查询对象
	 */
	public CategorialResourceQuerier usingResourceCategory(Object category);

	// ////////////用户及登陆相关//////////////////////
	/**
	 * 获得所属登陆
	 */
	public Login getLogin();

	/**
	 * 切换登陆用户
	 * 
	 * @param user
	 *            欲切换的用户
	 * @return 返回切换前的旧用户
	 */
	public User changeLoginUser(User user);

	public void setUserCurrentOrg(GUID orgID);

	public GUID getUserCurrentOrg();

	// //////////// 授权信息相关 //////////////////
	/**
	 * 根据名称返回授权信息
	 * 
	 * @param licenseEntryName
	 *            授权项全名，大小写敏感
	 * @return 找到则返回授权项，否则返回null
	 */
	public LicenseEntry findLicenseEntry(String licenseEntryName);

	// ////////////权限相关//////////////////////
	/**
	 * 检查是否对某资源拥有某类权限<br>
	 * 请不要使用该方法用作为过滤有权限的资源项，<br>
	 * 请使用getXXX(Operation<? super TFacade> operation,...)<br>
	 * 或findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            操作
	 * @param resource
	 *            资源
	 * @return 返回是权限信息
	 */
	public <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * 检查是否对某资源拥有某类权限，<br>
	 * 请不要使用该方法用作为过滤有权限的资源项，<br>
	 * 请使用getXXX(Operation<? super TFacade> operation,...)<br>
	 * 或findXXX(Operation<? super TFacade> operation,...)<br>
	 * 
	 * @param operation
	 *            操作
	 * @param resource
	 *            资源
	 * @return 返回是否拥有权限
	 */
	public <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceStub<TFacade> resource);

	/**
	 * 由于该方法执行效率较低，不建设频繁大量调用该方法。
	 */
	public <TFacade> Authority getAccreditAuthority(
			Operation<? super TFacade> operation, ResourceStub<TFacade> resource);

	/**
	 * 由于该方法执行效率较低，不建设频繁大量调用该方法。
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

	// /////////////远程调用相关/////////////////
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
	 * 新建一个高效的远程调用器，适用于部署环境简单（如局域网内）的远程调用
	 * 
	 * @param url
	 * @return
	 */
	public RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url);

	/**
	 * 新建一个高效的远程调用器，适用于部署环境简单（如局域网内）的远程调用
	 * 
	 * @param url
	 * @param proxy
	 * @return
	 */
	public RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			Proxy proxy);

	/**
	 * 新建一个高效的远程调用器，适用于部署环境简单（如局域网内）的远程调用
	 * 
	 * @param url
	 * @param userName
	 * @param passwordMD5
	 * @return
	 */
	public RemoteServiceInvoker newEfficientRemoteServiceInvoker(URL url,
			String userName, GUID passwordMD5);

	/**
	 * 新建一个高效的远程调用器，适用于部署环境简单（如局域网内）的远程调用
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
	 * 新建一个适应复杂网络环境，可靠性高的远程调用器。 通过返回的远程调用器，只能进行同步的远程调用，调用过程中只包含一次请求与应答。
	 * 
	 * @param url
	 * @return
	 */
	public ReliableRemoteServiceInvoker newReliableRemoteServiceInvoker(URL url);

	/**
	 * 新建一个适应复杂网络环境，可靠性高的远程调用器。 通过返回的远程调用器，只能进行同步的远程调用，调用过程中只包含一次请求与应答。
	 * 
	 * @param url
	 * @param proxy
	 * @return
	 */
	public ReliableRemoteServiceInvoker newReliableRemoteServiceInvoker(
			URL url, Proxy proxy);

	/**
	 * 新建一个适应复杂网络环境，可靠性高的远程调用器。 通过返回的远程调用器，只能进行同步的远程调用，调用过程中只包含一次请求与应答。
	 * 
	 * @param url
	 * @param userName
	 * @param passwordMD5
	 * @return
	 */
	public ReliableRemoteServiceInvoker newReliableRemoteServiceInvoker(
			URL url, String userName, GUID passwordMD5);

	/**
	 * 新建一个适应复杂网络环境，可靠性高的远程调用器。 通过返回的远程调用器，只能进行同步的远程调用，调用过程中只包含一次请求与应答。
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
	 * 获取远程匿名登陆信息，已经废弃，请使用newRemoteServiceInvoker方法
	 * 
	 * @param host
	 *            远程主机名或IP
	 * @param port
	 *            远程端口号
	 * @return 返回远程匿名登陆信息
	 */
	@Deprecated
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port);

	/**
	 * 获取远程登陆信息，已经废弃，请使用newRemoteServiceInvoker方法
	 * 
	 * @param host
	 *            远程主机名或IP
	 * @param port
	 *            远程端口号
	 * @param user
	 *            登陆用名称
	 * @param password
	 *            登陆用密码
	 * @return 返回远程登陆信息
	 */
	@Deprecated
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password);

	/**
	 * 获取远程登陆信息，已经废弃，请使用newRemoteServiceInvoker方法
	 * 
	 * @param host
	 *            远程主机名或IP
	 * @param port
	 *            远程端口号
	 * @param user
	 *            登陆用名称
	 * @param password
	 *            登陆用密码
	 * @param life
	 *            登陆生命周期设置
	 * @return 返回远程登陆信息
	 */
	@Deprecated
	public RemoteLoginInfo allocRemoteLoginInfo(String host, int port,
			String user, String password, RemoteLoginLife life);

	/**
	 * 使用远程服务调用器，已经废弃，请使用newRemoteServiceInvoker方法
	 * 
	 * @param remoteLoginInfo
	 *            远程登陆信息
	 * @return 返回对应的远程服务调用器
	 */
	@Deprecated
	public ServiceInvoker usingRemoteInvoker(RemoteLoginInfo remoteLoginInfo);

}
