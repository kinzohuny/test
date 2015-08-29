package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.type.GUID;

public abstract class ClearActorAuthorityTask extends SimpleTask {

	ClearActorAuthorityTask(final GUID actorID, final GUID orgID,
			final boolean operationAuthority) {
		if (actorID == null) {
			throw new IllegalArgumentException("参数[actorID]不能为null。");
		}
		this.actorID = actorID;
		this.orgID = orgID;
		this.operationAuthority = operationAuthority;
	}

	public final GUID actorID;

	public final GUID orgID;

	public final boolean operationAuthority;

}
