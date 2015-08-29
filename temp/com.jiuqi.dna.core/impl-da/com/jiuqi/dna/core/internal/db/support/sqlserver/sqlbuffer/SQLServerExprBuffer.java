package com.jiuqi.dna.core.internal.db.support.sqlserver.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.internal.db.support.sqlserver.SqlserverMetadata;
import com.jiuqi.dna.core.type.DateParser;

class SQLServerExprBuffer extends SqlExprBuffer {

	final SqlserverMetadata metadata;
	final SQLServerCommandBuffer command;

	SQLServerExprBuffer(SqlserverMetadata metadata,
			SQLServerCommandBuffer command) {
		this.metadata = metadata;
		this.command = command;
	}

	static final String quote(String name) {
		return "[" + name + "]";
	}

	@Override
	protected ISqlSelectBuffer newSubquery() {
		return new SQLServerSelectBuffer(this.metadata, this.command);
	}

	@Override
	protected void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q) {
		((SQLServerSelectBuffer) q).writeTo(sql, args);
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
	public ISqlExprBuffer loadDate(long val) {
		this.push("cast('" + DateParser.format(val, DateParser.FORMAT_DATE_TIME) + "' as datetime)");
		return this;
	}

	@Override
	public ISqlExprBuffer loadColumnRef(String ref, String column) {
		this.push(quote(ref) + "." + quote(column));
		return this;
	}

	@Override
	public ISqlExprBuffer loadVar(String name) {
		this.push("@" + name);
		return this;
	}

	private final void datepart(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("datepart(");
		this.append(type);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void dateadd(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("dateadd(");
		this.append(type);
		this.append(',');
		this.appendBuffer(i + 1, 0);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void datediff(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("datediff(");
		this.append(type);
		this.append(',');
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void cast(String type, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("cast(");
		this.appendBuffer(i, 0);
		this.append(" as ");
		this.append(type);
		this.append(")");
		this.endUpdate(PREC_MAX);
	}

	private static final String DATEPART_YEAR = "year";
	private static final String DATEPART_QUARTER = "quarter";
	private static final String DATEPART_MONTH = "month";
	private static final String DATEPART_WEEK = "week";
	private static final String DATEPART_DAYOFYEAR = "dayofyear";
	private static final String DATEPART_DAY = "day";
	private static final String DATEPART_WEEKDAY = "weekday";
	private static final String DATEPART_HOUR = "hour";
	private static final String DATEPART_MINUTE = "minute";
	private static final String DATEPART_SECOND = "second";
	private static final String DATEPART_MILLISECOND = "millisecond";

	public ISqlExprBuffer getdate() {
		this.push("getdate()");
		return this;
	}

	public ISqlExprBuffer year() {
		this.datepart(DATEPART_YEAR, 1);
		return this;
	}

	public ISqlExprBuffer quarter() {
		this.datepart(DATEPART_QUARTER, 1);
		return this;
	}

	public ISqlExprBuffer month() {
		this.datepart(DATEPART_MONTH, 1);
		return this;
	}

	public ISqlExprBuffer weekofyear() {
		this.datepart(DATEPART_WEEK, 1);
		return this;
	}

	public ISqlExprBuffer dayofyear() {
		this.datepart(DATEPART_DAYOFYEAR, 1);
		return this;
	}

	public ISqlExprBuffer dayofymonth() {
		this.datepart(DATEPART_DAY, 1);
		return this;
	}

	public ISqlExprBuffer dayofweek() {
		this.datepart(DATEPART_WEEKDAY, 1);
		return this;
	}

	public ISqlExprBuffer hour() {
		this.datepart(DATEPART_HOUR, 1);
		return this;
	}

	public ISqlExprBuffer minute() {
		this.datepart(DATEPART_MINUTE, 1);
		return this;
	}

	public ISqlExprBuffer second() {
		this.datepart(DATEPART_SECOND, 1);
		return this;
	}

	public ISqlExprBuffer millisecond() {
		this.datepart(DATEPART_MILLISECOND, 1);
		return this;
	}

	public ISqlExprBuffer yearadd() {
		this.dateadd(DATEPART_YEAR, 2);
		return this;
	}

	public ISqlExprBuffer quarteradd() {
		this.dateadd(DATEPART_QUARTER, 2);
		return this;
	}

	public ISqlExprBuffer monthadd() {
		this.dateadd(DATEPART_MONTH, 2);
		return this;
	}

	public ISqlExprBuffer weekadd() {
		this.dateadd(DATEPART_WEEK, 2);
		return this;
	}

	public ISqlExprBuffer dayadd() {
		this.dateadd(DATEPART_DAY, 2);
		return this;
	}

	public ISqlExprBuffer houradd() {
		this.dateadd(DATEPART_HOUR, 2);
		return this;
	}

	public ISqlExprBuffer minuteadd() {
		this.dateadd(DATEPART_MINUTE, 2);
		return this;
	}

	public ISqlExprBuffer secondadd() {
		this.dateadd(DATEPART_SECOND, 2);
		return this;
	}

	public ISqlExprBuffer yeardiff() {
		this.datediff(DATEPART_YEAR, 2);
		return this;
	}

	public ISqlExprBuffer quarterdiff() {
		this.datediff(DATEPART_QUARTER, 2);
		return this;
	}

	public ISqlExprBuffer monthdiff() {
		this.datediff(DATEPART_MONTH, 2);
		return this;
	}

	public ISqlExprBuffer weekdiff() {
		this.datediff(DATEPART_WEEK, 2);
		return this;
	}

	public ISqlExprBuffer daydiff() {
		this.datediff(DATEPART_DAY, 2);
		return this;
	}

	public ISqlExprBuffer hourdiff() {
		this.datediff(DATEPART_HOUR, 2);
		return this;
	}

	public ISqlExprBuffer minutediff() {
		this.datediff(DATEPART_MINUTE, 2);
		return this;
	}

	public ISqlExprBuffer seconddiff() {
		this.datediff(DATEPART_SECOND, 2);
		return this;
	}

	private final void dnafunc(String name, int count) {
		if (this.metadata.beforeYukon()) {
			this.call(this.metadata.schema.concat(".").concat(name), count);
		} else {
			this.call("dna.".concat(name), count);
		}
	}

	private final void userfunc(String name, int count) {
		if (this.metadata.beforeYukon()) {
			this.call(this.metadata.schema.concat(".").concat(name), count);
		} else {
			this.call("dbo.".concat(name), count);
		}
	}

	public ISqlExprBuffer isleapyear() {
		this.dnafunc("isleapyear", 1);
		return this;
	}

	public ISqlExprBuffer isleapmonth() {
		this.dnafunc("isleapmonth", 1);
		return this;
	}

	public ISqlExprBuffer isleapday() {
		this.dnafunc("isleapday", 1);
		return this;
	}

	public ISqlExprBuffer truncyear() {
		this.dnafunc("truncyear", 1);
		return this;
	}

	public ISqlExprBuffer truncquarter() {
		this.dnafunc("truncquarter", 1);
		return this;
	}

	public ISqlExprBuffer truncmonth() {
		this.dnafunc("truncmonth", 1);
		return this;
	}

	public ISqlExprBuffer truncweek() {
		this.dnafunc("truncweek", 1);
		return this;
	}

	public ISqlExprBuffer truncday() {
		this.dnafunc("truncday", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer ceil() {
		this.call("ceiling", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer lg() {
		this.call("log10", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer ln() {
		this.call("log", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer chr() {
		this.call("char", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer nchr() {
		this.call("nchar", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer trim() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("ltrim(rtrim(");
		this.appendBuffer(i, 0);
		this.append("))");
		this.endUpdate(PREC_MAX);
		return this;
	}

	private final void adjustSchema() {
		if (this.metadata.beforeYukon()) {
			this.append(this.metadata.user.concat("."));
		} else {
			this.append("dna.");
		}
	}

	public ISqlExprBuffer lpad(int paramCount) {
		if (paramCount == 3) {
			this.dnafunc("lpad", 3);
		} else {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.adjustSchema();
			this.append("lpad(");
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(",default)");
			this.endUpdate(PREC_MAX);
		}
		return this;
	}

	public ISqlExprBuffer rpad(int paramCount) {
		if (paramCount == 3) {
			this.dnafunc("rpad", 3);
		} else {
			int i = this.count() - 2;
			this.beginUpdate(i);
			this.adjustSchema();
			this.append("rpad(");
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(",default)");
			this.endUpdate(PREC_MAX);
		}
		return this;
	}

	public ISqlExprBuffer indexof(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("charindex(");
		this.appendBuffer(i + 1, 0);
		this.append(',');
		this.appendBuffer(i, 0);
		if (paramCount > 2) {
			this.append(',');
			this.appendBuffer(i + 2, 0);
		}
		this.append(')');
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer len() {
		this.dnafunc("length", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.coalesceEmptyStr(i);
		for (int j = i + 1; j < this.count(); j++) {
			this.append('+');
			this.coalesceEmptyStr(j);
		}
		this.endUpdate(PREC_ADD);
		return this;
	}

	private final ISqlExprBuffer substr0(int paramCount) {
		if (paramCount > 2) {
			this.call("substring", paramCount);
		} else {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("substring(");
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(",len(");
			this.appendBuffer(i, 0);
			this.append("))");
			this.endUpdate(PREC_MAX);
		}
		return this;
	}

	@Override
	public ISqlExprBuffer substr(int paramCount) {
		return this.substr0(paramCount);
	}

	public ISqlExprBuffer bin_substr(int paramCount) {
		return this.substr0(paramCount);
	}

	public ISqlExprBuffer bin_concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.coalesceEmptyBin(i);
		for (int j = i + 1; j < this.count(); j++) {
			this.append('+');
			this.coalesceEmptyBin(j);
		}
		this.endUpdate(PREC_ADD);
		return this;
	}

	// HCL ¼õ¶Ì
	final void coalesceEmptyBin(int i) {
		this.append("coalesce(");
		this.appendBuffer(i, PREC_MAX);
		this.append(", 0x)");
	}

	public ISqlExprBuffer bin_len() {
		this.call("datalength", 1);
		return this;
	}

	public ISqlExprBuffer new_recid() {
		// CORE2.5
		this.push("newid()");
		return this;
	}

	public ISqlExprBuffer rowcount() {
		this.push("@@rowcount");
		return this;
	}

	public ISqlExprBuffer hexstr() {
		this.dnafunc("hexstr", 1);
		return this;
	}

	public ISqlExprBuffer to_char() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("cast(");
		this.appendBuffer(i, 0);
		this.append(" as varchar(max))");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer numberstr() {
		this.cast("varchar", 1);
		return this;
	}
	
	public ISqlExprBuffer datestr() {
		this.call("dna.datestr", 2);
		return this;
	}
	
	public ISqlExprBuffer to_int() {
		this.cast("int", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer userfunction(String function, int paramCount) {
		this.userfunc(function, paramCount);
		return this;
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