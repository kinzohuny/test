package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * 模型关系定义
 * 
 * @author gaojingxin
 * 
 */
public interface ModelReferenceDeclare extends ModelReferenceDefine,
		NamedDeclare {
	/**
	 * 获得字段定义属于的模型定义
	 * 
	 * @return 返回模型定义
	 */
	public ModelDeclare getOwner();
}
