package com.jiuqi.dna.core.misc;

/**
 * 可渲染成XML对象的接口
 * 
 * @author gaojingxin
 * 
 */
public interface SXRenderable {

	/**
	 * 返回当前节点的XML标记名称
	 * 
	 * @return 返回当前节点的XML标记名称
	 */
	public String getXMLTagName();

	/**
	 * 实现该方法将定义写入XML
	 * 
	 * @param element
	 */
	public void render(SXElement element);
}
