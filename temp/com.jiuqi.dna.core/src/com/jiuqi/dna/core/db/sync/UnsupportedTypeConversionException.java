package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * ���߼�����������,�ֶ����͵�ת����֧��.
 * 
 * @author houchunlei
 * 
 */
public class UnsupportedTypeConversionException extends TableSyncException {

	private static final long serialVersionUID = 620953752493528558L;

	/**
	 * Ҫ����ֶζ���
	 */
	public final TableFieldDefine field;

	/**
	 * �ֶε�ǰ����������
	 */
	public final String before;

	public UnsupportedTypeConversionException(TableFieldDefine field,
			String type) {
		super(field.getOwner(), message(field, type));
		this.field = field;
		this.before = type;
	}

	private static final String message(TableFieldDefine field, String type) {
		return "�Է���ģʽ�����Խ��ֶ�" + intro(field) + "�����ݿ�����[" + type + "]����Ϊ["
				+ field.getType().toString() + "]��������ת����֧�֡�";
	}
}