package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.misc.Boundary;

/**
 * �仯����
 * 
 * @author houchunlei
 */
public interface VariationSet extends Iterable<Variation> {

	/**
	 * �仯��������
	 * 
	 * @return
	 */
	public int size();

	/**
	 * ��ȡָ����ŵı仯��
	 * 
	 * @param index
	 * @return
	 */
	public Variation get(int index);

	/**
	 * ��С�汾
	 * 
	 * @return
	 */
	public VariationVersion lower();

	/**
	 * ���汾
	 * 
	 * @return
	 */
	public VariationVersion upper();

	/**
	 * �Ӽ�
	 * 
	 * @param lower
	 * @param upper
	 * @return
	 */
	public VariationSet subset(Boundary<VariationVersion> lower,
			Boundary<VariationVersion> upper);
}