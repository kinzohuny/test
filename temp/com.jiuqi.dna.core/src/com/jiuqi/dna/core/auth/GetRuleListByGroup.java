/**
 * 
 */
package com.jiuqi.dna.core.auth;

/**
 * 获取一个分组下的权限规则
 * 
 * 使用示例：context.getList(RuleStub.class, new GetRuleListByGroup(XX));
 * 
 * @author yangduanxue
 *
 */
public final class GetRuleListByGroup {

	private AuthRuleGroup group;

	/**
	 * @param group
	 */
	public GetRuleListByGroup(AuthRuleGroup group) {
		super();
		this.group = group;
	}

	/**
	 * @return the group
	 */
	public final AuthRuleGroup getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public final void setGroup(AuthRuleGroup group) {
		this.group = group;
	}
	
	
}
