/**
 * 
 */
package com.jiuqi.dna.core.spi.auth;

import com.jiuqi.dna.core.type.GUID;

/**
 * ��Դ��𣨷��飩���գ���������ʶ������
 * 
 * @author yangduanxue
 *
 */
public final class ResourceCategoryStub {

	private Object identity; //����ʶ
	private String title; // ������
	
	private GUID resCategoryId; // Ȩ����Դ���id��acl����resCategoryId�ֶ�ֵ
	/**
	 * @param identity
	 * @param title
	 */
	public ResourceCategoryStub(Object identity, String title) {
		this.identity = identity;
		this.title = title;
	}
	public final Object getIdentity() {
		return identity;
	}
	public final String getTitle() {
		return title;
	}
	public GUID getResCategoryId() {
		return resCategoryId;
	}
	public void setResCategoryId(GUID resCategoryId) {
		this.resCategoryId = resCategoryId;
	}
	
}
