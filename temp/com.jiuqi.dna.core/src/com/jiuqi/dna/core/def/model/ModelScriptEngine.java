package com.jiuqi.dna.core.def.model;

import com.jiuqi.dna.core.Context;

/**
 * �ű�����ӿ�
 * 
 * @author gaojingxin
 * 
 */
public interface ModelScriptEngine<TPreparedData> {
	/**
	 * �ýű������ж��Ƿ�֧��
	 * 
	 * @param language
	 *            �ű����������ƣ�����ֵһ��Сд��
	 * @return ���ش����������ʾ֧�֣�������ֵԽ�󣬱�ʾ֧�ֶ�Խ�ߣ��������������������汾����
	 */
	public int suport(String language);

	/**
	 * ����뵱ǰ�����ģ���ǰ�̣߳���صĽű�������
	 */
	public ModelScriptContext<TPreparedData> allocContext(Context context);
}
