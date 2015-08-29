package com.jiuqi.dna.core.internal.da.statement;

import com.jiuqi.dna.core.def.obja.StructFieldDefine;
import com.jiuqi.dna.core.def.query.InsertStatementDefine;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.def.query.QueryColumnDefine;
import com.jiuqi.dna.core.def.query.UpdateStatementDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.DataType;

/**
 * ��֧�ֵĸ�ֵ�����쳣
 * 
 * @see com.jiuqi.dna.core.system.ContextVariable.StrictAssignType
 * 
 * @author houchunlei
 * 
 */
public final class UnsupportedAssignmentException extends RuntimeException {

	private static final long serialVersionUID = 7169287826812392421L;

	public UnsupportedAssignmentException(MappingQueryStatementDefine query,
			QueryColumnDefine column, StructFieldDefine field) {
		super(message(query, column, field));
	}

	public static final String message(MappingQueryStatementDefine query,
			QueryColumnDefine column, StructFieldDefine field) {
		return "��ORM����[" + query.getName() + "]�У�Javaʵ���ֶ�[" + field.getName() + "]������Ϊ[" + field.getType().toString() + "]�����ܰ󶨵�����Ϊ[" + column.getType() + "]��������ϡ�";
	}

	public UnsupportedAssignmentException(InsertStatementDefine insert,
			TableFieldDefine field, DataType type) {
		super(message(insert, field, type));
	}

	public static final String message(InsertStatementDefine insert,
			TableFieldDefine field, DataType type) {
		return "�ڲ�����䶨��[" + insert.getName() + "]�У��ֶ�[" + field.getName() + "]������Ϊ[" + field.getType().toString() + "]�����ܽ�������Ϊ[" + type.toString() + "]�ĸ�ֵ��";
	}

	public UnsupportedAssignmentException(UpdateStatementDefine update,
			TableFieldDefine field, DataType type) {
		super(message(update, field, type));
	}

	public static final String message(UpdateStatementDefine update,
			TableFieldDefine field, DataType type) {
		return "�ڸ�����䶨��[" + update.getName() + "]�У��ֶ�[" + field.getName() + "]������Ϊ[" + field.getType().toString() + "]�����ܽ�������Ϊ[" + type.toString() + "]�ĸ�ֵ��";
	}
}