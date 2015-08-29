package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.type.DataTypable;

/**
 * ��ѯ���ѡ���ж���
 * 
 * <p>
 * ��ʾһ�������ѯ����������
 * 
 * @author houchunlei
 * 
 */
public interface SelectColumnDefine extends RelationColumnDefine, DataTypable {

	/**
	 * ��ȡ�����Ĳ�ѯ����
	 * 
	 * @return ��ѯ����
	 */
	public SelectDefine getOwner();

	/**
	 * �����ж���ı��ʽ
	 * 
	 * @return �����ж���ı��ʽ
	 */
	public ValueExpression getExpression();
}
