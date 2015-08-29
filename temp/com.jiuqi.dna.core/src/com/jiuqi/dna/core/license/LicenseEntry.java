package com.jiuqi.dna.core.license;

/**
 * 授权许可条目，使用Context.findLicenseEntry(String name)获取授权许可条目
 * 
 * @author gaojingxin
 * 
 */
public interface LicenseEntry {
	/**
	 * 返回条目名称，是许可条目的唯一标识，大小写敏感。
	 * 
	 * @return
	 */
	public String getEntryName();

	/**
	 * 获得该条目的描述信息
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * 获取失效时间
	 * 
	 * @return
	 */
	public long getExpiredTime();

	/**
	 * 获取许可条目的额外属性，由具体功能和授权者自行约定
	 * 
	 * @param propertyName
	 *            属性名称
	 * @return 返回属性的值null表示没有该值
	 */
	public String getProperty(String propertyName);

	/**
	 * 获取许可条目的额外属性，由具体功能和授权者自行约定
	 * 
	 * @param propertyName
	 *            属性名称
	 * @param defaultValue
	 *            默认值
	 * @return 返回属性的值，或者defaultValue
	 */
	public String getProperty(String propertyName, String defaultValue);

	/**
	 * 按照当前时间检查是否过期，相当于=System.currentTimeMillis() > this.getExpiredTime();
	 * 
	 * @return 返回true表示过期
	 */
	public boolean hasExpired();
}
