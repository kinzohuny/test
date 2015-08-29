package com.jiuqi.dna.core.impl;

/**
 * 可定位对象接口
 * 
 * @author niuhaifeng
 * 
 */
public interface TextLocalizable {
	/**
	 * 获取起始行号
	 * 
	 * @return
	 */
	public abstract int startLine();

	/**
	 * 获取起始列号
	 * 
	 * @return
	 */
	public abstract int startCol();

	/**
	 * 获取结束行号
	 * 
	 * @return
	 */
	public abstract int endLine();

	/**
	 * 获取结束列号
	 * 
	 * @return
	 */
	public abstract int endCol();
}
