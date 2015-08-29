package com.jiuqi.dna.core.impl;

import java.util.Calendar;
import java.util.Date;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.misc.SXElement;

final class DayRebootStrategy extends ClockedRebootStrategyBase {
	private static final String xml_attr_cycle = "cycle";
	private static final String xml_attr_reboot_time = "reboot-time";
	private static final String DEFAULT_REBOOT_TIME_VALUE = "00:00:00";
	private static final int DEFAULT_CYCLE_VALUE = 3;

	@Override
	protected Date getRebootDate(Context context, SXElement config) {
		int cycle = config.getInt(xml_attr_cycle, DEFAULT_CYCLE_VALUE);
		if (cycle < 0) {
			throw new IllegalArgumentException("系统重启策略[" + config + "]属性[" + xml_attr_cycle + "]值无效");
		}
		String rebootTime = config.getAttribute(xml_attr_reboot_time, DEFAULT_REBOOT_TIME_VALUE);
		if (!rebootTime.matches("[0-9]{2}:[0-9]{2}:[0-9]{2}")) {
			throw new IllegalArgumentException("系统重启策略[" + config + "]属性[" + xml_attr_reboot_time + "]值无效");
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, cycle);
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(rebootTime.substring(0, 2)));
		c.set(Calendar.MINUTE, Integer.parseInt(rebootTime.substring(3, 5)));
		c.set(Calendar.SECOND, Integer.parseInt(rebootTime.substring(6, 8)));
		return c.getTime();
	}
}
