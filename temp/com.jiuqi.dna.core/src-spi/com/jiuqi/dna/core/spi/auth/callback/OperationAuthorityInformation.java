package com.jiuqi.dna.core.spi.auth.callback;

import java.util.ArrayList;

import com.jiuqi.dna.core.spi.auth.callback.AccessControlEntry.AuthorityEntry;
import com.jiuqi.dna.core.type.GUID;

public final class OperationAuthorityInformation {

	public OperationAuthorityInformation() {
		// this.ACVersionEntryList = new ArrayList<ACVersionEntry>();
		this.authorityEntryList = new ArrayList<AuthorityEntry>();
	}

	public final AuthorityEntry addOperationAuthorityEntry(
			final GUID groupIdentifier, final GUID itemIdentifier) {
		final AuthorityEntry authorityEntry = new AuthorityEntry(
				groupIdentifier, itemIdentifier);
		this.authorityEntryList.add(authorityEntry);
		return authorityEntry;
	}

	public final ArrayList<AuthorityEntry> authorityEntryList;

	// public final ACVersionEntry addACVersionEntry(final GUID ACVersion) {
	// final ACVersionEntry ACVersionEntry = new ACVersionEntry(ACVersion);
	// this.ACVersionEntryList.add(ACVersionEntry);
	// return ACVersionEntry;
	// }
	//
	// public final OperationAuthorityEntry addOperationAuthorityEntry(
	// final GUID ACVersion, final GUID groupIdentifier,
	// final GUID itemIdentifier) {
	// final OperationAuthorityEntry authorityEntry = new
	// OperationAuthorityEntry(
	// ACVersion, groupIdentifier, itemIdentifier);
	// this.authorityEntryList.add(authorityEntry);
	// return authorityEntry;
	// }
	//
	// public final ArrayList<ACVersionEntry> ACVersionEntryList;
	//
	// public static final class ACVersionEntry extends AccessControlEntry {
	//
	// private ACVersionEntry(final GUID ACVersion) {
	// this.ACVersion = ACVersion;
	// }
	//
	// public final GUID ACVersion;
	//
	// }
	//
	// public static final class OperationAuthorityEntry extends AuthorityEntry
	// {
	//
	// private OperationAuthorityEntry(final GUID ACVersion,
	// final GUID groupIdentifier, final GUID itemIdentifier) {
	// super(groupIdentifier, itemIdentifier);
	// this.ACVersion = ACVersion;
	// }
	//
	// public final GUID ACVersion;
	//
	// }

}
