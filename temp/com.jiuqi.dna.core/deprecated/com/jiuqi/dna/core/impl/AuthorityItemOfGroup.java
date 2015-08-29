package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.AuthorizedResourceCategoryItem;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.type.GUID;

/**
 * ����Ȩ��Դ�����ʵ����<br>
 * ����Ȩ��Դ�������һ������Ŀ���Ȩ��Դ���ϵͳΪÿ�����Ȩ��Դ���������ĸ���Դ�
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
