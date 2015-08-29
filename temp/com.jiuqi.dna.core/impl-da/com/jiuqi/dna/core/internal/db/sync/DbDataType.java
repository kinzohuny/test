package com.jiuqi.dna.core.internal.db.sync;

import com.jiuqi.dna.core.type.DataType;

public interface DbDataType<TTable extends DbTable<TTable, TColumn, TDataType, TIndex>, TColumn extends DbColumn<TTable, TColumn, TDataType, TIndex>, TDataType extends DbDataType<TTable, TColumn, TDataType, TIndex>, TIndex extends DbIndex<TTable, TColumn, TDataType, TIndex>> {

	/**
	 * 当前列数据类型到目标数据类型的可转换性
	 * 
	 * @param column
	 *            当前列
	 * @param target
	 *            目标数据类型
	 * @return
	 */
	TypeAlterability typeAlterable(TColumn column, DataType target);

	/**
	 * 列数据类型的声明
	 * 
	 * @param column
	 * @return
	 */
	String toString(TColumn column);

	/**
	 * 列数据类型的声明
	 * 
	 * @param column
	 * @param s
	 */
	void define(TColumn column, Appendable s);

	// DataTypeInternal dnaTypeOf(int length, int precision, int scale);
	
	/**
	 * 转换成逻辑表数据列类型
	 * @return
	 */
	DataType convertToDataType(TColumn column);
}