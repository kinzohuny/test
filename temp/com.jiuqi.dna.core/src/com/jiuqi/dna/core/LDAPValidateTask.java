package com.jiuqi.dna.core;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * LDAP身份验证任务
 * 
 * @author LiuZhi
 */
public class LDAPValidateTask extends SimpleTask {

	/**
	 * 用户名
	 */
	public String user;

	/**
	 * 密码
	 */
	public String password;

	/**
	 * 指示是否已通过验证
	 */
	public boolean isVerified;

	/**
	 * 验证过程中发生的异常
	 */
	public Throwable exception;

}
