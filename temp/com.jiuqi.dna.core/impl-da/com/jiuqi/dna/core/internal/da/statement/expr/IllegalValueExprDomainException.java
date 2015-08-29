package com.jiuqi.dna.core.internal.da.statement.expr;

import com.jiuqi.dna.core.def.exp.SelectColumnRefExpr;
import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.def.query.SubQueryExpression;

/**
 * 语句定义的表达式使用域非法
 * 
 * <p>
 * core2.5增加了语句定义的表达式使用域检查。该检查可以通过启动参数关闭。
 * 
 * @see com.jiuqi.dna.core.system.ContextVariable.StrictExprDomain
 * 
 * @since core2.5
 * 
 * @author houchunlei
 * 
 */
public final class IllegalValueExprDomainException extends RuntimeException {

	private static final long serialVersionUID = -1720255180362049350L;

	public IllegalValueExprDomainException(SelectColumnRefExpr columnRef) {
		super("查询列引用[" + columnRef.toString() + "]的使用域错误.");
	}

	public IllegalValueExprDomainException(TableFieldRefExpr fieldRef) {
		super("字段引用[" + fieldRef.toString() + "]的使用域错误.");
	}

	public IllegalValueExprDomainException(SubQueryExpression expr) {
		super("子查询的使用域不是其构造域.");
	}
}