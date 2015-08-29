package com.jiuqi.dna.core.spi.setl;

/**
 * 维度值集合。<br>
 * 该接口由指标提取功能实现。
 */
public interface SETLTargetDimValueSet {
	/**
	 * 获取值的数目
	 */
	public int size();

	/**
	 * 增加一个维度值
	 * 
	 * @param value
	 */
	public void addValue(Object value);

	/**
	 * 向集合中增加一个左闭右开的区间[from, to)，即该区间包含from，不包含to<br>
	 * 先后增加到同一个集合中的区间不能存在重叠的区域。
	 * 
	 * @param from
	 * @param to
	 */
	public void addRange(Object from, Object to);
}
