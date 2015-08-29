package com.jiuqi.dna.core.misc;


/**
 * 延迟合并动作接口
 * 
 * @author gaojingxin
 * 
 * @param <TAt>
 *            延迟所在位置对象
 */
public interface SXMergeDelayAction<TAt> {
	
	public abstract void doAction(TAt at, SXMergeHelper helper,
	        SXElement atElement);
}
