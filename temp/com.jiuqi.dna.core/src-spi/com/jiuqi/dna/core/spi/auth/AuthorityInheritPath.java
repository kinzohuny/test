package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.resource.ResourceToken;

/**
 * 权限继承路径
 * 
 * <pre>
 * null &lt;--InheritNode--+
 *                      |
 *                     NODE &lt;--InheritNode--+
 *                      |                   |
 *                      +------------------NODE &lt;--InheritNode--+
 *                                          |                   |
 *                                          +------------------NODE(BaseNode)
 * </pre>
 * 
 * @author liuzhi
 * 
 */
public interface AuthorityInheritPath {

	/**
	 * 权限继承路径节点
	 * 
	 * @author liuzhi
	 * 
	 */
	public interface Node {

		/**
		 * 获取继承节点
		 * 
		 * @return 返回继承节点
		 */
		public Node getInheritNode();

		/**
		 * 获取节点资源句柄
		 * 
		 * @return 返回节点资源句柄
		 */
		public ResourceToken<?> getValue();

		/**
		 * 获取节点资源标题
		 * 
		 * @return 返回节点资源标题
		 */
		public String getTitle();

		/**
		 * 获取节点资源操作枚举
		 * 
		 * @return 返回节点资源操作枚举
		 */
		public Operation<?>[] getOperations();

	}

	/**
	 * 获取基节点
	 * 
	 * @return 返回基节点
	 */
	public Node getBaseNode();

}
