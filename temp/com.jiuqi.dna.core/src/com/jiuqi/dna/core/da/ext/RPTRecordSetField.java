package com.jiuqi.dna.core.da.ext;

import com.jiuqi.dna.core.def.table.TableFieldDefine;

/**
 * �ֶ�
 * 
 * @deprecated
 * 
 */
public interface RPTRecordSetField extends RPTRecordSetColumn {
	/**
	 * ��Ӧ�ֶ�
	 */
	public TableFieldDefine getTableField();

	/**
	 * ���ظ��ֶε�Լ��
	 */
	public RPTRecordSetRestriction getRestriction();
}
