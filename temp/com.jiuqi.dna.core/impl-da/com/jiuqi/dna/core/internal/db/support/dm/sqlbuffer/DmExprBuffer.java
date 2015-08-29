package com.jiuqi.dna.core.internal.db.support.dm.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.impl.AnalyticFunctionExpr;
import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.Bound;
import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.WindowType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.type.DateParser;

class DmExprBuffer extends SqlExprBuffer {

	private String targetAlias;
	private String alternateAlias;

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	static final String quote(String name) {
		return "\"" + name + "\"";
	}

	@Override
	public ISqlExprBuffer loadDate(long val) {
		this.push("timestamp'" + DateParser.format(val, DateParser.FORMAT_DATE_TIME) + "'");
		return this;
	}

	@Override
	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("0x");
		this.bytesToBuffer(val);
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer loadColumnRef(String ref, String column) {
		ref = quote(ref);
		if (this.targetAlias != null && ref.equals(this.targetAlias)) {
			ref = this.alternateAlias;
		}
		this.push(ref + "." + quote(column));
		return this;
	}

	public ISqlExprBuffer getdate() {
		this.call("getdate", 0);
		return this;
	}

	public ISqlExprBuffer year() {
		this.call("year", 1);
		return this;
	}

	public ISqlExprBuffer quarter() {
		this.call("quarter", 1);
		return this;
	}

	public ISqlExprBuffer month() {
		this.call("month", 1);
		return this;
	}

	public ISqlExprBuffer weekofyear() {
		// HOU Auto-generated method stub
		return null;
	}

	public ISqlExprBuffer dayofyear() {
		this.call("dayofyear", 1);
		return this;
	}

	public ISqlExprBuffer dayofymonth() {
		this.call("dayofmonth", 1);
		return this;
	}

	public ISqlExprBuffer dayofweek() {
		this.call("dayofweek", 1);
		return this;
	}

	public ISqlExprBuffer hour() {
		this.call("hour", 1);
		return this;
	}

	public ISqlExprBuffer minute() {
		this.call("minute", 1);
		return this;
	}

	public ISqlExprBuffer second() {
		this.call("second", 1);
		return this;
	}

	public ISqlExprBuffer millisecond() {
		this.datepart("MS");
		return this;
	}

