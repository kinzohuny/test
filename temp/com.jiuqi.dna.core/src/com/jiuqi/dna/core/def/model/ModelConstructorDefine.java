package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.Context;

/**
 * ģ�͹���������ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ModelConstructorDefine extends ModelInvokeDefine {
	/**
	 * �������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDefine getScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * ����ģ��ʵ������
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param ao
	 *            ����
	 * @return ���ع���õ�ģ��ʵ������
	 */
	public Object newMO(Context context, Object ao);

	/**
	 * �޲ι���ģ��ʵ������
	 * 
	 * @param constructor
	 *            ģ�͹�����
	 */
	public Object newMO(Context context);

}
