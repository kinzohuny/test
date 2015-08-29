package com.jiuqi.dna.core.def.model;

/**
 * 脚本定义
 * 
 * @author gaojingxin
 * 
 */
public interface ScriptDefine {
	/**
	 * 获取脚本的语言
	 * 
	 * @return 返回语言
	 */
	public String getLanguage();

	/**
	 * 获取脚本
	 * 
	 * @return 返回脚本
	 */
	public String getScript();
}
