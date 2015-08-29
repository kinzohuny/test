package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.Actor;
import com.jiuqi.dna.core.auth.AuthorityedResourceStub;
import com.jiuqi.dna.core.auth.AuthorizedResourceCategoryItem;
import com.jiuqi.dna.core.auth.AuthorizedResourceItem;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.auth.SetRoleAuthEvent;
import com.jiuqi.dna.core.auth.SetUserAuthEvent;
import com.jiuqi.dna.core.auth.SetUserAuthEvent.AuthItem;
import com.jiuqi.dna.core.impl.Cache.CustomGroupSpace;
import com.jiuqi.dna.core.impl.IdentifyResourceService.DeleteIdentifyTask;
import com.jiuqi.dna.core.impl.IdentifyResourceService.NewIdentifyTask;
import com.jiuqi.dna.core.misc.MissingObjectException;
import com.jiuqi.dna.core.resource.ResourceToken;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.auth.AuthorityInheritPath;
import com.jiuqi.dna.core.spi.auth.AuthorityItem;
import com.jiuqi.dna.core.spi.auth.AuthorizableResourceCategoryItem;
import com.jiuqi.dna.core.spi.auth.ClearRoleAuthorityTask;
import com.jiuqi.dna.core.spi.auth.ClearUserAuthorityTask;
import com.jiuqi.dna.core.spi.auth.DeleteRoleOrganizationMappingTask;
import com.jiuqi.dna.core.spi.auth.DeleteUserOrganizationMappingTask;
import com.jiuqi.dna.core.spi.auth.DuplicateAuthorityTask;
import com.jiuqi.dna.core.spi.auth.GetAuthedResourceStubByFacadeClass;
import com.jiuqi.dna.core.spi.auth.GetAuthorityInheritPathKey;
import com.jiuqi.dna.core.spi.auth.GetAuthorizedResCategoryItemForRoleKey;
import com.jiuqi.dna.core.spi.auth.GetAuthorizedResCategoryItemForUserKey;
import com.jiuqi.dna.core.spi.auth.GetResourceCategoryStubByFacadeClass;
import com.jiuqi.dna.core.spi.auth.GetRoleAssignInfoForIdentifyKey;
import com.jiuqi.dna.core.spi.auth.GetRoleAssignInfoForRoleKey;
import com.jiuqi.dna.core.spi.auth.GetRoleAssignInfoForUserKey;
import com.jiuqi.dna.core.spi.auth.GetSubAuthorizedResourceItemsForRoleKey;
import com.jiuqi.dna.core.spi.auth.GetSubAuthorizedResourceItemsForUserKey;
import com.jiuqi.dna.core.spi.auth.HasIdentifySettedAuthorityKey;
import com.jiuqi.dna.core.spi.auth.MaintainActorAuthorityTask;
import com.jiuqi.dna.core.spi.auth.MaintainRoleAuthorityTask;
import com.jiuqi.dna.core.spi.auth.MaintainUserAuthorityTask;
import com.jiuqi.dna.core.spi.auth.ResetAuthorityTask;
import com.jiuqi.dna.core.spi.auth.ResourceCategoryStub;
import com.jiuqi.dna.core.spi.auth.UpdateIdentifyRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.UpdateRoleAuthorityTask;
import com.jiuqi.dna.core.spi.auth.UpdateUserAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishClearRoleAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishClearUserAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishCreateACVersionTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDeleteACVersionTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDuplicateAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateIdentifyRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateUserRoleAssignTask;
import com.jiuqi.dna.core.type.GUID;

@SuppressWarnings("deprecation")
final class CacheAccessControlService extends ServiceBase<ContextImpl<?, ?, ?>> {

	private static final String TITLE;

	static {
		TITLE = "缓存访问控制服务";
	}

	protected CacheAccessControlService() {
		super(TITLE);
	}

