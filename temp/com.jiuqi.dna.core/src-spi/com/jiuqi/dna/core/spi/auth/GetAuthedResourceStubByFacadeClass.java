/**
 * 
 */
package com.jiuqi.dna.core.spi.auth;

/**
 * �������������ȡ����Ȩ��Դ����
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
