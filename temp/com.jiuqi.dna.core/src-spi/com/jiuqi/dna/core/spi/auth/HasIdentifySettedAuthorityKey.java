package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

public final class HasIdentifySettedAuthorityKey {

	public HasIdentifySettedAuthorityKey(final GUID userIdentifier,
			final GUID identifyIdentifier, final boolean operationAuthority) {
		if (userIdentifier == null) {
			throw new IllegalArgumentException("参数[userIdentifier]不能为null。");
		}
		this.userIdentifier = userIdentifier;
		this.identifyIdentifier = identifyIdentifier;
		this.operationAuthority = operationAuthority;
	}

	public final GUID userIdentifier;

	public final GUID identifyIdentifier;

	public final boolean operationAuthority;

}
