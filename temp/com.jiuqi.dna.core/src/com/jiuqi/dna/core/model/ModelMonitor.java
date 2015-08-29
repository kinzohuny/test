package com.jiuqi.dna.core.model;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.model.ModelDefine;

/**
 * 模型访问器，用于接受事件，组织主从模型等。
 * 
 * @author gaojingxin
 * 
 */
public interface ModelMonitor {
	/**
	 * 获得模型定义
	 */
	public ModelDefine getModelDefine();

	/**
	 * 获得从模型定义
	 */
	public Container<ModelMonitor> getSubMonitor();

}