	@Publish
	final class AccessControlCacheItemOfGroupListProvider extends
			ResultListProvider<AuthorizableResourceCategoryItem> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final List<AuthorizableResourceCategoryItem> resultList)
				throws Throwable {
			resultList.addAll(CacheAccessControlService.this.site.cache.ACGroupContainer.getAccessControlCacheItemOfGroup());
		}

	}

	@Publish
	final class UpdateUserRoleAssignTaskHandler
			extends
			TaskMethodHandler<com.jiuqi.dna.core.spi.auth.UpdateUserRoleAssignTask, None> {

		protected UpdateUserRoleAssignTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final com.jiuqi.dna.core.spi.auth.UpdateUserRoleAssignTask task)
				throws Throwable {
			final CacheHolder<User, ?, ?> userHolder = context.findResourceToken(User.class, task.userID);
			if (userHolder == null) {
				return;
			} else {
				userHolder.tryRemoveAllReference(Role.class, context.transaction);
				final ArrayList<GUID> assignedList = new ArrayList<GUID>();
				if (task.assignActorIDList.size() > 0) {
					for (int index = 0, endIndex = task.assignActorIDList.size(); index < endIndex; index++) {
						final GUID roleGUIDIdentifier = task.assignActorIDList.get(index);
						if (assignedList.contains(roleGUIDIdentifier)) {
							continue;
						}
						final CacheHolder<Role, ?, ?> roleHolder = context.findResourceToken(Role.class, roleGUIDIdentifier);
						if (roleHolder != null) {
							userHolder.localTryCreateReference(roleHolder, context.transaction);
							assignedList.add(roleGUIDIdentifier);
						}
					}
				}
				context.handle(new FinishUpdateUserRoleAssignTask(task.userID, assignedList));
			}
		}

	}

	@Publish
	final class UpdateIdentifyRoleAssignTaskHandler extends
			TaskMethodHandler<UpdateIdentifyRoleAssignTask, None> {

		protected UpdateIdentifyRoleAssignTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final UpdateIdentifyRoleAssignTask task) throws Throwable {
			final CacheHolder<User, ?, ?> userHolder = context.findResourceToken(User.class, task.userID);
			if (userHolder == null) {
				return;
			}
			CacheHolder<Identify, ?, ?> identifyHolder = context.findResourceToken(Identify.class, task.userID, task.identifyID);
			if (identifyHolder == null) {
				final GUID targetIdentityIdentifier = task.identifyID;
				context.handle(new NewIdentifyTask(task.userID, targetIdentityIdentifier));
				identifyHolder = (context.findResourceToken(Identify.class, task.userID, targetIdentityIdentifier));
				context.handle(new FinishCreateACVersionTask(true, identifyHolder.getFacade(context.transaction).identifier, task.userID, targetIdentityIdentifier));
			}
			identifyHolder.tryRemoveAllReference(Role.class, context.transaction);
			final ArrayList<GUID> assignedList = new ArrayList<GUID>();
			if (task.assignRoleIDList.size() > 0) {
				for (int index = 0, endIndex = task.assignRoleIDList.size(); index < endIndex; index++) {
					final GUID roleGUIDIdentifier = task.assignRoleIDList.get(index);
					if (assignedList.contains(roleGUIDIdentifier)) {
						continue;
					}
					final CacheHolder<Role, ?, ?> roleHolder = context.findResourceToken(Role.class, roleGUIDIdentifier);
					if (roleHolder != null) {
						identifyHolder.localTryCreateReference(roleHolder, context.transaction);
						assignedList.add(roleGUIDIdentifier);
					}
				}
			}
			context.handle(new FinishUpdateIdentifyRoleAssignTask(task.userID, task.identifyID, assignedList));
		}

	}

	@Publish
	final class ForUserAssignedActorListProvider extends
			OneKeyResultListProvider<Actor, GetRoleAssignInfoForUserKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetRoleAssignInfoForUserKey key, List<Actor> resultList)
				throws Throwable {
			final CacheHolder<User, ?, ?> userHolder = context.findResourceToken(User.class, key.userID);
			if (userHolder == null) {
				return;
			} else {
				resultList.addAll(userHolder.tryGetReferences(Role.class, null, null, context.transaction));
				resultList.add(userHolder.tryGetValue(context.transaction));
			}
		}

	}

	@Publish
	final class ForUserAssignedRoleListProvider extends
			OneKeyResultListProvider<Role, GetRoleAssignInfoForUserKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetRoleAssignInfoForUserKey key, List<Role> resultList)
				throws Throwable {
			final CacheHolder<User, ?, ?> userHolder = context.findResourceToken(User.class, key.userID);
			if (userHolder == null) {
				return;
			} else {
				resultList.addAll(userHolder.tryGetReferences(Role.class, null, null, context.transaction));
			}
		}

	}

	@Publish
	final class ForIdentifyAssignedRoleListProvider extends
			OneKeyResultListProvider<Role, GetRoleAssignInfoForIdentifyKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetRoleAssignInfoForIdentifyKey key, List<Role> resultList)
				throws Throwable {
			final CacheHolder<Identify, ?, ?> identifyHolder = context.findResourceToken(Identify.class, key.userID, key.identifyID);
			if (identifyHolder == null) {
				return;
			} else {
				resultList.addAll(identifyHolder.tryGetReferences(Role.class, null, null, context.transaction));
			}
		}

	}

	@Publish
	final class ForRoleAssignedUserListProvider extends
			OneKeyResultListProvider<User, GetRoleAssignInfoForRoleKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetRoleAssignInfoForRoleKey key, List<User> resultList)
				throws Throwable {
			final CacheHolder<Role, ?, ?> roleHolder = context.findResourceToken(Role.class, key.roleID);
			if (roleHolder == null) {
				return;
			} else {
				resultList.addAll(roleHolder.tryGetReferencesBy(User.class, context.transaction));
			}
		}

	}

	@Publish
	final class DeleteUserACVersionTaskHandler extends
			TaskMethodHandler<DeleteUserOrganizationMappingTask, None> {

		protected DeleteUserACVersionTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final DeleteUserOrganizationMappingTask task) throws Throwable {
			if (AccessControlConstants.isDefaultACVersion(task.orgID)) {
				throw new UnsupportedOperationException("不支持修改默认权限版本。");
			}
			final UserCacheHolder userHolder = (UserCacheHolder) context.findResourceToken(User.class, task.actorID);
			if (userHolder != null) {
				final GUID identityIdentifier = task.orgID;
				final IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.actorID, identityIdentifier));
				if (identifyHolder != null) {
					userHolder.localTryRemoveReference(identifyHolder, context.transaction);
					DeleteIdentifyTask delTask = new DeleteIdentifyTask(task.actorID, task.orgID);
					context.handle(delTask);
					context.handle(new FinishDeleteACVersionTask(true, task.actorID, identityIdentifier));
				}
			}
		}

	}

	@Publish
	final class DeleteRoleACVersionTaskHandler extends
			TaskMethodHandler<DeleteRoleOrganizationMappingTask, None> {

		protected DeleteRoleACVersionTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final DeleteRoleOrganizationMappingTask task) throws Throwable {
			// to do nothing
		}
	}

	@Publish
	final class HasIdentifySettedAuthorityResultProvider extends
			OneKeyResultProvider<Boolean, HasIdentifySettedAuthorityKey> {

		@Override
		protected final Boolean provide(final ContextImpl<?, ?, ?> context,
				final HasIdentifySettedAuthorityKey key) throws Throwable {
			final UserCacheHolder userHolder = (UserCacheHolder) (context.findResourceToken(User.class, key.userIdentifier));
			if (userHolder == null) {
				return false;
			}
			if (AccessControlConstants.isDefaultACVersion(key.identifyIdentifier)) {
				if (key.operationAuthority) {
					return !AccessControlHelper.isEmpty(userHolder.tryGetOperationACL(context.transaction));
				} else {
					return !AccessControlHelper.isEmpty(userHolder.tryGetAccreditACL(context.transaction));
				}
			} else {
				final IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, key.userIdentifier, key.identifyIdentifier));
				if (identifyHolder == null) {
					return false;
				} else {
					if (key.operationAuthority) {
						return !AccessControlHelper.isEmpty(identifyHolder.tryGetOperationACL(context.transaction));
					} else {
						return !AccessControlHelper.isEmpty(identifyHolder.tryGetAccreditACL(context.transaction));
					}
				}
			}
		}

	}

	@Publish
	final class FillUserAuthorityTaskHandler
			extends
			TaskMethodHandler<MaintainUserAuthorityTask, MaintainUserAuthorityTask.Method> {

		protected FillUserAuthorityTaskHandler() {
			super(MaintainUserAuthorityTask.Method.FILL_AUTHORIZED_ITEM, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final MaintainUserAuthorityTask task) throws Throwable {
			final CacheHolder<User, ?, ?> user = context.findResourceToken(User.class, task.actorID);
			if (user instanceof UserCacheHolder) {
				final UserCacheHolder userHolder = (UserCacheHolder) user;
				final Transaction transaction = context.transaction;
				final long[] userACL;
				final GUID identityIdentifier = task.orgID;
				final AccessController accessController;
				if (AccessControlConstants.isDefaultACVersion(identityIdentifier)) {
					if (task.operationAuthority) {
						userACL = userHolder.tryGetOperationACL(transaction);
					} else {
						userACL = userHolder.tryGetAccreditACL(transaction);
					}
					accessController = AccessControlPolicy.CURRENT_POLICY.newUserAccessController(userHolder, task.operationAuthority, transaction);
				} else {
					final IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.actorID, identityIdentifier));
					if (identifyHolder != null) {
						if (task.operationAuthority) {
							userACL = identifyHolder.tryGetOperationACL(transaction);
						} else {
							userACL = identifyHolder.tryGetAccreditACL(transaction);
						}
						accessController = AccessControlPolicy.CURRENT_POLICY.newIdentifyAccessController(user.getFacade(context.transaction), identifyHolder, task.operationAuthority, transaction);
					} else {
						throw new MissingObjectException("找不到指定用户的指定身份。");
					}
				}
				final UserAccessController loginUserAccessController = UserAccessController.allocUserAccessController(context.session.getUser(), task.orgID, false, context);
				for (AuthorityEntry authorityEntry : task.authorizedItemList) {
					authorityEntry.fill(userACL, accessController, loginUserAccessController);
				}
			}
		}
	}

	@Publish
	final class FillRoleAuthorityTaskHandler
			extends
			TaskMethodHandler<MaintainRoleAuthorityTask, MaintainRoleAuthorityTask.Method> {

		protected FillRoleAuthorityTaskHandler() {
			super(MaintainRoleAuthorityTask.Method.FILL_AUTHORIZED_ITEM, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final MaintainRoleAuthorityTask task) throws Throwable {
			final RoleCacheHolder roleHolder = (RoleCacheHolder) context.findResourceToken(Role.class, task.actorID);
			if (roleHolder != null) {
				final Transaction transaction = context.transaction;
				final long[] roleACL;
				if (task.operationAuthority) {
					roleACL = roleHolder.tryGetOperationACL(transaction);
				} else {
					roleACL = roleHolder.tryGetAccreditACL(transaction);
				}
				final RoleAccessController roleAccessController = AccessControlPolicy.CURRENT_POLICY.newRoleAccessController(roleHolder, task.operationAuthority, transaction);
				final UserAccessController loginUserAccessController = UserAccessController.allocUserAccessController(context.session.getUser(), task.orgID, false, context);
				for (AuthorityEntry authorityEntry : task.authorizedItemList) {
					authorityEntry.fill(roleACL, roleAccessController, loginUserAccessController);
				}
			}
		}

	}

	@Publish
	final class UpdateUserAuthorityTaskHandler
			extends
			TaskMethodHandler<MaintainUserAuthorityTask, MaintainUserAuthorityTask.Method> {

		protected UpdateUserAuthorityTaskHandler() {
			super(MaintainUserAuthorityTask.Method.UPDATE_AUTHORITY, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final MaintainUserAuthorityTask task) throws Throwable {
			if (CacheAccessControlService.tidyAuthorityItemList(task.authorizedItemList)) {
				final UserCacheHolder userHolder = (UserCacheHolder) context.findResourceToken(User.class, task.actorID);
				if (userHolder != null) {
					if (AccessControlConstants.isDefaultACVersion(task.orgID)) {
						if (task.operationAuthority) {
							CacheAccessControlService.updateActorOperationAuthority(context, userHolder, task.authorizedItemList);
						}
					} else {
						final GUID identityIdentifier = task.orgID;
						IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.actorID, identityIdentifier));
						if (identifyHolder == null) {
							context.handle(new NewIdentifyTask(task.actorID, identityIdentifier));
							identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.actorID, identityIdentifier));
							context.handle(new FinishCreateACVersionTask(true, identifyHolder.getFacade(context.transaction).identifier, task.actorID, identityIdentifier));
						}
						if (task.operationAuthority) {
							CacheAccessControlService.updateActorOperationAuthority(context, identifyHolder, task.authorizedItemList);
						}
					}
					context.handle(new FinishUpdateAuthorityTask(true, task.actorID, task.operationAuthority, task.orgID, task.authorizedItemList));
					if (task.actorID.equals(context.session.getUser().getID())) {
						context.resetACLCache();
					}
					
					// 发送事件
					List<AuthItem> aies = new ArrayList<SetUserAuthEvent.AuthItem>();
					for (AuthorityItem item : task.authorizedItemList) {
						if (item.authes.size() == 0) {
							continue;
						}
						if (item.getResourceToken() instanceof CacheHolder) {
							CacheHolder hoder = (CacheHolder)item.getResourceToken();
							String category = null;
							if (hoder.ownGroup.ownSpace.identifier != null) {
								category = hoder.ownGroup.ownSpace.identifier.toString();
							}
							AuthItem ai = new AuthItem(item.getItemIdentifier(), item.getResourceToken().getFacadeClass().getName(), category, item.authes);
							aies.add(ai);
						}
					}
					if (aies.size() > 0) {
						context.dispatch(new SetUserAuthEvent(task.actorID, task.orgID, task.operationAuthority, aies));
					}
				}
			}
		}
	}

	@Publish
	final class UpdateRoleAuthorityTaskHandler
			extends
			TaskMethodHandler<MaintainRoleAuthorityTask, MaintainRoleAuthorityTask.Method> {

		protected UpdateRoleAuthorityTaskHandler() {
			super(MaintainRoleAuthorityTask.Method.UPDATE_AUTHORITY, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final MaintainRoleAuthorityTask task) throws Throwable {
			if (CacheAccessControlService.tidyAuthorityItemList(task.authorizedItemList)) {
				final RoleCacheHolder roleHolder = (RoleCacheHolder) context.findResourceToken(Role.class, task.actorID);
				if (roleHolder != null) {
					if (task.operationAuthority) {
						CacheAccessControlService.updateActorOperationAuthority(context, roleHolder, task.authorizedItemList);
					}
					context.handle(new FinishUpdateAuthorityTask(false, task.actorID, task.operationAuthority, task.orgID, task.authorizedItemList));

					// 发送事件
					List<AuthItem> aies = new ArrayList<SetUserAuthEvent.AuthItem>();
					for (AuthorityItem item : task.authorizedItemList) {
						if (item.authes.size() == 0) {
							continue;
						}
						if (item.getResourceToken() instanceof CacheHolder) {
							CacheHolder hoder = (CacheHolder)item.getResourceToken();
							String category = null;
							if (hoder.ownGroup.ownSpace.identifier != null) {
								category = hoder.ownGroup.ownSpace.identifier.toString();
							}
							AuthItem ai = new AuthItem(item.getItemIdentifier(), item.getResourceToken().getFacadeClass().getName(), category, item.authes);
							aies.add(ai);
						}
						
					}
					if (aies.size() > 0) {
						context.dispatch(new SetRoleAuthEvent(task.actorID, task.operationAuthority, aies));
					}
				}
			}
		}
	}

	@Publish
	final class ClearUserAuthorityTaskHandler extends
			TaskMethodHandler<ClearUserAuthorityTask, None> {

		protected ClearUserAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final ClearUserAuthorityTask task) throws Throwable {
			final UserCacheHolder userHolder = (UserCacheHolder) context.findResourceToken(User.class, task.actorID);
			if (userHolder == null) {
				return;
			}
			if (task.operationAuthority) {
				if (AccessControlConstants.isDefaultACVersion(task.orgID)) {
					userHolder.localModifyOperationACL(context.transaction);
					userHolder.postModifiedOperationACL(null, context.transaction);
				} else {
					final GUID identityIdentifier = task.orgID;
					IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.actorID, identityIdentifier));
					if (identifyHolder != null) {
						identifyHolder.localModifyOperationACL(context.transaction);
						identifyHolder.postModifiedOperationACL(null, context.transaction);
					}
				}
			}
			context.handle(new FinishClearUserAuthorityTask(task.actorID, task.orgID, task.operationAuthority));
		}

	}

	@Publish
	final class ClearRoleAuthorityTaskHandler extends
			TaskMethodHandler<ClearRoleAuthorityTask, None> {

		protected ClearRoleAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final ClearRoleAuthorityTask task) throws Throwable {
			final RoleCacheHolder roleHolder = (RoleCacheHolder) context.findResourceToken(Role.class, task.actorID);
			if (roleHolder == null) {
				return;
			}
			if (task.operationAuthority) {
				roleHolder.localModifyOperationACL(context.transaction);
				roleHolder.postModifiedOperationACL(null, context.transaction);
			}
			context.handle(new FinishClearRoleAuthorityTask(task.actorID, task.orgID, task.operationAuthority));
		}

	}

	@Publish
	final class DuplicateAuthorityTaskHandler extends
			TaskMethodHandler<DuplicateAuthorityTask, None> {

		protected DuplicateAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final DuplicateAuthorityTask task) throws Throwable {
			if (task.sourceActorID.equals(task.targetActorID) && ((task.sourceOrgID == task.targetOrgID) || (AccessControlConstants.isDefaultACVersion(task.sourceOrgID) ? AccessControlConstants.isDefaultACVersion(task.targetOrgID) : task.sourceOrgID.equals(task.targetOrgID)))) {
				return;
			}
			final long[] sourceACL;
			CacheHolder<?, ?, ?> cacheHolder = context.findResourceToken(User.class, task.sourceActorID);
			final GUID sourceIdentityIdentifier = task.sourceOrgID;
			if (cacheHolder == null) {
				cacheHolder = context.findResourceToken(Role.class, task.sourceActorID);
				if (cacheHolder == null) {
					return;
				}
			}
			if (!(AccessControlConstants.isDefaultACVersion(task.sourceOrgID) || cacheHolder.getFacadeClass() == Role.class)) {
				final IdentifyCacheHolder identifyHolder;
				if ((identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.sourceActorID, sourceIdentityIdentifier))) == null) {
					return;
				}
				sourceACL = identifyHolder.tryGetOperationACL(context.transaction);
			} else {
				sourceACL = ((ActorCacheHolder<?, ?, ?>) cacheHolder).tryGetOperationACL(context.transaction);
			}
			cacheHolder = context.findResourceToken(User.class, task.targetActorID);
			if (cacheHolder == null) {
				cacheHolder = context.findResourceToken(Role.class, task.targetActorID);
				if (cacheHolder == null) {
					return;
				}
			}
			if (task.operationAuthority) {
				final Transaction transaction = context.transaction;
				if (!(AccessControlConstants.isDefaultACVersion(task.targetOrgID) || cacheHolder.getFacadeClass() == Role.class)) {
					final GUID targetIdentityIdentifier = task.targetOrgID;
					IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.targetActorID, targetIdentityIdentifier));
					if (identifyHolder == null) {
						context.handle(new NewIdentifyTask(task.targetActorID, targetIdentityIdentifier));
						identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, task.targetActorID, targetIdentityIdentifier));
						context.handle(new FinishCreateACVersionTask(true, identifyHolder.getFacade(transaction).identifier, task.targetActorID, targetIdentityIdentifier));
					}
					identifyHolder.localModifyOperationACL(transaction);
					identifyHolder.postModifiedOperationACL(sourceACL, transaction);
				} else {
					final ActorCacheHolder<?, ?, ?> targetActor = (ActorCacheHolder<?, ?, ?>) cacheHolder;
					targetActor.localModifyOperationACL(transaction);
					targetActor.postModifiedOperationACL(sourceACL, transaction);
				}
			}
			context.handle(new FinishDuplicateAuthorityTask(task.sourceActorID, task.sourceOrgID, task.targetActorID, task.targetOrgID, task.operationAuthority));
		}
	}

	@Publish
	final class AuthorityInheritPathProvider
			extends
			OneKeyResultProvider<AuthorityInheritPath, GetAuthorityInheritPathKey> {

		@Override
		protected final AuthorityInheritPath provide(
				final ContextImpl<?, ?, ?> context,
				final GetAuthorityInheritPathKey key) throws Throwable {
			final ResourceToken<?> resourceOfBaseNode = key.resourceOfBaseNode;
			final AuthorityInheritPathImplement path = new AuthorityInheritPathImplement(resourceOfBaseNode);
			AccessControlPolicy.CURRENT_POLICY.buildAuthorityInheritPath(path, context.transaction);
			return path;
		}

	}

	@Publish
	final class ResetAuthorityHandler extends
			TaskMethodHandler<ResetAuthorityTask, None> {

		protected ResetAuthorityHandler() {
			super(None.NONE, null);
		}

		@Override
		protected void handle(ContextImpl<?, ?, ?> context,
				ResetAuthorityTask task) throws Throwable {

			for (User user : context.getList(User.class)) {
				ResourceToken<User> userToken = context.findResourceToken(User.class, user.getID());
				if (userToken == null) {
					continue;
				}
				UserCacheHolder userHoder = (UserCacheHolder) userToken;
				userHoder.resetACLInCluster(context.transaction);
			}

			for (Role role : context.getList(Role.class)) {
				ResourceToken<Role> roleToken = context.findResourceToken(Role.class, role.getID());
				if (roleToken == null) {
					continue;
				}
				RoleCacheHolder roleHoder = (RoleCacheHolder) roleToken;
				roleHoder.resetACLInCluster(context.transaction);
			}

			for (Identify iden : context.getList(Identify.class)) {
				ResourceToken<Identify> idenToken = context.findResourceToken(Identify.class, iden.userIdentifier, iden.identifyIdentifier);
				if (idenToken == null) {
					continue;
				}
				IdentifyCacheHolder idenHoder = (IdentifyCacheHolder) idenToken;
				idenHoder.resetACLInCluster(context.transaction);
			}

		}

	}

	private static final boolean tidyAuthorityItemList(
			final List<AuthorityItem> authorityItemList) {
		return authorityItemList != null && authorityItemList.size() != 0;
	}

	private static final void updateActorOperationAuthority(
			final ContextImpl<?, ?, ?> context,
			final ActorCacheHolder<?, ?, ?> actorHolder,
			final List<AuthorityItem> authorizedItemList) {
		final Transaction transaction = context.transaction;
		long[] ACL = actorHolder.localModifyOperationACL(transaction);
		for (AuthorityEntry authorityEntry : authorizedItemList) {
			ACL = AccessControlHelper.setAuthority(ACL, authorityEntry.getACLongIdentifier(), authorityEntry.authorityCode);
		}
		actorHolder.postModifiedOperationACL(ACL, transaction);
	}

	private static final void updateActorOperationAuthority(
			final ContextImpl<?, ?, ?> context,
			final IdentifyCacheHolder identifyHolder,
			final List<AuthorityItem> authorizedItemList) {
		final Transaction transaction = context.transaction;
		long[] ACL = identifyHolder.localModifyOperationACL(transaction);
		for (AuthorityEntry authorityEntry : authorizedItemList) {
			ACL = AccessControlHelper.setAuthority(ACL, authorityEntry.getACLongIdentifier(), authorityEntry.authorityCode);
		}
		identifyHolder.postModifiedOperationACL(ACL, transaction);
	}

	@Deprecated
	private static final class AuthorityItemImplement extends AuthorityItem {

		protected AuthorityItemImplement(final ResourceToken<?> resourceToken) {
			super(resourceToken);
		}

	}

	@Deprecated
	@Publish
	final class DeprecatedForUserAuthorityItemOfGroupListProvider
			extends
			OneKeyResultListProvider<AuthorizedResourceCategoryItem, GetAuthorizedResCategoryItemForUserKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetAuthorizedResCategoryItemForUserKey key,
				final List<AuthorizedResourceCategoryItem> resultList)
				throws Throwable {
			final UserCacheHolder userHolder = (UserCacheHolder) context.getResourceToken(User.class, key.actorID);
			final long[] ACL;
			if (AccessControlConstants.isDefaultACVersion(key.orgID)) {
				ACL = userHolder.tryGetOperationACL(context.transaction);
			} else {
				final GUID identityIdentifier = key.orgID;
				final IdentifyCacheHolder identifyHolder = (IdentifyCacheHolder) (context.findResourceToken(Identify.class, key.actorID, identityIdentifier));
				if (identifyHolder != null) {
					ACL = identifyHolder.tryGetOperationACL(context.transaction);
				} else {
					throw new MissingObjectException("找不到指定用户的指定身份。");
				}
			}
			if (ACL == AccessControlHelper.EMPTY_ACL) {
				for (AccessControlCacheHolderOfGroup groupHolder : CacheAccessControlService.this.site.cache.ACGroupContainer.getAccessControlCacheItemOfGroup()) {
					final CacheGroup<?, ?, ?> group = groupHolder.cacheGroup;
					resultList.add(new AuthorityItemOfGroup(group, 0));
				}
			} else {
				for (AccessControlCacheHolderOfGroup groupHolder : CacheAccessControlService.this.site.cache.ACGroupContainer.getAccessControlCacheItemOfGroup()) {
					final CacheGroup<?, ?, ?> group = groupHolder.cacheGroup;
					resultList.add(new AuthorityItemOfGroup(group, AccessControlHelper.getAuthority(ACL, group.accessControlInformation.ACLongIdentifier)));
				}
			}
		}
	}

	@Deprecated
	@Publish
	final class DeprecatedForRoleAuthorityItemOfGroupListProvider
			extends
			OneKeyResultListProvider<AuthorizedResourceCategoryItem, GetAuthorizedResCategoryItemForRoleKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetAuthorizedResCategoryItemForRoleKey key,
				final List<AuthorizedResourceCategoryItem> resultList)
				throws Throwable {
			final RoleCacheHolder roleHolder = (RoleCacheHolder) context.getResourceToken(Role.class, key.actorID);
			final long[] ACL = roleHolder.tryGetOperationACL(context.transaction);
			if (ACL == AccessControlHelper.EMPTY_ACL) {
				for (AccessControlCacheHolderOfGroup groupHolder : CacheAccessControlService.this.site.cache.ACGroupContainer.getAccessControlCacheItemOfGroup()) {
					final CacheGroup<?, ?, ?> group = groupHolder.cacheGroup;
					resultList.add(new AuthorityItemOfGroup(group, 0));
				}
			} else {
				for (AccessControlCacheHolderOfGroup groupHolder : CacheAccessControlService.this.site.cache.ACGroupContainer.getAccessControlCacheItemOfGroup()) {
					final CacheGroup<?, ?, ?> group = groupHolder.cacheGroup;
					resultList.add(new AuthorityItemOfGroup(group, AccessControlHelper.getAuthority(ACL, group.accessControlInformation.ACLongIdentifier)));
				}
			}
		}

	}

	@Deprecated
	@Publish
	final class DeprecatedForUserAuthorityItemOfItemListProvider
			extends
			OneKeyResultListProvider<AuthorizedResourceCategoryItem, GetSubAuthorizedResourceItemsForUserKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetSubAuthorizedResourceItemsForUserKey key,
				final List<AuthorizedResourceCategoryItem> resultList)
				throws Throwable {
			// TODO

		}

	}

	@Deprecated
	@Publish
	final class DeprecatedForRoleAuthorityItemOfItemListProvider
			extends
			OneKeyResultListProvider<AuthorizedResourceCategoryItem, GetSubAuthorizedResourceItemsForRoleKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetSubAuthorizedResourceItemsForRoleKey key,
				final List<AuthorizedResourceCategoryItem> resultList)
				throws Throwable {
			// TODO

		}

	}

	@Deprecated
	@Publish
	final class DeprecatedUpdateRoleAuthorityTaskHandler extends
			TaskMethodHandler<UpdateRoleAuthorityTask, None> {

		protected DeprecatedUpdateRoleAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final UpdateRoleAuthorityTask task) throws Throwable {
			if (task.authorityResourceTable == null || task.authorityResourceTable.size() == 0) {
				return;
			}
			final MaintainUserAuthorityTask maintainTask = new MaintainUserAuthorityTask(task.actorID, AccessControlConstants.orgnizationIdentifierToACVersion(task.orgID), true);
			final ArrayList<AccessControlCacheHolderOfGroup> groupHolders = CacheAccessControlService.this.site.cache.ACGroupContainer.getAccessControlCacheItemOfGroup();
			if (groupHolders != null) {
				for (AccessControlCacheHolderOfGroup groupHolder : groupHolders) {
					final CacheGroup.AccessControlInformation<?, ?, ?> accessControlInformationOfGroup = groupHolder.cacheGroup.accessControlInformation;
					if (accessControlInformationOfGroup.ACGUIDIdentifier.equals(task.resourceCategoryID)) {
						for (AuthorizedResourceItem authorizedResourcetem : task.authorityResourceTable) {
							if (authorizedResourcetem.getClass() == AuthorityItemOfGroup.class) {
								final AuthorityItemOfGroup authorityItemOfGroup = (AuthorityItemOfGroup) authorizedResourcetem;
								if (authorityItemOfGroup.itemGUID.equals(accessControlInformationOfGroup.ACGUIDIdentifier)) {
									final AuthorityItemImplement authorityItem = new AuthorityItemImplement(groupHolder);
									((AuthorityEntry) authorityItem).authorityCode = authorityItemOfGroup.authCode;
									maintainTask.authorizedItemList.add(authorityItem);
								}
							} else {
								final AuthorityItemOfItem authorityItemOfItem = (AuthorityItemOfItem) authorizedResourcetem;
								final CacheHolder<?, ?, ?> item = accessControlInformationOfGroup.accessControlIndex.findAccessControlHolder(authorityItemOfItem.itemGUID, context.transaction);
								if (item != null) {
									final AuthorityItemImplement authorityItem = new AuthorityItemImplement(item);
									((AuthorityEntry) authorityItem).authorityCode = authorityItemOfItem.authCode;
									maintainTask.authorizedItemList.add(authorityItem);
								}
							}
						}
						context.handle(maintainTask, MaintainActorAuthorityTask.Method.UPDATE_AUTHORITY);
					}
				}
			}
		}

	}

	@Deprecated
	@Publish
	final class DeprecatedUpdateUserAuthorityTaskHandler extends
			TaskMethodHandler<UpdateUserAuthorityTask, None> {

		protected DeprecatedUpdateUserAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final UpdateUserAuthorityTask task) throws Throwable {
			if (task.authorityResourceTable == null || task.authorityResourceTable.size() == 0) {
				return;
			}
			final MaintainRoleAuthorityTask maintainTask = new MaintainRoleAuthorityTask(task.actorID, AccessControlConstants.orgnizationIdentifierToACVersion(task.orgID), true);
			final ArrayList<AccessControlCacheHolderOfGroup> groupHolders = CacheAccessControlService.this.site.cache.ACGroupContainer.getAccessControlCacheItemOfGroup();
			if (groupHolders != null) {
				for (AccessControlCacheHolderOfGroup groupHolder : groupHolders) {
					final CacheGroup.AccessControlInformation<?, ?, ?> accessControlInformationOfGroup = groupHolder.cacheGroup.accessControlInformation;
					if (accessControlInformationOfGroup.ACGUIDIdentifier.equals(task.resourceCategoryID)) {
						for (AuthorizedResourceItem authorizedResourcetem : task.authorityResourceTable) {
							if (authorizedResourcetem.getClass() == AuthorityItemOfGroup.class) {
								final AuthorityItemOfGroup authorityItemOfGroup = (AuthorityItemOfGroup) authorizedResourcetem;
								if (authorityItemOfGroup.itemGUID.equals(accessControlInformationOfGroup.ACGUIDIdentifier)) {
									final AuthorityItemImplement authorityItem = new AuthorityItemImplement(groupHolder);
									((AuthorityEntry) authorityItem).authorityCode = authorityItemOfGroup.authCode;
									maintainTask.authorizedItemList.add(authorityItem);
								}
							} else {
								final AuthorityItemOfItem authorityItemOfItem = (AuthorityItemOfItem) authorizedResourcetem;
								final CacheHolder<?, ?, ?> item = accessControlInformationOfGroup.accessControlIndex.findAccessControlHolder(authorityItemOfItem.itemGUID, context.transaction);
								if (item != null) {
									final AuthorityItemImplement authorityItem = new AuthorityItemImplement(item);
									((AuthorityEntry) authorityItem).authorityCode = authorityItemOfItem.authCode;
									maintainTask.authorizedItemList.add(authorityItem);
								}
							}
						}
						context.handle(maintainTask, MaintainActorAuthorityTask.Method.UPDATE_AUTHORITY);
					}
				}
			}
		}

	}
	
	@Publish
	protected final class AuthorityedResourceStubProvider extends ResultListProvider<AuthorityedResourceStub> {

		@Override
		protected void provide(ContextImpl<?, ?, ?> context,
				List<AuthorityedResourceStub> resultList) throws Throwable {

			for (ResourceServiceBase<?, ?, ?> service : ApplicationImpl.getDefaultApp().getDefaultSite().cache.getResourceServiceContainer().values()) {
				if (service.getAuthorizableProvider() == null) {
					continue;
				}
				AuthorityedResourceStub stub =  new AuthorityedResourceStub(service.facadeClass, service.getAuthorizableProvider().getOperations(), service.getTitle());
				resultList.add(stub);
			}
		}
		
	}
	
	@Publish
	protected final class AuthedResourceStubProviderByFacade extends OneKeyResultProvider<AuthorityedResourceStub, GetAuthedResourceStubByFacadeClass> {

		@Override
		protected void provide(ContextImpl<?, ?, ?> context,
				GetAuthedResourceStubByFacadeClass key,
				List<AuthorityedResourceStub> resultList) throws Throwable {
			
			
			
		}

		@Override
		protected AuthorityedResourceStub provide(ContextImpl<?, ?, ?> context,
				GetAuthedResourceStubByFacadeClass key) throws Throwable {
			for (ResourceServiceBase<?, ?, ?> service : ApplicationImpl.getDefaultApp().getDefaultSite().cache.getResourceServiceContainer().values()) {
				if (service.getAuthorizableProvider() == null) {
					continue;
				}
				if (service.facadeClass.getName().equals(key.getFacadeClass())) {
					return new AuthorityedResourceStub(service.facadeClass, service.getAuthorizableProvider().getOperations(), service.getTitle());
				}
			}
			return null;
		}
		
	}
	
	@Publish
	protected final class AuthedResourceCategoryProvider extends OneKeyResultListProvider<ResourceCategoryStub, GetResourceCategoryStubByFacadeClass> {

		@Override
		protected void provide(ContextImpl<?, ?, ?> context,
				GetResourceCategoryStubByFacadeClass key, List<ResourceCategoryStub> resultList)
				throws Throwable {

			Class<?> clazz = context.find(Class.class, key.getFacadeClassName());
			for (CustomGroupSpace space : ApplicationImpl.getDefaultApp().getDefaultSite().cache.getCustomGroupSpace()) {
				if (space != null) {
					@SuppressWarnings("rawtypes")
					CacheGroup group = space.findGroup(clazz, context.transaction);
					if (group != null) {
						ResourceCategoryStub rcs = new ResourceCategoryStub(space.identifier, group.title);
						rcs.setResCategoryId(group.accessControlInformation.ACGUIDIdentifier);
						resultList.add(rcs);
					}
				}
			}
			
			/*CacheGroupSpace defSpace = ApplicationImpl.getDefaultApp().getDefaultSite().cache.defaultGroupSpace;
			if (defSpace != null) {
				@SuppressWarnings("rawtypes")
				CacheGroup group = defSpace.findGroup(clazz, context.transaction);
				if (group != null) {
					ResourceCategoryStub rcs = new ResourceCategoryStub(defSpace.identifier, group.title);
					rcs.setResCategoryId(group.accessControlInformation.ACGUIDIdentifier);
					resultList.add(rcs);
				}
			}*/
		}
		
	}

}
