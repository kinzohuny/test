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
	 * when�ĸ���
	 * 
	 * @return
	 */
	int size();

	/**
	 * ��ȡ��һ��when���ʽ
	 * 
	 * @return
	 */
	ConditionalExpression when();

	/**
	 * ��ȡָ����when���ʽ
	 * 
	 * @param index
	 * @return
	 */
	ConditionalExpression when(int index);

	/**
	 * ��ȡ��һ��then���ʽ
	 * 
	 * @return
	 */
	ValueExpression then();

	/**
	 * ��ȡָ����then���ʽ
	 * 
	 * @param index
	 * @return
	 */
	ValueExpression then(int index);

	/**
	 * ��ȡĬ��ֵ
	 * 
	 * <p>
	 * ����Ϊ��
	 * 
	 * @return
	 */
	ValueExpression other();
}