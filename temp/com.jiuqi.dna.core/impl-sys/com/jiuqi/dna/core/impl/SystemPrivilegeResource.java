package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.system.SystemPrivilege;
import com.jiuqi.dna.core.system.SystemPrivilegeOperation;
import com.jiuqi.dna.core.type.GUID;

/**
 * 系统权限资源
 * 
 * @author houchunlei
 * 
 */
final class SystemPrivilegeResource
		extends
		ResourceServiceBase<SystemPrivilege, SystemPrivilegeImpl, SystemPrivilegeImpl> {

	protected SystemPrivilegeResource() {
		super("系统权限", ResourceKind.SINGLETON_IN_SITE);
	}

	@Override
	protected void initResources(
			Context context,
			ResourceInserter<SystemPrivilege, SystemPrivilegeImpl, SystemPrivilegeImpl> initializer)
			throws Throwable {
		initializer.putResource(SystemPrivilegeImpl.DB_BACKUP);
	}

	final class SystemPrivilegeProvider extends
			AuthorizableResourceProvider<SystemPrivilegeOperation> {

		protected SystemPrivilegeProvider() {
			super(null, false);
		}

		@Override
		protected final String getResourceTitle(SystemPrivilegeImpl resource,
				SystemPrivilegeImpl keys) {
			return resource.title;
		}

		@Override
		protected final GUID getKey1(SystemPrivilegeImpl keys) {
			return keys.key;
		}

	}

}
