package com.jiuqi.dna.core.impl;

/**
 * ����Ȩ��Դ��ʵ����
 * 
 * @see com.jiuqi.dna.core.auth.AuthorizedResourceItem
 * @author LiuZhi 2009-12
 */
@Deprecated
final class AuthorityItemOfItem extends AuthorityItem {

	AuthorityItemOfItem(final AccessControlCacheHolder<?, ?, ?> item,
			final int authCode, final Transaction transaction) {
		super(item.ACLongIdentifier, item.ACGUIDIdentifier, item.getAccessControlTitle(), authCode);
	}
}
