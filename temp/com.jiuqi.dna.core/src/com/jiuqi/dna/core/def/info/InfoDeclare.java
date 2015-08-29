package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.FieldDefine;
import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Typable;

/**
 * ��Ϣ����ӿڣ�������ϵͳ�����쳣����־����Ϣ��֧�ֶ����ԡ�
 */
public interface InfoDeclare extends InfoDefine, NamedDeclare {
	/**
	 * ���ø�ʽ����Ϣ�ı�<br>
	 * ������"{������}"��ʽ�����ڸ�ʽ���ı���
	 */
	public void setMessage(String value);

	/**
	 * ��ȡ�Ƿ���Ҫ��¼��־
	 */
	public void setNeedLog(boolean value);

	/**
	 * ��ȡ�Ƿ���Ҫͨ���û�
	 */
	public boolean setReportToUser(boolean value);

	/**
	 * ��������
	 */
	public ModifiableNamedElementContainer<? extends InfoParameterDeclare> getParameters();

	/**
	 * ��������
	 */
	public InfoParameterDeclare newParameter(String name, DataType type);

	/**
	 * ��������
	 */

	public InfoParameterDeclare newParameter(String name, Typable typable);

	/**
	 * ��������
	 */
	public InfoParameterDeclare newParameter(FieldDefine sample);
}
