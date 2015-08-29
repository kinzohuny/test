package com.jiuqi.dna.core.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Formatter;

import com.jiuqi.dna.core.def.query.SQLFunc;

/**
 * 代码追加器
 */
public final class CodeBuilder extends ExprVisitor<Object> implements
		Closeable, Flushable {

	private final Appendable out;
	private final Formatter formatter;
	private int indent = 0;
	private boolean needIndent;

	/**
	 * 构造函数
	 * 
	 * @param out
	 *            输出接口
	 */
	public CodeBuilder(Appendable out) {
		if (out == null) {
			throw new NullPointerException();
		}
		this.out = out;
		this.formatter = new Formatter(out);
	}

	/**
	 * 构造函数
	 * 
	 * @param out
	 *            输出流
	 */
	public CodeBuilder(OutputStream out) {
		this(new OutputStreamWriter(out));
	}

	/**
	 * 构造函数
	 * 
	 * @param outFile
	 *            输出文件
	 * @throws FileNotFoundException
	 */
	public CodeBuilder(File outFile) throws FileNotFoundException {
		this(new FileOutputStream(outFile));

	}

	/**
	 * 构造函数
	 * 
	 * @param outFileName
	 *            输出文件名
	 * @throws FileNotFoundException
	 */
	public CodeBuilder(String outFileName) throws FileNotFoundException {
		this(new File(outFileName));
	}

	/**
	 * 关闭输出流
	 * 
	 * @throws IOException
	 */
	public final void flush() throws IOException {
		if (this.out instanceof Flushable) {
			((Flushable) this.out).flush();
		}
	}

	public final void close() throws IOException {
		if (this.out instanceof Closeable) {
			((Closeable) this.out).close();
		}
	}

	/**
	 * 增加一个缩进
	 * 
	 * @return 增加后的缩进个数
	 */
	public final CodeBuilder pi() {
		this.indent++;
		return this;
	}

	/**
	 * 减少一个缩进
	 * 
	 * @return 减少后的缩进个数
	 */
	public final CodeBuilder ri() {
		this.indent--;
		return this;
	}

	/**
	 * 追加缩进
	 * 
	 * @throws IOException
	 */
	private final void doIndent() throws IOException {
		if (this.needIndent) {
			for (int i = 0; i < this.indent; i++) {
				this.out.append('\t');
			}
			this.needIndent = false;
		}
	}

	/**
	 * 追加带要替换值的一行代码
	 * 
	 * @param linefmt
	 *            要追加的一行代码
	 * @param args
	 *            替换的值
	 * @return
	 * @throws IOException
	 */
	public final CodeBuilder appendLine(String linefmt, Object... args)
			throws IOException {
		this.doIndent();
		this.formatter.format(linefmt, args);
		this.appendLine();
		return this;
	}

	/**
	 * 追加一行固定代码
	 */
	public final CodeBuilder appendLine(String line) throws IOException {
		this.doIndent();
		this.out.append(line);
		this.appendLine();
		return this;
	}

	/**
	 * 追加只含一个字符的一行代码
	 */
	public final CodeBuilder appendLine(char c) throws IOException {
		this.doIndent();
		this.out.append(c);
		this.appendLine();
		return this;
	}

	/**
	 * 追加含替换值的代码
	 * 
	 * @param fmt
	 *            追加的代码
	 * @param args
	 *            替换值
	 * @return
	 * @throws IOException
	 */
	public final CodeBuilder append(String fmt, Object... args)
			throws IOException {
		this.doIndent();
		this.formatter.format(fmt, args);
		return this;
	}

	/**
	 * 追加代码
	 */
	public final CodeBuilder append(String str) throws IOException {
		this.doIndent();
		this.out.append(str);
		return this;
	}

	/**
	 * 追加字符
	 */
	public final CodeBuilder append(char c) throws IOException {
		this.doIndent();
		this.out.append(c);
		return this;
	}

	/**
	 * 追加一个回车换行
	 */
	public final CodeBuilder appendLine() throws IOException {
		this.out.append("\r\n");
		this.needIndent = true;
		return this;
	}

	final void importClass(String packageName, String classSimpleName)
			throws IOException {
		if (classSimpleName == null || classSimpleName.length() == 0) {
			throw new NullPointerException();
		}
		if (packageName == null || packageName.length() == 0) {
			this.append("import ").append(classSimpleName).appendLine(';');
		} else {
			this.append("import ").append(packageName).append('.').append(classSimpleName).appendLine(';');
		}
	}

	final void importClass(Class<?> clz) throws IOException {
		this.append("import ").append(clz.getName()).appendLine(';');
	}

	private final void buildCommonConstExpr(ConstExpr expr) {
		try {
			this.append("ConstExpression.builder.expOf(");
			this.append(expr.getString());
			this.append(')');
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	private void adjustNotForConditionExpr(ConditionalExpr condition)
			throws IOException {
		if (condition.not) {
			this.append(".not()");
		}
	}

	public void visitArgumentRefExpr(ArgumentRefExpr expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitBooleanExpr(BooleanConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitByteExpr(ByteConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitBytesExpr(BytesConstExpr value, Object context) {
		try {
			this.append("ConstExpression.builder.expOf(new byte[]{");
			for (int i = 0; i < value.getBytes().length; i++) {
				if (i > 0) {
					this.append(", ");
				}
				this.append(Byte.toString(value.getBytes()[i]));
			}
			this.append("})");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitCombinedExpr(CombinedExpr expr, Object context) {
		try {
			expr.conditions[0].visit(this, context);
			this.appendLine();
			this.pi();
			this.append(".%s(", expr.and ? "and" : "or");
			for (int i = 1; i < expr.conditions.length; i++) {
				expr.conditions[i].visit(this, context);
				if (i < expr.conditions.length - 1) {
					this.append(',');
				}
				this.appendLine();
			}
			this.ri();
			this.append(")");
			this.adjustNotForConditionExpr(expr);
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitDateExpr(DateConstExpr value, Object context) {
		try {
			this.append("ConstExpression.builder.expOf(new Date(");
			this.append(Long.toString(value.getDate()));
			this.append("L))");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitDoubleExpr(DoubleConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitFloatExpr(FloatConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitGUIDExor(GUIDConstExpr value, Object context) {
		try {
			this.append("ConstExpression.builder.expOf(GUID.valueOf(\"");
			this.append(value.getString());
			this.append("\"))");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitHierarchyOperateExpr(HierarchyOperateExpr expr,
			Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitHierarchyPredicateExpr(HierarchyPredicateExpr expr,
			Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitIntExpr(IntConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitLongExpr(LongConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitNullExpr(NullExpr expr, Object context) {
		try {
			this.append("ValueExpression.NULL");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	private static final String SQLFUNC_CLASS_NAME = SQLFunc.class.getSimpleName();

	private static final void buildSQLFuncCallCode(CodeBuilder builder,
			OperateExpr operate, String funcCallName) throws IOException {
		builder.append(SQLFUNC_CLASS_NAME).append('.');
		builder.append(funcCallName);
		builder.append('(');
		for (int i = 0; i < operate.values.length; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			operate.values[i].visit(builder, null);
		}
		builder.append(')');
	}

	private static final void buildValueExprCallCode(CodeBuilder builder,
			OperateExpr operate, String callName) throws IOException {
		operate.values[0].visit(builder, null);
		builder.append(".%s(", callName);
		for (int i = 1; i < operate.values.length; i++) {
			if (i > 1) {
				builder.append(", ");
			}
			operate.values[i].visit(builder, null);
		}
		builder.append(')');
	}

	private void visitScalarFunction(OperateExpr expr, ScalarFunction function)
			throws IOException {
		switch (function) {
		case ABS:
			buildSQLFuncCallCode(this, expr, "xAbs");
			break;
		case ACOS:
			buildSQLFuncCallCode(this, expr, "xAcos");
			break;
		case ADD_DAY:
			buildSQLFuncCallCode(this, expr, "xAddDay");
			break;
		case ADD_HOUR:
			buildSQLFuncCallCode(this, expr, "xAddHour");
			break;
		case ADD_MINUTE:
			buildSQLFuncCallCode(this, expr, "xAddMinute");
			break;
		case ADD_MONTH:
			buildSQLFuncCallCode(this, expr, "xAddMonth");
			break;
		case ADD_QUARTER:
			buildSQLFuncCallCode(this, expr, "xAddQuarter");
			break;
		case ADD_SECOND:
			buildSQLFuncCallCode(this, expr, "xAddSecond");
			break;
		case ADD_WEEK:
			buildSQLFuncCallCode(this, expr, "xAddWeek");
			break;
		case ADD_YEAR:
			buildSQLFuncCallCode(this, expr, "xAddYear");
			break;
		case ASCII:
			buildSQLFuncCallCode(this, expr, "xAscii");
			break;
		case ASIN:
			buildSQLFuncCallCode(this, expr, "xAsin");
			break;
		case ATAN:
			buildSQLFuncCallCode(this, expr, "xAtan");
			break;
		case CEIL:
			buildSQLFuncCallCode(this, expr, "xCeil");
			break;
		case CHR:
			buildSQLFuncCallCode(this, expr, "xChr");
			break;
		case COLLATE_GBK:
			buildSQLFuncCallCode(this, expr, "xCollateGBK");
			break;
		case COS:
			buildSQLFuncCallCode(this, expr, "xCos");
			break;
		case DAY_DIFF:
			buildSQLFuncCallCode(this, expr, "xDayDiff");
			break;
		case DAY_OF:
			buildSQLFuncCallCode(this, expr, "xDayOf");
			break;
		case DAY_OF_WEEK:
			buildSQLFuncCallCode(this, expr, "xDayOfWeek");
			break;
		case DAY_OF_YEAR:
			buildSQLFuncCallCode(this, expr, "xDayOfYear");
			break;
		case EXP:
			buildSQLFuncCallCode(this, expr, "xExp");
			break;
		case FLOOR:
			buildSQLFuncCallCode(this, expr, "xFloor");
			break;
		case GETDATE:
			buildSQLFuncCallCode(this, expr, "xGetDate");
			break;
		case HOUR_OF:
			buildSQLFuncCallCode(this, expr, "xHourOf");
			break;
		case INDEXOF:
			buildSQLFuncCallCode(this, expr, "xIndexOf");
			break;
		case IS_LEAP_DAY:
			buildSQLFuncCallCode(this, expr, "xIsLeapDay");
			break;
		case IS_LEAP_MONTH:
			buildSQLFuncCallCode(this, expr, "xIsLeapMonth");
			break;
		case IS_LEAP_YEAR:
			buildSQLFuncCallCode(this, expr, "xIsLeapYear");
			break;
		case LEN:
			buildSQLFuncCallCode(this, expr, "xLen");
			break;
		case LG:
			buildSQLFuncCallCode(this, expr, "xLg");
			break;
		case LN:
			buildSQLFuncCallCode(this, expr, "xLn");
			break;
		case LOWER:
			buildSQLFuncCallCode(this, expr, "xLower");
			break;
		case LPAD:
			buildSQLFuncCallCode(this, expr, "xLpad");
			break;
		case LTRIM:
			buildSQLFuncCallCode(this, expr, "xLtrim");
			break;
		case MILLISECOND_OF:
			buildSQLFuncCallCode(this, expr, "xMillisecondOf");
			break;
		case MINUTE_OF:
			buildSQLFuncCallCode(this, expr, "xMinuteOf");
			break;
		case MONTH_DIFF:
			buildSQLFuncCallCode(this, expr, "xMonthDiff");
			break;
		case MONTH_OF:
			buildSQLFuncCallCode(this, expr, "xMonthOf");
			break;
		case NCHR:
			buildSQLFuncCallCode(this, expr, "xNchr");
			break;
		case NEW_RECID:
			buildSQLFuncCallCode(this, expr, "xNewRecid");
			break;
		case POWER:
			buildSQLFuncCallCode(this, expr, "xPower");
			break;
		case QUARTER_DIFF:
			buildSQLFuncCallCode(this, expr, "xQuarterDiff");
			break;
		case QUARTER_OF:
			buildSQLFuncCallCode(this, expr, "xQuarterOf");
			break;
		case REPLACE:
			buildSQLFuncCallCode(this, expr, "xReplace");
			break;
		case ROUND:
			buildSQLFuncCallCode(this, expr, "xRound");
			break;
		case RPAD:
			buildSQLFuncCallCode(this, expr, "xRpad");
			break;
		case RTRIM:
			buildSQLFuncCallCode(this, expr, "xRtrim");
			break;
		case SECOND_OF:
			buildSQLFuncCallCode(this, expr, "xSecondOf");
			break;
		case SIGN:
			buildSQLFuncCallCode(this, expr, "xSign");
			break;
		case SIN:
			buildSQLFuncCallCode(this, expr, "xSin");
			break;
		case SQRT:
			buildSQLFuncCallCode(this, expr, "xSqrt");
			break;
		case SUBSTR:
			buildSQLFuncCallCode(this, expr, "xSubstr");
			break;
		case TAN:
			buildSQLFuncCallCode(this, expr, "xTan");
			break;
		case TO_CHAR:
			buildSQLFuncCallCode(this, expr, "xToChar");
			break;
		case TO_INT:
			buildSQLFuncCallCode(this, expr, "xToInt");
			break;
		case TRIM:
			buildSQLFuncCallCode(this, expr, "xTrim");
			break;
		case TRUNC_DAY:
			buildSQLFuncCallCode(this, expr, "xTruncDay");
			break;
		case TRUNC_MONTH:
			buildSQLFuncCallCode(this, expr, "xTruncMonth");
			break;
		case TRUNC_QUARTER:
			buildSQLFuncCallCode(this, expr, "xTruncQuarter");
			break;
		case TRUNC_WEEK:
			buildSQLFuncCallCode(this, expr, "xTruncWeek");
			break;
		case TRUNC_YEAR:
			buildSQLFuncCallCode(this, expr, "xTruncYear");
			break;
		case UPPER:
			buildSQLFuncCallCode(this, expr, "xUpper");
			break;
		case WEEK_DIFF:
			buildSQLFuncCallCode(this, expr, "xWeekDiff");
			break;
		case WEEK_OF:
			buildSQLFuncCallCode(this, expr, "xWeekOf");
			break;
		case YEAR_DIFF:
			buildSQLFuncCallCode(this, expr, "xYearDiff");
			break;
		case YEAR_OF:
			buildSQLFuncCallCode(this, expr, "xYearOf");
			break;
		default:
			throw new UnsupportedOperationException(function.toString());
		}
	}

	private final void visitiAggregateFunction(OperateExpr expr,
			AggregateFunction aggregate) throws IOException {
		switch (aggregate) {
		case AVG_ALL:
			buildSQLFuncCallCode(this, expr, "xAvg");
			break;
		case AVG_DISTINCT:
			buildSQLFuncCallCode(this, expr, "xAvgDistinct");
			break;
		case COUNT_ALL:
			buildSQLFuncCallCode(this, expr, "xCount");
			break;
		case COUNT_ASTERISK:
			buildSQLFuncCallCode(this, expr, "xCount");
			break;
		case COUNT_DISTINCT:
			buildSQLFuncCallCode(this, expr, "xCountDistinct");
			break;
		case GROUPING:
			buildSQLFuncCallCode(this, expr, "xGrouping");
			break;
		case MAX:
			buildSQLFuncCallCode(this, expr, "xMax");
			break;
		case MIN:
			buildSQLFuncCallCode(this, expr, "xMin");
			break;
		case SUM_ALL:
			buildSQLFuncCallCode(this, expr, "xSum");
			break;
		case SUM_DISTINCT:
			buildSQLFuncCallCode(this, expr, "xSumDistinct");
			break;
		default:
			throw new UnsupportedOperationException(aggregate.toString());
		}
	}

	private final void visitCommon(OperateExpr expr, CommonOperator operator)
			throws IOException {
		switch (operator) {
		case ABUSOLUTE_ANCESTOR_RECID:
			buildValueExprCallCode(this, expr, "xAncestorRECIDOfLevel");
			break;
		case ADD:
			buildValueExprCallCode(this, expr, "xAdd");
			break;
		case BIN_CONCAT:
			buildValueExprCallCode(this, expr, "xBinConcat");
			break;
		case COALESCE:
			buildValueExprCallCode(this, expr, "xCoalesce");
			break;
		case DIV:
			buildValueExprCallCode(this, expr, "xDiv");
			break;
		case LEVEVL_OF:
			buildValueExprCallCode(this, expr, "xLevelOf");
			break;
		case MINUS:
			buildValueExprCallCode(this, expr, "xMinus");
			break;
		case MOD:
			buildValueExprCallCode(this, expr, "xMod");
			break;
		case MUL:
			buildValueExprCallCode(this, expr, "xMul");
			break;
		case PARENT_RECID:
			buildValueExprCallCode(this, expr, "xParentRECID");
			break;
		case RELATIVE_ANCESTOR_RECID:
			buildValueExprCallCode(this, expr, "xAncestorRECID");
			break;
		case SIMPLE_CASE:
			buildValueExprCallCode(this, expr, "xSimpleCase");
			break;
		case SUB:
			buildValueExprCallCode(this, expr, "xSub");
			break;
		case STR_CONCAT:
			buildValueExprCallCode(this, expr, "xStrConcat");
			break;
		default:
			throw new UnsupportedOperationException(operator.toString());
		}
	}

	public void visitOperateExpr(OperateExpr expr, Object context) {
		try {
			if (expr.operator instanceof ScalarFunction) {
				ScalarFunction function = (ScalarFunction) expr.operator;
				this.visitScalarFunction(expr, function);
			} else if (expr.operator instanceof CommonOperator) {
				CommonOperator operator = (CommonOperator) expr.operator;
				this.visitCommon(expr, operator);
			} else if (expr.operator instanceof AggregateFunction) {
				AggregateFunction aggregate = (AggregateFunction) expr.operator;
				this.visitiAggregateFunction(expr, aggregate);
			} else {
				throw new UnsupportedOperationException(expr.operator.toString());
			}
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitAnalyticFunctionExpr(AnalyticFunctionExpr expr,
			Object context) {
		// TODO
		throw Utils.notImplemented();
	}

	public void visitPredicateExpr(PredicateExpr expr, Object context) {
		try {
			switch (expr.predicate) {
			case EQUAL_TO:
				buildCode(this, expr, "xEq");
				break;
			case NOT_EQUAL_TO:
				buildCode(this, expr, "xnEq");
				break;
			case GREATER_THAN:
				buildCode(this, expr, "xGreater");
				break;
			case GREATER_THAN_OR_EQUAL_TO:
				buildCode(this, expr, "xGE");
				break;
			case LESS_THAN:
				buildCode(this, expr, "xLess");
				break;
			case LESS_THAN_OR_EQUAL_TO:
				buildCode(this, expr, "xLE");
				break;
			default:
				throw new UnsupportedOperationException("表达式[" + expr.predicate + "]不支持代码生成。");
			}
			this.adjustNotForConditionExpr(expr);
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	private static final void buildCode(CodeBuilder builder,
			PredicateExpr expr, String method) {
		try {
			expr.values[0].visit(builder, null);
			builder.append('.');
			builder.append(method);
			builder.append('(');
			for (int i = 1; i < expr.values.length; i++) {
				if (i > 1) {
					builder.append(",");
				}
				expr.values[i].visit(builder, null);
			}
			builder.append(')');
		} catch (IOException e) {
			Utils.tryThrowException(e);
		}
	}

	public void visitSearchedCase(SearchedCaseExpr expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitSelectColumnRef(SelectColumnRefImpl expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitShortExpr(ShortConstExpr value, Object context) {
		this.buildCommonConstExpr(value);
	}

	public void visitStringExpr(StringConstExpr value, Object context) {
		try {
			this.append("ConstExpression.builder.expOf(\"");
			this.append(value.getString());
			this.append("\")");
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitSubQueryExpr(SubQueryExpr expr, Object context) {
		throw new UnsupportedOperationException();
	}

	public void visitTableFieldRef(TableFieldRefImpl fieldRef, Object context) {
		// 代码生成只用于表关系的代码生成,故tableRef只可能是TableSelfRef类型或TableRelationDefineImpl类型!
		try {
			if (fieldRef.tableRef instanceof TableSelfRef) {
				this.append("%s.expOf(this.%s)", DeclaratorBuilderImpl.THIS_TABLE, DeclaratorBuilderImpl.declareNameOf(fieldRef.field));
			} else {
				TableRelationDefineImpl rel = (TableRelationDefineImpl) fieldRef.tableRef;
				this.append("this.%s.expOf(%s)", DeclaratorBuilderImpl.declareNameOf(rel), DeclaratorBuilderImpl.declareNameOf(rel.target) + "." + DeclaratorBuilderImpl.declareNameOf(fieldRef.field));
			}
		} catch (Throwable t) {
			throw Utils.tryThrowException(t);
		}
	}

	public void visitQueryColumnRef(QueryColumnRefExpr expr, Object context) {
	}
}