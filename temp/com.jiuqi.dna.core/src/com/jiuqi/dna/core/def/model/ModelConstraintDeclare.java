package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.info.InfoDeclare;
import com.jiuqi.dna.core.model.ModelService;

/**
 * 模型约束定义
 * 
 * @author gaojingxin
 * 
 */
public interface ModelConstraintDeclare extends ModelConstraintDefine,
        InfoDeclare {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDeclare getOwner();

	/**
	 * 检查器的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDeclare getScript();

	/**
	 * 设置模型约束检查器，<br>
	 * 该方法提供给运行时模型设计器使用，模型声名器中不能使用
	 * 
	 * @return 返回旧的约束检查器
	 */
	public ModelService<?>.ModelConstraintChecker setChecker(
	        ModelService<?>.ModelConstraintChecker checker);

}
