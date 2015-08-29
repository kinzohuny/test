package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * �������ӷǿ��ֶ�,���޸��ֶ�Ϊ�ǿյ����ݿ�ͬ��������֧��.
 * 
 * @author houchunlei
 * 
 */
public class UnsupportedNotNullFieldException extends TableSyncException {

	/**
	 * �쳣�������ֶζ���
	 */
	public final TableFieldDefine field;

	/**
	 * ָʾ�ֶ����������޸�.true��ʾΪ�����ֶ�.
	 */
	public final boolean addOrModify;

	private static final long serialVersionUID = -9054776625593298664L;

	public UnsupportedNotNullFieldException(TableFieldDefine field,
			boolean addOrModify) {
		super(field.getOwner(), message(field, addOrModify));
		this.field = field;
		this.addOrModify = addOrModify;
	}

	public static final String message(TableFieldDefine field,
			boolean addOrModify) {
		return addOrModify ? "�������ӷǿ��ֶζ���" + intro(field) + ",��Ϊ�����ֶ�δ����Ĭ��ֵ."
				: "�����޸��ֶ�" + intro(field) + "Ϊ�ǿ�,ָ���ֶΰ�����ֵ.";
	}
}