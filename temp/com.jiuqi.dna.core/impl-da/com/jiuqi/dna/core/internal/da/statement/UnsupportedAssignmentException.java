package com.jiuqi.dna.core.internal.da.statement;

import com.jiuqi.dna.core.def.obja.StructFieldDefine;
import com.jiuqi.dna.core.def.query.InsertStatementDefine;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.def.query.QueryColumnDefine;
import com.jiuqi.dna.core.def.query.UpdateStatementDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.DataType;

/**
 * 不支持的赋值操作异常
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
		return "在ORM定义[" + query.getName() + "]中，Java实体字段[" + field.getName() + "]的类型为[" + field.getType().toString() + "]，不能绑定到类型为[" + column.getType() + "]的输出列上。";
	}

	public UnsupportedAssignmentException(InsertStatementDefine insert,
			TableFieldDefine field, DataType type) {
		super(message(insert, field, type));
	}

	public static final String message(InsertStatementDefine insert,
			TableFieldDefine field, DataType type) {
		return "在插入语句定义[" + insert.getName() + "]中，字段[" + field.getName() + "]的类型为[" + field.getType().toString() + "]，不能接受类型为[" + type.toString() + "]的赋值。";
	}

	public UnsupportedAssignmentException(UpdateStatementDefine update,
			TableFieldDefine field, DataType type) {
		super(message(update, field, type));
	}

	public static final String message(UpdateStatementDefine update,
			TableFieldDefine field, DataType type) {
		return "在更新语句定义[" + update.getName() + "]中，字段[" + field.getName() + "]的类型为[" + field.getType().toString() + "]，不能接受类型为[" + type.toString() + "]的赋值。";
	}
}