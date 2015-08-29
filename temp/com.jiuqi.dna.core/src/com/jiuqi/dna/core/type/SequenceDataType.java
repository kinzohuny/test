package com.jiuqi.dna.core.type;

/**
 * 字符串类型
 * 
 * @author gaojingxin
 * 
 */
public interface SequenceDataType extends ObjectDataType {
	/**
	 * 获取最大长度小于等于0表示没有限制
	 */
	public int getMaxLength();

	/**
	 * 是否是定长数据
	 */
	public boolean isFixedLength();
}
