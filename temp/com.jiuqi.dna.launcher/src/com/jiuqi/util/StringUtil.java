/**
 * 
 */
package com.jiuqi.util;

/**
 * �ַ���������
 * @author �ƿ���
 */
public class StringUtil {

	/**
	 * ��ȡ��ȫ�ַ���
	 * @param str �ַ���
	 * @return ���str��Ϊ<code>null</code>���򷵻�str�������򷵻ؿմ�
	 */
	public static String getSafeString(String str) {
		return str != null ? str : "";
	}

	/**
	 * �ж������ַ����Ƿ���ȡ���������ַ�����Ϊ<code>null</code>������Ϊ��ȡ�
	 * @param str1 �ַ���1
	 * @param str2 �ַ���2
	 * @return ��������ַ�����Ϊ<code>null</code>�����߶���Ϊ<code>null</code>������һ�����򷵻�<tt>true</tt>�����򷵻�<tt>false</tt>
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
	 * ��ʽ��·������Ҫָ���Ƿ�ʹ��windows����·����
	 * �����windows����·������·���ָ���ͳһʹ��"\"������ʹ��"/"��
	 * ���ص��ַ���������������"\"��"/"��
	 * @param path ·���ַ���
	 * @param useWindowsStyle �Ƿ�ʹ��windows���
	 * @return ��ʽ�����·���ַ���
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
