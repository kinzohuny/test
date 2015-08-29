package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishUpdateUserPasswordTask extends SimpleTask {

	public FinishUpdateUserPasswordTask(final GUID userIdentifier,
			final GUID newPassword) {
		this.userIdentifier = userIdentifier;
		this.newPassword = newPassword;
	}

	public final GUID userIdentifier;

	public final GUID newPassword;

}
