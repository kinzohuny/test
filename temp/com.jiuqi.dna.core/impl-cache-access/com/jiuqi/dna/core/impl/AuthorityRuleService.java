/**
 * 
 */
package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.auth.AuthRuleGroup;
import com.jiuqi.dna.core.auth.AuthRuleStub;
import com.jiuqi.dna.core.auth.AuthorityRule;
import com.jiuqi.dna.core.auth.AuthorityedResourceStub;
import com.jiuqi.dna.core.auth.GetRuleGroupList;
import com.jiuqi.dna.core.auth.GetRuleListByGroup;
import com.jiuqi.dna.core.auth.ModifyAuthRuleTask;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.da.RecordSet;
import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.def.query.ModifyStatementDefine;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.impl.AuthorityRuleService.AuthRuleStubImpl;
import com.jiuqi.dna.core.resource.ResourceContext;
import com.jiuqi.dna.core.resource.ResourceInserter;
import com.jiuqi.dna.core.resource.ResourceKind;
import com.jiuqi.dna.core.resource.ResourceService;
import com.jiuqi.dna.core.service.Publish;
import com.jiuqi.dna.core.spi.auth.GetResourceCategoryStubByFacadeClass;
import com.jiuqi.dna.core.spi.auth.ResourceCategoryStub;
import com.jiuqi.dna.core.type.GUID;

/**
 * 权限规则服务
 * 
 * @author yangduanxue
 * 
 */
