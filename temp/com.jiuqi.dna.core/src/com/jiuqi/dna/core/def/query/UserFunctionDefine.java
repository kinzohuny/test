package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.NamedElementContainer;
import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.type.DataType;

/**
 * �û����庯��
 * 
 * <ul>
 * <li>��֧�ֱ���������
 * <li>Ŀǰ��֧�ֿ�ѡ������
 * <li>�û����庯��֮�䲻���໥������
 * </ul>
 * 
 * @author houchunlei
 */
public interface UserFunctionDefine extends NamedDefine {

	/**
	 * ��ȡ�����ķ�������
	 * 
	 * @return
	 */
	public DataType getReturnType();

	/**
	 * ��ȡ�����б�
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends FunctionArgumentDefine> getArguments();

	/**
	 * ���캯��������ʽ
	 * 
	 * @param values
	 * @return
	 */
	public OperateExpression expOf(Object... values);
}
