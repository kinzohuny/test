package com.jiuqi.dna.core.spi.auth.callback;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;

public final class FinishInitializeRoleTask extends SimpleTask {

	public FinishInitializeRoleTask(final List<RoleEntry> roleInformations) {
		this.roleInformations = roleInformations;
	}

	public final List<RoleEntry> roleInformations;

}
