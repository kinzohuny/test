package com.jiuqi.dna.core.def.exp;

/**
 * @author houchunlei
 * 
 */
public interface SearchedCaseExpr extends ValueExpression {

	public interface Builder {

		public Builder when(ConditionalExpression when, Object then);

		public SearchedCaseExpr other(Object other);

		public SearchedCaseExpr build();
	}

	public interface Factory {

		public Builder newBuilder();
	}

	public static final Factory factory = new Factory() {

		public Builder newBuilder() {
			return new com.jiuqi.dna.core.impl.SearchedCaseExpr.BuilderImpl();
		}
	};

	/**
	 * when的个数
	 * 
	 * @return
	 */
	int size();

	/**
	 * 获取第一个when表达式
	 * 
	 * @return
	 */
	ConditionalExpression when();

	/**
	 * 获取指定的when表达式
	 * 
	 * @param index
	 * @return
	 */
	ConditionalExpression when(int index);

	/**
	 * 获取第一个then表达式
	 * 
	 * @return
	 */
	ValueExpression then();

	/**
	 * 获取指定的then表达式
	 * 
	 * @param index
	 * @return
	 */
	ValueExpression then(int index);

	/**
	 * 获取默认值
	 * 
	 * <p>
	 * 可能为空
	 * 
	 * @return
	 */
	ValueExpression other();
}