package com.jiuqi.dna.core.spi.auth;

import java.util.HashMap;
import java.util.Map;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.impl.AuthorityEntry;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.type.GUID;

public abstract class AuthorityItem extends AuthorityEntry {
	
	public Map<Integer, Integer> authes = new HashMap<Integer, Integer>();// 发送设置权限事件需要的信息

	protected AuthorityItem(ResourceToken<?> resourceToken) {
		super(resourceToken);
	}

	/**
	 * 获取授权项对应的资源句柄，如果授项是资源类别项，则返回空
	 * 
	 * @return
	 */
	public final ResourceToken<?> getResourceToken() {
		return super.getAccessControlItem();
	}

	/**
	 * 获取资源项的操作列表
	 */
	@Override
	public final Operation<?>[] getOperations() {
		return super.getOperations();
	}

	/**
	 * 判断授权项是否已填充了权限信息
	 */
	@Override
	public final boolean filled() {
		return super.filled();
	}

	/**
	 * 判断被授权对象对当前资源的操作权限是否是继承得来
	 */
	@Override
	public final boolean isInherit(Operation<?> operation) {
		return super.isInherit(operation);
	}

	/**
	 * 设置被授权对象对当前资源的权限
	 */
	@Override
	public final void setAuthority(Operation<?> operation, Authority authority) {
		this.authes.put(operation.getMask(), authority.code);
		super.setAuthority(operation, authority);
	}

	@Override
	public final Authority getAuthority(Operation<?> operation) {
		return super.getAuthority(operation);
	}

	/**
	 * 判断被授权对象是否对当前资源有操作权限
	 */
	public final boolean hasAuthority(Operation<?> operation) {
		return super.hasOperationAuthority(operation);
	}

	/**
	 * 判断当前登录用户是否拥有对当前资源的授权权限
	 */
	public final boolean hasAuthAuthority(Operation<?> operation) {
		return super.hasAccreditAuthority(operation);
	}

	@Override
	public final GUID getGroupIdentifier() {
		return super.getGroupIdentifier();
	}

	@Override
	public final GUID getItemIdentifier() {
		return super.getItemIdentifier();
	}

	// @Override
	// public final List<OperationAuthorityInformation.Entry>
	// getAuthorityEntryList() {
	// return super.getAuthorityEntryList();
	// }

}
