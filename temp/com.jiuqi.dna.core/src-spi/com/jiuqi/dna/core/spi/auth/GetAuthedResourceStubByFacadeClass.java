/**
 * 
 */
package com.jiuqi.dna.core.spi.auth;

/**
 * 根据外观类名获取可授权资源快照
 * 
 * @author yangduanxue
 *
 */
public final class GetAuthedResourceStubByFacadeClass {

	private String facadeClass;

	/**
	 * @param facadeClass
	 */
	public GetAuthedResourceStubByFacadeClass(String facadeClass) {
		super();
		this.facadeClass = facadeClass;
	}

	public final String getFacadeClass() {
		return facadeClass;
	}
	
}
