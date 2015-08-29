package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.resource.ResourceTokenLink;

final class ResourceTokenLinkImplement<TFacade> implements
		ResourceTokenLink<TFacade> {

	ResourceTokenLinkImplement(final ResourceToken<TFacade> token) {
		this.token = token;
	}

	public final ResourceToken<TFacade> getToken() {
		return this.token;
	}

	public final ResourceTokenLink<TFacade> next() {
		return this.next;
	}

	volatile ResourceTokenLinkImplement<TFacade> next;

	private final ResourceToken<TFacade> token;

}
