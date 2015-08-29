package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.LDAPConfiguration;

final class LDAPConfigurationImplement implements LDAPConfiguration {
	// LDAP是否可用
	private boolean enable;
	// 支持LDAP的入口列表
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
