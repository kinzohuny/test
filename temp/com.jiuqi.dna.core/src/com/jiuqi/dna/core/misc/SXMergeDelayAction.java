package com.jiuqi.dna.core.misc;


/**
 * �ӳٺϲ������ӿ�
 * 
 * @author gaojingxin
 * 
 * @param <TAt>
 *            �ӳ�����λ�ö���
 */
public interface SXMergeDelayAction<TAt> {
	
	public abstract void doAction(TAt at, SXMergeHelper helper,
	        SXElement atElement);
}
