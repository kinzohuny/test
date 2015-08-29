package com.jiuqi.dna.core.spi.auth.callback;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishUpdateIdentifyRoleAssignTask extends SimpleTask {

	public FinishUpdateIdentifyRoleAssignTask(final GUID userIdentifier,
			final GUID identifyIdentifier,
			final List<GUID> assignedRoleIdentifiers) {
		this.userIdentifier = userIdentifier;
		this.identifyIdentifier = identifyIdentifier;
		this.assignedRoleIdentifiers = assignedRoleIdentifiers;
	}

	public final GUID userIdentifier;

	public final GUID identifyIdentifier;

	public final List<GUID> assignedRoleIdentifiers;

}
