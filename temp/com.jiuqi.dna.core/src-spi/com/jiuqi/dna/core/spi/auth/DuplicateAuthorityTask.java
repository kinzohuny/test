package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public final class DuplicateAuthorityTask extends SimpleTask {

	public DuplicateAuthorityTask(final GUID sourceActorID,
			final GUID sourceOrgID, final GUID targetActorID,
			final GUID targetOrgID, final boolean operationAuthority) {
		if (sourceActorID == null) {
			throw new NullArgumentException("sourceActorID");
		}
		if (targetActorID == null) {
			throw new NullArgumentException("targetActorID");
		}
		this.sourceActorID = sourceActorID;
		this.targetActorID = targetActorID;
		this.sourceOrgID = sourceOrgID;
		this.targetOrgID = targetOrgID;
		this.operationAuthority = operationAuthority;
	}

	public final GUID sourceActorID;

	public final GUID targetActorID;

	public final GUID sourceOrgID;

	public final GUID targetOrgID;

	public final boolean operationAuthority;

}
