/**
 * 
 */
package com.jiuqi.dna.core.impl;

/**
 * 为了资源引用而设计
 * 
 * @author gaojingxin
 * 
 */
interface FieldValueAccessor {

	public Object internalGet(Object so);

	public void internalSet(Object so, Object value);
}