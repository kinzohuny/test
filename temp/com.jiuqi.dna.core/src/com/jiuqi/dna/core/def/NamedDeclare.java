package com.jiuqi.dna.core.def;

/**
 * 可修改的有名称的定义基接口
 * 
 * @author gaojingxin
 * 
 */
public interface NamedDeclare extends NamedDefine,DeclareBase {
	/**
	 * 设置标题
	 * 
	 * @param title 标题
	 */
	public void setTitle(String title);
}
