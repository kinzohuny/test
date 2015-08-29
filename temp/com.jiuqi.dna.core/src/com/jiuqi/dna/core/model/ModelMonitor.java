package com.jiuqi.dna.core.model;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.model.ModelDefine;

/**
 * ģ�ͷ����������ڽ����¼�����֯����ģ�͵ȡ�
 * 
 * @author gaojingxin
 * 
 */
public interface ModelMonitor {
	/**
	 * ���ģ�Ͷ���
	 */
	public ModelDefine getModelDefine();

	/**
	 * ��ô�ģ�Ͷ���
	 */
	public Container<ModelMonitor> getSubMonitor();

}
