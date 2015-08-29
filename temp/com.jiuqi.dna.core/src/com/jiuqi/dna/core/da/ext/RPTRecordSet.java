package com.jiuqi.dna.core.da.ext;

import com.jiuqi.dna.core.da.DBAdapter;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.internal.da.report.RPTRecordSetImpl.FactoryImpl;
import com.jiuqi.dna.core.misc.MissingObjectException;

/**
 * 报表查询用数据集
 * 
 * @author houchunlei
 * 
 * @deprecated
 */
public interface RPTRecordSet {

	/**
	 * @deprecated
	 *
	 */
	public interface Factory {

		public RPTRecordSet newRPTRecordSet();
	}

	public final static Factory factory = new FactoryImpl();

	/**
	 * 清空查询定义及查询结果
	 */
	public void reset();

	/**
	 * 获得记录集默认的约束
	 * 
	 * <p>
	 * 默认约束的每个键约束一般通过RPTRecordSetKey.getDefaultKeyRestriction()获得更方便<br>
	 */
	public RPTRecordSetRestriction getFirstRestriction();

	/**
	 * 根据键约束分配独立约束，供新建字段时使用
	 * 
	 * <p>
	 * 如果字段指定了独立的约束，则该独立约束中值为空的键约束使用RPTRecordSet的默认约束
	 */
	public RPTRecordSetRestriction newRestriction();

	/**
	 * 返回字段个数
	 */
	public int getFieldCount();

	/**
	 * 新建记录字段，使用默认的约束
	 */
	public RPTRecordSetField newField(TableFieldDefine tableField);

	public RPTRecordSetField newField(TableFieldDefine tableField,
			boolean usingBigDecimal);

	/**
	 * 获得某位置的字段
	 */
	public RPTRecordSetField getField(int index);

	/**
	 * 获得OrderBy的个数
	 */
	public int getOrderByCount();

	/**
	 * 添加排序项
	 * 
	 * @param desc
	 *            是否降序
	 */
	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column,
			boolean isDesc);

	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column,
			boolean isDesc, boolean isNullAsMIN);

	/**
	 * 添加升序排序项
	 */
	public RPTRecordSetOrderBy newOrderBy(RPTRecordSetColumn column);

	/**
	 * 获得OrderBy
	 */
	public RPTRecordSetOrderBy getOrderBy(int index);

	/**
	 * 获取键个数
	 */
	public int getKeyCount();

	/**
	 * 获取键
	 */
	public RPTRecordSetKey getKey(int index);

	/**
	 * 根据键名称查找键，找不到则返回null
	 */
	public RPTRecordSetKey findKey(String keyName);

	/**
	 * 根据键名称查找键,找不到则抛出异常
	 */
	public RPTRecordSetKey getKey(String keyName) throws MissingObjectException;

	/**
	 * 装载数据集
	 * 
	 * @return 返回记录个数
	 */
	public int load(DBAdapter dbAdapter);

	/**
	 * 装载数据集
	 * 
	 * @param dbAdapter
	 *            数据库适配器
	 * @param offset
	 *            要求返回的纪录的偏移量
	 * @param rowCount
	 *            要求返回的纪录的个数
	 * @return 返回记录个数
	 */
	public int load(DBAdapter dbAdapter, int offset, int rowCount);

	/**
	 * 获取数据库中符合条件的记录个数
	 * 
	 * @param dbAdapter
	 *            适配器
	 * @return 返回数据库中符合条件的记录个数
	 */
	public int getRecordCountInDB(DBAdapter dbAdapter);

	/**
	 * 获得记录个数
	 */
	public int getRecordCount();

	/**
	 * 获得当前记录位置
	 */
	public int getCurrentRecordIndex();

	/**
	 * 设置当前记录位置
	 */
	public void setCurrentRecordIndex(int recordIndex);

	/**
	 * 新建条目，并设置为当前位置
	 * 
	 * @return 返回新记录的位置
	 */
	public int newRecord();

	/**
	 * 删除记录
	 */
	public void remove(int recordIndex);

	/**
	 * 删除当前记录
	 */
	public void removeCurrentRecord();

	/**
	 * 更新数据集
	 * 
	 * @return 返回更新个数
	 */
	public int update(DBAdapter dbAdapter);
}
