package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.info.InfoDefine;

/**
 * 模型约束定义
 * 
 * @author gaojingxin
 * 
 */
public interface ModelConstraintDefine extends InfoDefine {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDefine getOwner();

	/**
	 * 检查器的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDefine getScript();
}
