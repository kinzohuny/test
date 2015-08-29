/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;
import java.util.Map;

import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.type.GUID;

/**
 * �����û�/���Ȩ���¼�
 * 
 * @author yangduanxue
 *
 */
public final class SetUserAuthEvent extends Event {

	private GUID userId;// �û�id
	private GUID identityId;// ���id
	private boolean operationAuth;// �Ƿ����Ȩ��
	private List<AuthItem> aies;// Ȩ����Դ��Ŀ
	
	/**
	 * @param userId
	 * @param identityId
	 * @param operationAuth
	 * @param aies
	 */
	public SetUserAuthEvent(GUID userId, GUID identityId,
			boolean operationAuth, List<AuthItem> aies) {
		super();
		this.userId = userId;
		this.identityId = identityId;
		this.operationAuth = operationAuth;
		this.aies = aies;
	}

	/**
	 * @return the userId
	 */
	public final GUID getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public final void setUserId(GUID userId) {
		this.userId = userId;
	}

	/**
	 * @return the operationAuth
	 */
	public final boolean isOperationAuth() {
		return operationAuth;
	}

	/**
	 * @param operationAuth the operationAuth to set
	 */
	public final void setOperationAuth(boolean operationAuth) {
		this.operationAuth = operationAuth;
	}

	/**
	 * @return the aies
	 */
	public final List<AuthItem> getAies() {
		return aies;
	}

	/**
	 * @param aies the aies to set
	 */
	public final void setAies(List<AuthItem> aies) {
		this.aies = aies;
	}
	 
	/**
	 * @return the identityId
	 */
	public final GUID getIdentityId() {
		return identityId;
	}

	/**
	 * @param identityId the identityId to set
	 */
	public final void setIdentityId(GUID identityId) {
		this.identityId = identityId;
	}

	public static final class AuthItem {
		private GUID id; // Ȩ����Դid
		private String className; // Ȩ����Դ�������
		private String category; //��Դ����
		private Map<Integer, Integer> authes;// Ȩ����Դ��������Ȩ�������һ�������ǲ��������룬�ڶ���������0����δ���壬1��������2�����ܾ�
		
		/**
		 * @param id
		 * @param className
		 * @param category
		 * @param authes
		 */
		public AuthItem(GUID id, String className, String category,
				Map<Integer, Integer> authes) {
			super();
			this.id = id;
			this.className = className;
			this.category = category;
			this.authes = authes;
		}

		/**
		 * @return the id
		 */
		public final GUID getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public final void setId(GUID id) {
			this.id = id;
		}
		/**
		 * @return the className
		 */
		public final String getClassName() {
			return className;
		}
		/**
		 * @param className the className to set
		 */
		public final void setClassName(String className) {
			this.className = className;
		}
		/**
		 * @return the authes
		 */
		public final Map<Integer, Integer> getAuthes() {
			return authes;
		}
		/**
		 * @param authes the authes to set
		 */
		public final void setAuthes(Map<Integer, Integer> authes) {
			this.authes = authes;
		}
		/**
		 * @return the category
		 */
		public final String getCategory() {
			return category;
		}
		/**
		 * @param category the category to set
		 */
		public final void setCategory(String category) {
			this.category = category;
		}
		
		
	}
}
