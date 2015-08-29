/**
 * 
 */
package com.jiuqi.util;

/**
 * 字符串工具类
 * @author 黄凯斌
 */
public class StringUtil {

	/**
	 * 获取安全字符串
	 * @param str 字符串
	 * @return 如果str不为<code>null</code>，则返回str本身，否则返回空串
	 */
	public static String getSafeString(String str) {
		return str != null ? str : "";
	}

	/**
	 * 判断两个字符串是否相等。如果两个字符串都为<code>null</code>，则认为相等。
	 * @param str1 字符串1
	 * @param str2 字符串2
	 * @return 如果两个字符串都为<code>null</code>，或者都不为<code>null</code>但内容一样，则返回<tt>true</tt>，否则返回<tt>false</tt>
	 */
	public static boolean isEqual(String str1, String str2) {
		if (str1 != null) {
			return str1.equals(str2);
		} else if (str2 != null) {
			return str2.equals(str1);
		}
		return true;
	}

	/**
	 * 格式化路径，需要指定是否使用windows风格的路径。
	 * 如果是windows风格的路径，则路径分隔符统一使用"\"，否则使用"/"。
	 * 返回的字符串不存在连续的"\"或"/"。
	 * @param path 路径字符串
	 * @param useWindowsStyle 是否使用windows风格
	 * @return 格式化后的路径字符串
	 */
	public static String formatPath(String path, boolean useWindowsStyle) {
		if (path == null)
			throw new NullPointerException("Path cannot be null!");

		String sourceRegex, replaceRegex;
		if (useWindowsStyle) {
			sourceRegex = "/";
			replaceRegex = "\\\\";
		} else {
			sourceRegex = "\\\\";
			replaceRegex = "/";
		}

		String result = path.replaceAll(sourceRegex, replaceRegex);
		return result.replaceAll(replaceRegex + "+", replaceRegex);
	}

}
