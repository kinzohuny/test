package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.type.GUID;

/**
 * 表数据变化监视器
 * 
 * @author houchunlei
 * 
 */
public interface VariationMonitor {

	/**
	 * 监视器标识
	 * 
	 * @return
	 */
	public GUID getId();

	/**
	 * 监视器名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 监视目标表
	 * 
	 * @return
	 */
	public String getTargetName();

	/**
	 * 变化量表
	 * 
	 * @return
	 */
	public String getVariationName();

	/**
	 * 监控的字段列表
	 * 
	 * <p>
	 * 可以按监控字段的名称来检索
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends VariationMonitorField> getWatches();
}