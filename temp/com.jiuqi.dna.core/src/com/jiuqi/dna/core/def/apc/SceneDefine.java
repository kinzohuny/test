package com.jiuqi.dna.core.def.apc;

import com.jiuqi.dna.core.def.NamedElementContainer;

/**
 * 场景定义
 * 
 * @author gaojingxin
 * 
 */
public interface SceneDefine {
	/**
	 * 获得该场景下的检查点
	 */
	public NamedElementContainer<? extends CheckPointDefine> getCheckPoints();
}
