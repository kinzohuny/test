package com.jiuqi.dna.core;

import java.util.List;

public interface LDAPConfiguration {
	/**
	 * 
	 * LDAP���ã�true���ã�false������.<br>
	 *
	 * @return  boolean
	 */
	public boolean isEnable();
	/**
	 * 
	 * ���֧��LDAP������б�.<br>
	 *
	 * @return  List<String>
	 */
	public List<String> getEntryNameList();

}
