package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishCreateUserTask extends SimpleTask {

	public FinishCreateUserTask(final GUID identifier, final String name,
			final String title, final ActorState state,
			final String description,final GUID password) {
		this.identifier = identifier;
		this.name = name;
		this.title = title;
		this.state = state;
		this.description = description;
		this.password = password;
	}

	public final GUID identifier;

	public final String name;

	public final String title;

	public final ActorState state;

	public final String description;
	
	public String level;

	public final GUID password;

}
