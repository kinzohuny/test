package com.jiuqi.dna.core.internal.db.sync;

import com.jiuqi.dna.core.type.DataType;

public interface DbDataType<TTable extends DbTable<TTable, TColumn, TDataType, TIndex>, TColumn extends DbColumn<TTable, TColumn, TDataType, TIndex>, TDataType extends DbDataType<TTable, TColumn, TDataType, TIndex>, TIndex extends DbIndex<TTable, TColumn, TDataType, TIndex>> {

	/**
	 * ��ǰ���������͵�Ŀ���������͵Ŀ�ת����
	 * 
	 * @param column
	 *            ��ǰ��
	 * @param target
	 *            Ŀ����������
	 * @return
	 */
	TypeAlterability typeAlterable(TColumn column, DataType target);

	/**
	 * ���������͵�����
	 * 
	 * @param column
	 * @return
	 */
	String toString(TColumn column);

	/**
	 * ���������͵�����
	 * 
	 * @param column
	 * @param s
	 */
	void define(TColumn column, Appendable s);

	// DataTypeInternal dnaTypeOf(int length, int precision, int scale);
	
	/**
	 * ת�����߼�������������
	 * @return
	 */
	DataType convertToDataType(TColumn column);
}