package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishDeleteUserTask extends SimpleTask {

	public FinishDeleteUserTask(final GUID userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public final GUID userIdentifier;

}
