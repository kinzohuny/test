package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.resource.ResourceToken;

/**
 * Ȩ�޼̳�·��
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
	 * Ȩ�޼̳�·���ڵ�
	 * 
	 * @author liuzhi
	 * 
	 */
	public interface Node {

		/**
		 * ��ȡ�̳нڵ�
		 * 
		 * @return ���ؼ̳нڵ�
		 */
		public Node getInheritNode();

		/**
		 * ��ȡ�ڵ���Դ���
		 * 
		 * @return ���ؽڵ���Դ���
		 */
		public ResourceToken<?> getValue();

		/**
		 * ��ȡ�ڵ���Դ����
		 * 
		 * @return ���ؽڵ���Դ����
		 */
		public String getTitle();

		/**
		 * ��ȡ�ڵ���Դ����ö��
		 * 
		 * @return ���ؽڵ���Դ����ö��
		 */
		public Operation<?>[] getOperations();

	}

	/**
	 * ��ȡ���ڵ�
	 * 
	 * @return ���ػ��ڵ�
	 */
	public Node getBaseNode();

}
