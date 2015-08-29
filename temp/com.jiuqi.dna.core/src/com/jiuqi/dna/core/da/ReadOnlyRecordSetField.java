package com.jiuqi.dna.core.da;

import java.text.Format;

import com.jiuqi.dna.core.def.query.QueryColumnDefine;
import com.jiuqi.dna.core.type.ReadableValue;

/**
 * 只读结果集字段
 * 
 * @author houchunlei
 * 
 */
public interface ReadOnlyRecordSetField extends ReadableValue {

	/**
	 * 获得对应的查询列定义
	 */
	public QueryColumnDefine getDefine();

	/**
	 * 获取字段名，用Sql装载结果集时有可能重名。
	 */
	public String getName();

	/**
	 * 获取格式化对象
	 */
	public Format getFormat();

	/**
	 * 设置格式化对象
	 */
	public void setFormat(Format format);

	/**
	 * 格式化输出
	 */
	public String formatText();

	/**
	 * 解析格式化文本
	 */
	public void parseText(String text);
}
