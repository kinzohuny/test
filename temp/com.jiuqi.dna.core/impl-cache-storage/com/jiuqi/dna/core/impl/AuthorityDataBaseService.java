package com.jiuqi.dna.core.impl;

import java.util.List;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.User;
import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Role;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.da.RecordSetField;
import com.jiuqi.dna.core.da.RecordSetFieldContainer;
import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.def.query.DeleteStatementDeclare;
import com.jiuqi.dna.core.def.query.DerivedQueryDeclare;
import com.jiuqi.dna.core.def.query.InsertStatementDeclare;
import com.jiuqi.dna.core.def.query.QuTableRefDeclare;
import com.jiuqi.dna.core.def.query.QueryColumnDeclare;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.def.query.SQLFunc;
import com.jiuqi.dna.core.def.query.UpdateStatementDeclare;
import com.jiuqi.dna.core.invoke.SimpleTask;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.auth.GetUserOrgMapForOrgKey;
import com.jiuqi.dna.core.spi.auth.UpdateResourceCategoryTask;
import com.jiuqi.dna.core.spi.auth.callback.AccessControlEntry;
import com.jiuqi.dna.core.spi.auth.callback.AccreditAuthorityInformation;
import com.jiuqi.dna.core.spi.auth.callback.FinishClearRoleAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishClearUserAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishCreateACVersionTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishCreateRoleTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishCreateUserTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDeleteACVersionTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDeleteRoleTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDeleteUserTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishDuplicateAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeAccreditAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeOperationAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeRoleTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishInitializeUserTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateAuthorityTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateIdentifyRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateRoleInformationTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateUserInformationTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateUserPasswordTask;
import com.jiuqi.dna.core.spi.auth.callback.FinishUpdateUserRoleAssignTask;
import com.jiuqi.dna.core.spi.auth.callback.GetAccreditAuthorityInformationKey;
import com.jiuqi.dna.core.spi.auth.callback.GetOperationAuthorityInformationKey;
import com.jiuqi.dna.core.spi.auth.callback.IdentifyMapEntry;
import com.jiuqi.dna.core.spi.auth.callback.OperationAuthorityInformation;
import com.jiuqi.dna.core.spi.auth.callback.RoleAssignEntry;
import com.jiuqi.dna.core.spi.auth.callback.RoleEntry;
import com.jiuqi.dna.core.spi.auth.callback.UserEntry;
import com.jiuqi.dna.core.type.GUID;

final class AuthorityDataBaseService extends ServiceBase<ContextImpl<?, ?, ?>> {

	static final String OLD_USERSERVICENAME = "com.jiuqi.dna.core.impl.CacheUserService";

	static final String OLD_ROLESERVICENAME = "com.jiuqi.dna.core.impl.CacheRoleService";

	static final GUID OLD_DEFAULTUSERGROUPID = GUID.MD5Of(OLD_USERSERVICENAME);

	static final GUID OLD_DEFAULTROLEGROUPID = GUID.MD5Of(OLD_ROLESERVICENAME);

	private static final String TITLE;

	private static final boolean CLEAR_DIRTYDATA;

	static {
		TITLE = "权限数据库服务";
		CLEAR_DIRTYDATA = Boolean.getBoolean("com.jiuqi.dna.core.cache.accesscontrol.cleardirtydata");
	}

	protected AuthorityDataBaseService(final TD_CoreAuthUser td_CoreAuthUser,
			final TD_CoreAuthRole td_CoreAuthRole,
			final TD_CoreAuthRA td_CoreAuthRA,
			final TD_CoreAuthUOM td_CoreAuthUOM,
			final TD_CoreAuthACL td_CoreAuthACL,
			final TD_CoreAuthAuthACL td_CoreAuthAuthACL) {
		super(TITLE);
		this.td_CoreAuthUser = td_CoreAuthUser;
		this.td_CoreAuthRole = td_CoreAuthRole;
		this.td_CoreAuthRA = td_CoreAuthRA;
		this.td_CoreAuthUOM = td_CoreAuthUOM;
		this.td_CoreAuthACL = td_CoreAuthACL;
		this.td_CoreAuthAuthACL = td_CoreAuthAuthACL;
		// this.notUpdatedUserGroupIDAndRoleGroupID = true;
	}

	final TD_CoreAuthUser td_CoreAuthUser;

	final TD_CoreAuthRole td_CoreAuthRole;

	final TD_CoreAuthRA td_CoreAuthRA;

	final TD_CoreAuthUOM td_CoreAuthUOM;

	final TD_CoreAuthACL td_CoreAuthACL;

	final TD_CoreAuthAuthACL td_CoreAuthAuthACL;

	@Override
	protected final void init(final Context context) throws Throwable {
		if (this.isDBValid()) {
			context.handle(new UpdateUserAndRoleGroupIdentifierTask());
		}
	}

	@Override
	protected final float getPriority() {
		return Integer.MIN_VALUE;
	}

	final boolean hasACVersion(final ContextImpl<?, ?, ?> context,
			final GUID actorIdentifier, final GUID ACVersion) {
		if (AccessControlConstants.isDefaultACVersion(ACVersion)) {
			return true;
		} else {
			final TD_CoreAuthUOM uomTable = this.td_CoreAuthUOM;
			final QueryStatementDeclare queryStatement = context.newQueryStatement();
			final QuTableRefDeclare referenceDefine_UOM = queryStatement.newReference(uomTable);
			queryStatement.newColumn(uomTable.f_orgID);
			final ArgumentDefine argument_ActorID = queryStatement.newArgument(uomTable.f_actorID);
			final ArgumentDefine argument_ACVersion = queryStatement.newArgument(uomTable.f_orgID);
			queryStatement.setCondition(referenceDefine_UOM.expOf(uomTable.f_actorID).xEq(argument_ActorID).and(referenceDefine_UOM.expOf(uomTable.f_orgID).xEq(argument_ACVersion)));
			final RecordSet recordSet = context.openQuery(queryStatement, actorIdentifier, ACVersion);
			return !recordSet.isEmpty();
		}
	}

	@SuppressWarnings("unused")
	private static final Object SYN_OBJECT = new Object();

	// private volatile boolean notUpdatedUserGroupIDAndRoleGroupID;
	//
	// private final void tryUpdateUserGroupIDAndRoleGroupID(
	// final ContextImpl<?, ?, ?> context) {
	// if (this.notUpdatedUserGroupIDAndRoleGroupID) {
	// // 不做同步，有可能造成多次重新，但不影响系统的正常运行
	// final AsyncTask<UpdateUserAndRoleGroupIdentifierTask, None> asyncHolder =
	// context
	// .asyncHandle(new UpdateUserAndRoleGroupIdentifierTask());
	// try {
	// context.waitFor(asyncHolder);
	// if (asyncHolder.getState() == AsyncState.FINISHED) {
	// this.notUpdatedUserGroupIDAndRoleGroupID = false;
	// }
	// } catch (InterruptedException e) {
	// context.catcher.catchException(e, null);
	// }
	// }
	// }

