package com.jiuqi.dna.core.internal.da.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.Bound;
import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.WindowType;
import com.jiuqi.dna.core.impl.DataTypeInternal;
import com.jiuqi.dna.core.impl.Utils;
import com.jiuqi.dna.core.type.DateParser;

public abstract class SqlExprBuffer extends SqlBuffer implements ISqlExprBuffer {

	public static final char PREC_MAX = 64;
	public static final char PREC_ADD = 6;
	public static final char PREC_MUL = 7;
	public static final char PREC_NEG = 8;
	public static final char PREC_AND = 3;
	public static final char PREC_OR = 2;
	public static final char PREC_NOT = 4;
	public static final char PREC_CMP = 5;

	private char[][] list;
	private int count;
	private ArrayList<ISqlSelectBuffer> subquery;
	private int index;
	private SqlStringBuffer buffer;
	private int subqueryIndex;
	protected ArrayList<ParameterPlaceholder> args;

	public SqlExprBuffer() {
		this.buffer = new SqlStringBuffer();
		this.list = new char[4][];
		this.index = -1;
	}

	protected int count() {
		return this.count;
	}

	protected void ensureCount(int count) {
		if (this.list.length < count) {
			int newCount = (this.list.length * 3) / 2 + 1;
			if (newCount < count) {
				newCount = count;
			}
			char[][] arr = new char[newCount][];
			System.arraycopy(this.list, 0, arr, 0, this.list.length);
			this.list = arr;
		}
	}

	protected void beginUpdate(int i) {
		if (this.index != -1) {
			throw new IllegalStateException();
		}
		this.index = i;
		this.buffer.clear();
		this.buffer.append('\0');
		this.subqueryIndex = -1;
	}

	protected void endUpdate(int prec) {
		this.buffer.set(0, (char) prec);
		char[] buffer = new char[this.buffer.size()];
		this.buffer.writeTo(buffer, 0);
		this.count = this.index + 1;
		this.ensureCount(this.count);
		this.list[this.index] = buffer;
		this.index = -1;
		if (this.subqueryIndex != -1) {
			int size = this.subquery.size();
			while (size > this.subqueryIndex) {
				this.subquery.remove(--size);
			}
		}
	}

	protected void push(String s) {
		this.beginUpdate(this.count);
		this.append(s);
		this.endUpdate(PREC_MAX);
	}

	protected int getPrec(int i) {
		return this.list[i][0];
	}

	protected void append(String s) {
		this.buffer.append(s);
	}

	protected void append(char c) {
		this.buffer.append(c);
	}

	public ISqlExprBuffer load(long val) {
		this.push(Long.toString(val));
		return this;
	}

	public ISqlExprBuffer load(double val) {
		this.push(Double.toString(val));
		return this;
	}

	public ISqlExprBuffer load(boolean val) {
		this.push(val ? "1" : "0");
		return this;
	}

	protected static String escape(String s) {
		StringBuilder sb = null;
		int i = 0;
		int len = s.length();
		int j = 0;
		while (i < len) {
			char c = s.charAt(i);
			switch (c) {
			case '\n':
			case '\r':
				throw new IllegalArgumentException("字符串常量中不允许包含回车或者换行");
			case '\'':
				if (sb == null) {
					sb = new StringBuilder(s);
				}
				sb.replace(j, ++j, "''");
				break;
			}
			i++;
			j++;
		}
		return sb == null ? s : sb.toString();
	}

