package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.arg.ArgumentDeclare;
import com.jiuqi.dna.core.type.DataType;

public interface StoredProcedureDeclare extends StoredProcedureDefine,
		StatementDeclare {

	/**
	 * ���ô洢���̷��صĽ��������.
	 * 
	 * @param count
	 */
	public void setResultSets(int count);

	/**
	 * ���Ӳ������壬��ָ���������
	 * 
	 * @param name
	 *            ��������
	 * @param type
	 *            ������������
	 * @param output
	 *            �����������
	 * @return
	 */
	public ArgumentDeclare newArgument(String name, DataType type,
			ArgumentOutput output);
}