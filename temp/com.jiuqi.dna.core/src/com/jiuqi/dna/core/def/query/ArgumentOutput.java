package com.jiuqi.dna.core.def.query;

/**
 * �����������
 * 
 * @author houchunlei
 * 
 */
public enum ArgumentOutput {

	/**
	 * �������
	 */
	IN {
		@Override
		public boolean checkPrevOutput(ArgumentOutput prev) {
			return prev == IN;
		}
	},

	/**
	 * �����������
	 */
	IN_OUT() {

		@Override
		public boolean checkPrevOutput(ArgumentOutput prev) {
			return prev != OUT;
		}
	},
	/**
	 * �������
	 */
	OUT {
		@Override
		public boolean checkPrevOutput(ArgumentOutput prev) {
			return true;
		}
	};

	public abstract boolean checkPrevOutput(ArgumentOutput prev);
}