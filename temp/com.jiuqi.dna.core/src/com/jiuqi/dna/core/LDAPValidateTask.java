package com.jiuqi.dna.core;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * LDAP�����֤����
 * 
 * @author LiuZhi
 */
public class LDAPValidateTask extends SimpleTask {

	/**
	 * �û���
	 */
	public String user;

	/**
	 * ����
	 */
	public String password;

	/**
	 * ָʾ�Ƿ���ͨ����֤
	 */
	public boolean isVerified;

	/**
	 * ��֤�����з������쳣
	 */
	public Throwable exception;

}
