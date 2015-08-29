package com.jiuqi.dna.core.cb;

import com.jiuqi.dna.core.def.MetaElementType;

/**
 * 元数据提供器
 * 
 * <p>
 * 回调接口
 * 
 * @author houchunlei
 * 
 */
public interface DefineProvider {

	/**
	 * 请求加载元数据定义到容器里
	 * 
	 * @param demander
	 * @param type
	 * @param name
	 */
	public void demand(DefineHolder demander, MetaElementType type, String name);
}
