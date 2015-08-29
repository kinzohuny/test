package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.exception.DisposedException;
import com.jiuqi.dna.core.type.GUID;

final class RoleCacheHolder extends
		ActorCacheHolder<Role, RoleImplement, RoleImplement> implements Role {

	RoleCacheHolder(
			final CacheGroup<Role, RoleImplement, RoleImplement> ownGroup,
			final RoleImplement value, final RoleImplement keysHolder,
			final Long fixLongIdentifier) {
		super(ownGroup, value.identifier, null, value, keysHolder, fixLongIdentifier);
		value.roleHolder = this;
	}

	@Override
	final RoleImplement tryGetModifiableValue() {
		final RoleImplement modifiableValue = super.tryGetModifiableValue();
		if (modifiableValue != null) {
			modifiableValue.roleHolder = this;
		}
		return modifiableValue;
	}

	@Override
	final void tryPostModifiedValueWithoutCheck(final Object modifiedValue,
			final Object newKeysHolder, final Transaction transaction) {
		super.tryPostModifiedValueWithoutCheck(modifiedValue, newKeysHolder, transaction);
		((RoleImplement) modifiedValue).roleHolder = this;
	}

	@Deprecated
	public final GUID getID() {
		return this.getValue().identifier;
	}

	@Deprecated
	public final String getName() {
		return this.getValue().name;
	}

	@Deprecated
	public final String getTitle() {
		return this.getValue().title;
	}

	@Deprecated
	public final ActorState getState() {
		try {
			return this.getValue().state;
		} catch (DisposedException e) {
			return ActorState.DISPOSED;
		}
	}

	@Deprecated
	public final String getDescription() {
		return this.getValue().description;
	}

	private final RoleImplement getValue() {
		final RoleImplement role = this.tryGetValue();
		if (role == null) {
			throw new DisposedException("缓存项已被销毁。");
		} else {
			return role;
		}
	}
}