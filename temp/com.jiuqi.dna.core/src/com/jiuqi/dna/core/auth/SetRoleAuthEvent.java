/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;

import com.jiuqi.dna.core.auth.SetUserAuthEvent.AuthItem;
import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.type.GUID;

/**
 * 设置角色权限事件
 * 
 * @author yangduanxue
 *
 */
public final class SetRoleAuthEvent extends Event {

	private GUID roleId;// 角色id
	private boolean operationAuth;// 是否操作权限
	private List<AuthItem> aies;// 权限资源条目
	/**
	 * @param roleId
	 * @param operationAuth
	 * @param aies
	 */
	public SetRoleAuthEvent(GUID roleId, boolean operationAuth,
			List<AuthItem> aies) {
		super();
		this.roleId = roleId;
		this.operationAuth = operationAuth;
		this.aies = aies;
	}
	/**
	 * @return the roleId
	 */
	public final GUID getRoleId() {
		return roleId;
	}
	/**
	 * @param roleId the roleId to set
	 */
	public final void setRoleId(GUID roleId) {
		this.roleId = roleId;
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
	
	
}
