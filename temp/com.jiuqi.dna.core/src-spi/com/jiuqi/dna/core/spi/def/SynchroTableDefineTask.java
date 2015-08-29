/**
 * 
 */
package com.jiuqi.dna.core.spi.def;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * ����������ʶͬ�����ɶ�Ӧ���߼���
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
	 * @param dbTableName ������ʶ not null
	 * @param title ����
	 * @param category ����
	 */ 
	public SynchroTableDefineTask(String dbTableName, String title,String category) {
		this.dbTableName = dbTableName;
		this.title = title;
		this.category = category;
	}

	/**
	 * �Ƿ�ͬ���ɹ�
	 * @return
	 */
	public boolean isSynchroSuccessed() {
		return synchroSuccessed;
	}

	public void setSynchroSuccessed(boolean synchroSuccessed) {
		this.synchroSuccessed = synchroSuccessed;
	}
	
}
