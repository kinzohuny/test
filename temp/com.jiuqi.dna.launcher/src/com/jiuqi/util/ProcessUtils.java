/**
 * 
 */
package com.jiuqi.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 进程相关的工具
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
	 * 获取当前进程的名称
	 * @return 当前进程的名称
	 * @see java.lang.management.RuntimeMXBean#getName()
	 */
	public static String getCurrentProcessName() {
		RuntimeMXBean rtb = getRuntimeMXBean();
		return rtb.getName();
	}

	/**
	 * <p>获取当前进程的PID。</p>
	 * <p>已知在windows、linux、mac os下有效。</p>
	 * @return 当前进程的PID。如果获取失败，就返回-1。
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
