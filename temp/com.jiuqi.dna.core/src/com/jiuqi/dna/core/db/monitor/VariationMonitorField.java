package com.jiuqi.dna.core.db.monitor;

/**
 * 监视的字段
 * 
 * @author houchunlei
 * 
 */
public interface VariationMonitorField {

	// /**
	// * 对于定点数值的字段，是否使用BigDecimal来装载数据。
	// *
	// * @return
	// */
	// public boolean usingBigDecimal();

	/**
	 * 监视的目标字段
	 * 
	 * @return
	 */
	public String getWatchFN();

	/**
	 * 变化时的旧值存储字段
	 * 
	 * @return
	 */
	public String getOldValueFN();

	/**
	 * 变化时的新值存储字段
	 * 
	 * @return
	 */
	public String getNewValueFN();
}