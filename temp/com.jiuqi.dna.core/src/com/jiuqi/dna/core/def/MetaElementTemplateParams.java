package com.jiuqi.dna.core.def;

/**
 * 声明器模板的参数接口
 * 
 * @author gaojingxin
 * 
 */
public interface MetaElementTemplateParams {
	/**
	 * 实例的名称
	 */
	public String getName();

	/**
	 * 获得实例所需的参数
	 */
	public <TParam> TParam getParam(Class<TParam> paramClass);

	/**
	 * 获得实例所需的参数
	 */
	public <TParam> TParam getParam(Class<TParam> paramClass, int tag);

}
