/**
 * 
 */
package com.jiuqi.dna.core.auth;

import java.util.List;

/**
 * Ȩ�޹������
 * 
 * @author yangduanxue
 *
 */
public interface AuthRuleStub {

	/**
	 * ���ع����ʶ
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * ���ع�������
	 * 
	 * @return
	 */
	public String getDescrition();
	
	/**
	 * ���ع������
	 * 
	 * @return
	 */
	public String getGroup();
	
	/**
	 * �����Ƿ�����
	 * 
	 * @return
	 */
	public boolean isUsing();
	
	/**
	 * ����Ȩ����Դ���
	 * 
	 * @return
	 */
	public List<Object> getResourceCategories();
	
	/**
	 * ����Ȩ����Դ����
	 * 
	 * @return
	 */
	public List<Operation<?>> getOperations();
	
	/**
	 * ���ع�����
	 * 
	 * @return
	 */
	public AuthorityRule<?> getAuthorityRule();
}