public final class AuthorityRuleService extends
		ResourceService<AuthRuleStub, AuthRuleStubImpl, AuthRuleStubImpl> {

	protected AuthorityRuleService() {
		super("权限规则服务", ResourceKind.SINGLETON_IN_CLUSTER);
	}

	protected void initResources(
			Context context,
			ResourceInserter<AuthRuleStub, AuthRuleStubImpl, AuthRuleStubImpl> initializer)
			throws Throwable {
		String q_rule = "define query q_rule()" + 
						" begin" + 
						" select r.name as name, r.isusing as isusing, r.categories as categories, r.operations as operations from core_auth_rule as r" + 
						" end";
		RecordSet rst = context.openQuery((QueryStatementDefine) context.parseStatement(q_rule));
		Map<String, Boolean> ruleState = new HashMap<String, Boolean>();
		Map<String, String> ruleCategories = new HashMap<String, String>();
		Map<String, String> ruleOperations = new HashMap<String, String>();
		while (rst.next()) {
			String name = rst.getFields().get(0).getString();
			boolean isUsing = rst.getFields().get(1).getBoolean();
			String categories = rst.getFields().get(2).getString();
			String operations = rst.getFields().get(3).getString();
			ruleState.put(name, isUsing);
			ruleCategories.put(name, categories);
			ruleOperations.put(name, operations);
		}
		
		Map<String, AuthorityedResourceStub> authedResource = new HashMap<String, AuthorityedResourceStub>();
		for (AuthorityedResourceStub stub : context.getList(AuthorityedResourceStub.class)) {
			authedResource.put(stub.getFacadeClass().getName(), stub);
		}
		
		for (Class<?> clazz : AuthorityRuleGather.getRuleGroups()) {
			for (AuthorityRule<?> rule : AuthorityRuleGather.getRules(clazz)) {
				AuthRuleStubImpl stub = new AuthRuleStubImpl(rule);
				Object isusing = ruleState.get(rule.getName());
				// 设置规则状态
				if (isusing != null) {
					stub.setUsing((Boolean) isusing);
				}
				// 设置规则资源类别
				if (ruleCategories.get(rule.getName()) != null) {
					stub.setCategories(deserializeCategory(context, rule.getFacadeClass().getName(), ruleCategories.get(rule.getName())));
				}
				// 设置规则操作
				if (ruleOperations.get(rule.getName()) != null) {
					stub.setOperations(deserializeOperation(ruleOperations.get(rule.getName()), authedResource.get(rule.getFacadeClass().getName())));
				}
				
				initializer.putResource(stub);
			}
		}
	}
	
	private final List<Operation<?>> deserializeOperation(String operations, AuthorityedResourceStub ars) {
		List<Operation<?>> lst = new ArrayList<Operation<?>>();
		if (operations != null && operations.length() > 0 && ars != null) {
			String[] arr = operations.split(":");
			for (String str : arr) {
				if (str == null || str.length() == 0) {
					continue;
				}
				for (Operation<?> op : ars.getOperations()) {
					if (String.valueOf(op.getMask()).equals(str)) {
						lst.add(op);
						break;
					}
				}
			}
		}
		return lst;
	}
	
	private final List<Object> deserializeCategory(Context context, String facadeName, String categories) {
		List<Object> lst = new ArrayList<Object>();
		List<ResourceCategoryStub> objs = context.getList(ResourceCategoryStub.class, new GetResourceCategoryStubByFacadeClass(facadeName));
		if (categories != null && categories.length() > 0 && objs.size() > 0) {
			String[] arr = categories.split(":");
			for (String str : arr) {
				if (str == null || str.length() == 0) {
					continue;
				}
				for (ResourceCategoryStub rcs : objs) {
					if (str.equals(rcs.getIdentity().toString())) {
						lst.add(rcs.getIdentity());
						break;
					}
				}
				lst.add(lst);
			}
		}
		return lst;
	}

	@Publish
	protected final class ByNameProvider extends OneKeyResourceProvider<String> {

		@Override
		protected String getKey1(AuthRuleStubImpl keysHolder) {
			return keysHolder.getName();
		}

	}

	@Publish
	protected final class GetRuleGroupListProvider extends
			OneKeyResultListProvider<AuthRuleGroup, GetRuleGroupList> {

		@Override
		protected void provide(
				ResourceContext<AuthRuleStub, AuthRuleStubImpl, AuthRuleStubImpl> context,
				GetRuleGroupList key, List<AuthRuleGroup> resultList)
				throws Throwable {
			for (Class<?> facade : AuthorityRuleGather.getRuleGroups()) {
				Map<String, AuthRuleGroupImpl> index = new HashMap<String, AuthRuleGroupImpl>();
				for (AuthorityRule<?> ar : AuthorityRuleGather.getRules(facade)) {
					if (index.get(ar.getGroup()) == null) {
						AuthRuleGroupImpl ag = new AuthRuleGroupImpl(ar.getGroup(), facade);
						resultList.add(ag);
						index.put(ag.getTitle(), ag);
					}
				}
			}
		}

	}

	@Publish
	protected final class GetAuthRuleByGroupProvider extends
			OneKeyResultListProvider<AuthRuleStub, GetRuleListByGroup> {

		@Override
		protected void provide(
				ResourceContext<AuthRuleStub, AuthRuleStubImpl, AuthRuleStubImpl> context,
				GetRuleListByGroup key, List<AuthRuleStub> resultList)
				throws Throwable {
			for (AuthRuleStub stub : context.getList(AuthRuleStub.class)) {
				if (key.getGroup().getTitle().equals(stub.getGroup())
						&& key.getGroup().getFacadeClass().equals(stub.getAuthorityRule().getFacadeClass())) {
					resultList.add(stub);
				}
			}
		}

	}

	@Publish
	protected final class GetAuthRuleByFacadeClassProvider extends
			OneKeyResultListProvider<AuthRuleStub, Class<?>> {

		@Override
		protected void provide(
				ResourceContext<AuthRuleStub, AuthRuleStubImpl, AuthRuleStubImpl> context,
				Class<?> key, List<AuthRuleStub> resultList) throws Throwable {

			for (AuthRuleStub stub : context.getList(AuthRuleStub.class)) {
				if (key.equals(stub.getAuthorityRule().getFacadeClass()) && stub.isUsing()) {
					resultList.add(stub);
				}
			}
		}

	}

	@Publish
	protected final class ModifyAuthRuleHandler extends
			SimpleTaskMethodHandler<ModifyAuthRuleTask> {

		@Override
		protected void handle(
				ResourceContext<AuthRuleStub, AuthRuleStubImpl, AuthRuleStubImpl> context,
				ModifyAuthRuleTask task) throws Throwable {

			AuthRuleStubImpl rs = context.modifyResource(task.getName());
			rs.setUsing(task.isUsing());
			rs.setCategories(task.getCategories());
			rs.setOperations(task.getOperations());
			context.postModifiedResource(rs);

			String q_rule = "define query q_rule(@name string)" + " begin" + " select count(*) from core_auth_rule as r where r.name = @name" + " end";
			RecordSet rst = context.openQuery((QueryStatementDefine) context.parseStatement(q_rule), task.getName());
			if (rst.next() && rst.getFields().get(0).getInt() > 0) {
				String u_rule = "define update u_rule(@isusing boolean, @categories string, @operations string)" +
						" begin" + 
						" update core_auth_rule as r set isusing = @isusing, categories = @categories, operations = @operations where r.name = \'" + task.getName() + "\'" 
						+ " end";
				context.executeUpdate((ModifyStatementDefine) context.parseStatement(u_rule), task.isUsing(), serializeCategories(task.getCategories()), serializeOperations(task.getOperations()));
			} else {
				String i_rule = "define insert i_rule(@recid guid, @name string, @isusing boolean, @categories string, @operations string)" + 
								" begin" + 
								" insert into core_auth_rule(recid, name, isusing, categories, operations) values(@recid, @name, @isusing, @categories, @operations)" + 
								" end";
				context.executeUpdate((ModifyStatementDefine) context.parseStatement(i_rule), GUID.randomID(), task.getName(), task.isUsing(), serializeCategories(task.getCategories()), serializeOperations(task.getOperations()));
			}
		}

	}
	
	private final String serializeCategories(List<Object> categories) {
		if (categories == null || categories.size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for (Object obj : categories) {
			if (index == categories.size() - 1) {
				sb.append(obj.toString());
			} else {
				sb.append(obj.toString()).append(":");
			}
			index ++;
		}
		return sb.toString();
	}
	
	private final String serializeOperations(List<Operation<?>> operations) {
		if (operations == null || operations.size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int index = 0;
		for (Operation<?> op : operations) {
			if (index == operations.size() - 1) {
				sb.append(op.getMask());
			} else {
				sb.append(op.getMask()).append(":");
			}
			index ++;
		}
		return sb.toString();
	}
	
	@Override
	protected Object extractSerialUserData(AuthRuleStubImpl impl, AuthRuleStubImpl keys) {
		return 1;
	}

	@Override
	protected void restoreSerialUserData(Object userData, AuthRuleStubImpl impl,
			AuthRuleStubImpl keys, ObjectQuerier querier) {
		if (impl.getAuthorityRule() != null) {
			return;
		}
		if (impl.getName() == null || impl.getName().length() == 0) {
			return;
		}
		for (Class<?> clazz : AuthorityRuleGather.getRuleGroups()) {
			for (AuthorityRule<?> rule : AuthorityRuleGather.getRules(clazz)) {
				if (impl.getName().equals(rule.getName())) {
					impl.setRule(rule);
					return;
				}
			}
		}
	}

	static final class AuthRuleGroupImpl implements AuthRuleGroup {
		private String title;
		private Class<?> key;

		/**
		 * @param title
		 * @param key
		 */
		public AuthRuleGroupImpl(String title, Class<?> key) {
			this.title = title;
			this.key = key;
		}

		/**
		 * @return the title
		 */
		public final String getTitle() {
			return this.title;
		}

		/**
		 * @param title
		 *            the title to set
		 */
		public final void setTitle(String title) {
			this.title = title;
		}

		/**
		 * @return the key
		 */
		public final Class<?> getKey() {
			return this.key;
		}

		/**
		 * @param key
		 *            the key to set
		 */
		public final void setKey(Class<?> key) {
			this.key = key;
		}

		public Class<?> getFacadeClass() {
			return this.key;
		}

	}

	@StructClass
	static final class AuthRuleStubImpl implements AuthRuleStub {

		private AuthorityRule<?> rule;
		private boolean using;
		private String name;
		private List<Object> categories;
		private List<Operation<?>> operations;

		/**
		 * @param rule
		 * @param using
		 */
		public AuthRuleStubImpl(AuthorityRule<?> rule) {
			this.rule = rule;
			this.name = rule.getName();
			this.using = rule.isUsing();
			this.categories = rule.getResourceCategories();
			this.operations = rule.getOpertions();
		}

		/**
		 * @param isUsing
		 *            the isUsing to set
		 */
		public final void setUsing(boolean isUsing) {
			this.using = isUsing;
		}

		public String getName() {
			return this.name;
		}

		public String getDescrition() {
			return this.rule.getDescription();
		}

		public String getGroup() {
			return this.rule.getGroup();
		}

		public boolean isUsing() {
			return this.using;
		}

		public AuthorityRule<?> getAuthorityRule() {
			return this.rule;
		}

		public void setRule(AuthorityRule<?> rule) {
			this.rule = rule;
			this.name = rule.getName();
		}

		public List<Object> getResourceCategories() {
			return this.categories;
		}

		public List<Operation<?>> getOperations() {
			return this.operations;
		}

		public final List<Object> getCategories() {
			return categories;
		}

		public final void setCategories(List<Object> categories) {
			this.categories = categories;
		}

		public final void setOperations(List<Operation<?>> operations) {
			this.operations = operations;
		}
		
	}
	
}