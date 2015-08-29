package com.jiuqi.dna.core.def.query;

/**
 * 参数输出类型
 * 
 * @author houchunlei
 * 
 */
public enum ArgumentOutput {

	/**
	 * 输入参数
	 */
	IN {
		@Override
		public boolean checkPrevOutput(ArgumentOutput prev) {
			return prev == IN;
		}
	},

	/**
	 * 输入输出参数
	 */
	IN_OUT() {

		@Override
		public boolean checkPrevOutput(ArgumentOutput prev) {
			return prev != OUT;
		}
	},
	/**
	 * 输出参数
	 */
	OUT {
		@Override
		public boolean checkPrevOutput(ArgumentOutput prev) {
			return true;
		}
	};

	public abstract boolean checkPrevOutput(ArgumentOutput prev);
}