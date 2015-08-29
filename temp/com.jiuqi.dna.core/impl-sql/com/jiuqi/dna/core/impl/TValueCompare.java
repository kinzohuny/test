package com.jiuqi.dna.core.impl;

/**
 * �Ƚ����������
 * 
 * @author niuhaifeng
 * 
 */
class TValueCompare extends Token {
	public final NCompareExpr.Operator value;

	public TValueCompare(NCompareExpr.Operator value, int line, int col,
			int length) {
		super(line, col, length);
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof TValueCompare) {
			return ((TValueCompare) obj).value == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
