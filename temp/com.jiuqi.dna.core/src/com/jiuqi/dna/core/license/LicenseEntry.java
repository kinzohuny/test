package com.jiuqi.dna.core.license;

/**
 * ��Ȩ�����Ŀ��ʹ��Context.findLicenseEntry(String name)��ȡ��Ȩ�����Ŀ
 * 
 * @author gaojingxin
 * 
 */
public interface LicenseEntry {
	/**
	 * ������Ŀ���ƣ��������Ŀ��Ψһ��ʶ����Сд���С�
	 * 
	 * @return
	 */
	public String getEntryName();

	/**
	 * ��ø���Ŀ��������Ϣ
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * ��ȡʧЧʱ��
	 * 
	 * @return
	 */
	public long getExpiredTime();

	/**
	 * ��ȡ�����Ŀ�Ķ������ԣ��ɾ��幦�ܺ���Ȩ������Լ��
	 * 
	 * @param propertyName
	 *            ��������
	 * @return �������Ե�ֵnull��ʾû�и�ֵ
	 */
	public String getProperty(String propertyName);

	/**
	 * ��ȡ�����Ŀ�Ķ������ԣ��ɾ��幦�ܺ���Ȩ������Լ��
	 * 
	 * @param propertyName
	 *            ��������
	 * @param defaultValue
	 *            Ĭ��ֵ
	 * @return �������Ե�ֵ������defaultValue
	 */
	public String getProperty(String propertyName, String defaultValue);

	/**
	 * ���յ�ǰʱ�����Ƿ���ڣ��൱��=System.currentTimeMillis() > this.getExpiredTime();
	 * 
	 * @return ����true��ʾ����
	 */
	public boolean hasExpired();
}
