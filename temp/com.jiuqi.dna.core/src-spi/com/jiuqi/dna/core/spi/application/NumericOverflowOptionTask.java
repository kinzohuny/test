package com.jiuqi.dna.core.spi.application;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 浮点数超精度后行文选项
 * 
 * @author gaojingxin
 * 
 */
public final class NumericOverflowOptionTask extends SimpleTask {
	/**
	 * 超精度时设为0，或null
	 */
	public static final int SET_NULL = 0;
	/**
	 * 超精度时设为0，或null并且打印错误
	 */
	public static final int SET_NULL_PRINT_ERROR = 1;
	/**
	 * 超精度时抛出异常
	 */
	public static final int THROW_ERROR = 2;
	/**
	 * 模式，参考上述模式
	 */
	public final int mode;

	public NumericOverflowOptionTask(int mode) {
		this.mode = mode;
	}
}
