package com.jiuqi.dna.core.db.monitor;

public final class VariationMonitorHelper {

	public static final String defaultMonitorName(String target) {
		return target.concat(DEFAULT_MONITOR_SUFFIX);
	}

	/**
	 * Ĭ�ϵļ������ı仯���������
	 * 
	 * @param target
	 *            ����Ŀ�������
	 * @return
	 */
	public static final String defaultVariationName(String target) {
		return target.concat(DEFAULT_VARIATION_SUFFIX);
	}

	/**
	 * �������仯�����У�Ĭ�ϵĴ洢��ֵ���ֶ�����
	 * 
	 * @param field
	 * @return
	 */
	public static final String defaultOldValueFN(String field) {
		return field.concat(DEFAULT_OLD_VALUE_SUFFIX);
	}

	/**
	 * �������仯�����У�Ĭ�ϵĴ洢��ֵ���ֶ�����
	 * 
	 * @param field
	 * @return
	 */
	public static final String defaultNewValueFN(String field) {
		return field.concat(DEFAULT_NEW_VALUE_SUFFIX);
	}

	public static final String DEFAULT_MONITOR_SUFFIX = "_MON";
	public static final String DEFAULT_VARIATION_SUFFIX = "_VAR";
	public static final String DEFAULT_OLD_VALUE_SUFFIX = "_OLD";
	public static final String DEFAULT_NEW_VALUE_SUFFIX = "_NEW";

}