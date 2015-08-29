/**
 * 
 */
package com.jiuqi.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ������صĹ���
 * @author huangkaibin
 *
 */
public class ProcessUtils {

	private static RuntimeMXBean runtimeMXBean = null;

	private static RuntimeMXBean getRuntimeMXBean() {
		if (runtimeMXBean == null) {
			runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		}
		return runtimeMXBean;
	}

	/**
	 * ��ȡ��ǰ���̵�����
	 * @return ��ǰ���̵�����
	 * @see java.lang.management.RuntimeMXBean#getName()
	 */
	public static String getCurrentProcessName() {
		RuntimeMXBean rtb = getRuntimeMXBean();
		return rtb.getName();
	}

	/**
	 * <p>��ȡ��ǰ���̵�PID��</p>
	 * <p>��֪��windows��linux��mac os����Ч��</p>
	 * @return ��ǰ���̵�PID�������ȡʧ�ܣ��ͷ���-1��
	 */
	public static int getCurrentProcessID() {
		String processName = getCurrentProcessName();
		int pid = -1;
		Pattern pattern = Pattern.compile("^([0-9]+)@.+$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(processName);
		if (matcher.matches()) {
			pid = Integer.parseInt(matcher.group(1));
		}
		return pid;
	}

}
