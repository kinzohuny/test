package com.jiuqi.dna.core.da;

import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.MissingDefineException;
import com.jiuqi.dna.core.def.query.QueryColumnDefine;

/**
 * ��¼���ֶ�����
 * 
 * @author gaojingxin
 * 
 */
public interface RecordSetFieldContainer<TField extends RecordSetField> extends
	Container<TField> {
	
	/**
	 * ���ݲ�ѯ�ж�����Ҽ�¼���ֶ�
	 * 
	 * @param column ��ѯ�ж���
	 * @return �����ж������null
	 * @throws IllegalArgumentException ���ж�����Чʱ�׳��쳣
	 */
	public TField find(QueryColumnDefine column)
			throws IllegalArgumentException;

	/**
	 * ���ݲ�ѯ�ж�����Ҽ�¼���ֶ�
	 * 
	 * @param column ��ѯ�ж���
	 * @return �����ж���
	 * @throws MissingDefineException ���Ҳ����ֶ�ʱ�׳��쳣
	 * @throws IllegalArgumentException �������в���Чʱ�׳��쳣
	 */
	public TField get(QueryColumnDefine column)
			throws MissingDefineException, IllegalArgumentException;
}
