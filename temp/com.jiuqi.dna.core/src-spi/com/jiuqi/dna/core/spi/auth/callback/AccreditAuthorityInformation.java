package com.jiuqi.dna.core.spi.auth.callback;

import java.util.ArrayList;

import com.jiuqi.dna.core.spi.auth.callback.AccessControlEntry.AuthorityEntry;
import com.jiuqi.dna.core.type.GUID;

public final class AccreditAuthorityInformation {

	public AccreditAuthorityInformation(final boolean noSuchACVersion) {
		this.noSuchACVersion = noSuchACVersion;
		if (noSuchACVersion) {
			this.authorityEntryList = null;
		} else {
			this.authorityEntryList = new ArrayList<AuthorityEntry>();
		}
	}

	public final AuthorityEntry addAccreditAuthorityEntry(
			final GUID groupIdentifier, final GUID itemIdentifier) {
		final AuthorityEntry authorityEntry = new AuthorityEntry(
				groupIdentifier, itemIdentifier);
		this.authorityEntryList.add(authorityEntry);
		return authorityEntry;
	}

	public final boolean noSuchACVersion;

	public final ArrayList<AuthorityEntry> authorityEntryList;

	// public static final class AccreditAuthorityEntry extends AuthorityEntry {
	//
	// AccreditAuthorityEntry(final GUID groupIdentifier,
	// final GUID itemIdentifier) {
	// super(groupIdentifier, itemIdentifier);
	// }
	//
	// }

}
