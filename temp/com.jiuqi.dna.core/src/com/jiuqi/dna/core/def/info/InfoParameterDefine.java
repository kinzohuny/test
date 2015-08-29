package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.FieldDefine;

/**
 * 信息参数定义接口
 * 
 * @author gaojingxin
 * 
 */
public interface InfoParameterDefine extends FieldDefine {
	/**
	 * 获得信息定义
	 * 
	 * @return 返回信息定义
	 */
	public InfoDefine getOwner();
}
