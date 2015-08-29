package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.LDAPConfiguration;

final class LDAPConfigurationImplement implements LDAPConfiguration {
	// LDAP�Ƿ����
	private boolean enable;
	// ֧��LDAP������б�
	private List<String> entryNameList;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public List<String> getEntryNameList() {
		return entryNameList;
	}

	public void setEntryNameList(List<String> entryNameList) {
		this.entryNameList = entryNameList;
	}

}
