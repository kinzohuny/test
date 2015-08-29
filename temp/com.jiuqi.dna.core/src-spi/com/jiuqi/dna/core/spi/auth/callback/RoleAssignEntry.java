package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.type.GUID;

public final class RoleAssignEntry extends AccessControlEntry {

	public RoleAssignEntry(final GUID actorIdentifier, final GUID identifyIdentifier,
			final GUID roleIdentifier) {
		this.userIdentifier = actorIdentifier;
		this.identifyIdentifier = identifyIdentifier;
		this.roleIdentifier = roleIdentifier;
	}

	public final GUID userIdentifier;
	
	public final GUID identifyIdentifier;

	public final GUID roleIdentifier;

}