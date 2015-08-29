package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.AuthorizedResourceItem;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

@Deprecated
abstract class AuthorityItem implements AuthorizedResourceItem {

	public final GUID getID() {
		return this.itemGUID;
	}

	public final String getTitle() {
		return this.title;
	}

	public final Authority getAuthority(Operation<?> operation) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		final int opMask = operation.getMask();
		int authMask = AccessControlHelper.toAuthorityMask(opMask);
		final int result = this.authCode & authMask;
		if (result == 0) {
			return Authority.UNDEFINE;
		}
		if (result == AccessControlHelper.toAuthorityCode(opMask, Authority.ALLOW.code)) {
			return Authority.ALLOW;
		}
		return Authority.DENY;
	}

	public final void setAuthority(Operation<?> operation, Authority authority) {
		if (operation == null) {
			throw new NullArgumentException("operation");
		}
		if (authority == null) {
			throw new NullArgumentException("authority");
		}
		final int opMask = operation.getMask();
		this.authCode &= (~AccessControlHelper.toAuthorityCode(opMask, 0x3));
		this.authCode |= AccessControlHelper.toAuthorityCode(opMask, authority.code);
	}

	AuthorityItem(long itemID, GUID itemGUID, String title, int authCode) {
		this.itemID = itemID;
		this.itemGUID = itemGUID;
		this.title = title;
		this.authCode = authCode;
	}

	final long itemID;

	final GUID itemGUID;

	final String title;

	int authCode;

}
