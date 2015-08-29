package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.MetaElement;
import com.jiuqi.dna.core.def.NamedElementContainer;

/**
 * 信息组定义
 * 
 * @author gaojingxin
 * 
 */
public interface InfoGroupDefine extends MetaElement {
	/**
	 * 得到参数容器
	 * 
	 * @return 返回参数容器
	 */
	public NamedElementContainer<? extends InfoDefine> getInfos();
}
