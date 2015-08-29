package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * 子查询引用buffer
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlQueryRefBuffer extends ISqlRelationRefBuffer {

	public ISqlSelectBuffer select();
}
