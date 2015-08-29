package com.jiuqi.dna.core.impl;

import java.util.Date;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.service.AsyncInfo;

public abstract class ClockedRebootStrategyBase extends RebootStrategy {
	private static final String xml_attr_delay_time_m = "delay-time-m";
	private static final String xml_attr_remind_interval_ms = "remind-interval-ms";
	private static final String xml_attr_session_floor = "session-floor";
	private static final int DEFAULT_DELAY_TIME_M_VALUE = 30;
	private static final long DEFAULT_REMIND_INTERVAL_MS_VALUE = 60000L;
	private static final int DEFAULT_SESSION_FLOOR_VALUE = 10;

	private long delayTime;
	private long remindInterval;
	private int sessionFloor;

	protected abstract Date getRebootDate(Context context, SXElement config);

	@Override
	protected final void init(Context context, SXElement config) {
		if (config == null) {
			throw new NullArgumentException("config");
		}
		this.delayTime = config.getLong(xml_attr_delay_time_m, DEFAULT_DELAY_TIME_M_VALUE) * 60000;
		this.remindInterval = config.getLong(xml_attr_remind_interval_ms, DEFAULT_REMIND_INTERVAL_MS_VALUE);
		this.sessionFloor = config.getInt(xml_attr_session_floor, DEFAULT_SESSION_FLOOR_VALUE);
		context.asyncHandle(new ClockedRestartAppTask(this), new AsyncInfo(this.getRebootDate(context, config)));
	}

	final void doReboot(ContextImpl<?, ?, ?> context) throws Throwable {
		this.lock(context);
		final Site site = context.occorAt.site;
		int sessionCount = site.getClusterSessionCount(context, true);
		if (sessionCount > this.sessionFloor) {
			System.out.println("当前会话数大于" + this.sessionFloor + ", 站点将在" + this.delayTime + "分钟后重新启动！");
			long now = System.currentTimeMillis();
			long limit = now + this.delayTime;
			while (now < limit && sessionCount > this.sessionFloor) {
				Thread.sleep(Math.min(limit - now, this.remindInterval));
				sessionCount = site.getClusterSessionCount(context, true);
				now = System.currentTimeMillis();
			}
		}
		this.shutdown(context, true);
	}
}
