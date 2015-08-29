package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.spi.sql.SQLSyntaxException;

/**
 * �������ʽ�ڵ�
 * 
 * @author niuhaifeng
 * 
 */
interface NConditionExpr extends TextLocalizable, SQLVisitable {
	public static final NConditionExpr EMPTY = new NConditionExpr() {
		public int startLine() {
			return 0;
		}

		public int startCol() {
			return 0;
		}

		public int endLine() {
			return 0;
		}

		public int endCol() {
			return 0;
		}

		public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
			throw new SQLSyntaxException();
		}
	};
}
