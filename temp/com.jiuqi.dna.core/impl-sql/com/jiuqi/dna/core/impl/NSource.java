package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.spi.sql.SQLSyntaxException;

/**
 * 关系节点
 * 
 * @author niuhaifeng
 * 
 */
abstract class NSource implements TextLocalizable, SQLVisitable {
	public static final NSource EMPTY = new NSource() {
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

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}
}
