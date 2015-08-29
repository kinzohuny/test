package com.jiuqi.dna.core.def.exp;

/**
 * �����Null���ʽʹ���쳣��
 * 
 * <p>
 * Null���ʽֻ������insert��update�ĸ�ֵ������ֱ�����������������������������Ϊν�ʵ������塣
 * 
 * @see com.jiuqi.dna.core.system.ContextVariable.StrictNullUsage
 * 
 * @author houchunlei
 * 
 */
public final class IllegalNullUsageException extends RuntimeException {

	private static final long serialVersionUID = 4656439410059208777L;

	public IllegalNullUsageException(Predicate predicate) {
		super(message(predicate));
	}

	public static final String message(Predicate predicate) {
		return "��ν������[" + predicate.toString()
				+ "]�У����ڷǷ���Null���ʽ��ʹ�ã�Null���ʽ������Ϊ��������������Լ�ν�ʵ������塣";
	}

	public IllegalNullUsageException(Operator operator) {
		super(message(operator));
	}

	public static final String message(Operator operator) {
		return "������[" + operator.toString()
				+ "]�У����ڷǷ���Null���ʽ��ʹ�ã�Null���ʽ������Ϊ��������������Լ�ν�ʵ������塣";
	}
}