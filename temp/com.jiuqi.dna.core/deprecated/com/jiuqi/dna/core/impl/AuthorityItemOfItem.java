package com.jiuqi.dna.core.impl;

/**
 * 可授权资源项实现类
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
