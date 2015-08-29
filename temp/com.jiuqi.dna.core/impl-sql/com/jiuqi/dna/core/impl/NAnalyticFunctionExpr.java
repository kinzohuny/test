package com.jiuqi.dna.core.impl;

import java.util.Arrays;

class NAnalyticFunctionExpr implements NValueExpr {
	public enum Func {
		SUM, ROW_NUMBER, RANK, DENSE_RANK
	}

	public enum WindowType {
		ROWS(){
			@Override
			public AnalyticFunctionExpr.WindowType change() {
				return AnalyticFunctionExpr.WindowType.ROWS;
			}
			
		}, RANGE(){
			@Override
			public AnalyticFunctionExpr.WindowType change() {
				return AnalyticFunctionExpr.WindowType.RANGE;
			}
		}
		
		;
		
		public abstract AnalyticFunctionExpr.WindowType change();
	}

	public static class NWindowClause {
		public final WindowType windowType;
		public final Object preceding;
		public final Object following;

		public NWindowClause(WindowType windowType, Object preceding,
				Object following) {
			this.windowType = windowType;
			this.preceding = preceding;
			this.following = following;
		}
	}

	public final static Object CURRENT_ROW = new Object();
	public final static Object UNBOUNDED = new Object();

	private final int startLine;
	private final int startCol;
	private final int endLine;
	private final int endCol;
	private final int hashCode;

	public final Func func;
	public final NValueExpr column;
	public final NValueExpr[] partitions;
	public final NOrderByColumn[] orders;
	public final WindowType windowType;
	public final Object preceding;
	public final Object following;

	public NAnalyticFunctionExpr(NAnalyticFunctionExpr.Func func,
			TextLocalizable start, Token end, NValueExpr column,
			NValueExpr[] partitions, NOrderByColumn[] orders,
			WindowType windowType, Object preceding, Object following) {
		this.func = func;
		this.column = column;
		this.partitions = partitions;
		this.orders = orders;
		this.windowType = windowType;
		this.preceding = preceding;
		this.following = following;
		this.startLine = start.startLine();
		this.startCol = start.startCol();
		this.endLine = end.line;
		this.endCol = end.col + end.length;
		int hashCode = (func.hashCode() << 24) ^ (column.hashCode() << 16);
		if (partitions != null) {
			for (NValueExpr e : partitions) {
				hashCode ^= e.hashCode() << 8;
			}
		}
		for (NOrderByColumn c : orders) {
			hashCode ^= c.hashCode();
		}
		if (windowType != null) {
			hashCode ^= windowType.hashCode();
		}
		if (preceding != null) {
			hashCode ^= preceding.hashCode();
		}
		if (following != null) {
			hashCode ^= following.hashCode();
		}
		this.hashCode = hashCode;
	}
	
	public NAnalyticFunctionExpr(NAnalyticFunctionExpr.Func func,
			TextLocalizable start, Token end, NValueExpr[] partitions,
			NOrderByColumn[] orders) {
		this.func = func;
		this.column = null;
		this.partitions = partitions;
		this.orders = orders;
		this.windowType = null;
		this.preceding = null;
		this.following = null;
		this.startLine = start.startLine();
		this.startCol = start.startCol();
		this.endLine = end.line;
		this.endCol = end.col + end.length;
		int hashCode = func.hashCode() << 24;
		if (column != null) {
			hashCode ^= column.hashCode() << 16;
		}
		if (partitions != null) {
			for (NValueExpr e : partitions) {
				hashCode ^= e.hashCode() << 8;
			}
		}
		for (NOrderByColumn c : orders) {
			hashCode ^= c.hashCode();
		}
		if (windowType != null) {
			hashCode ^= windowType.hashCode();
		}
		if (preceding != null) {
			hashCode ^= preceding.hashCode();
		}
		if (following != null) {
			hashCode ^= following.hashCode();
		}
		this.hashCode = hashCode;
	}


	public int startLine() {
		return this.startLine;
	}

	public int startCol() {
		return this.startCol;
	}

	public int endLine() {
		return this.endLine;
	}

	public int endCol() {
		return this.endCol;
	}

	public <T> void accept(T visitorContext, SQLVisitor<T> visitor) {
		visitor.visitAnalyticFunction(visitorContext, this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof NAnalyticFunctionExpr)) {
			return false;
		}
		NAnalyticFunctionExpr expr = (NAnalyticFunctionExpr) obj;
		if (expr.func != this.func || expr.column == null
				&& this.column != null || expr.column != null
				&& !expr.column.equals(this.column)
				|| !Arrays.equals(expr.partitions, this.partitions)
				|| !Arrays.equals(expr.orders, this.orders)
				|| expr.windowType == null && this.windowType != null
				|| expr.windowType != null
				&& expr.windowType != this.windowType || expr.preceding == null
				&& this.preceding != null || expr.preceding != null
				&& !expr.preceding.equals(this.preceding)
				|| expr.following == null && this.following != null
				|| expr.following != null
				&& !expr.following.equals(this.following)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return RenderVisitor.render(this);
	}

}