	private final void updateGroupIdentifier(
			final ContextImpl<?, ?, ?> context, final GUID oldGroupID,
			final GUID newGroupID) {
		final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
		UpdateStatementDeclare updateStatement = context.newUpdateStatement(aclTable);
		updateStatement.assignArgument(aclTable.f_resCategoryID);
		ArgumentDefine argument_GroupID = updateStatement.newArgument("argument_GroupID", aclTable.f_resCategoryID.getType());
		updateStatement.setCondition(updateStatement.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID));
		context.executeUpdate(updateStatement, newGroupID, oldGroupID);
		final TD_CoreAuthAuthACL authACLTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
		updateStatement = context.newUpdateStatement(authACLTable);
		updateStatement.assignArgument(authACLTable.f_resCategoryID);
		argument_GroupID = updateStatement.newArgument("argument_GroupID", authACLTable.f_resCategoryID.getType());
		updateStatement.setCondition(updateStatement.expOf(authACLTable.f_resCategoryID).xEq(argument_GroupID));
		context.executeUpdate(updateStatement, newGroupID, oldGroupID);
	}

	private static final class UpdateUserAndRoleGroupIdentifierTask extends
			SimpleTask {
		// nothing
	}

	@Publish
	final class AllUserInformationListProvider extends
			ResultListProvider<UserEntry> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final List<UserEntry> resultList) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthUser userTable = AuthorityDataBaseService.this.td_CoreAuthUser;
				final QueryStatementDeclare queryStatement = context.newQueryStatement();
				queryStatement.newReference(userTable);
				final QueryColumnDeclare column_RECID = queryStatement.newColumn(userTable.f_RECID);
				final QueryColumnDeclare column_Name = queryStatement.newColumn(userTable.f_name);
				final QueryColumnDeclare column_Title = queryStatement.newColumn(userTable.f_title);
				final QueryColumnDeclare column_State = queryStatement.newColumn(userTable.f_state);
				final QueryColumnDeclare column_Description = queryStatement.newColumn(userTable.f_description);
				final QueryColumnDeclare column_Level = queryStatement.newColumn(userTable.f_level);
				final QueryColumnDeclare column_Password = queryStatement.newColumn(userTable.f_password);
				final QueryColumnDeclare column_Priority = queryStatement.newColumn(userTable.f_priorityInfo);
				queryStatement.newOrderBy(userTable.f_RECID);
				final RecordSet recordSet = context.openQuery(queryStatement);
				final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
				final RecordSetField field_RECID = recordSetFields.find(column_RECID);
				final RecordSetField field_Name = recordSetFields.find(column_Name);
				final RecordSetField field_Title = recordSetFields.find(column_Title);
				final RecordSetField field_State = recordSetFields.find(column_State);
				final RecordSetField field_Description = recordSetFields.find(column_Description);
				final RecordSetField field_Level = recordSetFields.find(column_Level);
				final RecordSetField field_Password = recordSetFields.find(column_Password);
				final RecordSetField field_Priority = recordSetFields.find(column_Priority);
				while (recordSet.next()) {
					UserEntry ue = new UserEntry(field_RECID.getGUID(), field_Name.getString(), field_Title.getString(), ActorState.valueOf(field_State.getString()), field_Description.getString(), field_Password.getGUID(), field_Priority.getInt());
					ue.level = field_Level.getString();
					resultList.add(ue);
				}
			}
		}

	}

	@Publish
	final class AllRoleInformationListProvider extends
			ResultListProvider<RoleEntry> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final List<RoleEntry> resultList) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthRole roleTable = AuthorityDataBaseService.this.td_CoreAuthRole;
				final QueryStatementDeclare queryStatement = context.newQueryStatement();
				queryStatement.newReference(roleTable);
				final QueryColumnDeclare column_RECID = queryStatement.newColumn(roleTable.f_RECID);
				final QueryColumnDeclare column_Name = queryStatement.newColumn(roleTable.f_name);
				final QueryColumnDeclare column_Title = queryStatement.newColumn(roleTable.f_title);
				final QueryColumnDeclare column_State = queryStatement.newColumn(roleTable.f_state);
				final QueryColumnDeclare column_Description = queryStatement.newColumn(roleTable.f_description);
				queryStatement.newOrderBy(roleTable.f_RECID);
				final RecordSet recordSet = context.openQuery(queryStatement);
				final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
				final RecordSetField field_RECID = recordSetFields.find(column_RECID);
				final RecordSetField field_Name = recordSetFields.find(column_Name);
				final RecordSetField field_Title = recordSetFields.find(column_Title);
				final RecordSetField field_State = recordSetFields.find(column_State);
				final RecordSetField field_Description = recordSetFields.find(column_Description);
				while (recordSet.next()) {
					resultList.add(new RoleEntry(field_RECID.getGUID(), field_Name.getString(), field_Title.getString(), ActorState.valueOf(field_State.getString()), field_Description.getString()));
				}
			}
		}

	}

	@Publish
	final class AllRoleAssignInformationListProvider extends
			ResultListProvider<RoleAssignEntry> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final List<RoleAssignEntry> resultList) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthRA raTable = AuthorityDataBaseService.this.td_CoreAuthRA;
				final QueryStatementDeclare queryStatement = context.newQueryStatement();
				queryStatement.newReference(raTable);
				final QueryColumnDeclare column_UserID = queryStatement.newColumn(raTable.f_actorID);
				final QueryColumnDeclare column_OrgID = queryStatement.newColumn(raTable.f_orgID);
				final QueryColumnDeclare column_RoleID = queryStatement.newColumn(raTable.f_roleID);
				queryStatement.newOrderBy(raTable.f_actorID, true);
				final RecordSet recordSet = context.openQuery(queryStatement);
				final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
				final RecordSetField field_UserID = recordSetFields.find(column_UserID);
				final RecordSetField field_OrgID = recordSetFields.find(column_OrgID);
				final RecordSetField field_RoleID = recordSetFields.find(column_RoleID);
				while (recordSet.next()) {
					resultList.add(new RoleAssignEntry(field_UserID.getGUID(), field_OrgID.getGUID(), field_RoleID.getGUID()));
				}
			}
		}
	}

	@Publish
	final class AllIdentifyMapInformationListProvider extends
			ResultListProvider<IdentifyMapEntry> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final List<IdentifyMapEntry> resultList) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthUOM uomTable = AuthorityDataBaseService.this.td_CoreAuthUOM;
				final QueryStatementDeclare queryStatement = context.newQueryStatement();
				queryStatement.newReference(uomTable);
				final QueryColumnDeclare column_ID = queryStatement.newColumn(uomTable.f_RECID);
				final QueryColumnDeclare column_UserID = queryStatement.newColumn(uomTable.f_actorID);
				final QueryColumnDeclare column_OrgID = queryStatement.newColumn(uomTable.f_orgID);
				queryStatement.newOrderBy(uomTable.f_actorID, true);
				final RecordSet recordSet = context.openQuery(queryStatement);
				final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
				final RecordSetField field_ID = recordSetFields.find(column_ID);
				final RecordSetField field_UserID = recordSetFields.find(column_UserID);
				final RecordSetField field_OrgID = recordSetFields.find(column_OrgID);
				while (recordSet.next()) {
					resultList.add(new IdentifyMapEntry(field_ID.getGUID(), field_UserID.getGUID(), field_OrgID.getGUID()));
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Publish
	final class RoleAssignInformationListProvider extends
			OneKeyResultListProvider<RoleAssignEntry, Class> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final Class key, final List<RoleAssignEntry> resultList)
				throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				if (key != User.class && key != Identify.class) {
					throw new UnsupportedOperationException("不支持的键值" + key);
				}
				final TD_CoreAuthRA raTable = AuthorityDataBaseService.this.td_CoreAuthRA;
				final QueryStatementDeclare queryStatement = context.newQueryStatement();
				final QuTableRefDeclare referenceDefine_RA = queryStatement.newReference(raTable);
				final QueryColumnDeclare column_UserID = queryStatement.newColumn(raTable.f_actorID);
				final QueryColumnDeclare column_OrgID = queryStatement.newColumn(raTable.f_orgID);
				final QueryColumnDeclare column_RoleID = queryStatement.newColumn(raTable.f_roleID);
				final TableFieldRefExpr fieldReferenceExpress = referenceDefine_RA.expOf(raTable.f_orgID);
				if (key == User.class) {
					queryStatement.setCondition(fieldReferenceExpress.xIsNull().or(fieldReferenceExpress.xEq(AccessControlConstants.DEFAULT_ACVERSION)));
					queryStatement.newOrderBy(raTable.f_actorID, true);
				} else {
					queryStatement.setCondition(fieldReferenceExpress.xIsNull().or(fieldReferenceExpress.xEq(AccessControlConstants.DEFAULT_ACVERSION)).not());
					queryStatement.newOrderBy(raTable.f_actorID, true);
					queryStatement.newOrderBy(raTable.f_orgID, true);
				}
				final RecordSet recordSet = context.openQuery(queryStatement);
				final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
				final RecordSetField field_UserID = recordSetFields.find(column_UserID);
				final RecordSetField field_OrgID = recordSetFields.find(column_OrgID);
				final RecordSetField field_RoleID = recordSetFields.find(column_RoleID);
				while (recordSet.next()) {
					resultList.add(new RoleAssignEntry(field_UserID.getGUID(), field_OrgID.getGUID(), field_RoleID.getGUID()));
				}
			}
		}

	}

	@Publish
	final class OperationAuthorityInformationProvider
			extends
			OneKeyResultProvider<OperationAuthorityInformation, GetOperationAuthorityInformationKey> {

		@Override
		protected final OperationAuthorityInformation provide(
				final ContextImpl<?, ?, ?> context,
				final GetOperationAuthorityInformationKey key) throws Throwable {
			final OperationAuthorityInformation information = new OperationAuthorityInformation();
			if (!AuthorityDataBaseService.this.isDBValid()) {
				return information;
			}
			final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
			final QueryStatementDeclare queryStatement;
			final QueryColumnDeclare column_GroupID;
			final QueryColumnDeclare column_ResourceID;
			final QueryColumnDeclare column_Code;
			final boolean isDefaultACVersion = AccessControlConstants.isDefaultACVersion(key.ACVersion);
			if (isDefaultACVersion && this.queryStatement_default != null) {
				queryStatement = this.queryStatement_default;
				column_Code = this.column_Code_default;
				column_GroupID = this.column_GroupID_default;
				column_ResourceID = this.column_ResourceID_default;
			} else if (!isDefaultACVersion && this.queryStatement != null) {
				queryStatement = this.queryStatement;
				column_Code = this.column_Code;
				column_GroupID = this.column_GroupID;
				column_ResourceID = this.column_ResourceID;
			} else {
				queryStatement = context.newQueryStatement();
				final QuTableRefDeclare tableReference = queryStatement.newReference(aclTable);
				final ArgumentDefine argument_ActorID = queryStatement.newArgument(aclTable.f_actorID);
				final ArgumentDefine argument_ACVersion = queryStatement.newArgument(aclTable.f_orgID);
				column_GroupID = queryStatement.newColumn(aclTable.f_resCategoryID);
				column_ResourceID = queryStatement.newColumn(aclTable.f_resourceID);
				column_Code = queryStatement.newColumn(aclTable.f_authorityCode);
				queryStatement.newOrderBy(aclTable.f_resCategoryID);
				if (isDefaultACVersion) {
					queryStatement.setCondition(tableReference.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(tableReference.expOf(aclTable.f_orgID).xIsNull().or(tableReference.expOf(aclTable.f_orgID).xEq(argument_ACVersion))));
					synchronized (this.lock) {
						if (this.queryStatement_default == null) {
							this.column_Code_default = column_Code;
							this.column_GroupID_default = column_GroupID;
							this.column_ResourceID_default = column_ResourceID;
							this.queryStatement_default = queryStatement;
						}
					}
				} else {
					queryStatement.setCondition(tableReference.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(tableReference.expOf(aclTable.f_orgID).xEq(argument_ACVersion)));
					synchronized (this.lock) {
						if (this.queryStatement == null) {
							this.column_Code = column_Code;
							this.column_GroupID = column_GroupID;
							this.column_ResourceID = column_ResourceID;
							this.queryStatement = queryStatement;
						}
					}
				}
			}
			final RecordSet recordSet = context.openQuery(queryStatement, key.actorIdentifier, AccessControlConstants.adjustACVersion(key.ACVersion));
			final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
			final RecordSetField field_GroupID = recordSetFields.find(column_GroupID);
			final RecordSetField field_ResourceID = recordSetFields.find(column_ResourceID);
			final RecordSetField field_Code = recordSetFields.find(column_Code);
			final Cache cache = AuthorityDataBaseService.this.site.cache;
			DeleteStatementDeclare deleteStatement = null;
			if (recordSet.next()) {
				while (true) {
					final GUID currentGroupID = field_GroupID.getGUID();
					final CacheGroup<?, ?, ?> group = cache.ACGroupContainer.get(currentGroupID);
					if (group == null) {
						if (AuthorityDataBaseService.CLEAR_DIRTYDATA) {
							if (deleteStatement == null) {
								deleteStatement = context.newDeleteStatement(aclTable);
								final ArgumentDefine arguement = deleteStatement.newArgument(aclTable.f_resCategoryID);
								deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_resCategoryID).xEq(arguement));
							}
							context.executeUpdate(deleteStatement, currentGroupID);
						}
						do {
							if (!recordSet.next()) {
								return information;
							}
						} while (currentGroupID.equals(field_GroupID.getGUID()));
					} else {
						final OperationEntry[] operationEntrys = group.define.accessControlDefine.operationEntrys;
						do {
							final AccessControlEntry.AuthorityEntry authorityEntry = information.addOperationAuthorityEntry(currentGroupID, field_ResourceID.getGUID());
							final int code = field_Code.getInt();
							for (OperationEntry operationEntry : operationEntrys) {
								authorityEntry.addDataItem(operationEntry.operation, AccessControlHelper.getAuthority(code, operationEntry));
							}
							if (!recordSet.next()) {
								return information;
							}
						} while (currentGroupID.equals(field_GroupID.getGUID()));
					}
				}
			} else {
				return information;
			}
		}

		private final Object lock = new Object();

		private volatile QueryStatementDeclare queryStatement_default;
		private volatile QueryColumnDeclare column_GroupID_default;
		private volatile QueryColumnDeclare column_ResourceID_default;
		private volatile QueryColumnDeclare column_Code_default;

		private volatile QueryStatementDeclare queryStatement;
		private volatile QueryColumnDeclare column_GroupID;
		private volatile QueryColumnDeclare column_ResourceID;
		private volatile QueryColumnDeclare column_Code;

	}

	@Publish
	final class AccreditAuthorityInformationProvider
			extends
			OneKeyResultProvider<AccreditAuthorityInformation, GetAccreditAuthorityInformationKey> {

		@Override
		protected final AccreditAuthorityInformation provide(
				final ContextImpl<?, ?, ?> context,
				final GetAccreditAuthorityInformationKey key) throws Throwable {
			// AuthorityDataBaseService.this
			// .tryUpdateUserGroupIDAndRoleGroupID(context);
			final boolean noSuchVersion = !AuthorityDataBaseService.this.isDBValid() || !AuthorityDataBaseService.this.hasACVersion(context, key.actorIdentifier, key.ACVersion);
			final AccreditAuthorityInformation information = new AccreditAuthorityInformation(noSuchVersion);
			if (noSuchVersion) {
				return information;
			}
			final TD_CoreAuthAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
			final QueryStatementDeclare queryStatement;
			final QueryColumnDeclare column_GroupID;
			final QueryColumnDeclare column_ResourceID;
			final QueryColumnDeclare column_Code;
			final boolean isDefaultACVersion = AccessControlConstants.isDefaultACVersion(key.ACVersion);
			if (isDefaultACVersion && this.queryStatement_default != null) {
				queryStatement = this.queryStatement_default;
				column_Code = this.column_Code_default;
				column_GroupID = this.column_GroupID_default;
				column_ResourceID = this.column_ResourceID_default;
			} else if (!isDefaultACVersion && this.queryStatement != null) {
				queryStatement = this.queryStatement;
				column_Code = this.column_Code;
				column_GroupID = this.column_GroupID;
				column_ResourceID = this.column_ResourceID;
			} else {
				queryStatement = context.newQueryStatement();
				final QuTableRefDeclare tableReference = queryStatement.newReference(aclTable);
				final ArgumentDefine argument_ActorID = queryStatement.newArgument(aclTable.f_actorID);
				final ArgumentDefine argument_ACVersion = queryStatement.newArgument(aclTable.f_orgID);
				column_GroupID = queryStatement.newColumn(aclTable.f_resCategoryID);
				column_ResourceID = queryStatement.newColumn(aclTable.f_resourceID);
				column_Code = queryStatement.newColumn(aclTable.f_authorityCode);
				queryStatement.newOrderBy(aclTable.f_resCategoryID);
				if (isDefaultACVersion) {
					queryStatement.setCondition(tableReference.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(tableReference.expOf(aclTable.f_orgID).xIsNull().or(tableReference.expOf(aclTable.f_orgID).xEq(argument_ACVersion))));
					synchronized (this.lock) {
						if (this.queryStatement_default == null) {
							this.column_Code_default = column_Code;
							this.column_GroupID_default = column_GroupID;
							this.column_ResourceID_default = column_ResourceID;
							this.queryStatement_default = queryStatement;
						}
					}
				} else {
					queryStatement.setCondition(tableReference.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(tableReference.expOf(aclTable.f_orgID).xEq(argument_ACVersion)));
					synchronized (this.lock) {
						if (this.queryStatement == null) {
							this.column_Code = column_Code;
							this.column_GroupID = column_GroupID;
							this.column_ResourceID = column_ResourceID;
							this.queryStatement = queryStatement;
						}
					}
				}
			}
			final RecordSet recordSet = context.openQuery(queryStatement, key.actorIdentifier, AccessControlConstants.adjustACVersion(key.ACVersion));
			final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
			final RecordSetField field_GroupID = recordSetFields.find(column_GroupID);
			final RecordSetField field_ResourceID = recordSetFields.find(column_ResourceID);
			final RecordSetField field_Code = recordSetFields.find(column_Code);
			final Cache cache = AuthorityDataBaseService.this.site.cache;
			DeleteStatementDeclare deleteStatement = null;
			if (recordSet.next()) {
				while (true) {
					final GUID currentGroupID = field_GroupID.getGUID();
					final CacheGroup<?, ?, ?> group = cache.ACGroupContainer.get(currentGroupID);
					if (group == null) {
						if (AuthorityDataBaseService.CLEAR_DIRTYDATA) {
							if (deleteStatement == null) {
								deleteStatement = context.newDeleteStatement(aclTable);
								final ArgumentDefine arguement = deleteStatement.newArgument(aclTable.f_resCategoryID);
								deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_resCategoryID).xEq(arguement));
							}
							context.executeUpdate(deleteStatement, currentGroupID);
						}
						do {
							if (!recordSet.next()) {
								return information;
							}
						} while (currentGroupID.equals(field_GroupID.getGUID()));
					} else {
						final OperationEntry[] operationEntrys = group.define.accessControlDefine.operationEntrys;
						do {
							final AccessControlEntry.AuthorityEntry authorityEntry = information.addAccreditAuthorityEntry(currentGroupID, field_ResourceID.getGUID());
							final int code = field_Code.getInt();
							for (OperationEntry operationEntry : operationEntrys) {
								authorityEntry.addDataItem(operationEntry.operation, AccessControlHelper.getAuthority(code, operationEntry));
							}
							if (!recordSet.next()) {
								return information;
							}
						} while (currentGroupID.equals(field_GroupID.getGUID()));
					}
				}
			} else {
				return information;
			}
		}

		private final Object lock = new Object();

		private volatile QueryStatementDeclare queryStatement_default;
		private volatile QueryColumnDeclare column_GroupID_default;
		private volatile QueryColumnDeclare column_ResourceID_default;
		private volatile QueryColumnDeclare column_Code_default;

		private volatile QueryStatementDeclare queryStatement;
		private volatile QueryColumnDeclare column_GroupID;
		private volatile QueryColumnDeclare column_ResourceID;
		private volatile QueryColumnDeclare column_Code;

	}

	@Publish
	final class FinishInitializeUserTaskHandler extends
			TaskMethodHandler<FinishInitializeUserTask, None> {

		protected FinishInitializeUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishInitializeUserTask task) throws Throwable {
			// do nothing
		}

	}

	@Publish
	final class FinishInitializeRoleTaskHandler extends
			TaskMethodHandler<FinishInitializeRoleTask, None> {

		protected FinishInitializeRoleTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishInitializeRoleTask task) throws Throwable {
			// do nothing
		}

	}

	@Publish
	final class FinishCreateUserTaskHandler extends
			TaskMethodHandler<FinishCreateUserTask, None> {

		protected FinishCreateUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishCreateUserTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthUser userTable = AuthorityDataBaseService.this.td_CoreAuthUser;
				InsertStatementDeclare insertStatement = context.newInsertStatement(userTable);
				insertStatement.assignArgument(userTable.f_RECID);
				insertStatement.assignArgument(userTable.f_name);
				insertStatement.assignArgument(userTable.f_title);
				insertStatement.assignArgument(userTable.f_state);
				insertStatement.assignArgument(userTable.f_description);
				insertStatement.assignArgument(userTable.f_level);
				insertStatement.assignArgument(userTable.f_password);
				insertStatement.assignArgument(userTable.f_priorityInfo);
				context.executeUpdate(insertStatement, task.identifier, task.name, task.title, task.state.name(), task.description, task.level,task.password, 0);
			}
		}

	}

	@Publish
	final class FinishCreateRoleTaskHandler extends
			TaskMethodHandler<FinishCreateRoleTask, None> {

		protected FinishCreateRoleTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishCreateRoleTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthRole roleTable = AuthorityDataBaseService.this.td_CoreAuthRole;
				InsertStatementDeclare insertStatement = context.newInsertStatement(roleTable);
				insertStatement.assignArgument(roleTable.f_RECID);
				insertStatement.assignArgument(roleTable.f_name);
				insertStatement.assignArgument(roleTable.f_title);
				insertStatement.assignArgument(roleTable.f_state);
				insertStatement.assignArgument(roleTable.f_description);
				context.executeUpdate(insertStatement, task.identifier, task.name, task.title, task.state.name(), task.description);
			}
		}

	}

	@Publish
	final class FinishUpdateUserInformationTaskHandler extends
			TaskMethodHandler<FinishUpdateUserInformationTask, None> {

		protected FinishUpdateUserInformationTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishUpdateUserInformationTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthUser userTable = AuthorityDataBaseService.this.td_CoreAuthUser;
				final UpdateStatementDeclare updateStatement = context.newUpdateStatement(userTable);
				final ArgumentDefine argument = updateStatement.newArgument(userTable.f_RECID);
				updateStatement.assignArgument(userTable.f_name);
				updateStatement.assignArgument(userTable.f_title);
				updateStatement.assignArgument(userTable.f_state);
				updateStatement.assignArgument(userTable.f_description);
				updateStatement.assignArgument(userTable.f_level);
				updateStatement.setCondition(updateStatement.expOf(userTable.f_RECID).xEq(argument));
				context.executeUpdate(updateStatement, task.userIdentifier, task.name, task.title, task.state.name(), task.description,task.level);
			}
		}

	}

	@Publish
	final class FinishUpdateRoleInformationTaskHandler extends
			TaskMethodHandler<FinishUpdateRoleInformationTask, None> {

		protected FinishUpdateRoleInformationTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishUpdateRoleInformationTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthRole roleTable = AuthorityDataBaseService.this.td_CoreAuthRole;
				final UpdateStatementDeclare updateStatement = context.newUpdateStatement(roleTable);
				final ArgumentDefine argument = updateStatement.newArgument(roleTable.f_RECID);
				updateStatement.assignArgument(roleTable.f_name);
				updateStatement.assignArgument(roleTable.f_title);
				updateStatement.assignArgument(roleTable.f_state);
				updateStatement.assignArgument(roleTable.f_description);
				updateStatement.setCondition(updateStatement.expOf(roleTable.f_RECID).xEq(argument));
				context.executeUpdate(updateStatement, task.roleIdentifier, task.name, task.title, task.state.name(), task.description);
			}
		}

	}

	@Publish
	final class FinishUpdateUserPasswordTaskHandler extends
			TaskMethodHandler<FinishUpdateUserPasswordTask, None> {

		protected FinishUpdateUserPasswordTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishUpdateUserPasswordTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthUser userTable = AuthorityDataBaseService.this.td_CoreAuthUser;
				final UpdateStatementDeclare updateStatement = context.newUpdateStatement(userTable);
				final ArgumentDefine argument = updateStatement.newArgument(userTable.f_RECID);
				updateStatement.assignArgument(userTable.f_password);
				updateStatement.setCondition(updateStatement.expOf(userTable.f_RECID).xEq(argument));
				context.executeUpdate(updateStatement, task.userIdentifier, task.newPassword);
			}
		}

	}

	@Publish
	final class FinishDeleteUserTaskHandler extends
			TaskMethodHandler<FinishDeleteUserTask, None> {

		protected FinishDeleteUserTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishDeleteUserTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthAuthACL authACLTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
				DeleteStatementDeclare deleteStatement = context.newDeleteStatement(authACLTable);
				ArgumentDefine argument = deleteStatement.newArgument(authACLTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(authACLTable.f_actorID).xEq(argument));
				context.executeUpdate(deleteStatement, task.userIdentifier);
				final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
				deleteStatement = context.newDeleteStatement(aclTable);
				argument = deleteStatement.newArgument(aclTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument));
				context.executeUpdate(deleteStatement, task.userIdentifier);
				final TD_CoreAuthUOM uomTable = AuthorityDataBaseService.this.td_CoreAuthUOM;
				deleteStatement = context.newDeleteStatement(uomTable);
				argument = deleteStatement.newArgument(uomTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(uomTable.f_actorID).xEq(argument));
				context.executeUpdate(deleteStatement, task.userIdentifier);
				final TD_CoreAuthRA raTable = AuthorityDataBaseService.this.td_CoreAuthRA;
				deleteStatement = context.newDeleteStatement(raTable);
				argument = deleteStatement.newArgument(raTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(raTable.f_actorID).xEq(argument));
				context.executeUpdate(deleteStatement, task.userIdentifier);
				final TD_CoreAuthUser userTable = AuthorityDataBaseService.this.td_CoreAuthUser;
				deleteStatement = context.newDeleteStatement(userTable);
				argument = deleteStatement.newArgument(userTable.f_RECID);
				deleteStatement.setCondition(deleteStatement.expOf(userTable.f_RECID).xEq(argument));
				context.executeUpdate(deleteStatement, task.userIdentifier);
			}
		}

	}

	@Publish
	final class FinishDeleteRoleTaskHandler extends
			TaskMethodHandler<FinishDeleteRoleTask, None> {

		protected FinishDeleteRoleTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishDeleteRoleTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthAuthACL authACLTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
				DeleteStatementDeclare deleteStatement = context.newDeleteStatement(authACLTable);
				ArgumentDefine argument = deleteStatement.newArgument(authACLTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(authACLTable.f_actorID).xEq(argument));
				context.executeUpdate(deleteStatement, task.roleIdentifier);
				final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
				deleteStatement = context.newDeleteStatement(aclTable);
				argument = deleteStatement.newArgument(aclTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument));
				context.executeUpdate(deleteStatement, task.roleIdentifier);
				final TD_CoreAuthUOM uomTable = AuthorityDataBaseService.this.td_CoreAuthUOM;
				deleteStatement = context.newDeleteStatement(uomTable);
				argument = deleteStatement.newArgument(uomTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(uomTable.f_actorID).xEq(argument));
				context.executeUpdate(deleteStatement, task.roleIdentifier);
				final TD_CoreAuthRA raTable = AuthorityDataBaseService.this.td_CoreAuthRA;
				deleteStatement = context.newDeleteStatement(raTable);
				argument = deleteStatement.newArgument(raTable.f_roleID);
				deleteStatement.setCondition(deleteStatement.expOf(raTable.f_roleID).xEq(argument));
				context.executeUpdate(deleteStatement, task.roleIdentifier);
				final TD_CoreAuthRole roleTable = AuthorityDataBaseService.this.td_CoreAuthRole;
				deleteStatement = context.newDeleteStatement(roleTable);
				argument = deleteStatement.newArgument(roleTable.f_RECID);
				deleteStatement.setCondition(deleteStatement.expOf(roleTable.f_RECID).xEq(argument));
				context.executeUpdate(deleteStatement, task.roleIdentifier);
			}
		}

	}

	@Publish
	final class FinishInitializeOperationAuthorityTaskHandler extends
			TaskMethodHandler<FinishInitializeOperationAuthorityTask, None> {

		protected FinishInitializeOperationAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishInitializeOperationAuthorityTask task)
				throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid() && AuthorityDataBaseService.CLEAR_DIRTYDATA) {
				final List<AccessControlEntry.AuthorityEntry> authorityEntryList = task.authorityInformation.authorityEntryList;
				if (authorityEntryList.size() == 0) {
					return;
				} else {
					DeleteStatementDeclare deleteStatement = null;
					DeleteStatementDeclare deleteStatement_ACVersionIsNull = null;
					for (AccessControlEntry.AuthorityEntry authorityEntry : authorityEntryList) {
						if (authorityEntry.exception == null) {
							continue;
						} else {
							if (deleteStatement == null) {
								final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
								deleteStatement = context.newDeleteStatement(aclTable);
								ArgumentDefine argument_ActorID = deleteStatement.newArgument(aclTable.f_actorID);
								final ArgumentDefine argument_ACVersion = deleteStatement.newArgument(aclTable.f_orgID);
								ArgumentDefine argument_GroupID = deleteStatement.newArgument(aclTable.f_resCategoryID);
								ArgumentDefine argument_ItemID = deleteStatement.newArgument(aclTable.f_resourceID);
								deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(argument_ACVersion), deleteStatement.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
								deleteStatement_ACVersionIsNull = context.newDeleteStatement(aclTable);
								argument_ActorID = deleteStatement_ACVersionIsNull.newArgument(aclTable.f_actorID);
								argument_GroupID = deleteStatement_ACVersionIsNull.newArgument(aclTable.f_resCategoryID);
								argument_ItemID = deleteStatement_ACVersionIsNull.newArgument(aclTable.f_resourceID);
								deleteStatement_ACVersionIsNull.setCondition(deleteStatement_ACVersionIsNull.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement_ACVersionIsNull.expOf(aclTable.f_orgID).xIsNull(), deleteStatement_ACVersionIsNull.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement_ACVersionIsNull.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
							}
							if (task.ACVersion == null) {
								context.executeUpdate(deleteStatement_ACVersionIsNull, task.actorIdentifier, authorityEntry.groupIdentifier, authorityEntry.itemIdentifier);
							} else {
								context.executeUpdate(deleteStatement, task.actorIdentifier, task.ACVersion, authorityEntry.groupIdentifier, authorityEntry.itemIdentifier);
							}
						}
					}
				}
			}
		}
	}

	@Publish
	final class FinishInitializeAccreditAuthorityTaskHandler extends
			TaskMethodHandler<FinishInitializeAccreditAuthorityTask, None> {

		protected FinishInitializeAccreditAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishInitializeAccreditAuthorityTask task)
				throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid() && AuthorityDataBaseService.CLEAR_DIRTYDATA) {
				final List<AccessControlEntry.AuthorityEntry> authorityEntryList = task.authorityInformation.authorityEntryList;
				if (task.authorityInformation.noSuchACVersion) {
					final TD_CoreAuthAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
					final DeleteStatementDeclare deleteStatement = context.newDeleteStatement(aclTable);
					if (AccessControlConstants.isDefaultACVersion(task.ACVersion)) {
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.actorIdentifier).and(deleteStatement.expOf(aclTable.f_orgID).xIsNull().or(deleteStatement.expOf(aclTable.f_orgID).xEq(task.ACVersion))));
					} else {
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.actorIdentifier).and(deleteStatement.expOf(aclTable.f_orgID).xEq(task.ACVersion)));
					}
					context.executeUpdate(deleteStatement);
				} else if (authorityEntryList.size() == 0) {
					return;
				} else {
					DeleteStatementDeclare deleteStatement = null;
					for (AccessControlEntry.AuthorityEntry authorityEntry : authorityEntryList) {
						if (authorityEntry.exception == null) {
							continue;
						} else {
							if (deleteStatement == null) {
								final TD_CoreAuthAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
								deleteStatement = context.newDeleteStatement(aclTable);
								final ArgumentDefine argument_GroupID = deleteStatement.newArgument(aclTable.f_resCategoryID);
								final ArgumentDefine argument_ItemID = deleteStatement.newArgument(aclTable.f_resourceID);
								if (AccessControlConstants.isDefaultACVersion(task.ACVersion)) {
									deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.actorIdentifier).and(deleteStatement.expOf(aclTable.f_orgID).xIsNull().or(deleteStatement.expOf(aclTable.f_orgID).xEq(task.ACVersion)), deleteStatement.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
								} else {
									deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.actorIdentifier).and(deleteStatement.expOf(aclTable.f_orgID).xEq(task.ACVersion), deleteStatement.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
								}
							}
							context.executeUpdate(deleteStatement, authorityEntry.groupIdentifier, authorityEntry.itemIdentifier);
						}
					}
				}
			}
		}
	}

	@Publish
	final class FinishInitializeRoleAssignTaskHandler extends
			TaskMethodHandler<FinishInitializeRoleAssignTask, None> {

		protected FinishInitializeRoleAssignTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishInitializeRoleAssignTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid() && AuthorityDataBaseService.CLEAR_DIRTYDATA) {
				if (task.roleAssignEntrys == null || task.roleAssignEntrys.size() == 0) {
					return;
				} else {
					DeleteStatementDeclare deleteStatement = null;
					for (RoleAssignEntry roleAssignEntry : task.roleAssignEntrys) {
						if (roleAssignEntry.exception != null) {
							if (deleteStatement == null) {
								final TD_CoreAuthRA raTable = AuthorityDataBaseService.this.td_CoreAuthRA;
								deleteStatement = context.newDeleteStatement(raTable);
								final ArgumentDefine argument_ActorID = deleteStatement.newArgument(raTable.f_actorID);
								final ArgumentDefine argument_RoleID = deleteStatement.newArgument(raTable.f_roleID);
								deleteStatement.setCondition(deleteStatement.expOf(raTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(raTable.f_roleID).xEq(argument_RoleID)));
							}
							context.executeUpdate(deleteStatement, roleAssignEntry.userIdentifier, roleAssignEntry.roleIdentifier);
						}
					}
				}
			}
		}

	}

	@Publish
	final class FinishUpdateUserRoleAssignTaskHandler extends
			TaskMethodHandler<FinishUpdateUserRoleAssignTask, None> {

		protected FinishUpdateUserRoleAssignTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishUpdateUserRoleAssignTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthRA raTable = AuthorityDataBaseService.this.td_CoreAuthRA;
				final DeleteStatementDeclare deleteStatement = context.newDeleteStatement(raTable);
				final ArgumentDefine argument = deleteStatement.newArgument(raTable.f_actorID);
				deleteStatement.setCondition(deleteStatement.expOf(raTable.f_actorID).xEq(argument).and(deleteStatement.expOf(raTable.f_orgID).xIsNull().or(deleteStatement.expOf(raTable.f_orgID).xEq(AccessControlConstants.DEFAULT_ACVERSION))));
				context.executeUpdate(deleteStatement, task.userIdentifier);
				if (task.assignedRoleIdentifiers == null || task.assignedRoleIdentifiers.size() == 0) {
					return;
				} else {
					final InsertStatementDeclare insertStatement = context.newInsertStatement(raTable);
					insertStatement.assignArgument(raTable.f_RECID);
					insertStatement.assignArgument(raTable.f_actorID);
					insertStatement.assignArgument(raTable.f_orgID);
					insertStatement.assignArgument(raTable.f_roleID);
					insertStatement.assignArgument(raTable.f_priority);
					for (GUID roleIdentifier : task.assignedRoleIdentifiers) {
						context.executeUpdate(insertStatement, context.newRECID(), task.userIdentifier, null, roleIdentifier, 0);
					}
				}
			}
		}

	}

	@Publish
	final class FinishUpdateIdentifyRoleAssignTaskHandler extends
			TaskMethodHandler<FinishUpdateIdentifyRoleAssignTask, None> {

		protected FinishUpdateIdentifyRoleAssignTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishUpdateIdentifyRoleAssignTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthRA raTable = AuthorityDataBaseService.this.td_CoreAuthRA;
				final DeleteStatementDeclare deleteStatement = context.newDeleteStatement(raTable);
				final ArgumentDefine argument = deleteStatement.newArgument(raTable.f_actorID);
				final ArgumentDefine argument2 = deleteStatement.newArgument(raTable.f_orgID);
				deleteStatement.setCondition(deleteStatement.expOf(raTable.f_actorID).xEq(argument).and(deleteStatement.expOf(raTable.f_orgID).xEq(argument2)));
				context.executeUpdate(deleteStatement, task.userIdentifier, task.identifyIdentifier);
				if (task.assignedRoleIdentifiers == null || task.assignedRoleIdentifiers.size() == 0) {
					return;
				} else {
					final InsertStatementDeclare insertStatement = context.newInsertStatement(raTable);
					insertStatement.assignArgument(raTable.f_RECID);
					insertStatement.assignArgument(raTable.f_actorID);
					insertStatement.assignArgument(raTable.f_orgID);
					insertStatement.assignArgument(raTable.f_roleID);
					insertStatement.assignArgument(raTable.f_priority);
					for (GUID roleIdentifier : task.assignedRoleIdentifiers) {
						context.executeUpdate(insertStatement, context.newRECID(), task.userIdentifier, task.identifyIdentifier, roleIdentifier, 0);
					}
				}
			}
		}

	}

	@Publish
	final class FinishCreateACVersionTaskHandler extends
			TaskMethodHandler<FinishCreateACVersionTask, None> {

		protected FinishCreateACVersionTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishCreateACVersionTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthUOM uomTable = AuthorityDataBaseService.this.td_CoreAuthUOM;
				final QueryStatementDeclare queryStatement = context.newQueryStatement();
				final QuTableRefDeclare referenceDefine = queryStatement.newReference(uomTable);
				final ArgumentDefine argument_ActorID = queryStatement.newArgument(uomTable.f_actorID);
				final ArgumentDefine argument_ACVersion = queryStatement.newArgument(uomTable.f_orgID);
				queryStatement.newColumn(uomTable.f_RECID);
				queryStatement.setCondition(referenceDefine.expOf(uomTable.f_actorID).xEq(argument_ActorID).and(referenceDefine.expOf(uomTable.f_orgID).xEq(argument_ACVersion)));
				final RecordSet recordSet = context.openQuery(queryStatement, task.actorIdentifier, task.ACVersion);
				if (recordSet.isEmpty()) {
					final InsertStatementDeclare insertStatement = context.newInsertStatement(uomTable);
					insertStatement.assignArgument(uomTable.f_RECID);
					insertStatement.assignArgument(uomTable.f_actorID);
					insertStatement.assignArgument(uomTable.f_orgID);
					context.executeUpdate(insertStatement, task.identifier, task.actorIdentifier, task.ACVersion);
				}
			}
		}

	}

	@Publish
	final class FinishDeleteACVersionTaskHandler extends
			TaskMethodHandler<FinishDeleteACVersionTask, None> {

		protected FinishDeleteACVersionTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishDeleteACVersionTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
				DeleteStatementDeclare deleteStatement = context.newDeleteStatement(aclTable);
				ArgumentDefine argument_ActorID = deleteStatement.newArgument(aclTable.f_actorID);
				ArgumentDefine argument_ACVersion = deleteStatement.newArgument(aclTable.f_orgID);
				deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(argument_ACVersion)));
				context.executeUpdate(deleteStatement, task.actorIdentifier, task.ACVersion);
				final TD_CoreAuthAuthACL authACLTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
				deleteStatement = context.newDeleteStatement(authACLTable);
				argument_ActorID = deleteStatement.newArgument(authACLTable.f_actorID);
				argument_ACVersion = deleteStatement.newArgument(authACLTable.f_orgID);
				deleteStatement.setCondition(deleteStatement.expOf(authACLTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(authACLTable.f_orgID).xEq(argument_ACVersion)));
				context.executeUpdate(deleteStatement, task.actorIdentifier, task.ACVersion);
				final TD_CoreAuthUOM uomTable = AuthorityDataBaseService.this.td_CoreAuthUOM;
				deleteStatement = context.newDeleteStatement(uomTable);
				argument_ActorID = deleteStatement.newArgument(uomTable.f_actorID);
				argument_ACVersion = deleteStatement.newArgument(uomTable.f_orgID);
				deleteStatement.setCondition(deleteStatement.expOf(uomTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(uomTable.f_orgID).xEq(argument_ACVersion)));
				context.executeUpdate(deleteStatement, task.actorIdentifier, task.ACVersion);
			}
		}

	}

	@Publish
	final class FinishClearUserAuthorityTaskHandler extends
			TaskMethodHandler<FinishClearUserAuthorityTask, None> {

		protected FinishClearUserAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishClearUserAuthorityTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final DeleteStatementDeclare deleteStatement;
				final ArgumentDefine argument_ActorID;
				final ArgumentDefine argument_ACVersion;
				if (task.operationAuthority) {
					final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
					deleteStatement = context.newDeleteStatement(aclTable);
					argument_ActorID = deleteStatement.newArgument(aclTable.f_actorID);
					argument_ACVersion = deleteStatement.newArgument(aclTable.f_orgID);
					deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(argument_ACVersion)));
				} else {
					final TD_CoreAuthAuthACL authACLTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
					deleteStatement = context.newDeleteStatement(authACLTable);
					argument_ActorID = deleteStatement.newArgument(authACLTable.f_actorID);
					argument_ACVersion = deleteStatement.newArgument(authACLTable.f_orgID);
					deleteStatement.setCondition(deleteStatement.expOf(authACLTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(authACLTable.f_orgID).xEq(argument_ACVersion)));
				}
				context.executeUpdate(deleteStatement, task.actorIdentifier, task.ACVersion);
			}
		}

	}

	@Publish
	final class FinishClearRoleAuthorityTaskHandler extends
			TaskMethodHandler<FinishClearRoleAuthorityTask, None> {

		protected FinishClearRoleAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishClearRoleAuthorityTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final DeleteStatementDeclare deleteStatement;
				final ArgumentDefine argument_ActorID;
				final ArgumentDefine argument_ACVersion;
				if (task.operationAuthority) {
					final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
					deleteStatement = context.newDeleteStatement(aclTable);
					argument_ActorID = deleteStatement.newArgument(aclTable.f_actorID);
					argument_ACVersion = deleteStatement.newArgument(aclTable.f_orgID);
					deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(argument_ACVersion)));
				} else {
					final TD_CoreAuthAuthACL authACLTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
					deleteStatement = context.newDeleteStatement(authACLTable);
					argument_ActorID = deleteStatement.newArgument(authACLTable.f_actorID);
					argument_ACVersion = deleteStatement.newArgument(authACLTable.f_orgID);
					deleteStatement.setCondition(deleteStatement.expOf(authACLTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(authACLTable.f_orgID).xEq(argument_ACVersion)));
				}
				context.executeUpdate(deleteStatement, task.actorIdentifier, task.ACVersion);
			}
		}

	}

	@Publish
	final class FinishUpdateAuthorityTaskHandler extends
			TaskMethodHandler<FinishUpdateAuthorityTask, None> {

		protected FinishUpdateAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishUpdateAuthorityTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final DeleteStatementDeclare deleteStatement;
				final InsertStatementDeclare insertStatement;
				final DeleteStatementDeclare deleteStatement_defaultACVersion;
				if (task.operationAuthority) {
					if (this.acl_deleteStatement != null) {
						deleteStatement = this.acl_deleteStatement;
						insertStatement = this.acl_insertStatement;
						deleteStatement_defaultACVersion = this.acl_deleteStatement_defaultACVersion;
					} else {
						final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
						deleteStatement = context.newDeleteStatement(aclTable);
						ArgumentDefine argument_ActorID = deleteStatement.newArgument(aclTable.f_actorID);
						ArgumentDefine argument_ACVersion = deleteStatement.newArgument(aclTable.f_orgID);
						ArgumentDefine argument_GroupID = deleteStatement.newArgument(aclTable.f_resCategoryID);
						ArgumentDefine argument_ItemID = deleteStatement.newArgument(aclTable.f_resourceID);
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(argument_ACVersion), deleteStatement.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
						deleteStatement_defaultACVersion = context.newDeleteStatement(aclTable);
						argument_ActorID = deleteStatement_defaultACVersion.newArgument(aclTable.f_actorID);
						argument_ACVersion = deleteStatement_defaultACVersion.newArgument(aclTable.f_orgID);
						argument_GroupID = deleteStatement_defaultACVersion.newArgument(aclTable.f_resCategoryID);
						argument_ItemID = deleteStatement_defaultACVersion.newArgument(aclTable.f_resourceID);
						deleteStatement_defaultACVersion.setCondition(deleteStatement_defaultACVersion.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement_defaultACVersion.expOf(aclTable.f_orgID).xEq(argument_ACVersion).or(deleteStatement_defaultACVersion.expOf(aclTable.f_orgID).xIsNull()), deleteStatement_defaultACVersion.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement_defaultACVersion.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
						insertStatement = context.newInsertStatement(aclTable);
						insertStatement.assignArgument(aclTable.f_RECID);
						insertStatement.assignArgument(aclTable.f_actorID);
						insertStatement.assignArgument(aclTable.f_orgID);
						insertStatement.assignArgument(aclTable.f_resCategoryID);
						insertStatement.assignArgument(aclTable.f_resourceID);
						insertStatement.assignArgument(aclTable.f_authorityCode);
						synchronized (this.lock) {
							if (this.acl_deleteStatement == null) {
								this.acl_deleteStatement_defaultACVersion = deleteStatement_defaultACVersion;
								this.acl_insertStatement = insertStatement;
								this.acl_deleteStatement = deleteStatement;
							}
						}
					}
				} else {
					if (this.auth_deleteStatement != null) {
						deleteStatement = this.auth_deleteStatement;
						insertStatement = this.auth_insertStatement;
						deleteStatement_defaultACVersion = this.auth_deleteStatement_defaultACVersion;
					} else {
						final TD_CoreAuthAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
						deleteStatement = context.newDeleteStatement(aclTable);
						ArgumentDefine argument_ActorID = deleteStatement.newArgument(aclTable.f_actorID);
						ArgumentDefine argument_ACVersion = deleteStatement.newArgument(aclTable.f_orgID);
						ArgumentDefine argument_GroupID = deleteStatement.newArgument(aclTable.f_resCategoryID);
						ArgumentDefine argument_ItemID = deleteStatement.newArgument(aclTable.f_resourceID);
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(argument_ACVersion), deleteStatement.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
						deleteStatement_defaultACVersion = context.newDeleteStatement(aclTable);
						argument_ActorID = deleteStatement_defaultACVersion.newArgument(aclTable.f_actorID);
						argument_ACVersion = deleteStatement_defaultACVersion.newArgument(aclTable.f_orgID);
						argument_GroupID = deleteStatement_defaultACVersion.newArgument(aclTable.f_resCategoryID);
						argument_ItemID = deleteStatement_defaultACVersion.newArgument(aclTable.f_resourceID);
						deleteStatement_defaultACVersion.setCondition(deleteStatement_defaultACVersion.expOf(aclTable.f_actorID).xEq(argument_ActorID).and(deleteStatement_defaultACVersion.expOf(aclTable.f_orgID).xEq(argument_ACVersion).or(deleteStatement_defaultACVersion.expOf(aclTable.f_orgID).xIsNull()), deleteStatement_defaultACVersion.expOf(aclTable.f_resCategoryID).xEq(argument_GroupID), deleteStatement_defaultACVersion.expOf(aclTable.f_resourceID).xEq(argument_ItemID)));
						insertStatement = context.newInsertStatement(aclTable);
						insertStatement.assignArgument(aclTable.f_RECID);
						insertStatement.assignArgument(aclTable.f_actorID);
						insertStatement.assignArgument(aclTable.f_orgID);
						insertStatement.assignArgument(aclTable.f_resCategoryID);
						insertStatement.assignArgument(aclTable.f_resourceID);
						insertStatement.assignArgument(aclTable.f_authorityCode);
						synchronized (this.lock) {
							if (this.auth_deleteStatement == null) {
								this.auth_deleteStatement_defaultACVersion = deleteStatement_defaultACVersion;
								this.auth_insertStatement = insertStatement;
								this.auth_deleteStatement = deleteStatement;
							}
						}
					}
				}
				final GUID userIdentifier = task.actorIdentifier;
				final GUID ACVersionForDelete;
				final DeleteStatementDeclare ds;
				if (AccessControlConstants.isDefaultACVersion(task.ACVersion)) {
					ds = deleteStatement_defaultACVersion;
					ACVersionForDelete = AccessControlConstants.DEFAULT_ACVERSION;
				} else {
					ds = deleteStatement;
					ACVersionForDelete = task.ACVersion;
				}
				for (AuthorityEntry authorityEntry : task.authorityItemList) {
					if (authorityEntry.isChanged()) {
						final GUID groupIdentifier = authorityEntry.getGroupIdentifier();
						final GUID itemIdentifier = authorityEntry.getItemIdentifier();
						context.executeUpdate(ds, userIdentifier, ACVersionForDelete, groupIdentifier, itemIdentifier);
						if (authorityEntry.authorityCode != 0) {
							context.executeUpdate(insertStatement, context.newRECID(), userIdentifier, task.ACVersion, groupIdentifier, itemIdentifier, authorityEntry.authorityCode);
						}
					}
				}
			}
		}

		private final Object lock = new Object();

		private volatile DeleteStatementDeclare acl_deleteStatement;
		private volatile InsertStatementDeclare acl_insertStatement;
		private volatile DeleteStatementDeclare acl_deleteStatement_defaultACVersion;

		private volatile DeleteStatementDeclare auth_deleteStatement;
		private volatile InsertStatementDeclare auth_insertStatement;
		private volatile DeleteStatementDeclare auth_deleteStatement_defaultACVersion;
	}

	@Publish
	final class FinishDuplicateAuthorityTaskHandler extends
			TaskMethodHandler<FinishDuplicateAuthorityTask, None> {

		protected FinishDuplicateAuthorityTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final FinishDuplicateAuthorityTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final DeleteStatementDeclare deleteStatement;
				final InsertStatementDeclare insertStatement;
				if (task.operationAuthority) {
					final TD_CoreAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthACL;
					deleteStatement = context.newDeleteStatement(aclTable);
					insertStatement = context.newInsertStatement(aclTable);
					final DerivedQueryDeclare subQuery = insertStatement.getInsertValues();
					final QuTableRefDeclare aclTableReference = subQuery.newReference(aclTable);
					subQuery.newColumn(SQLFunc.xNewRecid(), aclTable.f_RECID.getName());
					subQuery.newColumn(ConstExpr.expOf(task.targetActorID), aclTable.f_actorID.getName());
					subQuery.newColumn(ConstExpr.expOf(task.targetOrgID), aclTable.f_orgID.getName());
					subQuery.newColumn(aclTable.f_resCategoryID, aclTable.f_resCategoryID.getName());
					subQuery.newColumn(aclTable.f_resourceID, aclTable.f_resourceID.getName());
					subQuery.newColumn(aclTable.f_authorityCode, aclTable.f_authorityCode.getName());
					if (AccessControlConstants.isDefaultACVersion(task.targetOrgID)) {
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.targetActorID).and(deleteStatement.expOf(aclTable.f_orgID).xIsNull().or(deleteStatement.expOf(aclTable.f_orgID).xEq(AccessControlConstants.DEFAULT_ACVERSION))));
						subQuery.setCondition(aclTableReference.expOf(aclTable.f_actorID).xEq(task.sourceActorID).and(aclTableReference.expOf(aclTable.f_orgID).xIsNull().or(aclTableReference.expOf(aclTable.f_orgID).xEq(AccessControlConstants.DEFAULT_ACVERSION))));
					} else {
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.targetActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(task.targetOrgID)));
						if (AccessControlConstants.isDefaultACVersion(task.sourceOrgID)) {
							subQuery.setCondition(aclTableReference.expOf(aclTable.f_actorID).xEq(task.sourceActorID).and(aclTableReference.expOf(aclTable.f_orgID).xIsNull().or(aclTableReference.expOf(aclTable.f_orgID).xEq(AccessControlConstants.DEFAULT_ACVERSION))));
						} else {
							subQuery.setCondition(aclTableReference.expOf(aclTable.f_actorID).xEq(task.sourceActorID).and(aclTableReference.expOf(aclTable.f_orgID).xEq(task.sourceOrgID)));
						}
					}
				} else {
					final TD_CoreAuthAuthACL aclTable = AuthorityDataBaseService.this.td_CoreAuthAuthACL;
					deleteStatement = context.newDeleteStatement(aclTable);
					insertStatement = context.newInsertStatement(aclTable);
					final DerivedQueryDeclare subQuery = insertStatement.getInsertValues();
					final QuTableRefDeclare aclTableReference = subQuery.newReference(aclTable);
					subQuery.newColumn(SQLFunc.xNewRecid(), aclTable.f_RECID.getName());
					subQuery.newColumn(ConstExpr.expOf(task.targetActorID), aclTable.f_actorID.getName());
					subQuery.newColumn(ConstExpr.expOf(task.targetOrgID), aclTable.f_orgID.getName());
					subQuery.newColumn(aclTable.f_resCategoryID, aclTable.f_resCategoryID.getName());
					subQuery.newColumn(aclTable.f_resourceID, aclTable.f_resourceID.getName());
					subQuery.newColumn(aclTable.f_authorityCode, aclTable.f_authorityCode.getName());
					if (AccessControlConstants.isDefaultACVersion(task.targetOrgID)) {
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.targetActorID).and(deleteStatement.expOf(aclTable.f_orgID).xIsNull().or(deleteStatement.expOf(aclTable.f_orgID).xEq(AccessControlConstants.DEFAULT_ACVERSION))));
						subQuery.setCondition(aclTableReference.expOf(aclTable.f_actorID).xEq(task.sourceActorID).and(aclTableReference.expOf(aclTable.f_orgID).xIsNull().or(aclTableReference.expOf(aclTable.f_orgID).xEq(AccessControlConstants.DEFAULT_ACVERSION))));
					} else {
						deleteStatement.setCondition(deleteStatement.expOf(aclTable.f_actorID).xEq(task.targetActorID).and(deleteStatement.expOf(aclTable.f_orgID).xEq(task.targetOrgID)));
						if (AccessControlConstants.isDefaultACVersion(task.sourceOrgID)) {
							subQuery.setCondition(aclTableReference.expOf(aclTable.f_actorID).xEq(task.sourceActorID).and(aclTableReference.expOf(aclTable.f_orgID).xIsNull().or(aclTableReference.expOf(aclTable.f_orgID).xEq(AccessControlConstants.DEFAULT_ACVERSION))));
						} else {
							subQuery.setCondition(aclTableReference.expOf(aclTable.f_actorID).xEq(task.sourceActorID).and(aclTableReference.expOf(aclTable.f_orgID).xEq(task.sourceOrgID)));
						}
					}
				}
				// 删除之前的权限数据
				context.executeUpdate(deleteStatement);
				context.executeUpdate(insertStatement);
			}
		}

	}

	@Publish
	final class UpdateGroupIdentifierTaskHandler extends
			TaskMethodHandler<UpdateResourceCategoryTask, None> {

		protected UpdateGroupIdentifierTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final UpdateResourceCategoryTask task) throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final GUID oldGroupID = GUID.MD5Of(task.oldFacadeClass.getName());
				final GUID newGroupID = GUID.MD5Of(task.newFacadeClass.getName());
				AuthorityDataBaseService.this.updateGroupIdentifier(context, oldGroupID, newGroupID);
			}
		}

	}

	@Publish
	final class UpdateUserAndRoleGroupIdentifierTaskHandler extends
			TaskMethodHandler<UpdateUserAndRoleGroupIdentifierTask, None> {

		protected UpdateUserAndRoleGroupIdentifierTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final UpdateUserAndRoleGroupIdentifierTask task)
				throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final Cache cache = context.occorAt.site.cache;
				final CacheGroup<?, ?, ?> newUserGroup = cache.defaultGroupSpace.findGroup(User.class, context.transaction);
				final CacheGroup<?, ?, ?> newRoleGroup = cache.defaultGroupSpace.findGroup(Role.class, context.transaction);
				if (newUserGroup != null && newUserGroup.accessControlInformation != null) {
					AuthorityDataBaseService.this.updateGroupIdentifier(context, OLD_DEFAULTUSERGROUPID, newUserGroup.accessControlInformation.ACGUIDIdentifier);
				}
				if (newRoleGroup != null && newRoleGroup.accessControlInformation != null) {
					AuthorityDataBaseService.this.updateGroupIdentifier(context, OLD_DEFAULTROLEGROUPID, newRoleGroup.accessControlInformation.ACGUIDIdentifier);
				}
			}
		}
	}

	@Publish
	final class ForACVersionUserListProvider extends
			OneKeyResultListProvider<User, GetUserOrgMapForOrgKey> {

		@Override
		protected final void provide(final ContextImpl<?, ?, ?> context,
				final GetUserOrgMapForOrgKey key, List<User> resultList)
				throws Throwable {
			if (AuthorityDataBaseService.this.isDBValid()) {
				final TD_CoreAuthUOM uomTable = AuthorityDataBaseService.this.td_CoreAuthUOM;
				final QueryStatementDeclare queryStatement = context.newQueryStatement();
				final QuTableRefDeclare referenceDefine_UOM = queryStatement.newReference(uomTable);
				final QueryColumnDeclare column_UserID = queryStatement.newColumn(uomTable.f_actorID);
				final ArgumentDefine argument_ACVersion = queryStatement.newArgument(uomTable.f_orgID);
				queryStatement.setCondition(referenceDefine_UOM.expOf(uomTable.f_orgID).xEq(argument_ACVersion));
				queryStatement.newOrderBy(uomTable.f_actorID, true);
				final RecordSet recordSet = context.openQuery(queryStatement, key.orgID);
				final RecordSetFieldContainer<? extends RecordSetField> recordSetFields = recordSet.getFields();
				final RecordSetField field_UserID = recordSetFields.find(column_UserID);
				while (recordSet.next()) {
					User user = context.find(User.class, field_UserID.getGUID());
					if (user != null) {
						resultList.add(user);
					}
				}
			}
		}
	}
}