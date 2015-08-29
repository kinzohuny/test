package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.Context;

/**
 * ģ�Ͷ�������
 * 
 * @author gaojingxin
 * 
 */
public interface ModelActionDefine extends ModelInvokeDefine {
	/**
	 * �����Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDefine getScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * ִ�ж���
	 * 
	 * @param context
	 *            ������
	 * @param mo
	 *            ģ�Ͷ���
	 */
	public void execute(Context context, Object mo);

	/**
	 * ִ�ж���
	 * 
	 * @param context
	 *            ������
	 * @param mo
	 *            ģ�Ͷ���
	 * @param ao
	 *            ��������
	 */
	public void execute(Context context, Object mo, Object ao);
}
