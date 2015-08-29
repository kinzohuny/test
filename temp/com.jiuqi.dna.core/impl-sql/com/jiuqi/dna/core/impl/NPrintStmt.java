package com.jiuqi.dna.core.impl;

/**
 * PRINTÓï¾ä
 * 
 * @author niuhaifeng
 * 
 */
class NPrintStmt implements NStatement {
	private final int startLine;
	private final int startCol;
	public final NValueExpr expr;

	public NPrintStmt(Token start, NValueExpr expr) {
		this.expr = expr;
		this.startLine = start.line;
		this.startCol = start.col;
	}

	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.expr.endLine();
	}

	public int endCol() {
		return this.expr.endCol();
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitPrintStmt(visitorContext, this);
	}
}
