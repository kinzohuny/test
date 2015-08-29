package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.auth.callback.ACEntryException_InvalidEntry;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.callback.IdentifyMapEntry;
import com.jiuqi.dna.core.spi.auth.callback.RoleAssignEntry;
import com.jiuqi.dna.core.type.GUID;

final class IdentifyResourceService extends
		ResourceServiceBase<Identify, Identify, Identify> {

	static final class NewIdentifyTask extends SimpleTask {

		NewIdentifyTask(final GUID userIdentifier, final GUID identifyIdentifier) {
			this.userIdentifier = userIdentifier;
			this.identifyIdentifier = identifyIdentifier;
		}

		GUID userIdentifier;

		GUID identifyIdentifier;

	}

	static final class DeleteIdentifyTask extends SimpleTask {

		DeleteIdentifyTask(final GUID userIdentifier,
				final GUID identifyIdentifier) {
			this.userIdentifier = userIdentifier;
			this.identifyIdentifier = identifyIdentifier;
		}

		GUID userIdentifier;

		GUID identifyIdentifier;

	}

	IdentifyResourceService() {
		super("身份资源服务", ResourceKind.SINGLETON_IN_CLUSTER);
	}

	@Override
	protected final void initResources(final Context context,
			final ResourceInserter<Identify, Identify, Identify> initializer)
			throws Throwable {
		if (this.isDBValid()) {
			// if (!dataUpgraded(context)) {
			// upgradeData(context);
			// }
			final List<IdentifyMapEntry> identifyMapEntryList = context.getList(IdentifyMapEntry.class);
			final List<RoleAssignEntry> roleAssignInformationList = context.getList(RoleAssignEntry.class, Identify.class);
			for (IdentifyMapEntry entry : identifyMapEntryList) {
				final GUID userIdentifier = entry.userIdentifier;
				final GUID identifyIdentifier = entry.identifyIdentifier;
				final Identify identify = new Identify();
				identify.identifier = entry.identifier;
				identify.userIdentifier = userIdentifier;
				identify.identifyIdentifier = identifyIdentifier;
				final IdentifyCacheHolder identifyItem = (IdentifyCacheHolder) (initializer.putResource(identify));
				for (RoleAssignEntry roleAssignEntry : roleAssignInformationList) {
					if (userIdentifier.equals(roleAssignEntry.userIdentifier) && (identifyIdentifier.equals(roleAssignEntry.identifyIdentifier))) {
						final ResourceToken<Role> roleItem = context.findResourceToken(Role.class, roleAssignEntry.roleIdentifier);
						if (roleItem == null) {
							roleAssignEntry.exception = ACEntryException_InvalidEntry.INSTANCE;
						} else {
							initializer.putResourceReferenceBy(identifyItem, roleItem);
							roleAssignEntry.exception = null;
						}
					}
				}
			}
			context.handle(new FinishInitializeRoleAssignTask(roleAssignInformationList));
		}
	}

	@Override
	void ensureCacheDefine(final Cache cache) {
		if (super.cacheDefine == null) {
			synchronized (this) {
				if (super.cacheDefine == null) {
					super.cacheDefine = new IdentifyCacheDefine(cache, this);
				}
			}
		}
	}

	final class IdentifyProvider extends TwoKeyResourceProvider<GUID, GUID> {

		protected IdentifyProvider() {
			// to do nothing
		}

		@Override
		protected final GUID getKey1(final Identify keysHolder) {
			return keysHolder.userIdentifier;
		}

		@Override
		protected final GUID getKey2(final Identify keysHolder) {
			return keysHolder.identifyIdentifier;
		}

	}

	final class RoleReferenceDefine extends ReferenceDefine<Role> {

		RoleReferenceDefine() {
			super();
		}

		@Override
		final Class<?> getHolderFacadeClass() {
			return Identify.class;
		}

		@Override
		final Class<?> getReferenceFacadeClass() {
			return Role.class;
		}

		@Override
		final Operation<?>[] getOperationMap(final OperationEntry[] operations) {
			return null;
		}

	}

	@Publish
	final class NewIdentifyTaskHandler extends
			TaskMethodHandler<NewIdentifyTask, None> {

		protected NewIdentifyTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<Identify, Identify, Identify> context,
				final NewIdentifyTask task) throws Throwable {
			final Identify identify = new Identify();
			identify.identifier = context.newRECID();
			identify.userIdentifier = task.userIdentifier;
			identify.identifyIdentifier = task.identifyIdentifier;
			context.putResource(identify);
		}

	}

	@Publish
	final class DeleteIdentifyTaskHandler extends
			TaskMethodHandler<DeleteIdentifyTask, None> {

		protected DeleteIdentifyTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<Identify, Identify, Identify> context,
				final DeleteIdentifyTask task) throws Throwable {
			final Identify identify = context.find(Identify.class, task.userIdentifier, task.identifyIdentifier);
			if (identify == null) {
				return;
			} else {
				context.removeResource(task.userIdentifier, task.identifyIdentifier);
			}
		}

	}

}
