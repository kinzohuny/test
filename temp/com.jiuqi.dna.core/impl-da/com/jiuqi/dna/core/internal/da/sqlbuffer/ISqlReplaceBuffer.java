package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * mysql��replace���buffer
 * 
 * @author houchunlei
 * 
 */
public interface ISqlReplaceBuffer extends ISqlBuffer, ISqlCommandBuffer {

	/**
	 * ����replace�ֶ�
	 * 
	 * @param name
	 *            unquoted
	 */
	void newField(String name);

	/**
	 * replace�ֶε�ֵ
	 * 
	 * @return
	 */
	ISqlExprBuffer newValue();
}
