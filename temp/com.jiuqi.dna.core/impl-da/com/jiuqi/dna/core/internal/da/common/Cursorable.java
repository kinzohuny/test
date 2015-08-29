package com.jiuqi.dna.core.internal.da.common;

/**
 * 可游标移动的
 * 
 * @author houchunlei
 */
public interface Cursorable {

	/**
	 * 游标移动到第一行之前
	 */
	public void beforeFirst();

	/**
	 * 游标移动到最后一行之后
	 */
	public void afterLast();

	/**
	 * 是否在第一行之前
	 * 
	 * @return
	 */
	public boolean isBeforeFirst();

	/**
	 * 是否在最后一行之后
	 * 
	 * @return
	 */
	public boolean isAfterLast();

	/**
	 * 游标移动到第一行
	 * 
	 * @return 是否有效记录行
	 */
	public boolean first();

	/**
	 * 游标移动到最后一行
	 * 
	 * @return 是否有效记录行
	 */
	public boolean last();

	/**
	 * 是否第一行
	 * 
	 * @return
	 */
	public boolean isFirst();

	/**
	 * 是否最后一行
	 * 
	 * @return
	 */
	public boolean isLast();

	/**
	 * 游标后移一行
	 * 
	 * @return 是否有效行
	 */
	public boolean next();

	/**
	 * 游标前移一行
	 * 
	 * @return 是否有效行
	 */
	public boolean previous();

	/**
	 * 相对移动游标
	 * 
	 * @param rows
	 * @return 是否有效行
	 */
	public boolean relative(int rows);

	/**
	 * 绝对移动游标
	 * 
	 * @param row
	 * @return 是否有效行
	 */
	public boolean absolute(int row);
}