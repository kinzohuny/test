package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.spi.auth.callback.AccessControlEntry.ActorEntry;
import com.jiuqi.dna.core.type.GUID;

public final class UserEntry extends ActorEntry {

	public UserEntry(final GUID identifier, final String name,
			final String title, final ActorState state,
			final String description, final GUID password, final int priority) {
		super(identifier, name, title, state, description);
		this.password = password;
		this.priority = priority;
	}

	public final GUID password;

	public final int priority;
	
	public String level;

}
