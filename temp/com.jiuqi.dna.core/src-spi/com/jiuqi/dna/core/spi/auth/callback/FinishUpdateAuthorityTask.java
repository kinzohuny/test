package com.jiuqi.dna.core.spi.auth.callback;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.spi.auth.AuthorityItem;
import com.jiuqi.dna.core.type.GUID;

public final class FinishUpdateAuthorityTask extends SimpleTask {

	public FinishUpdateAuthorityTask(final boolean forUser,
			final GUID actorIdentifier, final boolean operationAuthority,
			final GUID ACVersion, final List<AuthorityItem> authorityItemList) {
		this.forUser = forUser;
		this.actorIdentifier = actorIdentifier;
		this.operationAuthority = operationAuthority;
		this.ACVersion = ACVersion;
		this.authorityItemList = authorityItemList;
	}

	public final boolean forUser;

	public final GUID actorIdentifier;

	public final boolean operationAuthority;

	public final GUID ACVersion;

	public final List<AuthorityItem> authorityItemList;

}
