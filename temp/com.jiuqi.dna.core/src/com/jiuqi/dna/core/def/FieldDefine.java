package com.jiuqi.dna.core.def;

import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.type.DataTypable;

/**
 * �ֶλ��ӿڶ���
 * 
 * @author gaojingxin
 * 
 */
public interface FieldDefine extends NamedDefine, DataTypable {
	/**
	 * �Ƿ�����Ҫһֱ���ֿ���(�ǿ�)
	 * 
	 * @return �����Ƿ��Ǳ����ֶ�
	 */
	public boolean isKeepValid();

	/**
	 * �Ƿ���ֻ���ֶΣ�ֻ�ڹ���ʱ���ã�
	 * 
	 * @return �����Ƿ���ֻ���ֶ�
	 */
	public boolean isReadonly();

	/**
	 * ��ȡ�ֶε�Ĭ��ֵ
	 * 
	 * @return �����ֶζ����Ĭ��ֵ
	 */
	public ValueExpression getDefault();
}
