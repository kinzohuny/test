package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.system.SystemPrivilege;
import com.jiuqi.dna.core.type.GUID;

/**
 * 系统权限
 * 
 * @author houchunlei
 * 
 */
@StructClass
public final class SystemPrivilegeImpl implements SystemPrivilege {

	private static final String MESSAGE_DB_BACKUP = "database_backup";

	public static final SystemPrivilegeImpl DB_BACKUP = new SystemPrivilegeImpl(GUID.MD5Of(MESSAGE_DB_BACKUP), "数据库备份");

	final GUID key;

	final String title;

	private SystemPrivilegeImpl(GUID key, String title) {
		this.key = key;
		this.title = title;
	}

	public final GUID getKey() {
		return this.key;
	}

	public final String getTitle() {
		return this.title;
	}
}
