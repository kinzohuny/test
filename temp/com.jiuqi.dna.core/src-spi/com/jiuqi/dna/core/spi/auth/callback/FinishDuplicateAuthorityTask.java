package com.jiuqi.dna.core.spi.auth.callback;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class FinishDuplicateAuthorityTask extends SimpleTask {

	public FinishDuplicateAuthorityTask(final GUID sourceActorID,
			final GUID sourceOrgID, final GUID targetActorID,
			final GUID targetOrgID, final boolean operationAuthority) {
		this.sourceActorID = sourceActorID;
		this.sourceOrgID = sourceOrgID;
		this.targetActorID = targetActorID;
		this.targetOrgID = targetOrgID;
		this.operationAuthority = operationAuthority;
	}

	public final GUID sourceActorID;

	public final GUID sourceOrgID;

	public final GUID targetActorID;

	public final GUID targetOrgID;

	public final boolean operationAuthority;

}
