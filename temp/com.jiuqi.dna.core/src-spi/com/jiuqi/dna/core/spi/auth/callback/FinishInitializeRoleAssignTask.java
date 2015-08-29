package com.jiuqi.dna.core.spi.auth.callback;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;

public final class FinishInitializeRoleAssignTask extends SimpleTask {

	public FinishInitializeRoleAssignTask(
			final List<RoleAssignEntry> roleAssignInformations) {
		this.roleAssignEntrys = roleAssignInformations;
	}

	public final List<RoleAssignEntry> roleAssignEntrys;

}
