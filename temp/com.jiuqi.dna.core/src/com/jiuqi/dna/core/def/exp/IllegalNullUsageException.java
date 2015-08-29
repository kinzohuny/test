package com.jiuqi.dna.core.def.exp;

/**
 * 错误的Null表达式使用异常。
 * 
 * <p>
 * Null表达式只能用于insert及update的赋值，不能直接用作函数的输入参数，不能用作为谓词的运算体。
 * 
 * @see com.jiuqi.dna.core.system.ContextVariable.StrictNullUsage
 * 
 * @author houchunlei
 * 
 */
public final class IllegalNullUsageException extends RuntimeException {

	private static final long serialVersionUID = 4656439410059208777L;

	public IllegalNullUsageException(Predicate predicate) {
		super(message(predicate));
	}

	public static final String message(Predicate predicate) {
		return "在谓词运算[" + predicate.toString()
				+ "]中，存在非法的Null表达式的使用：Null表达式不能作为函数的输入参数以及谓词的运算体。";
	}

	public IllegalNullUsageException(Operator operator) {
		super(message(operator));
	}

	public static final String message(Operator operator) {
		return "在运算[" + operator.toString()
				+ "]中，存在非法的Null表达式的使用：Null表达式不能作为函数的输入参数以及谓词的运算体。";
	}
}