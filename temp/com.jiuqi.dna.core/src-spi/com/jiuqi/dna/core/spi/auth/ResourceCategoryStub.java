/**
 * 
 */
package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 资源类别（分组）快照，包括类别标识、标题
 * 
 * @author yangduanxue
 *
 */
public final class ResourceCategoryStub {

	private Object identity; //类别标识
	private String title; // 类别标题
	
	private GUID resCategoryId; // 权限资源类别id，acl表中resCategoryId字段值
	/**
	 * @param identity
	 * @param title
	 */
	public ResourceCategoryStub(Object identity, String title) {
		this.identity = identity;
		this.title = title;
	}
	public final Object getIdentity() {
		return identity;
	}
	public final String getTitle() {
		return title;
	}
	public GUID getResCategoryId() {
		return resCategoryId;
	}
	public void setResCategoryId(GUID resCategoryId) {
		this.resCategoryId = resCategoryId;
	}
	
}
