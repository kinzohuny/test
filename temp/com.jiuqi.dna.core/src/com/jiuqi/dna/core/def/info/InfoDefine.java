package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.NamedElementContainer;

/**
 * ��Ϣ����ӿڣ�������ϵͳ�����쳣����־����Ϣ��
 */
public interface InfoDefine extends NamedDefine {
	/**
	 * �����Ϣ������
	 * 
	 * @return ������Ϣ������
	 */
	public InfoKind getKind();

	/**
	 * ��ø�ʽ����Ϣ�ı� <br>
	 * ������"{������[:type]}"��ʽ����
	 * 
	 * @return ���ظ�ʽ����Ϣ
	 */
	public String getMessage();

	/**
	 * ��ȡ�Ƿ���Ҫ��¼��־��Ĭ�ϲ���¼��־
	 */
	public boolean isNeedLog();

	/**
	 * ��ȡ�Ƿ���Ҫͨ���û���Ĭ�ϵ����òο�InfoKind.defaultReportToUser
	 */
	public boolean isReportToUser();

	/**
	 * �õ���������
	 * 
	 * @return ���ز�������
	 */
	public NamedElementContainer<? extends InfoParameterDefine> getParameters();
}
