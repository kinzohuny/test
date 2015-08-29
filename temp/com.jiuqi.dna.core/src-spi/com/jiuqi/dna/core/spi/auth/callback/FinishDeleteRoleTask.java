package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishDeleteRoleTask extends SimpleTask {

	public FinishDeleteRoleTask(final GUID roleIdentifier) {
		this.roleIdentifier = roleIdentifier;
	}

	public final GUID roleIdentifier;

}
