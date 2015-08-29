/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 修改权限规则任务，设置规则是否启用和参数信息
 * 
 * @author yangduanxue
 *
 */
public final class ModifyAuthRuleTask extends SimpleTask {

	private boolean using;
	private String name;
	private List<Object> categories;
	private List<Operation<?>> operations;
	
	/**
	 * @param using
	 * @param name
	 */
	public ModifyAuthRuleTask(boolean using, String name) {
		super();
		this.using = using;
		this.name = name;
	}

	/**
	 * @return the using
	 */
	public final boolean isUsing() {
		return using;
	}

	/**
	 * @param using the using to set
	 */
	public final void setUsing(boolean using) {
		this.using = using;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	public final List<Object> getCategories() {
		return categories;
	}

	public final void setCategories(List<Object> categories) {
		this.categories = categories;
	}

	public final List<Operation<?>> getOperations() {
		return operations;
	}

	public final void setOperations(List<Operation<?>> operations) {
		this.operations = operations;
	}

	
}
