package com.jiuqi.dna.core.impl;

import java.util.Calendar;
import java.util.Date;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.misc.SXElement;

final class WeekRebootStrategy extends ClockedRebootStrategyBase {
	private static final String xml_attr_weekday = "weekday";
	private static final String xml_attr_reboot_time = "reboot-time";
	private static final int DEFAULT_WEEKDAY_VALUE = 0;
	private static final String DEFAULT_REBOOT_TIME_VALUE = "00:00:00";

	@Override
	protected Date getRebootDate(Context context, SXElement config) {
		int weekday = config.getInt(xml_attr_weekday, DEFAULT_WEEKDAY_VALUE);
		if (weekday < 0 || weekday > 6) {
			throw new IllegalArgumentException("系统重启策略[" + config + "]属性[" + xml_attr_weekday + "]值无效");
		}
		String rebootTime = config.getAttribute(xml_attr_reboot_time, DEFAULT_REBOOT_TIME_VALUE);
		if (!rebootTime.matches("[0-9]{2}:[0-9]{2}:[0-9]{2}")) {
			throw new IllegalArgumentException("系统重启策略[" + config + "]属性[" + xml_attr_reboot_time + "]值无效");
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_WEEK) - 1 - weekday);
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(rebootTime.substring(0, 2)));
		c.set(Calendar.MINUTE, Integer.parseInt(rebootTime.substring(3, 5)));
		c.set(Calendar.SECOND, Integer.parseInt(rebootTime.substring(6, 8)));
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			c.add(Calendar.DAY_OF_YEAR, 7);
		}
		return c.getTime();
	}
}
