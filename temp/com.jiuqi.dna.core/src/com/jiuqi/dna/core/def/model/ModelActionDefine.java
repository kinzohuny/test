package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.Context;

/**
 * 模型动作定义
 * 
 * @author gaojingxin
 * 
 */
public interface ModelActionDefine extends ModelInvokeDefine {
	/**
	 * 动作的脚本
	 * 
	 * @return 返回脚本定义对象
	 */
	public ScriptDefine getScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * 执行动作
	 * 
	 * @param context
	 *            上下文
	 * @param mo
	 *            模型对象
	 */
	public void execute(Context context, Object mo);

	/**
	 * 执行动作
	 * 
	 * @param context
	 *            上下文
	 * @param mo
	 *            模型对象
	 * @param ao
	 *            参数对象
	 */
	public void execute(Context context, Object mo, Object ao);
}
