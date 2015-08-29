package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishUpdateUserInformationTask extends SimpleTask {

	public FinishUpdateUserInformationTask(final GUID userIdentifier,
			final String name, final String title, final ActorState state,
			final String description) {
		this.userIdentifier = userIdentifier;
		this.name = name;
		this.title = title;
		this.state = state;
		this.description = description;
	}

	public final GUID userIdentifier;

	public final String name;

	public final String title;

	public final ActorState state;

	public final String description;
	
	public String level;

}
