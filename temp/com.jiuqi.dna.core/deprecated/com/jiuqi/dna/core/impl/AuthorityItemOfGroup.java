package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.AuthorizedResourceCategoryItem;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.type.GUID;

/**
 * 可授权资源类别项实现类<br>
 * 可授权资源类别项是一个特殊的可授权资源项，是系统为每类可授权资源定义的虚拟的根资源项。
 * 
 * @see com.jiuqi.dna.core.auth.AuthorizedResourceCategoryItem
 * @see com.jiuqi.dna.core.impl.AuthorityItemOfItem
 * @author LiuZhi 2009-12
 */
@Deprecated
final class AuthorityItemOfGroup extends AuthorityItem implements
		AuthorizedResourceCategoryItem {

	public final GUID getResourceCategoryID() {
		return this.itemGUID;
	}

	public final Operation<?>[] getResourceOperations() {
		return this.operations;
	}

	AuthorityItemOfGroup(CacheGroup<?, ?, ?> group, int authCode) {
		super(group.longIdentifier, group.accessControlInformation.ACGUIDIdentifier, group.define.title, authCode);
		this.operations = group.define.accessControlDefine.operationEntrys;
	}

	final Operation<?>[] operations;

}
