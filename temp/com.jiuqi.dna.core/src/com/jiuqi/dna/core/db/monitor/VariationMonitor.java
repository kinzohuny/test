package com.jiuqi.dna.core.db.monitor;

import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.type.GUID;

/**
 * �����ݱ仯������
 * 
 * @author houchunlei
 * 
 */
public interface VariationMonitor {

	/**
	 * ��������ʶ
	 * 
	 * @return
	 */
	public GUID getId();

	/**
	 * ����������
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * ����Ŀ���
	 * 
	 * @return
	 */
	public String getTargetName();

	/**
	 * �仯����
	 * 
	 * @return
	 */
	public String getVariationName();

	/**
	 * ��ص��ֶ��б�
	 * 
	 * <p>
	 * ���԰�����ֶε�����������
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends VariationMonitorField> getWatches();
}