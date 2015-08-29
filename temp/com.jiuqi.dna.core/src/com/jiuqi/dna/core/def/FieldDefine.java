package com.jiuqi.dna.core.def;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.type.DataTypable;

/**
 * 字段基接口定义
 * 
 * @author gaojingxin
 * 
 */
public interface FieldDefine extends NamedDefine, DataTypable {
	/**
	 * 是否是需要一直保持可用(非空)
	 * 
	 * @return 返回是否是必填字段
	 */
	public boolean isKeepValid();

	/**
	 * 是否是只读字段（只在构造时设置）
	 * 
	 * @return 返回是否是只读字段
	 */
	public boolean isReadonly();

	/**
	 * 获取字段的默认值
	 * 
	 * @return 返回字段定义的默认值
	 */
	public ValueExpression getDefault();
}
