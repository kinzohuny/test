package com.jiuqi.dna.core.internal.db.support.oracle.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.impl.AnalyticFunctionExpr;
import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.Bound;
import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.WindowType;
import com.jiuqi.dna.core.impl.ContextVariableIntl;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.type.DateParser;

class OracleExprBuffer extends SqlExprBuffer {

	private String targetAlias;
	private String alternateAlias;

	static final String quote(String name) {
		return "\"" + name + "\"";
	}

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	@Override
	protected ISqlSelectBuffer newSubquery() {
		OracleSelectBuffer sub = new OracleSelectBuffer();
		sub.replace(this.targetAlias, this.alternateAlias);
		return sub;
	}

	@Override
	protected void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q) {
		((OracleSelectBuffer) q).writeTo(sql, args);
	}

	@Override
	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("hextoraw('");
		this.bytesToBuffer(val);
		this.append("')");
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer loadDate(long val) {
		this.push("timestamp'" + DateParser.format(val, DateParser.FORMAT_DATE_TIME) + "'");
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

	@Override
	public ISqlExprBuffer loadVar(String name) {
		return super.loadVar(quote(name));
	}

	@Override
	public ISqlExprBuffer mod() {
		this.call("mod", 2);
		return this;
	}

	@SuppressWarnings("unused")
	private final void add_interval(int paramCount, String type) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.appendBuffer(i, PREC_ADD);
		this.append("+numtodsinterval(");
		this.appendBuffer(i + 1, 0);
		this.append(",'");
		this.append(type);
		this.append("')");
		this.endUpdate(PREC_ADD);
	}

	@SuppressWarnings("unused")
	private final void extract(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("extract(");
		this.append(type);
		this.append(" from ");
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	@SuppressWarnings("unused")
	private final void trunc(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("trunc(");
		this.appendBuffer(i, 0);
		this.append(",'");
		this.append(type);
		this.append("')");
		this.endUpdate(PREC_MAX);
	}

	public ISqlExprBuffer getdate() {
		this.push("systimestamp");
		return this;
	}

	public ISqlExprBuffer year() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("extract(year from ");
			this.appendBuffer(i, 0);
			this.append(')');
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.year", 1);
		}
		return this;
	}

	public ISqlExprBuffer quarter() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("ceil(extract(month from ");
			this.appendBuffer(i, 0);
			this.append(")/3)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.quarter", 1);
		}
		return this;
	}

	public ISqlExprBuffer month() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("extract(month from ");
			this.appendBuffer(i, 0);
			this.append(')');
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.month", 1);
		}
		return this;
	}

	public ISqlExprBuffer weekofyear() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("(trunc(");
			this.appendBuffer(i, 0);
			this.append(",'d')-trunc(trunc(");
			this.appendBuffer(i, 0);
			this.append(",'y'),'d'))/7+1");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.weekofyear", 1);
		}
		return this;
	}

	public ISqlExprBuffer dayofyear() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("trunc(");
			this.appendBuffer(i, 0);
			this.append(",'dd')-trunc(");
			this.appendBuffer(i, 0);
			this.append(",'y')+1");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.dayofyear", 1);
		}
		return this;
	}

	public ISqlExprBuffer dayofymonth() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("extract(day from ");
			this.appendBuffer(i, 0);
			this.append(')');
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.dayofmonth", 1);
		}
		return this;
	}

	public ISqlExprBuffer dayofweek() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("trunc(");
			this.appendBuffer(i, 0);
			this.append(",'dd')-trunc(");
			this.appendBuffer(i, 0);
			this.append(",'d')+1");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.dayofweek", 1);
		}
		return this;
	}

	public ISqlExprBuffer hour() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("extract(hour from ");
			this.appendBuffer(i, 0);
			this.append(')');
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.hour", 1);
		}
		return this;
	}

	public ISqlExprBuffer minute() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("extract(minute from ");
			this.appendBuffer(i, 0);
			this.append(')');
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.minute", 1);
		}
		return this;
	}

	public ISqlExprBuffer second() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("floor(extract(second from ");
			this.appendBuffer(i, 0);
			this.append("))");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.second", 1);
		}
		return this;
	}

	public ISqlExprBuffer millisecond() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("to_number(to_char(");
			this.appendBuffer(i, 0);
			this.append(",'ff3'))");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.millisecond", 1);
		}
		return this;
	}

	public ISqlExprBuffer yearadd() {
		this.call("dna.yearadd", 2);
		return this;
	}

	public ISqlExprBuffer quarteradd() {
		this.call("dna.quarteradd", 2);
		return this;
	}

	public ISqlExprBuffer monthadd() {
		this.call("dna.monthadd", 2);
		return this;
	}

	public ISqlExprBuffer weekadd() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append("+numtodsinterval(7*(");
			this.appendBuffer(i + 1, 0);
			this.append("),'day')");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.weekadd", 2);
		}
		return this;
	}

	public ISqlExprBuffer dayadd() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append("+numtodsinterval(");
			this.appendBuffer(i + 1, 0);
			this.append(",'day')");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.dayadd", 2);
		}
		return this;
	}

	public ISqlExprBuffer houradd() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append("+numtodsinterval(");
			this.appendBuffer(i + 1, 0);
			this.append(",'hour')");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.houradd", 2);
		}
		return this;
	}

	public ISqlExprBuffer minuteadd() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append("+numtodsinterval(");
			this.appendBuffer(i + 1, 0);
			this.append(",'minute')");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.minuteadd", 2);
		}
		return null;
	}

	public ISqlExprBuffer secondadd() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.appendBuffer(i, 0);
			this.append("+numtodsinterval(");
			this.appendBuffer(i + 1, 0);
			this.append(",'second')");
			this.endUpdate(PREC_ADD);
		} else {
			this.call("dna.secondadd", 2);
		}
		return this;
	}

	public ISqlExprBuffer yeardiff() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.append("floor(months_between(trunc(");
			this.appendBuffer(i + 1, 0);
			this.append(",'y'),trunc(");
			this.appendBuffer(i, 0);
			this.append(",'y'))/12)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.yeardiff", 2);
		}
		return this;
	}

	public ISqlExprBuffer quarterdiff() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.append("floor(months_between(trunc(");
			this.appendBuffer(i + 1, 0);
			this.append(",'q'),trunc(");
			this.appendBuffer(i, 0);
			this.append(",'q'))/3)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.quarterdiff", 2);
		}
		return this;
	}

	public ISqlExprBuffer monthdiff() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.append("months_between(trunc(");
			this.appendBuffer(i + 1, 0);
			this.append(",'mm'),trunc(");
			this.appendBuffer(i, 0);
			this.append(",'mm'))");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.monthdiff", 2);
		}
		return this;
	}

	public ISqlExprBuffer weekdiff() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.append("trunc((trunc(");
			this.appendBuffer(i + 1, 0);
			this.append(",'d')-trunc(");
			this.appendBuffer(i, 0);
			this.append(",'d'))/7)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.weekdiff", 2);
		}
		return this;
	}

	public ISqlExprBuffer daydiff() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.append("(trunc(");
			this.appendBuffer(i + 1, 0);
			this.append(",'dd')-trunc(");
			this.appendBuffer(i, 0);
			this.append(",'dd'))");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.daydiff", 2);
		}
		return this;
	}

	public ISqlExprBuffer hourdiff() {
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer minutediff() {
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer seconddiff() {
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer isleapyear() {
		this.call("dna.isleapyear", 1);
		return this;
	}

	public ISqlExprBuffer isleapmonth() {
		this.call("dna.isleapmonth", 1);
		return this;
	}

	public ISqlExprBuffer isleapday() {
		this.call("dna.isleapday", 1);
		return this;
	}

	public ISqlExprBuffer truncyear() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("cast(trunc(cast(");
			this.appendBuffer(i, 0);
			this.append(" as date),'y') as timestamp)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.truncyear", 1);
		}
		return this;
	}

	public ISqlExprBuffer truncquarter() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("cast(trunc(cast(");
			this.appendBuffer(i, 0);
			this.append(" as date),'q') as timestamp)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.truncquarter", 1);
		}
		return this;
	}

	public ISqlExprBuffer truncmonth() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("cast(trunc(cast(");
			this.appendBuffer(i, 0);
			this.append(" as date),'mm') as timestamp)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.truncmonth", 1);
		}
		return this;
	}

	public ISqlExprBuffer truncweek() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("cast(trunc(cast(");
			this.appendBuffer(i, 0);
			this.append(" as date),'d') as timestamp)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.truncweek", 1);
		}
		return this;
	}

	public ISqlExprBuffer truncday() {
		if (ContextVariableIntl.isOracleUsingNativeFunc()) {
			int i = this.count() - 1;
			this.beginUpdate(i);
			this.append("cast(trunc(cast(");
			this.appendBuffer(i, 0);
			this.append(" as date),'dd') as timestamp)");
			this.endUpdate(PREC_MAX);
		} else {
			this.call("dna.truncday", 1);
		}
		return this;
	}

	@Override
	public ISqlExprBuffer lg() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("log(10,");
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
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

	public ISqlExprBuffer len() {
		this.call("length", 1);
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

	public ISqlExprBuffer bin_substr(int paramCount) {
		this.call("utl_raw.substr", paramCount);
		return this;
	}

	public ISqlExprBuffer bin_concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		int j = i;
		int deep = 0;
		int count = this.count();
		while (j < count) {
			this.append("utl_raw.concat(");
			deep++;
			int c = j + 11;
			if (c > count) {
				c = count;
			}
			this.appendBuffer(j++, 0);
			while (j < c) {
				this.append(',');
				this.appendBuffer(j++, 0);
			}
			if (j < count) {
				this.append(',');
			}
			if (count - j == 1) {
				this.appendBuffer(j++, 0);
			}
		}
		for (; deep > 0; deep--) {
			this.append(')');
		}
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer bin_len() {
		this.call("utl_raw.length", 1);
		return this;
	}

	public ISqlExprBuffer hexstr() {
		this.call("rawtohex", 1);
		return this;
	}

	public ISqlExprBuffer new_recid() {
		this.call("dna.new_recid", 0);
		return this;
	}

	public ISqlExprBuffer rowcount() {
		this.push("SQL%ROWCOUNT");
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

	public ISqlExprBuffer to_char() {
		this.call("to_char", 1);
		return this;
	}
	
	public ISqlExprBuffer numberstr() {
		this.call("to_char", 1);
		return this;
	}
	
	public ISqlExprBuffer datestr() {
		this.call("to_char", 2);
		return this;
	}

	@Override
	public ISqlExprBuffer collate_gbk() {
		this.call("dna.collate_gbk", 1);
		return this;
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
			this.appendBuffer(i, 0);
			if (pof) {
				this.append(" preceding");
			} else {
				this.append(" following");
			}
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