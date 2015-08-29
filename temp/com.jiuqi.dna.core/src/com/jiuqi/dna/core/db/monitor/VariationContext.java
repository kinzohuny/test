package com.jiuqi.dna.core.db.monitor;

/**
 * 用于控制已发生的变化量
 * 
 * <p>
 * 上下文对象，只能在请求级别保存。
 * 
 * @author Hou
 * 
 */
public interface VariationContext {

	/**
	 * 获取所有的变化量集合
	 * 
	 * @return
	 */
	public VariationSet get();

	/**
	 * 获取在指定版本之后的所有变化量集合
	 * 
	 * @param lower
	 * @return
	 */
	public VariationSet getAfter(long lower);

	// public VariationSet get(Boundary<VariationVersion> lower);
	// public VariationSet get(Boundary<VariationVersion> lower,
	// Boundary<VariationVersion> upper);

	/**
	 * 获取当前存在的最大的变化量版本
	 * 
	 * @return
	 */
	public VariationVersion max();

	/**
	 * 移除指定及之前所有版本的变化量
	 * 
	 * @param upper
	 * @return
	 */
	public int removeOutdated(long upper);

	public int removeSpecified(Iterable<Variation> it);
}