/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;
import java.util.Map;

import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.type.GUID;

/**
 * 设置用户/身份权限事件
 * 
 * @author yangduanxue
 *
 */
public final class SetUserAuthEvent extends Event {

	private GUID userId;// 用户id
	private GUID identityId;// 身份id
	private boolean operationAuth;// 是否操作权限
	private List<AuthItem> aies;// 权限资源条目
	
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
		private GUID id; // 权限资源id
		private String className; // 权限资源外观类名
		private String category; //资源分组
		private Map<Integer, Integer> authes;// 权限资源各操作授权情况，第一个参数是操作的掩码，第二个参数：0——未定义，1——允许，2——拒绝
		
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
