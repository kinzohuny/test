package com.jiuqi.dna.core.auth;

import com.jiuqi.dna.core.User;

public interface UserAuthorityChecker extends ActorAuthorityChecker {

	public User getUser();

}
