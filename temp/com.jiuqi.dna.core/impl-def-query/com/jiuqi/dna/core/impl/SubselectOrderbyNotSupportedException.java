package com.jiuqi.dna.core.impl;

/**
 * ��֧�����Ӳ�ѯ�ṹ��ʹ��ORDER BY�Ӿ�
 * 
 * @author houchunlei
 * 
 */
public final class SubselectOrderbyNotSupportedException extends
		RuntimeException {

	private static final long serialVersionUID = -5546304362768363490L;

	public SubselectOrderbyNotSupportedException() {
		super(message());
	}

	public static final String message() {
		return "��֧�����Ӳ�ѯ�ṹ��ʹ��ORDER BY�Ӿ䡣";
	}

}
