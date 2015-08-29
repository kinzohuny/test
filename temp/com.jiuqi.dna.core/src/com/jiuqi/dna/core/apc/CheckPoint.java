package com.jiuqi.dna.core.apc;

import com.jiuqi.dna.core.def.apc.Accessibility;
import com.jiuqi.dna.core.def.apc.CheckPointDefine;

/**
 * ����ʵ��
 * 
 * @author gaojingxin
 * 
 */
public interface CheckPoint {
	/**
	 * �����Ҫ���Ĳ���
	 */
	public CheckPointDefine getDefine();

	/**
	 * ��õ�ǰ����
	 */
	public Scene getScene();

	/**
	 * ���¼��Ľ�����ɼ��ϵͳ�ص�
	 */
	public void updateAccessibility(Accessibility accessibility);
}
