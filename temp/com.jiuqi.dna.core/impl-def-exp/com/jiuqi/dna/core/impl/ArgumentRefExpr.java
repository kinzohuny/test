package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.arg.ArgumentRefExpression;
import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ArgumentPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 参数引用表达式实现
 * 
 * @author gaojingxin
 * 
 */
public final class ArgumentRefExpr extends ValueExpr implements
		ArgumentRefExpression {

	public final StructFieldDefineImpl getArgument() {
		return this.arg;
	}

	@Override
	public final DataTypeInternal getType() {
		return (DataTypeInternal) this.arg.type;
	}

	@Override
	public final String toString() {
		return "@".concat(this.arg.name);
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_arg_ref;
	}

	@Override
	public final void render(SXElement element) {
		element.setAttribute(xml_attr_arg_name, this.arg.name);
	}

	static final String xml_name_arg_ref = "argument-ref";
	static final String xml_attr_arg_name = "argument";

	public final StructFieldDefineImpl arg;

	public ArgumentRefExpr(ArgumentDefine arg) {
		this((StructFieldDefineImpl) arg);
	}

	ArgumentRefExpr(StructFieldDefineImpl arg) {
		if (arg == null) {
			throw new NullPointerException();
		}
		this.arg = arg;
	}

	@Override
	protected final ValueExpr clone(RelationRefDomain domain,
			ArgumentableDefine args) {
		if (args == null) {
			throw new NullPointerException();
		}
		return new ArgumentRefExpr(args.getArguments().get(this.arg.name));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitArgumentRefExpr(this, context);
	}

	@Override
	protected final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected final boolean isNoneEnumArg() {
		return !(this.arg.type instanceof EnumTypeImpl<?>);
	}

	@Override
	public final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadParam(new ArgumentPlaceholder(this.arg, this.arg.type));
	}

	public final void renderWithRefer(ISqlExprBuffer buffer) {
		buffer.loadParam(new ArgumentPlaceholder(this.arg, this.arg.type), (DataTypeInternal) this.arg.type);
	}

	public final void renderUsingRefer(ISqlExprBuffer buffer,
			DataTypeInternal refer) {
		buffer.loadParam(new ArgumentPlaceholder(this.arg, refer), refer);
	}
}