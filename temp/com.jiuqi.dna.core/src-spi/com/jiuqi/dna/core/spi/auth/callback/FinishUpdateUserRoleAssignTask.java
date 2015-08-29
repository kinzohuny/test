package com.jiuqi.dna.core.spi.auth.callback;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishUpdateUserRoleAssignTask extends SimpleTask {

	public FinishUpdateUserRoleAssignTask(final GUID userIdentifier,
			final List<GUID> assignedRoleIdentifiers) {
		this.userIdentifier = userIdentifier;
		this.assignedRoleIdentifiers = assignedRoleIdentifiers;
	}

	public final GUID userIdentifier;

	public final List<GUID> assignedRoleIdentifiers;

}
