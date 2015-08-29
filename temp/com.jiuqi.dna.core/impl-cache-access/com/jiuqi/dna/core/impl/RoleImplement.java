package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.type.GUID;

@StructClass
final class RoleImplement implements Role {

	RoleImplement(final GUID identifier) {
		this.identifier = identifier;
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
		return this.state;
	}

	public final String getDescription() {
		return this.description;
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

	transient volatile RoleCacheHolder roleHolder;

	@Deprecated
	public final int getMappingOrganizationCount() {
		return this.roleHolder.getMappingOrganizationCount();
	}

	@Deprecated
	public final GUID getMappingOrganizationID(final int index) {
		return this.roleHolder.getMappingOrganizationID(index);
	}

}
