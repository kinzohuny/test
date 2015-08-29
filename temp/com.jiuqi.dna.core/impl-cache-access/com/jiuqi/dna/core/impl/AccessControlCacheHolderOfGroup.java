package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.resource.ResourceTokenLink;
import com.jiuqi.dna.core.spi.auth.AuthorizableResourceCategoryItem;
import com.jiuqi.dna.core.type.GUID;

@SuppressWarnings({ "rawtypes" })
final class AccessControlCacheHolderOfGroup implements
		AuthorizableResourceCategoryItem {

	AccessControlCacheHolderOfGroup(final CacheGroup<?, ?, ?> group) {
		this.cacheGroup = group;
	}

	public final GUID getGUID() {
		return this.cacheGroup.accessControlInformation.ACGUIDIdentifier;
	}

	public final String getTitle() {
		return this.cacheGroup.title;
	}

	public final ResourceKind getKind() {
		throw new UnsupportedOperationException();
	}

	public final Operation<?>[] getOperations() {
		return this.cacheGroup.define.accessControlDefine.operations;
	}

	public final Class getFacadeClass() {
		return this.cacheGroup.define.facadeClass;
	}

	public final Object getCategory() {
		throw new UnsupportedOperationException();
	}

	public final Object getFacade() throws DisposedException {
		throw new UnsupportedOperationException();
	}

	public final Object tryGetFacade() {
		throw new UnsupportedOperationException();
	}

	public final ResourceToken getParent() {
		throw new UnsupportedOperationException();
	}

	public final ResourceTokenLink getChildren() {
		throw new UnsupportedOperationException();
	}

	public final ResourceTokenLink getSubTokens(Class subTokenFacadeClass)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	public final ResourceToken getSuperToken(Class superTokenFacadeClass)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	final CacheGroup<?, ?, ?> cacheGroup;

}
