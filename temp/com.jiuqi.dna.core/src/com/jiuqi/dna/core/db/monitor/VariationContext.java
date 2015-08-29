package com.jiuqi.dna.core.db.monitor;

/**
 * ���ڿ����ѷ����ı仯��
 * 
 * <p>
 * �����Ķ���ֻ�������󼶱𱣴档
 * 
 * @author Hou
 * 
 */
public interface VariationContext {

	/**
	 * ��ȡ���еı仯������
	 * 
	 * @return
	 */
	public VariationSet get();

	/**
	 * ��ȡ��ָ���汾֮������б仯������
	 * 
	 * @param lower
	 * @return
	 */
	public VariationSet getAfter(long lower);

	// public VariationSet get(Boundary<VariationVersion> lower);
	// public VariationSet get(Boundary<VariationVersion> lower,
	// Boundary<VariationVersion> upper);

	/**
	 * ��ȡ��ǰ���ڵ����ı仯���汾
	 * 
	 * @return
	 */
	public VariationVersion max();

	/**
	 * �Ƴ�ָ����֮ǰ���а汾�ı仯��
	 * 
	 * @param upper
	 * @return
	 */
	public int removeOutdated(long upper);

	public int removeSpecified(Iterable<Variation> it);
}