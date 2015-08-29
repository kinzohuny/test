package com.jiuqi.dna.core.internal.db.datasource;

import com.jiuqi.dna.core.impl.NamedDefineImpl;
import com.jiuqi.dna.core.misc.Obfuscater;
import com.jiuqi.dna.core.misc.SXElement;

abstract class ComboDataSourceConf extends NamedDefineImpl {

	public final int getMaxConnectionCount() {
		return this.maxConnCount;
	}

	public final int getMinConnectionCount() {
		return this.minConnCount;
	}

	ComboDataSourceConf(SXElement element) {
		super(element.getString(xml_attr_name));
		this.location = element.getString(XML_ATTR_LOCATION);
		if (this.location == null || this.location.length() == 0) {
			throw new IllegalArgumentException("���ݿ������ַ���Ϊ�ա�");
		}
		this.user = element.getString(XML_ATTR_USER);
		String pw = element.getString(XML_ATTR_PASSWORD);
		this.password = pw != null ? Obfuscater.unobfuscate(pw) : null;
		this.maxConnCount = element.getInt(XML_ATTR_MAX_CONNECTIONS, DEFAULT_MAX_CONNECTIONS);
		this.minConnCount = element.getInt(XML_ATTR_MIN_CONNECTIONS, DEFAULT_MIN_CONNECTIONS);
		this.loginTimeoutS = element.getInt(XML_ATTR_LOGIN_TIMEOUT, DEFAULT_LOGIN_TIMEOUT);
		this.commandTimeoutS = element.getInt(XML_ATTR_COMMAND_TIMEOUT, DEFAULT_COMMAND_TIMEOUT);
		this.idleLifeMS = element.getInt(XML_ATTR_IDLE_LIFE, DEFAULT_IDLE_LIFE);
		this.trustLifeMS = element.getInt(XML_ATTR_TRUST_LIFE, DEFAULT_TRUST_LIFE);
	}

	/**
	 * �����ַ���
	 */
	final String location;

	/**
	 * �û�
	 */
	final String user;

	/**
	 * ����
	 */
	final String password;

	/**
	 * ���ӳ����������
	 */
	final int maxConnCount;

	/**
	 * ���ӳ���С�������
	 */
	final int minConnCount;

	/**
	 * ���ӳ�ʱʱ��
	 */
	final int loginTimeoutS;

	/**
	 * ���ʱʱ��
	 */
	final int commandTimeoutS;

	/**
	 * �������ӵı�����
	 */
	final long idleLifeMS;

	/**
	 * ���ε���Ч���ӱ�����
	 */
	final long trustLifeMS;

	static final int DEFAULT_MAX_CONNECTIONS = 20;
	static final int DEFAULT_MIN_CONNECTIONS = 5;

	static final int DEFAULT_LOGIN_TIMEOUT = 20;// 20��
	static final int DEFAULT_COMMAND_TIMEOUT = 0;// ��, ������
	static final int DEFAULT_IDLE_LIFE = 60 * 2 * 1000;// 2����
	static final int DEFAULT_TRUST_LIFE = 60 * 1 * 1000;// 1����

	static final String XML_ATTR_LOCATION = "location";
	static final String XML_ATTR_MAX_CONNECTIONS = "max-connections";
	static final String XML_ATTR_MIN_CONNECTIONS = "min-connections";
	static final String XML_ATTR_LOGIN_TIMEOUT = "login-timeout-s";
	static final String XML_ATTR_COMMAND_TIMEOUT = "command-timeout-s";
	static final String XML_ATTR_IDLE_LIFE = "idle-life-ms";
	static final String XML_ATTR_TRUST_LIFE = "trust-life-ms";
	static final String XML_ATTR_USER = "user";
	static final String XML_ATTR_PASSWORD = "password";

	@Override
	public void render(SXElement e) {
		super.render(e);
		e.setAttribute(XML_ATTR_LOCATION, this.location);
		e.setInt(XML_ATTR_MAX_CONNECTIONS, this.maxConnCount);
		e.setInt(XML_ATTR_MIN_CONNECTIONS, this.minConnCount);
		e.setLong(XML_ATTR_IDLE_LIFE, this.idleLifeMS);
		e.setInt(XML_ATTR_LOGIN_TIMEOUT, this.loginTimeoutS);
		e.setInt(XML_ATTR_COMMAND_TIMEOUT, this.commandTimeoutS);
		e.setAttribute(XML_ATTR_USER, this.user);
		if (this.password != null) {
			e.setAttribute(XML_ATTR_PASSWORD, Obfuscater.obfuscate(this.password));
		}
	}
}