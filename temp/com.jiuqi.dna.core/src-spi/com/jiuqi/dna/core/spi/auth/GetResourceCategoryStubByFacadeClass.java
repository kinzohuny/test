/**
 * 
 */
package com.jiuqi.dna.core.spi.auth;

/**
 * 获取外观类所有的资源分组
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
