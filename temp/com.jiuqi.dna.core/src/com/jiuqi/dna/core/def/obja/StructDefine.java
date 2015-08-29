package com.jiuqi.dna.core.def.obja;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.ObjectDataType;
import com.jiuqi.dna.core.type.TupleType;

/**
 * 结构定义
 * 
 * @author gaojingxin
 * 
 */
public interface StructDefine extends NamedDefine, ObjectDataType, TupleType {
	/**
	 * 获得字段定义列表
	 * 
	 * @return 返回字段定义列表
	 */
	public NamedElementContainer<? extends StructFieldDefine> getFields();

	/**
	 * 尝试转换
	 * 
	 * @param obj
	 *            需要被转换的对象，不可为空
	 * @return 返回null表示转换失败
	 * @exception NullArgumentException
	 *                obj为null
	 */
	public Object tryConvert(Object convertFrom) throws NullArgumentException;

	/**
	 * 检查是否是结构定义对应的对象实例
	 */
	public boolean isInstance(Object obj);
}
