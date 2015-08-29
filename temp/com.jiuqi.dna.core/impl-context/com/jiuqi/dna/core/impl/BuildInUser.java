package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.type.GUID;

/**
 * 内置用户，系统保留用户
 * 
 * @see com.jiuqi.dna.core.User
 * @author gaojingxin
 */
public abstract class BuildInUser implements User {

	private BuildInUser(final GUID identifier, final ActorState state) {
		this.identifier = identifier;
		this.state = state;
	}

	public final boolean isBuildInUser() {
		return true;
	}

	public final UserCacheHolder asUserCacheItem() {
		return null;
	}

	public final GUID getID() {
		return this.identifier;
	}

	public final ActorState getState() {
		return this.state;
	}

	abstract UserAccessController getAccessController();

	private final GUID identifier;

	private final ActorState state;

	/**
	 * 匿名用户
	 */
	public static final BuildInUser anonym = new BuildInUser(GUID.emptyID, ActorState.NORMAL) {

		public final String getName() {
			return User.USER_NAME_ANONYM;
		}

		public final String getTitle() {
			return "匿名";
		}

		public final String getDescription() {
			return "代表所有匿名用户的虚拟用户";
		}

		public final int getPriorityIndex() {
			return 0;
		}

		public final boolean validatePassword(final String password) {
			return false;
		}

		public final boolean validatePassword(final GUID password) {
			return false;
		}

		@Override
		final UserAccessController getAccessController() {
			return AccessControllerFactory.ANONYM_USER_ACCESSCONTROLLER;
		}

		@Deprecated
		public final int getAssignedRoleCount() {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final Role getAssignedRole(final int index) {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final int getMappingOrganizationCount() {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final GUID getMappingOrganizationID(final int index) {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		public String getLevel() {
			return "V0";
		}

	};

	/**
	 * 测试用户
	 */
	public static final BuildInUser debugger = new BuildInUser(GUID.valueOf(2, 0), Application.IN_DEBUG_MODE ? ActorState.NORMAL : ActorState.DISABLE) {

		public final String getName() {
			return User.USER_NAME_DEBUGGER;
		}

		public final String getTitle() {
			return "测试用户账号";
		}

		public final String getDescription() {
			return "测试状态下使用的用户账号";
		}

		public final int getPriorityIndex() {
			return 0;
		}

		public final boolean validatePassword(final String password) {
			return true;
		}

		public final boolean validatePassword(final GUID password) {
			return true;
		}

		@Override
		final UserAccessController getAccessController() {
			return AccessControllerFactory.DEBUGGER_USER_ACCESSCONTROLLER;
		}

		@Deprecated
		public final int getAssignedRoleCount() {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final Role getAssignedRole(final int index) {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final int getMappingOrganizationCount() {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final GUID getMappingOrganizationID(final int index) {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		public String getLevel() {
			return "V0";
		}

	};

	/**
	 * 系统用户
	 */
	public static final BuildInUser system = new BuildInUser(GUID.valueOf(1, 0), ActorState.NORMAL) {

		public final String getName() {
			return User.USER_NAME_SYSTEM;
		}

		public final String getTitle() {
			return "系统";
		}

		public final String getDescription() {
			return "代表系统行为的虚拟用户";
		}

		public final int getPriorityIndex() {
			return 0;
		}

		public final boolean validatePassword(final String password) {
			return false;
		}

		public final boolean validatePassword(final GUID password) {
			return false;
		}

		@Override
		final UserAccessController getAccessController() {
			return AccessControllerFactory.SYSTEM_USER_ACCESSCONTROLLER;
		}

		@Deprecated
		public final int getAssignedRoleCount() {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final Role getAssignedRole(final int index) {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final int getMappingOrganizationCount() {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		@Deprecated
		public final GUID getMappingOrganizationID(final int index) {
			throw new UnsupportedOperationException("已不支持该方法。");
		}

		public String getLevel() {
			return "V0";
		}

	};

}
