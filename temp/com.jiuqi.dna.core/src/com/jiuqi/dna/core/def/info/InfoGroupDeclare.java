package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * ��Ϣ������
 * 
 * @author gaojingxin
 * 
 */
public interface InfoGroupDeclare extends InfoGroupDefine, NamedDeclare {
	/**
	 * �õ���������
	 * 
	 * @return ���ز�������
	 */
	public ModifiableNamedElementContainer<? extends InfoDeclare> getInfos();

	public ErrorInfoDeclare newError(String name, String messageFrmt);

	public WarningInfoDeclare newWarning(String name, String messageFrmt);

	public HintInfoDeclare newHint(String name, String messageFrmt);

	/**
	 * �½�������Ϣ��
	 */
	public ProcessInfoDeclare newProcess(String name, String messageFrmt);
}
