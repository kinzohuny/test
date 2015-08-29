package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.Context;

/**
 * 脚本引擎接口
 * 
 * @author gaojingxin
 * 
 */
public interface ModelScriptEngine<TPreparedData> {
	/**
	 * 让脚本引擎判断是否支持
	 * 
	 * @param language
	 *            脚本的语言名称，传入值一律小写。
	 * @return 返回大于零的数表示支持，返回数值越大，表示支持度越高，可以利用这种特性作版本区分
	 */
	public int suport(String language);

	/**
	 * 获得与当前上下文（当前线程）相关的脚本上下文
	 */
	public ModelScriptContext<TPreparedData> allocContext(Context context);
}
