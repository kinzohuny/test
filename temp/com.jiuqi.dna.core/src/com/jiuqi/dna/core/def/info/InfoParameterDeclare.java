package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.FieldDeclare;

/**
 * ��Ϣ��������ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface InfoParameterDeclare extends InfoParameterDefine, FieldDeclare {
	/**
	 * �����Ϣ����
	 * 
	 * @return ������Ϣ����
	 */
	public InfoDeclare getOwner();
}
