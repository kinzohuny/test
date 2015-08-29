/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;

/**
 * 权限规则快照
 * 
 * @author yangduanxue
 *
 */
public interface AuthRuleStub {

	/**
	 * 返回规则标识
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 返回规则描述
	 * 
	 * @return
	 */
	public String getDescrition();
	
	/**
	 * 返回规则分组
	 * 
	 * @return
	 */
	public String getGroup();
	
	/**
	 * 规则是否启用
	 * 
	 * @return
	 */
	public boolean isUsing();
	
	/**
	 * 返回权限资源类别
	 * 
	 * @return
	 */
	public List<Object> getResourceCategories();
	
	/**
	 * 返回权限资源操作
	 * 
	 * @return
	 */
	public List<Operation<?>> getOperations();
	
	/**
	 * 返回规则定义
	 * 
	 * @return
	 */
	public AuthorityRule<?> getAuthorityRule();
}
