package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.resource.ResourceHandle;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceQuerier;
import com.jiuqi.dna.core.resource.ResourceToken;

final class ResourceHandleImplement<TFacade> implements ResourceHandle<TFacade> {

	ResourceHandleImplement(final CacheHolder<TFacade, ?, ?> resourceToken,
			final ResourceQuerier resourceQuerier, final Transaction transaction) {
		this.resourceToken = resourceToken;
		this.resourceQuerier = resourceQuerier;
		this.transaction = transaction;
	}

	public final void closeHandle() {
		this.transaction.releaseAcquirable(this.resourceToken);
	}

	public final ResourceQuerier getOwnedResourceQuerier() {
		return this.resourceQuerier;
	}

	public final ResourceToken<TFacade> getToken() {
		return this.resourceToken;
	}

	public final Object getCategory() {
		return this.resourceQuerier.getCategory();
	}

	public final Class<TFacade> getFacadeClass() {
		return this.resourceToken.getFacadeClass();
	}

	public final ResourceKind getKind() {
		return this.resourceToken.getKind();
	}

	private final CacheHolder<TFacade, ?, ?> resourceToken;

	private final ResourceQuerier resourceQuerier;

	private final Transaction transaction;

	public final TFacade getFacade() throws DisposedException {
		return this.resourceToken.getFacade();
	}

	public final TFacade tryGetFacade() {
		return this.resourceToken.tryGetFacade();
	}

}
