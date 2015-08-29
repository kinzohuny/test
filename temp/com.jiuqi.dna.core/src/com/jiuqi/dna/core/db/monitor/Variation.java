package com.jiuqi.dna.core.db.monitor;

import java.sql.Timestamp;

import com.jiuqi.dna.core.da.DataManipulation;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

/**
 * 变化量
 * 
 * @author houchunlei
 * 
 */
public interface Variation {

	/**
	 * 变化量的唯一标识
	 * 
	 * @return
	 */
	public GUID id();

	/**
	 * 变化量发生的时间
	 * 
	 * <p>
	 * 指示DML操作发生的时间，而非事务提交的时间。相同的变化量版本，其发生时间可能存在微小的差值。
	 * 
	 * @return
	 */
	public Timestamp instant();

	/**
	 * 发生的操作
	 * 
	 * @return
	 */
	public DataManipulation operation();

	/**
	 * 变化量的版本
	 * 
	 * <p>
	 * 在一个变化量集合中，可能包括多个事务版本的变化量，由监视器的配置决定。
	 * 
	 * @return
	 */
	public VariationVersion version();

	/**
	 * 变化量字段的列数
	 * 
	 * @return
	 */
	public int size();

	/**
	 * 获取旧值
	 * 
	 * @param field
	 *            MonitorField的顺序号，从0开始。
	 * @return
	 */
	public ReadableValue oldValue(int field);

	/**
	 * 获取旧值
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue oldValue(VariationMonitorField field);

	/**
	 * 获取旧值
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue oldValue(TableFieldDefine field);

	/**
	 * 获取新值
	 * 
	 * @param field
	 *            MonitorField的顺序号，从0开始。
	 * @return
	 */
	public ReadableValue newValue(int field);

	/**
	 * 获取新值
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue newValue(VariationMonitorField field);

	/**
	 * 获取新值
	 * 
	 * @param field
	 * @return
	 */
	public ReadableValue newValue(TableFieldDefine field);
}