package com.jiuqi.dna.core.spi.auth.callback;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;

public final class FinishInitializeUserTask extends SimpleTask {

	public FinishInitializeUserTask(final List<UserEntry> userInformations) {
		this.userInformations = userInformations;
	}

	public final List<UserEntry> userInformations;

}
