package com.jiuqi.dna.core.da;

import com.jiuqi.dna.core.Context;

/**
 * ��¼����ĵ�������
 * 
 * @author houchunlei
 * 
 */
public interface RecordIterateAction {

	/**
	 * ��¼����ĵ�������
	 * 
	 * @param context
	 *            ��ǰcontext
	 * @param record
	 *            ��ǰ�������ļ�¼����
	 * @param recordIndex
	 *            ��ǰ��������¼��������,��0��ʼ
	 * @return �����Ƿ���ֹ��������.Ϊtrue�򲻻������ȡ��һ�в�ѯ���.
	 * @throws Throwable
	 */
	public boolean iterate(Context context, IteratedRecord record,
			long recordIndex) throws Throwable;
}
