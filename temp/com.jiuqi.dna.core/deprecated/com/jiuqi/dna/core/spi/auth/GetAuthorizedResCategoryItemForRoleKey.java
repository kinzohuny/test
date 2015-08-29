package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * 为角色授权时，获取所有可授权资源类别项的键<br>
 * 返回所有可授权资源类别项。
 * 
 * <pre>
 * 使用示例：
 * key = new GetAuthorizedResCategoryItemForRoleKey(roleID, orgID);
 * context.getList(AuthorityResourceCategoryItem.class, key);
 * </pre>
 * 
 * @see com.jiuqi.dna.core.spi.auth.GetAuthorizedResCategoryItemForActorKey
 * @author LiuZhi 2009-11
 */
@Deprecated
public final class GetAuthorizedResCategoryItemForRoleKey extends
		GetAuthorizedResCategoryItemForActorKey {

	/**
	 * 新建获取所有可授权资源类别项的键
	 * 
	 * @param roleID
	 *            角色ID，不能为空
	 * @param orgID
	 *            组织机构ID，可为空，为空代表默认关联的组织机构ID
	 */
	public GetAuthorizedResCategoryItemForRoleKey(GUID roleID, GUID orgID) {
		super(roleID, orgID);
	}

}
