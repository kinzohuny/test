package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.spi.application.Application;
import com.jiuqi.dna.core.type.GUID;

/**
 * �����û���ϵͳ�����û�
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
	 * �����û�
	 */
	public static final BuildInUser anonym = new BuildInUser(GUID.emptyID, ActorState.NORMAL) {

		public final String getName() {
			return User.USER_NAME_ANONYM;
		}

		public final String getTitle() {
			return "����";
		}

		public final String getDescription() {
			return "�������������û��������û�";
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
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final Role getAssignedRole(final int index) {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final int getMappingOrganizationCount() {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final GUID getMappingOrganizationID(final int index) {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		public String getLevel() {
			return "V0";
		}

	};

	/**
	 * �����û�
	 */
	public static final BuildInUser debugger = new BuildInUser(GUID.valueOf(2, 0), Application.IN_DEBUG_MODE ? ActorState.NORMAL : ActorState.DISABLE) {

		public final String getName() {
			return User.USER_NAME_DEBUGGER;
		}

		public final String getTitle() {
			return "�����û��˺�";
		}

		public final String getDescription() {
			return "����״̬��ʹ�õ��û��˺�";
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
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final Role getAssignedRole(final int index) {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final int getMappingOrganizationCount() {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final GUID getMappingOrganizationID(final int index) {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		public String getLevel() {
			return "V0";
		}

	};

	/**
	 * ϵͳ�û�
	 */
	public static final BuildInUser system = new BuildInUser(GUID.valueOf(1, 0), ActorState.NORMAL) {

		public final String getName() {
			return User.USER_NAME_SYSTEM;
		}

		public final String getTitle() {
			return "ϵͳ";
		}

		public final String getDescription() {
			return "����ϵͳ��Ϊ�������û�";
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
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final Role getAssignedRole(final int index) {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final int getMappingOrganizationCount() {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		@Deprecated
		public final GUID getMappingOrganizationID(final int index) {
			throw new UnsupportedOperationException("�Ѳ�֧�ָ÷�����");
		}

		public String getLevel() {
			return "V0";
		}

	};

}
