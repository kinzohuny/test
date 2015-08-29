/**
 * 
 */
package com.jiuqi.dna.core.auth;


/**
 * 可授权的资源快照，包含资源外观以及对应的所有操作
 * 
 * @author yangduanxue
 *
 */
public final class AuthorityedResourceStub {

	private Class<?> facadeClass;
	private Operation<?>[] operations;
	private String title;
	
	/**
	 * @param facadeClass
	 * @param operations
	 */
	public AuthorityedResourceStub(Class<?> facadeClass,
			Operation<?>[] operations) {
		super();
		this.facadeClass = facadeClass;
		this.operations = operations;
	}
	public AuthorityedResourceStub(Class<?> facadeClass,
			Operation<?>[] operations, String title) {
		super();
		this.facadeClass = facadeClass;
		this.operations = operations;
		this.title = title;
	}
	
	/**
	 * @return the facadeClass
	 */
	public final Class<?> getFacadeClass() {
		return facadeClass;
	}
	/**
	 * @param facadeClass the facadeClass to set
	 */
	public final void setFacadeClass(Class<?> facadeClass) {
		this.facadeClass = facadeClass;
	}
	/**
	 * @return the operations
	 */
	public final Operation<?>[] getOperations() {
		return operations;
	}
	/**
	 * @param operations the operations to set
	 */
	public final void setOperations(Operation<?>[] operations) {
		this.operations = operations;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
}
