package com.jiuqi.dna.core.exception;

/**
 * ���ִ�С�洦�쳣
 * 
 * @author gaojingxin
 * 
 */
public class NumericOverflowException extends CoreException {
	private static final long serialVersionUID = 1L;
	public final byte precision;
	public final byte scale;
	public final double overflow;
	public final String fieldName;

	public static String formatMessage(byte precision, byte scale,
			double overflow, String fieldName) {
		return "�ֶ�[" + fieldName + "]ֵ(" + overflow + ")ת����numeric("
				+ precision + "," + scale + ")ʱ���";
	}

	public NumericOverflowException(byte precision, byte scale,
			double overflow, String fieldName) {
		super(formatMessage(precision, scale, overflow, fieldName));
		this.precision = precision;
		this.scale = scale;
		this.overflow = overflow;
		this.fieldName = fieldName;
	}

}
