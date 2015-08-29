package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.exp.ConstExpression;
import com.jiuqi.dna.core.def.query.RelationColumnDefine;

/**
 * 逻辑字段定义
 * 
 * @author gaojingxin
 * 
 */
public interface TableFieldDefine extends FieldDefine, RelationColumnDefine {

	/**
	 * 获取所属的逻辑表定义
	 */
	public TableDefine getOwner();

	/**
	 * 获取字段实际存储的数据库表定义
	 * 
	 * @return
	 */
	public DBTableDefine getDBTable();

	/**
	 * 获取是否是主键字段（逻辑主键）
	 * 
	 * @return
	 */
	public boolean isPrimaryKey();

	/**
	 * 获取是否是记录行标识字段(RECID)
	 * 
	 * @return
	 */
	public boolean isRECID();

	/**
	 * 获取是否是行版本字段(RECVER)
	 * 
	 * @return
	 */
	public boolean isRECVER();

	public ConstExpression getDefault();

	/**
	 * 获取在数据库中，存储该字段的列名。
	 * 
	 * <p>
	 * 默认为大写化的字段名称。当与数据库或DNA-SQL的关键字冲突时，或者字段名称的长度超过数据库限制时，会被重命名，以“_1”类似的规则。
	 * 
	 * @return 返回实际名
	 */
	public String getNameInDB();

	/**
	 * 是否为模板字段
	 * 
	 * @return
	 */
	public boolean isTemplated();
}