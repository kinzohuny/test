/**
 * 
 */
package com.jiuqi.dna.core.spi.auth;

/**
 * ��ȡ��������е���Դ����
 * 
 * @author yangduanxue
 *
 */
public final class GetResourceCategoryStubByFacadeClass {

	private String facadeClassName;

	/**
	 * @param facadeClass
	 */
	public GetResourceCategoryStubByFacadeClass(String facadeClass) {
		super();
		this.facadeClassName = facadeClass;
	}

	public final String getFacadeClassName() {
		return facadeClassName;
	}
	
	
}
