package com.jiuqi.dna.core.system;

import com.jiuqi.dna.core.impl.SystemPrivilegeImpl;
import com.jiuqi.dna.core.type.GUID;

/**
 * 系统权限
 * 
 * @author houchunlei
 * 
 */
public interface SystemPrivilege {

	public static final SystemPrivilege DB_BACKUP = SystemPrivilegeImpl.DB_BACKUP;

	public GUID getKey();

	public String getTitle();
}