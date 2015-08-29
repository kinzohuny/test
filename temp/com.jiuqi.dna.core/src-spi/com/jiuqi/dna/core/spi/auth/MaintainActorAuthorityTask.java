package com.jiuqi.dna.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.type.GUID;

public abstract class MaintainActorAuthorityTask extends
		Task<MaintainActorAuthorityTask.Method> {

	public enum Method {
		FILL_AUTHORIZED_ITEM, UPDATE_AUTHORITY
	}

	protected MaintainActorAuthorityTask(GUID actorID, GUID orgID,
			boolean operationAuthority) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
		this.operationAuthority = operationAuthority;
		this.authorizedItemList = new ArrayList<AuthorityItem>();
	}

	public final GUID actorID;

	public GUID orgID;

	public final boolean operationAuthority;

	public final List<AuthorityItem> authorizedItemList;

}
