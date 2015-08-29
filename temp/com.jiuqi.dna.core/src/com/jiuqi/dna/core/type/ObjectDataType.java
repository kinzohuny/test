package com.jiuqi.dna.core.type;

/**
 * 对象类型
 * 
 * @author gaojingxin
 * 
 */
public interface ObjectDataType extends DataType {
	/**
	 * 是否是该枚举类型的实例
	 */
	public boolean isInstance(Object obj);
}
