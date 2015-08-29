package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.Actor;
import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.auth.UserOperation;
import com.jiuqi.dna.core.def.table.TableDeclare;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceService.WhenExists;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.auth.DeleteUserTask;
import com.jiuqi.dna.core.spi.auth.LoadNewUserTask;
import com.jiuqi.dna.core.spi.auth.NewUserTask;
import com.jiuqi.dna.core.spi.auth.ReloadUserPasswordTask;
import com.jiuqi.dna.core.spi.auth.UpdateUserBaseInfoTask;
import com.jiuqi.dna.core.spi.auth.UpdateUserPasswordTask;
import com.jiuqi.dna.core.spi.auth.UpdateUserRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.callback.ACEntryException_InvalidEntry;
import com.jiuqi.dna.core.spi.auth.callback.FinishCreateUserTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDeleteUserTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeUserTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateUserInformationTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateUserPasswordTask;
import com.jiuqi.dna.core.spi.auth.callback.RoleAssignEntry;
import com.jiuqi.dna.core.spi.auth.callback.UserEntry;
import com.jiuqi.dna.core.spi.def.DeclarePostTask;
import com.jiuqi.dna.core.type.GUID;

final class UserResourceService extends
		ResourceServiceBase<User, UserImplement, UserImplement> {

	private static final String AUTO_CLEAR_NAME_CONFLICT_USER = "com.jiuqi.dna.autoClearNameConflictUser";

	private static final String TITLE;

	static {
		TITLE = "缓存用户服务";
	}

	protected UserResourceService() {
		super(TITLE, ResourceKind.SINGLETON_IN_CLUSTER);
	}

	@Override
	protected final void initResources(
			final Context context,
			final ResourceInserter<User, UserImplement, UserImplement> initializer)
			throws Throwable {
		if (this.isDBValid()) {
			final List<UserEntry> userInformationList = context.getList(UserEntry.class);
			final List<RoleAssignEntry> roleAssignInformationList = context.getList(RoleAssignEntry.class, User.class);
			final TableDefine userTableDefine = context.get(TableDefine.class, TD_CoreAuthUser.NAME);
			// 首先判断是否已经对用户表的name字段创建了唯一索引
			if (!userTableDefine.getIndexes().get(TD_CoreAuthUser.INDEX_COREAUTHUSER_NAME_NAME).isUnique()) {
				final boolean autoClear = Boolean.getBoolean(AUTO_CLEAR_NAME_CONFLICT_USER);
				final HashMap<String, GUID> userNameIDMap = new HashMap<String, GUID>();
				final ArrayList<GUID> needDeleteUserIDList = new ArrayList<GUID>();
				for (UserEntry userInformation : userInformationList) {
					final String lowerCaseUserName = userInformation.name.toLowerCase();
					final GUID existID = userNameIDMap.put(lowerCaseUserName, userInformation.identifier);
					if (existID != null) {
						if (autoClear) {
							needDeleteUserIDList.add(existID);
						} else {
							throw new RuntimeException("数据库中存在用户名相同的用户记录，请清理后再重新启动系统。\n" + "如果需要系统按照默认策略自动完成数据清理，请设置参数" + AUTO_CLEAR_NAME_CONFLICT_USER + "=true。\n" + "注意：系统自动清理存在一定的风险！");
						}
					}
				}
				if (needDeleteUserIDList.size() > 0) {
					// 删除重名的用户
					for (GUID needDeleteUserID : needDeleteUserIDList) {
						context.handle(new FinishDeleteUserTask(needDeleteUserID));
					}
				}
				// 创建唯一索引
				final TableDeclare userTableDeclare = context.get(TableDeclare.class, TD_CoreAuthUser.NAME);
				userTableDeclare.getIndexes().get(TD_CoreAuthUser.INDEX_COREAUTHUSER_NAME_NAME).setUnique(true);
				context.handle(new DeclarePostTask(userTableDeclare));
			}
			final HashMap<GUID, ArrayList<RoleAssignEntry>> roleAssignEntryListHashMapByUserIdentifier = new HashMap<GUID, ArrayList<RoleAssignEntry>>();
			for (RoleAssignEntry roleAssignInformation : roleAssignInformationList) {
				final GUID userIdentifier = roleAssignInformation.userIdentifier;
				ArrayList<RoleAssignEntry> roleAssignEntryList = roleAssignEntryListHashMapByUserIdentifier.get(userIdentifier);
				if (roleAssignEntryList == null) {
					roleAssignEntryList = new ArrayList<RoleAssignEntry>();
					roleAssignEntryListHashMapByUserIdentifier.put(userIdentifier, roleAssignEntryList);
				}
				roleAssignEntryList.add(roleAssignInformation);
			}
			for (UserEntry userInformation : userInformationList) {
				final GUID userIdentifier = userInformation.identifier;
				final UserImplement user = new UserImplement(userIdentifier);
				user.name = userInformation.name;
				user.title = userInformation.title;
				user.state = userInformation.state;
				user.description = userInformation.description;
				user.level = userInformation.level;
				user.password = userInformation.password;
				final UserCacheHolder userItem = (UserCacheHolder) (initializer.putResource(user));
				final ArrayList<RoleAssignEntry> roleAssignEntryList = roleAssignEntryListHashMapByUserIdentifier.get(userIdentifier);
				if (roleAssignEntryList == null) {
					continue;
				}
				for (RoleAssignEntry roleAssignEntry : roleAssignEntryList) {
					final ResourceToken<Role> roleItem = context.findResourceToken(Role.class, roleAssignEntry.roleIdentifier);
					if (roleItem == null) {
						roleAssignEntry.exception = ACEntryException_InvalidEntry.INSTANCE;
					} else {
						initializer.putResourceReferenceBy(userItem, roleItem);
						roleAssignEntry.exception = null;
					}
				}
			}
			context.handle(new FinishInitializeUserTask(userInformationList));
			context.handle(new FinishInitializeRoleAssignTask(roleAssignInformationList));
		}
	}
	
	private final void reloadResources(
			final ResourceContext<User, UserImplement, UserImplement> context)
			throws Throwable {
		final List<UserEntry> userInformationList = context.getList(UserEntry.class);
		final List<RoleAssignEntry> roleAssignInformationList = context.getList(RoleAssignEntry.class, User.class);
		final HashMap<GUID, ArrayList<RoleAssignEntry>> roleAssignEntryListHashMapByUserIdentifier = new HashMap<GUID, ArrayList<RoleAssignEntry>>();
		for (RoleAssignEntry roleAssignInformation : roleAssignInformationList) {
			final GUID userIdentifier = roleAssignInformation.userIdentifier;
			ArrayList<RoleAssignEntry> roleAssignEntryList = roleAssignEntryListHashMapByUserIdentifier.get(userIdentifier);
			if (roleAssignEntryList == null) {
				roleAssignEntryList = new ArrayList<RoleAssignEntry>();
				roleAssignEntryListHashMapByUserIdentifier.put(userIdentifier, roleAssignEntryList);
			}
			roleAssignEntryList.add(roleAssignInformation);
		}
		for (UserEntry userInformation : userInformationList) {
			if(context.find(User.class,userInformation.identifier) == null) {
				final GUID userIdentifier = userInformation.identifier;
				final UserImplement user = new UserImplement(userIdentifier);
				user.name = userInformation.name;
				user.title = userInformation.title;
				user.state = userInformation.state;
				user.description = userInformation.description;
				user.level = userInformation.level;
				user.password = userInformation.password;
				final UserCacheHolder userItem = (UserCacheHolder) (context.putResource(user));
				final ArrayList<RoleAssignEntry> roleAssignEntryList = roleAssignEntryListHashMapByUserIdentifier.get(userIdentifier);
				if (roleAssignEntryList == null) {
					continue;
				}
				for (RoleAssignEntry roleAssignEntry : roleAssignEntryList) {
					final ResourceToken<Role> roleItem = context.findResourceToken(Role.class, roleAssignEntry.roleIdentifier);
					if (roleItem == null) {
						roleAssignEntry.exception = ACEntryException_InvalidEntry.INSTANCE;
					} else {
						context.putResourceReferenceBy(userItem, roleItem);
						roleAssignEntry.exception = null;
					}
				}
			}
		}
	}

	@Override
	void ensureCacheDefine(final Cache cache) {
		if (super.cacheDefine == null) {
			synchronized (this) {
				if (super.cacheDefine == null) {
					super.cacheDefine = new UserCacheDefine(cache, this);
				}
			}
		}
	}

	final class AccessControlUserProvider extends
			AuthorizableResourceProvider<UserOperation> {

		protected AccessControlUserProvider() {
			super(null, false);
		}

		@Override
		protected final GUID getKey1(final UserImplement keysHolder) {
			return keysHolder.identifier;
		}

		@Override
		protected final String getResourceTitle(final UserImplement resource,
				final UserImplement keysHolder) {
			return resource.title;
		}

	}

	final class UserProvider extends OneKeyResourceProvider<String> {

		@Override
		protected final String getKey1(final UserImplement keysHolder) {
			return keysHolder.name.toLowerCase();
		}

	}

	final class RoleReferenceDefine extends ReferenceDefine<Role> {

		RoleReferenceDefine() {
			super();
		}

		@Override
		final Class<?> getHolderFacadeClass() {
			return User.class;
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

	final class IdentifyReferenceDefine extends ReferenceDefine<Identify> {

		IdentifyReferenceDefine() {
			super();
		}

		@Override
		final Class<?> getHolderFacadeClass() {
			return User.class;
		}

		@Override
		final Class<?> getReferenceFacadeClass() {
			return Identify.class;
		}

		@Override
		final Operation<?>[] getOperationMap(final OperationEntry[] operations) {
			return null;
		}

	}

	@Publish
	final class CreateUserTaskHandler extends
			TaskMethodHandler<NewUserTask, None> {

		protected CreateUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<User, UserImplement, UserImplement> context,
				final NewUserTask task) throws Throwable {
			if (task.id == null) {
				throw new NullArgumentException("id");
			}
			if (task.name == null) {
				throw new NullArgumentException("name");
			}
			if (task.name.length() > Actor.MAX_NAME_LENGTH) {
				throw new IllegalArgumentException("用户名称[" + task.name + "]超过最大长度限制。最大长度为：" + Actor.MAX_NAME_LENGTH);
			}
			if (task.title != null && task.title.length() > Actor.MAX_TITLE_LENGTH) {
				throw new IllegalArgumentException("用户标题[" + task.title + "]超过最大长度限制。最大长度为：" + Actor.MAX_TITLE_LENGTH);
			}
			if (task.description != null && task.description.length() > Actor.MAX_DESCRIPTION_LENGTH) {
				throw new IllegalArgumentException("用户描述[" + task.description + "]超过最大长度限制。最大长度为：" + Actor.MAX_DESCRIPTION_LENGTH);
			}
			this.checkUserName(task.name);
			final UserImplement user = new UserImplement(task.id);
			user.name = task.name;
			user.title = task.title == null ? task.name : task.title;
			user.state = task.state == null ? ActorState.DEFUALT_STATE : task.state;
			user.description = task.description;
			user.level = task.level;
			user.password = task.passwordNeedEncrypt ? task.password == null ? GUID.MD5Of("") : GUID.MD5Of(task.password) : GUID.valueOf(task.password);
			context.putResource(user, user, WhenExists.EXCEPTION);
			final UpdateUserRoleAssignTask roleAssignTask = new UpdateUserRoleAssignTask(user.identifier);
			roleAssignTask.assignActorIDList.addAll(task.assignRoleIDList);
			context.handle(roleAssignTask);
			
			FinishCreateUserTask fcut = new FinishCreateUserTask(user.identifier, user.name, user.title, user.state, user.description,user.password);
			fcut.level = user.level;
			context.handle(fcut);
		}

		private final void checkUserName(String userName) {
			userName = userName.toLowerCase();
			if (userName.equals(User.USER_NAME_ANONYM) || userName.equals(User.USER_NAME_DEBUGGER) || userName.equals(User.USER_NAME_SYSTEM)) {
				throw new IllegalArgumentException("[" + userName + "]为系统保留用户名。");
			}
		}

	}

	@Publish
	final class DeleteUserTaskHandler extends
			TaskMethodHandler<DeleteUserTask, None> {

		protected DeleteUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<User, UserImplement, UserImplement> context,
				final DeleteUserTask task) throws Throwable {
			final UserImplement user = context.removeResource(task.actorID);
			if (user == null) {
				return;
			} else {
				user.state = ActorState.DISPOSED;
				context.handle(new FinishDeleteUserTask(user.identifier));
			}
		}

	}

	@Publish
	final class UpdateUserInformationTaskHandler extends
			TaskMethodHandler<UpdateUserBaseInfoTask, None> {

		protected UpdateUserInformationTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<User, UserImplement, UserImplement> context,
				final UpdateUserBaseInfoTask task) throws Throwable {
			if (task.name != null && task.name.length() > Actor.MAX_NAME_LENGTH) {
				throw new IllegalArgumentException("用户名称[" + task.name + "]超过最大长度限制。最大长度为：" + Actor.MAX_NAME_LENGTH);
			}
			if (task.title != null && task.title.length() > Actor.MAX_TITLE_LENGTH) {
				throw new IllegalArgumentException("用户标题[" + task.title + "]超过最大长度限制。最大长度为：" + Actor.MAX_TITLE_LENGTH);
			}
			if (task.description != null && task.description.length() > Actor.MAX_DESCRIPTION_LENGTH) {
				throw new IllegalArgumentException("用户描述[" + task.description + "]超过最大长度限制。最大长度为：" + Actor.MAX_DESCRIPTION_LENGTH);
			}
			final UserImplement user = context.modifyResource(task.actorID);
			if (user == null) {
				return;
			} else {
				if (task.name != null) {
					user.name = task.name;
				}
				if (task.title != null) {
					user.title = task.title;
				}
				if (task.state != null) {
					user.state = task.state;
				}
				if (task.description != null) {
					user.description = task.description;
				}
				if (task.level != null) {
					user.level = task.level;
				}
				
				context.postModifiedResource(user);
				
				FinishUpdateUserInformationTask fuuit = new FinishUpdateUserInformationTask(user.identifier, user.name, user.title, user.state, user.description);
				fuuit.level = user.level;
				context.handle(fuuit);
			}
		}

	}

	@Publish
	final class UpdateUserPasswordTaskHandler extends
			TaskMethodHandler<UpdateUserPasswordTask, None> {

		protected UpdateUserPasswordTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(
				final ResourceContext<User, UserImplement, UserImplement> context,
				final UpdateUserPasswordTask task) throws Throwable {
			final UserImplement user = context.modifyResource(task.userID);
			if (user == null) {
				return;
			} else {
				user.password = task.passwordNeedEncrypt ? task.newPassword == null ? GUID.MD5Of("") : GUID.MD5Of(task.newPassword) : GUID.valueOf(task.newPassword);
				context.postModifiedResource(user);
				context.handle(new FinishUpdateUserPasswordTask(user.identifier, user.password));
			}
		}
	}
	
	@Publish
	final class ReloadUserPasswordHandler extends TaskMethodHandler<ReloadUserPasswordTask, None> {

		protected ReloadUserPasswordHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(
				ResourceContext<User, UserImplement, UserImplement> context,
				ReloadUserPasswordTask task) throws Throwable {

			List<UserEntry> userInformationList = context.getList(UserEntry.class);
			Map<GUID, UserEntry> indexes = new HashMap<GUID,UserEntry>();
			for (UserEntry ue : userInformationList) {
				indexes.put(ue.identifier, ue);
			}
			for (User user : context.getList(User.class)) {
				UserImplement userImpl = context.modifyResource(user.getID());
				UserEntry ue = indexes.get(user.getID());
				if (ue == null) {
					continue;
				}
				userImpl.password = ue.password;
				context.postModifiedResource(userImpl);
			}
		}
		
	}
	
	@Publish
	final class LoadNewUserTaskHandler extends TaskMethodHandler<LoadNewUserTask, None> {

		protected LoadNewUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(
				ResourceContext<User, UserImplement, UserImplement> context,
				LoadNewUserTask task) throws Throwable {
			reloadResources(context);
		}
		
	}
}