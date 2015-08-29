package com.jiuqi.dna.core;

import java.util.Locale;

import com.jiuqi.dna.core.type.GUID;

/**
 * 登陆对象，管理用户信息和权限
 * 
 * @author gaojingxin
 * 
 */
public interface Login {
	/**
	 * 获得登陆的状态
	 * 
	 * @return
	 */
	public LoginState getState();

	/**
	 * 获得登陆ID
	 * 
	 * @return
	 */
	public long getID();

	/**
	 * 获得登陆对应的用户对象，用户验证后改对象为目标用户，验证前为匿名用户
	 */
	public User getUser();

	/**
	 * 获得客户端或浏览器端的信息
	 */
	public RemoteInfo getRemoteInfo();

	/**
	 * 获取会话的方位
	 */
	public Locale getLocale();

	/**
	 * 获取会话类型
	 */
	public SessionKind getKind();

	/**
	 * 权限相关，获取会话用户当前映射的组织机构ID
	 * 
	 * @return 返回会话用户当前映射的组织机构ID
	 */
	public GUID getUserCurrentOrg();

	/**
	 * 权限相关，设置用户当前的组织机构映射
	 * 
	 * @param context
	 *            当前上下文，不能为空
	 * @param orgID
	 *            组织机构ID，不能为空
	 */
	public void setUserCurrentOrg(GUID orgID);
}