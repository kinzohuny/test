package com.jiuqi.dna.core.impl;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.TableFieldRefExpr;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.def.query.SubQueryDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.misc.SXElement;

/**
 * 值表达式内部统一接口
 * 
 * @author gaojingxin
 */
public abstract class ValueExpr extends MetaBase implements ValueExpression,
		OMVisitable {

	public static ValueExpr expOf(Object object) {
		if (object == null) {
			return NullExpr.NULL;
		} else if (object instanceof ValueExpr) {
			return (ValueExpr) object;
		} else if (object instanceof ArgumentDefine) {
			return new ArgumentRefExpr((StructFieldDefineImpl) object);
		} else if (object instanceof SubQueryImpl) {
			return new SubQueryExpr((SubQueryImpl) object);
		} else if (object instanceof Enum<?>) {
			// HCL 可否确定类型?
			return new IntConstExpr(((Enum<?>) object).ordinal());
		} else {
			return ConstExpr.expOf(object);
		}
	}

	public final PredicateExpr xEq(Object value) {
		return new PredicateExpr(false, PredicateImpl.EQUAL_TO, this, expOf(value));
	}

	public final PredicateExpr xnEq(Object value) {
		return new PredicateExpr(true, PredicateImpl.EQUAL_TO, this, expOf(value));
	}

	public final PredicateExpr xLess(Object value) {
		return new PredicateExpr(false, PredicateImpl.LESS_THAN, this, expOf(value));
	}

	public final PredicateExpr xGreater(Object value) {
		return new PredicateExpr(false, PredicateImpl.GREATER_THAN, this, expOf(value));
	}

	public final PredicateExpr xLE(Object value) {
		return new PredicateExpr(false, PredicateImpl.LESS_THAN_OR_EQUAL_TO, this, expOf(value));
	}

	public final PredicateExpr xGE(Object value) {
		return new PredicateExpr(false, PredicateImpl.GREATER_THAN_OR_EQUAL_TO, this, expOf(value));
	}

	public final PredicateExpr xBtwn(Object value1, Object value2) {
		return new PredicateExpr(false, PredicateImpl.BETWEEN, this, expOf(value1), expOf(value2));
	}

	public final PredicateExpr xnBtwn(Object value1, Object value2) {
		return new PredicateExpr(true, PredicateImpl.BETWEEN, this, expOf(value1), expOf(value2));
	}

	public final PredicateExpr xLike(Object value) {
		return new PredicateExpr(false, PredicateImpl.STR_LIKE, this, expOf(value));
	}

	public final PredicateExpr xLike(Object value, Object escape) {
		return new PredicateExpr(false, PredicateImpl.STR_LIKE, this, expOf(value), expOf(escape));
	}

	public final PredicateExpr xnLike(Object value, Object escape) {
		return new PredicateExpr(true, PredicateImpl.STR_LIKE, this, expOf(value), expOf(escape));
	}

	public final PredicateExpr xnLike(Object value) {
		return new PredicateExpr(true, PredicateImpl.STR_LIKE, this, expOf(value));
	}

	public final PredicateExpr xStartW(Object value) {
		return new PredicateExpr(false, PredicateImpl.STR_STARTS_WITH, this, expOf(value));
	}

	public final PredicateExpr xEndW(Object value) {
		return new PredicateExpr(false, PredicateImpl.STR_ENDS_WITH, this, expOf(value));
	}

	public final PredicateExpr xContain(Object value) {
		return new PredicateExpr(false, PredicateImpl.STR_CONTAINS, this, expOf(value));
	}

	public final PredicateExpr xnContain(Object value) {
		return new PredicateExpr(true, PredicateImpl.STR_CONTAINS, this, expOf(value));
	}

	public final PredicateExpr xIn(Object value, Object... values) {
		return new PredicateExpr(false, PredicateImpl.IN, concat(this, value, values));
	}

	public final PredicateExpr xIn(Object value) {
		return new PredicateExpr(false, PredicateImpl.IN, this, expOf(value));
	}

	public final PredicateExpr xIn(Object[] values) {
		return new PredicateExpr(false, PredicateImpl.IN, concat(this, values));
	}

	public final PredicateExpr xIn(SubQueryDefine subquery) {
		return new PredicateExpr(false, PredicateImpl.IN, this, (SubQueryExpr) subquery.newExpression());
	}

	public final PredicateExpr xnIn(Object value) {
		return new PredicateExpr(true, PredicateImpl.IN, this, expOf(value));
	}

	public final PredicateExpr xnIn(Object value, Object... values) {
		return new PredicateExpr(true, PredicateImpl.IN, concat(this, value, values));
	}

	public final PredicateExpr xnIn(Object[] values) {
		return new PredicateExpr(true, PredicateImpl.IN, concat(this, values));
	}

	public final PredicateExpr xnIn(SubQueryDefine subquery) {
		return new PredicateExpr(true, PredicateImpl.IN, this, (SubQueryExpr) subquery.newExpression());
	}

	public final PredicateExpr xIsNull() {
		return new PredicateExpr(false, PredicateImpl.IS_NULL, this);
	}

	public final PredicateExpr xnNull() {
		return new PredicateExpr(false, PredicateImpl.IS_NOT_NULL, this);
	}

	public final OperateExpr xAdd(Object value, Object... values) {
		return new OperateExpr(CommonOperator.ADD, concat(this, value, values));
	}

	public final OperateExpr xAdd(Object value) {
		return new OperateExpr(CommonOperator.ADD, this, expOf(value));
	}

	public final OperateExpr xSub(Object value, Object... values) {
		return new OperateExpr(CommonOperator.SUB, concat(this, value, values));
	}

	public final OperateExpr xSub(Object value) {
		return new OperateExpr(CommonOperator.SUB, this, expOf(value));
	}

	public final OperateExpr xMul(Object value, Object... values) {
		return new OperateExpr(CommonOperator.MUL, concat(this, value, values));
	}

	public final OperateExpr xMul(Object value) {
		return new OperateExpr(CommonOperator.MUL, this, expOf(value));
	}

	public final OperateExpr xDiv(Object value) {
		return new OperateExpr(CommonOperator.DIV, this, expOf(value));
	}

	public final OperateExpr xMinus() {
		return new OperateExpr(CommonOperator.MINUS, this);
	}

	public final OperateExpr xMod(Object value) {
		return new OperateExpr(CommonOperator.MOD, this, expOf(value));
	}

	public final OperateExpr xSimpleCase(Object whenValue, Object resultValue,
			Object... others) {
		return new OperateExpr(CommonOperator.SIMPLE_CASE, concat(this, whenValue, resultValue, others));
	}

	public final OperateExpr xCoalesce(Object value, Object... values) {
		return new OperateExpr(CommonOperator.COALESCE, concat(this, value, values));
	}

	public final OperateExpr xCoalesce(Object value) {
		return new OperateExpr(CommonOperator.COALESCE, this, expOf(value));
	}

	private static final boolean DNASQL_USING_LEGACY_OPERATION_MERGE = Boolean.getBoolean("com.jiuqi.dna.dnasql-using-legacy-operation-merge");

	public final OperateExpr xStrConcat(Object value) {
		if (!DNASQL_USING_LEGACY_OPERATION_MERGE && this instanceof OperateExpr) {
			OperateExpr operate = (OperateExpr) this;
			if (operate.operator == CommonOperator.STR_CONCAT) {
				ValueExpr[] ves = new ValueExpr[operate.size() + 1];
				System.arraycopy(operate.values, 0, ves, 0, operate.size());
				ves[operate.size()] = expOf(value);
				return new OperateExpr(CommonOperator.STR_CONCAT, ves);
			}
		}
		return new OperateExpr(CommonOperator.STR_CONCAT, this, expOf(value));
	}

	public final OperateExpr xStrConcat(Object value, Object... values) {
		if (!DNASQL_USING_LEGACY_OPERATION_MERGE && this instanceof OperateExpr) {
			OperateExpr operate = (OperateExpr) this;
			if (operate.operator == CommonOperator.STR_CONCAT) {
				ArrayList<ValueExpr> list = new ArrayList<ValueExpr>();
				for (ValueExpr v : operate.values) {
					list.add(v);
				}
				list.add(expOf(value));
				for (Object v : values) {
					list.add(expOf(v));
				}
				return new OperateExpr(CommonOperator.STR_CONCAT, list.toArray(new ValueExpr[list.size()]));
			}
		}
		return new OperateExpr(CommonOperator.STR_CONCAT, concat(this, value, values));
	}

	public final OperateExpr xBinConcat(Object value) {
		return new OperateExpr(CommonOperator.BIN_CONCAT, this, expOf(value));
	}

	public final OperateExpr xBinConcat(Object value, Object... values) {
		return new OperateExpr(CommonOperator.BIN_CONCAT, concat(this, value, values));
	}

	public final OperateExpr xParentRECID() {
		return new OperateExpr(CommonOperator.PARENT_RECID, this);
	}

	public final OperateExpr xAncestorRECID(Object relative) {
		return new OperateExpr(CommonOperator.RELATIVE_ANCESTOR_RECID, this, ValueExpr.expOf(relative));
	}

	public final OperateExpr xAncestorRECIDOfLevel(Object absolute) {
		return new OperateExpr(CommonOperator.ABUSOLUTE_ANCESTOR_RECID, this, ValueExpr.expOf(absolute));
	}

	public final OperateExpr xLevelOf() {
		return new OperateExpr(CommonOperator.LEVEVL_OF, this);
	}

	public final PredicateExpr xIsChildOf(TableFieldRefExpr parent) {
		if (parent == null) {
			throw new NullArgumentException("目标级次路径表达式");
		}
		return new PredicateExpr(false, PredicateImpl.IS_CHILD_OF, this, (TableFieldRefImpl) parent);
	}

	public final PredicateExpr xIsDescendantOf(TableFieldRefExpr ancestor) {
		if (ancestor == null) {
			throw new NullArgumentException("目标级次路径表达式");
		}
		return new PredicateExpr(false, PredicateImpl.IS_DESCENDANT_OF, this, (TableFieldRefImpl) ancestor);
	}

	public final PredicateExpr xIsDescendantOf(TableFieldRefExpr ancestor,
			Object range) {
		if (ancestor == null) {
			throw new NullArgumentException("目标级次路径表达式");
		}
		return new PredicateExpr(false, PredicateImpl.IS_RANGE_DESCENDANT_OF, this, (TableFieldRefImpl) ancestor, ValueExpr.expOf(range));
	}

	public final PredicateExpr xIsRelativeDescendantOf(
			TableFieldRefExpr ancestor, Object relative) {
		if (ancestor == null) {
			throw new NullArgumentException("目标级次路径表达式");
		}
		return new PredicateExpr(false, PredicateImpl.IS_RELATIVE_DESCENDANT_OF, this, (TableFieldRefImpl) ancestor, ValueExpr.expOf(relative));
	}

	public final PredicateExpr xIsParentOf(TableFieldRefExpr child) {
		if (child == null) {
			throw new NullArgumentException("目标级次路径表达式");
		}
		return new PredicateExpr(false, PredicateImpl.IS_PARENT_OF, this, (TableFieldRefImpl) child);
	}

	public final PredicateExpr xIsRelativeAncestorOf(
			TableFieldRefExpr descendant, Object relative) {
		if (descendant == null) {
			throw new NullArgumentException("目标级次路径表达式");
		}
		return new PredicateExpr(false, PredicateImpl.IS_RELATIVE_ANCESTOR_OF, this, (TableFieldRefImpl) descendant, ValueExpr.expOf(relative));
	}

	@Override
	String getDescription() {
		return null;
	}

	public abstract DataTypeInternal getType();

	final static ValueExpr[] emptyArray = {};

	static final ValueExpr[] expArrayOf(Object... objects) {
		if (objects == null) {
			return null;
		} else if (objects.length == 0) {
			return emptyArray;
		} else {
			ValueExpr[] values = new ValueExpr[objects.length];
			for (int i = 0; i < objects.length; i++) {
				values[i] = expOf(objects[i]);
			}
			return values;
		}
	}

	static final ValueExpr loadValue(SXElement element,
			RelationRefOwner refOwner, ArgumentableDefine args) {
		String tagName = element.name;
		if (tagName.equals(TableFieldRefImpl.xml_name_fieldref) || tagName.equals("fieldref")/* 兼容早期版本 */) {
			return new TableFieldRefImpl(element, refOwner);
		} else if (tagName.equals(SelectColumnRefImpl.xml_name_columnref)) {
			return new SelectColumnRefImpl(element, refOwner);
		} else if (tagName.equals(ConstExpr.xml_element_const)) {
			return ConstExpr.loadConst(element);
		} else if (tagName.equals(ArgumentRefExpr.xml_name_arg_ref)) {
			if (args == null) {
				throw new UnsupportedOperationException("表达式的所有者不支持参数");
			}
			String an = element.getAttribute(ArgumentRefExpr.xml_attr_arg_name, null);
			return new ArgumentRefExpr(args.getArguments().get(an));
		} else if (tagName.equals(OperateExpr.xml_name_operate)) {
			return new OperateExpr(element, refOwner, args);
		} else if (tagName.equals(SearchedCaseExpr.xml_name_searched_case)) {
			return SearchedCaseExpr.newSearchedCase(element, refOwner, args);
		} else if (tagName.equals(NullExpr.xml_element_null)) {
			return NullExpr.NULL;
		}
		throw new UnsupportedOperationException("不支持的tagName[" + tagName + "]");
	}

	/**
	 * 加载XML对象及其所有后续兄弟节点为ValueExpr
	 * 
	 * @param first
	 *            第一个xml对象
	 * @param refOwner
	 *            关系引用容器,从这里构造关系列引用表达式
	 * @param args
	 *            参数容器,从这里查找参数定义
	 * @return
	 */
	static final ValueExpr[] loadValues(SXElement first,
			RelationRefOwner refOwner, ArgumentableDefine args) {
		List<ValueExpr> list = null;
		for (; first != null; first = first.nextSibling()) {
			if (list == null) {
				list = new ArrayList<ValueExpr>();
			}
			list.add(loadValue(first, refOwner, args));
		}
		return list != null ? list.toArray(new ValueExpr[list.size()]) : emptyArray;
	}

	static final ValueExpr[] concat(ValueExpr expr1, ValueExpr expr2,
			ValueExpression[] exprx) {
		if (expr1 == null || expr2 == null) {
			throw new NullPointerException();
		}
		if (exprx == null || exprx.length == 0) {
			return new ValueExpr[] { expr1, expr2 };
		}
		ValueExpr[] exprs = new ValueExpr[exprx.length + 2];
		exprs[0] = expr1;
		exprs[1] = expr2;
		int i = 2;
		for (ValueExpression e : exprx) {
			if (e == null) {
				throw new NullPointerException();
			}
			exprs[i++] = (ValueExpr) e;
		}
		return exprs;
	}

	final static ValueExpr[] concat(Object v, Object[] ov) {
		if (ov == null || ov.length == 0) {
			return new ValueExpr[] { expOf(v) };
		}
		ValueExpr[] exprs = new ValueExpr[ov.length + 1];
		exprs[0] = expOf(v);
		int i = 1;
		for (Object o : ov) {
			exprs[i++] = expOf(o);
		}
		return exprs;
	}

	final static ValueExpr[] concat(Object v1, Object v2, Object[] ov) {
		if (ov == null || ov.length == 0) {
			return new ValueExpr[] { expOf(v1), expOf(v2) };
		}
		ValueExpr[] exps = new ValueExpr[ov.length + 2];
		exps[0] = expOf(v1);
		exps[1] = expOf(v2);
		int i = 2;
		for (Object o : ov) {
			exps[i++] = expOf(o);
		}
		return exps;
	}

	final static ValueExpr[] concat(Object v1, Object v2, Object v3, Object[] ov) {
		if (ov == null || ov.length == 0) {
			return new ValueExpr[] { expOf(v1), expOf(v2), expOf(v3) };
		}
		ValueExpr[] exprs = new ValueExpr[ov.length + 3];
		exprs[0] = expOf(v1);
		exprs[1] = expOf(v2);
		exprs[2] = expOf(v3);
		int i = 3;
		for (Object o : ov) {
			exprs[i++] = expOf(o);
		}
		return exprs;
	}

	/**
	 * 克隆值表达式
	 * 
	 * <p>
	 * 只在克隆表关系时使用
	 * 
	 * @param fromSample
	 *            selfRef
	 * @param from
	 *            ref
	 * @param toSample
	 *            relation
	 * @param to
	 *            join
	 * 
	 * @return
	 */
	protected abstract ValueExpr clone(RelationRef fromSample,
			RelationRef from, RelationRef toSample, RelationRef to);

	/**
	 * 克隆值表达式
	 * 
	 * @param domain
	 *            表达式所在的关系域(从该域查找关系引用及构造查询模型)
	 * @param args
	 *            参数容器(从该参数查找参数定义)
	 * @return
	 */
	protected abstract ValueExpr clone(RelationRefDomain domain,
			ArgumentableDefine args);

	/**
	 * 检查当前表达式在目前关系域中是否合法
	 */
	final void checkDomain(RelationRefDomain domain) {
		this.visit(ExprDomainValidator.INSTANCE, domain);
	}

	public abstract void render(ISqlExprBuffer buffer, TableUsages usages);

	/**
	 * 是否非枚举参数
	 */
	protected boolean isNoneEnumArg() {
		return true;
	}
}