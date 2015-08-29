package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.misc.Boundary;

/**
 * 变化量集
 * 
 * @author houchunlei
 */
public interface VariationSet extends Iterable<Variation> {

	/**
	 * 变化量的条数
	 * 
	 * @return
	 */
	public int size();

	/**
	 * 获取指定序号的变化量
	 * 
	 * @param index
	 * @return
	 */
	public Variation get(int index);

	/**
	 * 最小版本
	 * 
	 * @return
	 */
	public VariationVersion lower();

	/**
	 * 最大版本
	 * 
	 * @return
	 */
	public VariationVersion upper();

	/**
	 * 子集
	 * 
	 * @param lower
	 * @param upper
	 * @return
	 */
	public VariationSet subset(Boundary<VariationVersion> lower,
			Boundary<VariationVersion> upper);
}