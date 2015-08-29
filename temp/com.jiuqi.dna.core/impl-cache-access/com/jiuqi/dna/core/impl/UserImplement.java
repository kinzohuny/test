package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.GUID;

@StructClass
final class UserImplement implements User {

	UserImplement(final GUID identifier) {
		this.identifier = identifier;
	}

	public final boolean isBuildInUser() {
		return false;
	}

	public final GUID getID() {
		return this.identifier;
	}

	public final String getName() {
		return this.name;
	}

	public final String getTitle() {
		return this.title;
	}

	public final ActorState getState() {
		if (this.userHolder == null || this.userHolder.isDisposed()) {
			return ActorState.DISPOSED;
		}
		return this.state;
	}

	public final String getDescription() {
		return this.description;
	}
	
	public final String getLevel(){
		return this.level;
	}

	public final int getPriorityIndex() {
		return 0;
	}

	public final boolean validatePassword(final String password) {
		if (password == null) {
			throw new NullArgumentException("password");
		}
		return GUID.MD5Of(password).equals(this.password);
	}

	public final boolean validatePassword(final GUID password) {
		if (password == null) {
			throw new NullArgumentException("password");
		}
		return password.equals(this.password);
	}

	@Override
	public final String toString() {
		return "GUID:" + this.identifier + "\n" + "Name:" + this.name;
	}

	final GUID identifier;

	String name;

	String title;

	ActorState state;

	String description;

	GUID password;
	
	String level;

	transient volatile UserCacheHolder userHolder;

	@Deprecated
	public final int getMappingOrganizationCount() {
		return this.userHolder.getMappingOrganizationCount();
	}

	@Deprecated
	public final GUID getMappingOrganizationID(final int index) {
		return this.userHolder.getMappingOrganizationID(index);
	}

	@Deprecated
	public final int getAssignedRoleCount() {
		return this.userHolder.tryGetReferences(Role.class).size();
	}

	@Deprecated
	public final Role getAssignedRole(final int index) {
		return this.userHolder.tryGetReferences(Role.class).get(index);
	}
}