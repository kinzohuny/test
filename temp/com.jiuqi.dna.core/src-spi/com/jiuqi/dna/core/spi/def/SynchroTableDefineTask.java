/**
 * 
 */
package com.jiuqi.dna.core.spi.def;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * 根据物理表标识同步生成对应的逻辑表
 * @author hongqingbin
 *
 */
public final class SynchroTableDefineTask extends SimpleTask {
	
	public final String dbTableName;
	
	public final String title;
	
	public final String category;
	
	private boolean synchroSuccessed;

	/**
	 * 
	 * @param dbTableName 物理表标识 not null
	 * @param title 标题
	 * @param category 分组
	 */ 
	public SynchroTableDefineTask(String dbTableName, String title,String category) {
		this.dbTableName = dbTableName;
		this.title = title;
		this.category = category;
	}

	/**
	 * 是否同步成功
	 * @return
	 */
	public boolean isSynchroSuccessed() {
		return synchroSuccessed;
	}

	public void setSynchroSuccessed(boolean synchroSuccessed) {
		this.synchroSuccessed = synchroSuccessed;
	}
	
}
