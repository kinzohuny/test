package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.Container;
import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.apc.CheckPointDefine;
import com.jiuqi.dna.core.def.arg.ArgumentableDefine;

/**
 * ģ�͵��ö��壬��ģ�����ԣ�����������֮���ӿ�
 * 
 * @author gaojingxin
 * @param <TAO>
 *            ���õĲ���ʵ�����ͣ�Object����յĲ���
 */
public abstract interface ModelInvokeDefine extends NamedDefine,
        ArgumentableDefine, CheckPointDefine {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDefine getOwner();

	/**
	 * ����Ƿ���ҪȨ�޿���
	 * 
	 * @return �����Ƿ�Ȩ�޿���
	 */
	public boolean isAuthorizable();

	/**
	 * ���ÿ�ʼ֮ǰ�ļ��㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ؼ��㼯��
	 */
	public Container<? extends InspectPoint> getBeforeInspects();

	/**
	 * �������֮��ļ��㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ؼ��㼯��
	 */
	public Container<? extends InspectPoint> getAfterInspects();

	/**
	 * �������֮��ļ��㣬�������������ĵ���
	 * 
	 * @return ���ؼ��㼯��
	 */
	public Container<? extends InspectPoint> getFinallyInspects();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * ��õ��õ���Ч��
	 * 
	 * @param context
	 *            ������
	 * @param mo
	 *            ģ�Ͷ���
	 * @return ������Ч��
	 */
	public ModelInvokeValidity getValidity(Context context, Object mo);
}
