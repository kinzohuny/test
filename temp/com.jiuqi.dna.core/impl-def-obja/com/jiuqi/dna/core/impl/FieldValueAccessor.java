/**
 * 
 */
package com.jiuqi.dna.core.impl;

/**
 * Ϊ����Դ���ö����
 * 
 * @author gaojingxin
 * 
 */
interface FieldValueAccessor {

	public Object internalGet(Object so);

	public void internalSet(Object so, Object value);
}