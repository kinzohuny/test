package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.invoke.Event;
import com.jiuqi.dna.core.type.GUID;

public final class AdjuestUserIdentifyEvent extends Event {

	public AdjuestUserIdentifyEvent(final GUID userIdentifier,
			final GUID identifyIdentifier) {
		if (userIdentifier == null) {
			throw new IllegalArgumentException("参数[userIdentifier]不能为null。");
		}
		this.userIdentifier = userIdentifier;
		this.identifyIdentifier = identifyIdentifier;
	}

	public final GUID userIdentifier;

	public GUID identifyIdentifier;

}
