package com.jiuqi.dna.core;

import java.util.Locale;

import com.jiuqi.dna.core.def.info.InfoDefine;

/**
 * 本地化器
 * 
 * @author gaojingxin
 * 
 */
public interface Localizer {
	/**
	 * 获得当前的地域
	 */
	public Locale getLocale();

	/**
	 * 本地化字符串
	 */
	public String localize(InfoDefine info);

	/**
	 * 本地化字符串
	 */
	public String localize(InfoDefine info, Object param1);

	/**
	 * 本地化字符串
	 */
	public String localize(InfoDefine info, Object param1, Object param2);

	/**
	 * 本地化字符串
	 */
	public String localize(InfoDefine info, Object param1, Object param2,
	        Object param3);

	/**
	 * 本地化字符串
	 */
	public String localize(InfoDefine info, Object param1, Object param2,
	        Object param3, Object... others);

	/**
	 * 本地化字符串
	 */
	public void localize(Appendable to, InfoDefine info);

	/**
	 * 本地化字符串
	 */
	public void localize(Appendable to, InfoDefine info, Object param1);

	/**
	 * 本地化字符串
	 */
	public void localize(Appendable to, InfoDefine info, Object param1,
	        Object param2);

	/**
	 * 本地化字符串
	 */
	public void localize(Appendable to, InfoDefine info, Object param1,
	        Object param2, Object param3);

	/**
	 * 本地化字符串
	 */
	public void localize(Appendable to, InfoDefine info, Object param1,
	        Object param2, Object param3, Object... others);
}
