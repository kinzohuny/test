package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.NamedDeclare;
import com.jiuqi.dna.core.type.DataType;

/**
 * �û����庯��
 * 
 * @see com.jiuqi.dna.core.def.query.UserFunctionDefine
 * 
 * @author houchunlei
 * 
 */
public interface UserFunctionDeclare extends UserFunctionDefine, NamedDeclare {

	/**
	 * ���Ӳ�������
	 * 
	 * <p>
	 * Ĭ��ʹ��arg0��arg1��arg2��Ϊ�������ơ�
	 * 
	 * @param type
	 * @return
	 */
	public FunctionArgumentDeclare newArgument(DataType type);

	/**
	 * ���Ӳ�����������ָ���������ơ�
	 * 
	 * <p>
	 * ������Ϣ�ṩDNA-SQL�༭��ʹ�ã�ǿ�ҽ����ṩ��չ��Ϣ��
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public FunctionArgumentDeclare newArgument(String name, DataType type);
}
