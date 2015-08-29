package com.jiuqi.dna.core.impl;

/**
 * ����ֵ Token�������ࣺTInt,TLong,TString,TBoolean,TDouble,TFunction
 * �����е����Ͳ���ʾ���ŵ����ͣ�����ʾ���ŷ���ֵ�����͡�
 * 
 * @author niuhaifeng
 * 
 */
class Token implements TextLocalizable {
	public static final Token EMPTY = new Token(0, 0, 0);
	
	public final int line;
	public final int col;
	public final int length;

	public Token(int line, int col, int length) {
		this.line = line;
		this.col = col;
		this.length = length;
	}

	public int startLine() {
		return this.line;
	}

	public int startCol() {
		return this.col;
	}

	public int endLine() {
		return this.line;
	}

	public int endCol() {
		return this.col + this.length;
	}
}
