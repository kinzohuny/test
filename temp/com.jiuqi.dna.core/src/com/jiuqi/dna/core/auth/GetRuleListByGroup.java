/**
 * 
 */
package com.jiuqi.dna.core.auth;

/**
 * ��ȡһ�������µ�Ȩ�޹���
 * 
 * ʹ��ʾ����context.getList(RuleStub.class, new GetRuleListByGroup(XX));
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
