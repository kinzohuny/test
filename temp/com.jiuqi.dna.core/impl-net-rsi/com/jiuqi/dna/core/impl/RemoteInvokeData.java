/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

abstract class RemoteInvokeData {
	/**
	 * �û�����v3.0.1
	 */
	final static int PROP_USERNAME = 1;
	/**
	 * �����MD5ֵ��v3.0.1
	 */
	final static int PROP_PASSWORDMD5 = 2;
	/**
	 * ָʾԶ��TaskHandler����Ҫ����task�������Ż�����Ҫ�����л������紫�䣬valueӦ����Boolean����
	 */
	final static int PROP_NORETURN = 3;

	public Object setProp(int key, Object value) {
		throw new UnsupportedOperationException();
	}

	public Object getProp(int key) {
		return null;
	}

	void invoke(ContextImpl<?, ?, ?> context) throws Throwable {
		final String username = (String) this.getProp(PROP_USERNAME);
		if (username != null && username.length() > 0 && !username.equals(context.session.getUser().getName())) {
			context.remoteLogin(username, (GUID) this.getProp(PROP_PASSWORDMD5));
		}
	}

	abstract int getTransactionID();
}