package com.jiuqi.dna.core.def.table;

import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.TableFieldDefineImpl;

/**
 * ��֧�ֵĶ��߼����ֶε��޸Ĳ���
 * 
 * @author houchunlei
 * 
 */
public final class UnsupportedTableFieldModificationException extends
		UnsupportedTableModificationException {

	private static final long serialVersionUID = -435716537558819809L;

	/**
	 * ��֧���޸ĵ��ֶ�
	 */
	public final transient TableFieldDefine field;

	/**
	 * ��֧�ֵĲ�������
	 */
	public final int action;

	/**
	 * ��֧�ֵ�ԭ��
	 */
	public final int condition;

	/**
	 * Ϊϵͳ�ֶ�
	 */
	public static final int SYSTEM_FIELD = 0;
	/**
	 * Ϊ�߼����������ֶ�
	 */
	public static final int KEY_FIELD = 1;
	/**
	 * Ϊ�������ֶ�
	 */
	public static final int INDEX_FIELD = 2;
	/**
	 * Ϊ�����ֶ�
	 */
	public static final int PARTITION_FIELD = 3;

	public UnsupportedTableFieldModificationException(TableDefineImpl table,
			TableFieldDefineImpl field, int action, int condition) {
		super(table, message(field, action, condition));
		this.field = field;
		this.action = action;
		this.condition = condition;
	}

	public static final String message(TableFieldDefineImpl field, int action,
			int condition) {
		return "������" + action(action) + condition(condition) + "�ֶζ���"
				+ field.desc() + ".";
	}

	private static final String condition(int condition) {
		switch (condition) {
		case SYSTEM_FIELD:
			return "ϵͳ";
		case KEY_FIELD:
			return "����";
		case INDEX_FIELD:
			return "������";
		case PARTITION_FIELD:
			return "����";
		default:
			throw new IllegalArgumentException();
		}
	}
}