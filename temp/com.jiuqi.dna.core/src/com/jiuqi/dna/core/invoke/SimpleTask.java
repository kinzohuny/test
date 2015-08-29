package com.jiuqi.dna.core.invoke;

import com.jiuqi.dna.core.None;

/**
 * 简单任务，即只有一种处理方法的任务
 * 
 * @author gaojingxin
 * 
 */
public abstract class SimpleTask extends Task<None> {
	public SimpleTask() {
		this.method = None.NONE;
	}
}
