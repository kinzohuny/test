package com.jiuqi.dna.core.db.monitor;

/**
 * ���ӵ��ֶ�
 * 
 * @author houchunlei
 * 
 */
public interface VariationMonitorField {

	// /**
	// * ���ڶ�����ֵ���ֶΣ��Ƿ�ʹ��BigDecimal��װ�����ݡ�
	// *
	// * @return
	// */
	// public boolean usingBigDecimal();

	/**
	 * ���ӵ�Ŀ���ֶ�
	 * 
	 * @return
	 */
	public String getWatchFN();

	/**
	 * �仯ʱ�ľ�ֵ�洢�ֶ�
	 * 
	 * @return
	 */
	public String getOldValueFN();

	/**
	 * �仯ʱ����ֵ�洢�ֶ�
	 * 
	 * @return
	 */
	public String getNewValueFN();
}