package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

final class AccessControlCompatible {

	static final UserAccessController newUserAccessController(
			final AccessControlPolicy policy, final UserCacheHolder userHolder,
			final GUID identifyIdentifier, final boolean operationAuthority,
			final ContextImpl<?, ?, ?> context) {
		if (AccessControlConstants.isDefaultACVersion(identifyIdentifier)) {
			return policy.newUserAccessController(userHolder, operationAuthority, context.transaction);
		} else {
			IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, userHolder.identifier, identifyIdentifier));
			if (identifyHolder != null) {
				return policy.newIdentifyAccessController(userHolder.getFacade(), identifyHolder, operationAuthority, context.transaction);
			} else {
				// FIXME 返回用户本身的，还是抛异常？
				return policy.newUserAccessController(userHolder, operationAuthority, context.transaction);
			}
		}
	}

	private AccessControlCompatible() {
		// to do nothing
	}

}