	public ISqlExprBuffer loadDate(long val) {
		this.push("'" + DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS) + "'");
		return this;
	}

	public ISqlExprBuffer loadStr(String val) {
		this.push("'" + escape(val) + "'");
		return this;
	}

	public ISqlExprBuffer loadVar(String name) {
		this.push(name);
		return this;
	}

	public ISqlExprBuffer loadParam(ParameterPlaceholder placeholder) {
		this.push("?");
		if (this.args == null) {
			this.args = new ArrayList<ParameterPlaceholder>();
		}
		this.args.add(placeholder);
		return this;
	}

	public ISqlExprBuffer loadParam(ParameterPlaceholder reserver,
			DataTypeInternal refer) {
		return this.loadParam(reserver);
	}

	public ISqlExprBuffer loadNull(DataTypeInternal type) {
		this.push("null");
		return this;
	}

	public ISqlExprBuffer loadColumnRef(String ref, String column) {
		this.push(ref + '.' + column);
		return this;
	}

	protected final void bytesToBuffer(byte[] val) {
		for (byte b : val) {
			int j = (b >>> 4) & 0x0f;
			j = j > 9 ? (j - 10 + 'A') : (j + '0');
			this.buffer.append((char) j);
			j = b & 0x0f;
			j = j > 9 ? (j - 10 + 'A') : (j + '0');
			this.buffer.append((char) j);
		}
	}

	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count);
		this.append('\'');
		this.bytesToBuffer(val);
		this.append('\'');
		this.endUpdate(PREC_MAX);
		return this;
	}

	protected abstract ISqlSelectBuffer newSubquery();

	protected abstract void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q);

	public ISqlSelectBuffer subquery() {
		if (this.subquery == null) {
			this.subquery = new ArrayList<ISqlSelectBuffer>();
		}
		ISqlSelectBuffer q = this.newSubquery();
		this.subquery.add(q);
		this.ensureCount(++this.count);
		char p = (char) (PREC_MAX + this.subquery.size());
		if (this.list[this.count - 1] == null) {
			this.list[this.count - 1] = new char[] { p };
		} else {
			this.list[this.count - 1][0] = p;
		}
		return q;
	}

	protected final void appendBuffer(int i, int prec) {
		char[] buffer = this.list[i];
		if (buffer[0] > PREC_MAX) {
			this.buffer.append('(');
			ISqlSelectBuffer q = this.subquery.get(buffer[0] - PREC_MAX - 1);
			if (this.args == null) {
				this.args = new ArrayList<ParameterPlaceholder>();
			}
			this.writeSubquery(this.buffer, this.args, q);
			this.buffer.append(')');
			if (i < this.subqueryIndex || this.subqueryIndex == -1) {
				this.subqueryIndex = i;
			}
		} else if (prec >= buffer[0]) {
			this.buffer.append('(');
			this.buffer.append(buffer, 1, buffer.length - 1);
			this.buffer.append(')');
		} else {
			this.buffer.append(buffer, 1, buffer.length - 1);
		}
	}

	protected final void binary(String op, int prec, int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		// 左结合性的二元运算，左操作数运算更为优先，所以左操作数运算优先级相当于+1
		this.appendBuffer(i, prec - 1);
		for (int j = i + 1; j < this.count; j++) {
			this.append(op);
			this.appendBuffer(j, prec);
		}
		this.endUpdate(prec);
	}

	protected final void unary(String op, int prec) {
		int i = this.count - 1;
		this.beginUpdate(i);
		this.append(op);
		this.appendBuffer(i, prec);
		this.endUpdate(prec);
	}

	protected final void call(String name, int count) {
		if (count == 0) {
			this.push(name + "()");
		} else {
			int i = this.count - count;
			this.beginUpdate(i);
			this.append(name);
			this.append('(');
			this.appendBuffer(i, 0);
			for (int j = i + 1; j < this.count; j++) {
				this.append(',');
				this.appendBuffer(j, 0);
			}
			this.append(')');
			this.endUpdate(PREC_MAX);
		}
	}

	public ISqlExprBuffer minus() {
		this.unary("-", PREC_NEG);
		return this;
	}

	public ISqlExprBuffer add(int paramCount) {
		this.binary("+", PREC_ADD, paramCount);
		return this;
	}

	public ISqlExprBuffer sub(int paramCount) {
		this.binary("-", PREC_ADD, paramCount);
		return this;
	}

	public ISqlExprBuffer mul(int paramCount) {
		this.binary("*", PREC_MUL, paramCount);
		return this;
	}

	public ISqlExprBuffer div(int paramCount) {
		this.binary("/", PREC_MUL, paramCount);
		return this;
	}

	public ISqlExprBuffer mod() {
		this.binary("%", PREC_MUL, 2);
		return this;
	}

	private final void distinct_func(String name, int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		this.append(name);
		this.append("(distinct ");
		this.appendBuffer(i, 0);
		for (int j = i + 1; j < this.count; j++) {
			this.append(',');
			this.appendBuffer(j, 0);
		}
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	public ISqlExprBuffer coalesce(int paramCount) {
		this.call("coalesce", paramCount);
		return this;
	}

	public ISqlExprBuffer simpleCase(int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		this.append("case ");
		this.appendBuffer(i, 0);
		int j = i + 1;
		for (int c = this.count - 1; j < c;) {
			this.append(" when ");
			this.appendBuffer(j++, 0);
			this.append(" then ");
			this.appendBuffer(j++, 0);
		}
		if (j < this.count) {
			this.append(" else ");
			this.appendBuffer(j, 0);
		}
		this.append(" end");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer searchedCase(int paramCount) {
		int i = this.count - paramCount;
		this.beginUpdate(i);
		this.append("case ");
		int j = i;
		for (int c = this.count - 1; j < c;) {
			this.append(" when ");
			this.appendBuffer(j++, 0);
			this.append(" then ");
			this.appendBuffer(j++, 0);
		}
		if (j < this.count) {
			this.append(" else ");
			this.appendBuffer(j, 0);
		}
		this.append(" end");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer lt() {
		this.binary("<", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer le() {
		this.binary("<=", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer gt() {
		this.binary(">", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer ge() {
		this.binary(">=", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer eq() {
		this.binary("=", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer ne() {
		this.binary("<>", PREC_CMP, 2);
		return this;
	}

	public ISqlExprBuffer and(int paramCount) {
		this.binary(" and ", PREC_AND, paramCount);
		return this;
	}

	public ISqlExprBuffer or(int paramCount) {
		this.binary(" or ", PREC_OR, paramCount);
		return this;
	}

	public ISqlExprBuffer not() {
		this.unary("not ", PREC_NOT);
		return this;
	}

	public ISqlExprBuffer predicate(SqlPredicate pred, int paramCount) {
		int i = this.count - paramCount;
		switch (pred) {
		case BETWEEN:
		case NOT_BETWEEN:
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append(pred == SqlPredicate.BETWEEN ? " between " : " not between ");
			this.appendBuffer(i + 1, 0);
			this.append(" and ");
			this.appendBuffer(i + 2, 0);
			this.endUpdate(PREC_CMP);
			break;
		case EXISTS:
			this.beginUpdate(i);
			this.append("exists");
			this.appendBuffer(i, 0);
			this.endUpdate(PREC_MAX);
			break;
		case IN:
		case NOT_IN:
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append(pred == SqlPredicate.IN ? " in " : " not in ");
			// if (paramCount == 2) {
			// this.appendBuffer(i + 1, PREC_MAX - 1);
			// } else {
			this.append('(');
			this.appendBuffer(i + 1, 0);
			for (int j = i + 2, c = this.count; j < c; j++) {
				this.append(',');
				this.appendBuffer(j, 0);
			}
			this.append(')');
			// }
			this.endUpdate(PREC_CMP);
			break;
		case LIKE:
		case NOT_LIKE:
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append(pred == SqlPredicate.LIKE ? " like " : " not like ");
			this.appendBuffer(i + 1, 0);
			if (paramCount > 2) {
				this.append(" escape ");
				this.appendBuffer(i + 2, 0);
			}
			this.endUpdate(PREC_CMP);
			break;
		case IS_NULL:
		case IS_NOT_NULL:
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append(pred == SqlPredicate.IS_NULL ? " is null" : " is not null");
			this.endUpdate(PREC_CMP);
			break;
		default:
			throw new IllegalStateException();
		}
		return this;
	}

	public void writeTo(SqlStringBuffer sql, List<ParameterPlaceholder> args) {
		char[] b = this.list[0];
		if (b[0] > PREC_MAX) {
			sql.append('(');
			this.writeSubquery(sql, args, this.subquery.get(b[0] - PREC_MAX - 1));
			sql.append(')');
		} else {
			sql.append(b, 1, b.length - 1);
		}
		if (args != null && this.args != null) {
			args.addAll(this.args);
		}
	}

	protected final void coalesceEmptyStr(int i) {
		this.append("coalesce(");
		this.appendBuffer(i, PREC_MAX);
		this.append(", '')");
	}

	public ISqlExprBuffer count(int paramCount, boolean distinct) {
		if (paramCount == 0) {
			this.push("count(1)");
		} else if (distinct) {
			this.distinct_func("count", paramCount);
		} else {
			this.call("count", paramCount);
		}
		return this;
	}

	public ISqlExprBuffer avg(boolean distinct) {
		if (distinct) {
			this.distinct_func("avg", 1);
		} else {
			this.call("avg", 1);
		}
		return this;
	}

	public ISqlExprBuffer sum(boolean distinct) {
		if (distinct) {
			this.distinct_func("sum", 1);
		} else {
			this.call("sum", 1);
		}
		return this;
	}

	public ISqlExprBuffer max() {
		this.call("max", 1);
		return this;
	}

	public ISqlExprBuffer min() {
		this.call("min", 1);
		return this;
	}

	public ISqlExprBuffer grouping() {
		this.call("grouping", 1);
		return this;
	}

	public ISqlExprBuffer sin() {
		this.call("sin", 1);
		return this;
	}

	public ISqlExprBuffer cos() {
		this.call("cos", 1);
		return this;
	}

	public ISqlExprBuffer tan() {
		this.call("tan", 1);
		return this;
	}

	public ISqlExprBuffer asin() {
		this.call("asin", 1);
		return this;
	}

	public ISqlExprBuffer acos() {
		this.call("acos", 1);
		return this;
	}

	public ISqlExprBuffer atan() {
		this.call("atan", 1);
		return this;
	}

	public ISqlExprBuffer exp() {
		this.call("exp", 1);
		return this;
	}

	public ISqlExprBuffer power() {
		this.call("power", 2);
		return this;
	}

	public ISqlExprBuffer lg() {
		this.call("lg", 1);
		return this;
	}

	public ISqlExprBuffer ln() {
		this.call("ln", 1);
		return this;
	}

	public ISqlExprBuffer sqrt() {
		this.call("sqrt", 1);
		return this;
	}

	public ISqlExprBuffer ceil() {
		this.call("ceil", 1);
		return this;
	}

	public ISqlExprBuffer floor() {
		this.call("floor", 1);
		return this;
	}

	public ISqlExprBuffer round(int paramCount) {
		this.call("round", paramCount);
		return this;
	}

	public ISqlExprBuffer sign() {
		this.call("sign", 1);
		return this;
	}

	public ISqlExprBuffer abs() {
		this.call("abs", 1);
		return this;
	}

	public ISqlExprBuffer chr() {
		// HCL Auto-generated method stub
		return null;
	}

	public ISqlExprBuffer nchr() {
		// HCL Auto-generated method stub
		return null;
	}

	public ISqlExprBuffer ascii() {
		// HCL Auto-generated method stub
		return null;
	}

	public ISqlExprBuffer lower() {
		this.call("lower", 1);
		return this;
	}

	public ISqlExprBuffer upper() {
		this.call("upper", 1);
		return this;
	}

	public ISqlExprBuffer ltrim() {
		this.call("ltrim", 1);
		return this;
	}

	public ISqlExprBuffer rtrim() {
		this.call("rtrim", 1);
		return this;
	}

	public ISqlExprBuffer trim() {
		this.call("trim", 1);
		return this;
	}

	public ISqlExprBuffer replace() {
		this.call("replace", 3);
		return this;
	}

	public ISqlExprBuffer substr(int paramCount) {
		this.call("substr", paramCount);
		return this;
	}

	public ISqlExprBuffer concat(int paramCount) {
		this.binary("||", PREC_ADD, paramCount);
		return this;
	}

	public ISqlExprBuffer collate_gbk() {
		return this;
	}

	public ISqlExprBuffer userfunction(String function, int paramCount) {
		this.call(function, paramCount);
		return null;
	}

	public ISqlExprBuffer analytic(String af, int partitionCount,
			int orderbyCount, int desc, WindowType type, Bound preceding,
			Bound following) {
		throw Utils.notImplemented();
	}
	
	public ISqlExprBuffer analytic(String af, int partitionCount,
			int orderbyCount, int desc) {
		throw Utils.notImplemented();
	}
}