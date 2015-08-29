package com.jiuqi.dna.core.internal.da.sqlbuffer;

/**
 * 查询语句buffer,包含with和orderby,limit和offset
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlQueryBuffer extends ISqlBuffer, ISqlCommandBuffer {

	public ISqlSelectBuffer newWith(String alias);

	public ISqlSelectBuffer select();

	public ISqlExprBuffer limit();

	public ISqlExprBuffer offset();

	public ISqlExprBuffer newOrder(boolean desc);

	public void newOrder(String column, boolean desc);
}
