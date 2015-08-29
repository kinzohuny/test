package com.jiuqi.dna.core.internal.db.monitor;

import java.sql.Timestamp;
import java.util.ArrayList;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 用于周期性的监听的任务
 * 
 * <p>
 * 必须使用带period的AsyncInfo来调用该任务。该任务的处理器主要负责处理多次轮询的逻辑和统计信息的记录。
 * 
 * @author houchunlei
 * 
 */
final class VariationMonitorListenPeriodicallyTask extends SimpleTask {

	final Timestamp born = new Timestamp(System.currentTimeMillis());

	final String monitor;

	final int tolerance;

	final ArrayList<Throwable> exceptions = new ArrayList<Throwable>();

	VariationMonitorListenPeriodicallyTask(String monitor, int tolerance) {
		this.monitor = monitor;
		this.tolerance = tolerance;
	}
}