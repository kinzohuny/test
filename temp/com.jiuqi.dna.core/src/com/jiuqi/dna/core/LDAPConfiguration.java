package com.jiuqi.dna.core;

import java.util.List;

public interface LDAPConfiguration {
	/**
	 * 
	 * LDAP可用，true可用，false不可用.<br>
	 *
	 * @return  boolean
	 */
	public boolean isEnable();
	/**
	 * 
	 * 获得支持LDAP的入口列表.<br>
	 *
	 * @return  List<String>
	 */
	public List<String> getEntryNameList();

}
