package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.obja.StructFieldDefine;

/**
 * 查询语句定义的输出列定义
 * 
 * @see com.jiuqi.dna.core.def.query.QueryColumnDefine
 * 
 * @author gaojingxin
 * 
 */
public interface QueryColumnDeclare extends QueryColumnDefine,
		SelectColumnDeclare {

	public QueryStatementDeclare getOwner();

	/**
	 * 设置列定义的表达式
	 */
	public void setExpression(ValueExpression value);

	/**
	 * 设置映射到的模型的字段
	 * 
	 * @param field
	 *            java实体属性的结构字段定义
	 */
	public void setMapingField(StructFieldDefine field);

	/**
	 * 设置映射到的模型的字段
	 * 
	 * @param structFieldName
	 *            java实体属性的名称(区分大小写)
	 */
	public void setMapingField(String structFieldName);

	/**
	 * 设置是否使用高精度的BigDecimal类型读取结果
	 * 
	 * @param usingBigDecimal
	 */
	public void setUsingBigDecimal(boolean usingBigDecimal);
}