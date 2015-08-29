package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.auth.Actor;
import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.auth.RoleOperation;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceService.WhenExists;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.auth.DeleteRoleTask;
import com.jiuqi.dna.core.spi.auth.NewRoleTask;
import com.jiuqi.dna.core.spi.auth.UpdateRoleBaseInfoTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishCreateRoleTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDeleteRoleTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeRoleTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateRoleInformationTask;
import com.jiuqi.dna.core.spi.auth.callback.RoleEntry;
import com.jiuqi.dna.core.type.GUID;

final class RoleResourceService extends
		ResourceServiceBase<Role, RoleImplement, RoleImplement> {

	private static final String TITLE;

	static {
		TITLE = "缓存角色服务";
	}

	protected RoleResourceService() {
		super(TITLE, ResourceKind.SINGLETON_IN_CLUSTER);
	}

	@Override
	protected final void initResources(
			final Context context,
			final ResourceInserter<Role, RoleImplement, RoleImplement> initializer)
			throws Throwable {
		if (this.isDBValid()) {
			final List<RoleEntry> roleInformationList = context.getList(RoleEntry.class);
			for (RoleEntry roleInformation : roleInformationList) {
				final GUID roleIdentifier = roleInformation.identifier;
				final RoleImplement role = new RoleImplement(roleIdentifier);
				role.name = roleInformation.name;
				role.title = roleInformation.title;
				role.state = roleInformation.state;
				role.description = roleInformation.description;
				initializer.putResource(role);
			}
			context.handle(new FinishInitializeRoleTask(roleInformationList));
		}
	}

	@Override
	void ensureCacheDefine(final Cache cache) {
		if (super.cacheDefine == null) {
			synchronized (this) {
				if (super.cacheDefine == null) {
					super.cacheDefine = new RoleCacheDefine(cache, this);
				}
			}
		}
	}

	final class AccessControlRoleProvider extends
			AuthorizableResourceProvider<RoleOperation> {

		protected AccessControlRoleProvider() {
			super(null, false);
		}

		@Override
		protected final GUID getKey1(final RoleImplement keysHolder) {
			return keysHolder.identifier;
		}

		@Override
		protected final String getResourceTitle(final RoleImplement resource,
				final RoleImplement keysHolder) {
			return resource.title;
		}

	}

	final class RoleProvider extends OneKeyResourceProvider<String> {

		@Override
		protected final String getKey1(final RoleImplement keysHolder) {
			return keysHolder.name.toLowerCase();
		}

	}

	@Publish
	final class CreateRoleTaskHandler extends
			TaskMethodHandler<NewRoleTask, None> {

		protected CreateRoleTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<Role, RoleImplement, RoleImplement> context,
				final NewRoleTask task) throws Throwable {
			if (task.id == null) {
				throw new NullArgumentException("id");
			}
			if (task.name == null) {
				throw new NullArgumentException("name");
			}
			if (task.name.length() > Actor.MAX_NAME_LENGTH) {
				throw new IllegalArgumentException("角色名称[" + task.name + "]超过最大长度限制。最大长度为：" + Actor.MAX_NAME_LENGTH);
			}
			if (task.title != null && task.title.length() > Actor.MAX_TITLE_LENGTH) {
				throw new IllegalArgumentException("角色标题[" + task.title + "]超过最大长度限制。最大长度为：" + Actor.MAX_TITLE_LENGTH);
			}
			if (task.description != null && task.description.length() > Actor.MAX_DESCRIPTION_LENGTH) {
				throw new IllegalArgumentException("角色描述[" + task.description + "]超过最大长度限制。最大长度为：" + Actor.MAX_DESCRIPTION_LENGTH);
			}
			final RoleImplement role = new RoleImplement(task.id);
			role.name = task.name;
			role.title = task.title == null ? task.name : task.title;
			role.state = task.state == null ? ActorState.DEFUALT_STATE : task.state;
			role.description = task.description;
			context.putResource(role, role, WhenExists.EXCEPTION);
			context.handle(new FinishCreateRoleTask(role.identifier, role.name, role.title, role.state, role.description));
		}

	}

	@Publish
	final class DeleteRoleTaskHandler extends
			TaskMethodHandler<DeleteRoleTask, None> {

		protected DeleteRoleTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<Role, RoleImplement, RoleImplement> context,
				final DeleteRoleTask task) throws Throwable {
			final RoleImplement role = context.removeResource(task.actorID);
			if (role == null) {
				return;
			} else {
				role.state = ActorState.DISPOSED;
				context.handle(new FinishDeleteRoleTask(role.identifier));
			}
		}

	}

	@Publish
	final class UpdateRolenformationTaskHandler extends
			TaskMethodHandler<UpdateRoleBaseInfoTask, None> {

		protected UpdateRolenformationTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<Role, RoleImplement, RoleImplement> context,
				final UpdateRoleBaseInfoTask task) throws Throwable {
			if (task.name != null && task.name.length() > Actor.MAX_NAME_LENGTH) {
				throw new IllegalArgumentException("角色名称[" + task.name + "]超过最大长度限制。最大长度为：" + Actor.MAX_NAME_LENGTH);
			}
			if (task.title != null && task.title.length() > Actor.MAX_TITLE_LENGTH) {
				throw new IllegalArgumentException("角色标题[" + task.title + "]超过最大长度限制。最大长度为：" + Actor.MAX_TITLE_LENGTH);
			}
			if (task.description != null && task.description.length() > Actor.MAX_DESCRIPTION_LENGTH) {
				throw new IllegalArgumentException("角色描述[" + task.description + "]超过最大长度限制。最大长度为：" + Actor.MAX_DESCRIPTION_LENGTH);
			}
			if(task.name != null) {
				Role role = context.find(Role.class, task.name.toLowerCase());;
				if(role != null && role.getID() != task.actorID) {
					throw new IllegalArgumentException("角色标识[" + task.name + "]已存在！");
				}
			}
			final RoleImplement role2 = context.modifyResource(task.actorID);
			if (role2 == null) {
				return;
			} else {
				if (task.name != null) {
					role2.name = task.name;
				}
				if (task.title != null) {
					role2.title = task.title;
				}
				if (task.state != null) {
					role2.state = task.state;
				}
				if (task.description != null) {
					role2.description = task.description;
				}
				context.postModifiedResource(role2);
				context.handle(new FinishUpdateRoleInformationTask(role2.identifier, role2.name, role2.title, role2.state, role2.description));
			}
		}

	}

}
