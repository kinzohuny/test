package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.spi.auth.callback.AccessControlEntry.ActorEntry;
import com.jiuqi.dna.core.type.GUID;

public final class RoleEntry extends ActorEntry {

	public RoleEntry(final GUID identifier, final String name,
			final String title, final ActorState state, final String description) {
		super(identifier, name, title, state, description);
	}

}
