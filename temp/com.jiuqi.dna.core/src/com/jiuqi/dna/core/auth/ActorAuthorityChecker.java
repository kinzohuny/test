package com.jiuqi.dna.core.auth;

import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.type.GUID;

public interface ActorAuthorityChecker {

	@Deprecated
	public GUID getOrgID();

	public <TFacade> boolean hasAuthority(Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken);

	public <TFacade> Authority getAuthority(
			Operation<? super TFacade> operation,
			ResourceToken<TFacade> resoureceToken);

}
