package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.type.DataType;

/**
 * �α�ѭ������
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlCursorLoopBuffer extends ISqlSegmentBuffer {

	/**
	 * �����α�Ĳ�ѯ����
	 * 
	 * @return
	 */
	public ISqlQueryBuffer query();

	/**
	 * �����򿪶�ȡ�α�ÿ��ʱ,װ��ı���.
	 */
	public void declare(String name, DataType type);
}
