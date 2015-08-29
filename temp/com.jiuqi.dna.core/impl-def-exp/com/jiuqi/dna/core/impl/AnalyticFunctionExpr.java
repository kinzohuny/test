package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;

public final class AnalyticFunctionExpr extends ValueExpr {

	public static abstract class Bound {

		<TContext> void visit(OMVisitor<TContext> visitor, TContext context) {
		}

		public int valueCount() {
			return 0;
		}
	}

	public static final Bound CURRENT_ROW = new Bound() {

		@Override
		public String toString() {
			return "current row";
		}
	};

	public static final Bound UNBOUNDED = new Bound() {

		@Override
		public String toString() {
			return "unbounded";
		}
	};

	public static final class LimitBound extends Bound {

		final ValueExpr value;

		public LimitBound(ValueExpr value) {
			this.value = value;
		}

		@Override
		<TContext> void visit(OMVisitor<TContext> visitor, TContext context) {
			this.value.visit(visitor, context);
		}

		@Override
		public final int valueCount() {
			return 1;
		}
	}

	public enum WindowType {

		ROWS, RANGE
	}

	final AnalyticFunction function;
	final ValueExpr value;
	final ValueExpr[] partitions;
	final OrderByItemImpl[] orderbys;
	final WindowType windowType;
	final Bound preceding;
	final Bound following;

	final DataTypeInternal type;

	public AnalyticFunctionExpr(AnalyticFunction function,
			ValueExpr value, OrderByItemImpl[] orderbys) {
		if (function == null || value == null || orderbys == null || orderbys.length == 0) {
			throw new NullPointerException();
		}
		this.function = function;
		this.value = value;
		this.type = function.checkValue(value);
		this.partitions = null;
		this.orderbys = orderbys;
		this.windowType = WindowType.ROWS;
		this.preceding = UNBOUNDED;
		this.following = null;
	}

	public AnalyticFunctionExpr(AnalyticFunction function,
			ValueExpr value, ValueExpr[] partitions,
			OrderByItemImpl[] orderbys, WindowType windowType, Bound preceding,
			Bound following) {
		if (function == null || value == null || orderbys == null || orderbys.length == 0) {
			throw new NullPointerException();
		}
		this.function = function;
		this.value = value;
		this.type = function.checkValue(value);
		this.partitions = partitions;
		this.orderbys = orderbys;
		this.windowType = windowType;
		if (windowType == null || preceding == null) {
			throw new NullArgumentException("type or preceding");
		}
		this.preceding = preceding;
		this.following = following;
	}
	
	public AnalyticFunctionExpr(AnalyticFunction function,
			ValueExpr[] partitions, OrderByItemImpl[] orderbys) {
		if (function == null || orderbys == null || orderbys.length == 0) {
			throw new NullPointerException();
		}
		this.function = function;
		this.value = null;
		this.type = IntType.TYPE;
		this.partitions = partitions;
		this.orderbys = orderbys;
		this.windowType = null;
		this.preceding = null;
		this.following = null;
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitAnalyticFunctionExpr(this, context);
	}

	@Override
	public final DataTypeInternal getType() {
		return this.type;
	}

	@Override
	protected ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw Utils.notImplemented();
	}

	@Override
	protected ValueExpr clone(RelationRefDomain domain, ArgumentableDefine args) {
		ValueExpr[] partitions = null;
		if (this.partitions != null) {
			partitions = new ValueExpr[this.partitions.length];
			for (int i = 0; i < this.partitions.length; i++) {
				partitions[i] = this.partitions[i].clone(domain, args);
			}
		}
		OrderByItemImpl[] orderbys = new OrderByItemImpl[this.orderbys.length];
		for (int i = 0; i < this.orderbys.length; i++) {
			ValueExpr orderbyValue = this.orderbys[i].value.clone(domain, args);
			orderbys[i] = new OrderByItemImpl(null, orderbyValue);
			orderbys[i].setDesc(this.orderbys[i].isDesc());
		}
		if (this.value != null) {
			Bound preceding = this.preceding;
			if (this.preceding instanceof LimitBound) {
				LimitBound bound = (LimitBound) this.preceding;
				preceding = new LimitBound(bound.value.clone(domain, args));
			}
			Bound following = this.following;
			if (this.following instanceof LimitBound) {
				LimitBound bound = (LimitBound) this.following;
				following = new LimitBound(bound.value.clone(domain, args));
			}
			ValueExpr value = this.value.clone(domain, args);
			return new AnalyticFunctionExpr(this.function, value, partitions,
					orderbys, this.windowType, preceding, following);
		} else {
			return new AnalyticFunctionExpr(this.function, partitions, orderbys);
		}
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		this.function.render(buffer, usages, this);
	}

	@Override
	public String getXMLTagName() {
		throw Utils.notImplemented();
	}

}
