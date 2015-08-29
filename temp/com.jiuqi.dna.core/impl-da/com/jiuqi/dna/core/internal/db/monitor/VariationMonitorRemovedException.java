package com.jiuqi.dna.core.internal.db.monitor;

/**
 * 监视器已经被删除
 * 
 * @author houchunlei
 * 
 */
public final class VariationMonitorRemovedException extends RuntimeException {

	private static final long serialVersionUID = -4723003276615403372L;

	public VariationMonitorRemovedException(String message) {
		super(message);
	}
}