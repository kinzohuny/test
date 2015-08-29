package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

final class AccessControlConstants {

	static final GUID orgnizationIdentifierToACVersion(
			final GUID orgnizationIdentifier) {
		return isDefaultACVersion(orgnizationIdentifier) ? null : orgnizationIdentifier;
	}

	static final boolean isDefaultACVersion(final GUID ACVersion) {
		return ACVersion == null || ACVersion.equals(DEFAULT_ACVERSION);
	}

	static final GUID adjustACVersion(final GUID ACVersion) {
		if (isDefaultACVersion(ACVersion)) {
			return DEFAULT_ACVERSION;
		} else {
			return ACVersion;
		}

	}

	static final GUID DEFAULT_ACVERSION;

	static {
		DEFAULT_ACVERSION = GUID.valueOf("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	}

	private AccessControlConstants() {
		// do nothing
	}

}