	private final void datepart(String part) {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("datepart(");
		this.append(part);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void dateadd(String part) {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("dateadd(");
		this.append(part);
		this.append(',');
		this.appendBuffer(i + 1, 0);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	public ISqlExprBuffer yearadd() {
		this.dateadd("YY");
		return this;
	}

	public ISqlExprBuffer quarteradd() {
		this.dateadd("Q");
		return this;
	}

	public ISqlExprBuffer monthadd() {
		this.dateadd("M");
		return this;
	}

	public ISqlExprBuffer weekadd() {
		this.dateadd("WW");
		return this;
	}

	public ISqlExprBuffer dayadd() {
		this.dateadd("D");
		return this;
	}

	public ISqlExprBuffer houradd() {
		this.dateadd("HH");
		return this;
	}

	public ISqlExprBuffer minuteadd() {
		this.dateadd("MI");
		return this;
	}

	public ISqlExprBuffer secondadd() {
		this.dateadd("S");
		return this;
	}

	private final void datediff(String part) {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("datediff(");
		this.append(part);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	public ISqlExprBuffer yeardiff() {
		this.datediff("YY");
		return this;
	}

	public ISqlExprBuffer quarterdiff() {
		this.datediff("Q");
		return this;
	}

	public ISqlExprBuffer monthdiff() {
		this.datediff("M");
		return this;
	}

	public ISqlExprBuffer weekdiff() {
		this.datediff("WW");
		return this;
	}

	public ISqlExprBuffer daydiff() {
		this.datediff("D");
		return this;
	}

	public ISqlExprBuffer hourdiff() {
		this.datediff("HH");
		return this;
	}

	public ISqlExprBuffer minutediff() {
		this.datediff("MI");
		return this;
	}

	public ISqlExprBuffer seconddiff() {
		this.datediff("S");
		return this;
	}

	public ISqlExprBuffer isleapyear() {
		// HOU Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer isleapmonth() {
		// HOU Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer isleapday() {
		// HOU Auto-generated method stub
		return this;
	}

	private final void trunc(String type) {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("trunc(");
		this.appendBuffer(i, 0);
		this.append(",'");
		this.append(type);
		this.append("')");
		this.endUpdate(PREC_MAX);
	}

	public ISqlExprBuffer truncyear() {
		this.trunc("YEAR");
		return this;
	}

	public ISqlExprBuffer truncquarter() {
		this.trunc("q");
		return this;
	}

	public ISqlExprBuffer truncmonth() {
		this.trunc("month");
		return this;
	}

	public ISqlExprBuffer truncweek() {
		this.trunc("ww");
		return this;
	}

	public ISqlExprBuffer truncday() {
		this.trunc("ddd");
		return this;
	}

	public ISqlExprBuffer lpad(int paramCount) {
		this.call("lpad", paramCount);
		return this;
	}

	public ISqlExprBuffer rpad(int paramCount) {
		this.call("rpad", paramCount);
		return this;
	}

	public ISqlExprBuffer indexof(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("instr(");
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, 0);
		if (paramCount > 2) {
			this.append(',');
			this.appendBuffer(i + 2, 0);
		}
		this.append(')');
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer len() {
		this.call("char_length", 1);
		return this;
	}

	public ISqlExprBuffer bin_substr(int paramCount) {
		this.call("utl_raw.substr", paramCount);
		return this;
	}

	public ISqlExprBuffer bin_concat(int paramCount) {
		this.binary("+", PREC_ADD, paramCount);
		return this;
	}

	public ISqlExprBuffer bin_len() {
		this.call("utl_raw.length", 1);
		return this;
	}

	public ISqlExprBuffer new_recid() {
		this.call("sys_guid", 0);
		return this;
	}

	public ISqlExprBuffer rowcount() {
		// HOU Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer hexstr() {
		this.call("rawtohex", 1);
		return this;
	}

	public ISqlExprBuffer to_char() {
		this.call("to_char", 1);
		return this;
	}

	public ISqlExprBuffer numberstr() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("to_char(");
		this.appendBuffer(i, 0);
		this.append(",'TM9')");
		this.endUpdate(PREC_MAX);
		return this;
	}
	
	public ISqlExprBuffer datestr() {
		this.call("to_char", 2);
		return this;
	}
	
	public ISqlExprBuffer to_int() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("to_number(");
		this.appendBuffer(i, 0);
		this.append(",'9999999999')");
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer lg() {
		this.call("log10", 1);
		return this;
	}

	@Override
	protected ISqlSelectBuffer newSubquery() {
		DmSelectBuffer select = new DmSelectBuffer();
		select.replace(this.targetAlias, this.alternateAlias);
		return select;
	}

	@Override
	protected void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q) {
		((DmSelectBuffer) q).writeTo(sql, args);
	}

	@Override
	public ISqlExprBuffer analytic(String af, int partitionCount,
			int orderbyCount, int desc, WindowType type, Bound preceding,
			Bound following) {
		int i = this.count() - (1 + partitionCount + orderbyCount + (preceding != null ? preceding.valueCount() : 0) + (following != null ? following.valueCount() : 0));
		this.beginUpdate(i);
		this.append(af);
		this.append('(');
		this.appendBuffer(i, 0);
		this.append(")over(");
		if (partitionCount > 0) {
			this.append("partition by ");
			for (int pi = 0; pi < partitionCount; pi++) {
				if (pi > 0) {
					this.append(',');
				}
				this.appendBuffer(i + 1 + pi, 0);
			}
			this.append(' ');
		}
		if (orderbyCount > 0) {
			this.append("order by ");
			for (int oi = 0; oi < orderbyCount; oi++) {
				if (oi > 0) {
					this.append(',');
				}
				this.appendBuffer(i + 1 + partitionCount + oi, 0);
				if ((desc & (1 << oi)) != 0) {
					this.append(" desc");
				}
			}
			this.append(' ');
			if (type == WindowType.RANGE) {
				throw new IllegalArgumentException("range type is not supported.");
			}
			this.append(type.name().toLowerCase());
			this.append(' ');
			if (following != null) {
				this.append("between ");
				this.format(preceding, true, i + 1 + partitionCount + orderbyCount);
				this.append(" and ");
				this.format(following, false, i + 1 + partitionCount + orderbyCount + preceding.valueCount());
			} else {
				this.format(preceding, true, i + 1 + partitionCount + orderbyCount);
			}
		}
		this.append(')');
		this.endUpdate(PREC_MAX);
		return this;
	}

	private final void format(Bound bound, boolean pof, int i) {
		if (bound == AnalyticFunctionExpr.CURRENT_ROW) {
			this.append("current row");
		} else if (bound == AnalyticFunctionExpr.UNBOUNDED) {
			this.append("unbounded ");
			if (pof) {
				this.append("preceding");
			} else {
				this.append("following");
			}
		} else {
			throw new IllegalArgumentException("window is not supported.");
		}
	}
	
	@Override
	public ISqlExprBuffer analytic(String af, int partitionCount,
			int orderbyCount, int desc) {
		int i = this.count() - partitionCount - orderbyCount;
		this.beginUpdate(i);
		this.append(af);
		this.append('(');
		this.append(")over(");
		if (partitionCount > 0) {
			this.append("partition by ");
			for (int pi = 0; pi < partitionCount; pi++) {
				if (pi > 0) {
					this.append(',');
				}
				this.appendBuffer(i + pi, 0);
			}
			this.append(' ');
		}
		if (orderbyCount > 0) {
			this.append("order by ");
			for (int oi = 0; oi < orderbyCount; oi++) {
				if (oi > 0) {
					this.append(',');
				}
				this.appendBuffer(i + partitionCount + oi, 0);
				if ((desc & (1 << oi)) != 0) {
					this.append(" desc");
				}
			}
		}
		this.append(')');
		this.endUpdate(PREC_MAX);
		return this;
	}

}