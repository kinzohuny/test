package com.jiuqi.dna.core.db.monitor;

/**
 * 监视器目标表忙而无法锁定
 * 
 * <p>
 * 一般是由于存在事务导致
 * 
 * @author Hou
 * 
 */
public final class VariationMonitorTargetBusyException extends RuntimeException {

	private static final long serialVersionUID = 1666828637211406911L;
}