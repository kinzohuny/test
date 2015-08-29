package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.impl.Cache.CustomGroupSpace;
import com.jiuqi.dna.core.type.GUID;

abstract class CacheGroupSpace {

	static final boolean isPreservedSpaceIdentifier(final Object identifier) {
		for (Object preservedIdentifier : PRESERVED_SPACE_IDENTIFIER) {
			if (identifier == preservedIdentifier) {
				return true;
			}
		}
		return false;
	}

	static final boolean isValidatedSpaceIdentifier(final Object identifier) {
		for (Object preservedIdentifier : PRESERVED_SPACE_IDENTIFIER) {
			if (identifier == preservedIdentifier) {
				return true;
			}
		}
		final Class<?> identifierClass = identifier.getClass();
		return (identifierClass == GUID.class || identifierClass == String.class || Enum.class.isAssignableFrom(identifierClass));
	}

	static final GUID generateGroupACIdentifier(
			final String resourceServiceName, final Class<?> facadeClass,
			final Object spaceIdentifier) {
		// final String facadeClassName = facadeClass.getName();
		if (isPreservedSpaceIdentifier(spaceIdentifier)) {
			return GUID.MD5Of(resourceServiceName);
		} else if (spaceIdentifier.getClass() == GUID.class) {
			return (GUID) spaceIdentifier;
		} else if (spaceIdentifier.getClass() == String.class) {
			return GUID.MD5Of(resourceServiceName + ":" + spaceIdentifier);
		} else if (spaceIdentifier instanceof Enum<?>) {
			return GUID.MD5Of(resourceServiceName + ":" + spaceIdentifier.getClass().getName() + "." + ((Enum<?>) spaceIdentifier).name());
		} else {
			throw new IllegalArgumentException("非法的缓存组空间标识。");
		}
	}

	static final Object[] PRESERVED_SPACE_IDENTIFIER;

	static {
		PRESERVED_SPACE_IDENTIFIER = new Object[] { null, None.NONE };
	}

	CacheGroupSpace(final Object objectIdentifier) {
		this.identifier = objectIdentifier;
	}

	abstract CustomGroupSpace asCustomGroupSpace();

	abstract <TFacade> CacheGroup<TFacade, ?, ?> getGroup(
			Class<TFacade> facadeClass, Transaction transaction);

	abstract <TFacade> CacheGroup<TFacade, ?, ?> findGroup(
			Class<TFacade> facadeClass, Transaction transaction);

	final Object identifier;
}