package com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.Bound;
import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.WindowType;
import com.jiuqi.dna.core.impl.DataTypeInternal;
import com.jiuqi.dna.core.impl.NullType;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.type.DateParser;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public class DB2ExprBuffer extends SqlExprBuffer {

	static final String quote(String name) {
		return "\"" + name + "\"";
	}

	@Override
	public final DB2ExprBuffer loadNull(DataTypeInternal type) {
		if (type == null || type == NullType.TYPE) {
			super.loadNull(null);
		} else {
			type.detect(nullRender, this);
		}
		return this;
	}

	private static final TypeDetectorBase<Object, DB2ExprBuffer> nullRender = new TypeDetectorBase<Object, DB2ExprBuffer>() {

		@Override
		public Object inBoolean(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as smallint)");
			return null;
		}

		@Override
		public Object inByte(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as smallint)");
			return null;
		}

		@Override
		public Object inInt(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as int)");
			return null;
		}

		@Override
		public Object inLong(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as bigint)");
			return null;
		}

		@Override
		public Object inNumeric(DB2ExprBuffer buffer, int precision, int scale)
				throws Throwable {
			buffer.push("cast(null as int)");
			return null;
		}

		@Override
		public Object inFloat(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as real)");
			return null;
		}

		@Override
		public Object inDouble(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as double)");
			return null;
		}

		@Override
		public Object inString(DB2ExprBuffer buffer, SequenceDataType type)
				throws Throwable {
			buffer.push("cast(null as varchar(1))");
			return null;
		}

		@Override
		public Object inBytes(DB2ExprBuffer buffer, SequenceDataType type)
				throws Throwable {
			buffer.push("cast(null as varchar(1) for bit data)");
			return null;
		}

		@Override
		public Object inDate(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as timestamp)");
			return null;
		}

		@Override
		public Object inGUID(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(null as varchar(16) for bit data)");
			return null;
		}
	};

	@Override
	public final DB2ExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("x'");
		this.bytesToBuffer(val);
		this.append('\'');
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public final DB2ExprBuffer loadDate(long val) {
		this.push("timestamp('" + DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS) + "')");
		return this;
	}

	@Override
	public final DB2ExprBuffer loadColumnRef(String ref, String column) {
		this.push(quote(ref) + "." + quote(column));
		return this;
	}

	@Override
	public final DB2ExprBuffer loadStr(String val) {
		this.push("'" + escape(val) + "'");
		return this;
	}

	@Override
	public final DB2ExprBuffer loadParam(ParameterPlaceholder reserver,
			DataTypeInternal refer) {
		refer.detect(paramRender, this);
		if (this.args == null) {
			this.args = new ArrayList<ParameterPlaceholder>();
		}
		this.args.add(reserver);
		return this;
	}

	private static final TypeDetectorBase<Object, DB2ExprBuffer> paramRender = new TypeDetectorBase<Object, DB2ExprBuffer>() {

		@Override
		public Object inBoolean(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as smallint)");
			return null;
		}

		@Override
		public Object inByte(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as smallint)");
			return null;
		}

		@Override
		public Object inInt(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as int)");
			return null;
		}

		@Override
		public Object inLong(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as bigint)");
			return null;
		}

		@Override
		public Object inNumeric(DB2ExprBuffer buffer, int precision, int scale)
				throws Throwable {
			buffer.push("cast(? as decimal(" + precision + "," + scale + "))");
			return null;
		}

		@Override
		public Object inFloat(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as real)");
			return null;
		}

		@Override
		public Object inDouble(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as double)");
			return null;
		}

		@Override
		public Object inString(DB2ExprBuffer buffer, SequenceDataType type)
				throws Throwable {
			buffer.push("cast(? as varchar(2000))");
			return null;
		}

		@Override
		public Object inBytes(DB2ExprBuffer buffer, SequenceDataType type)
				throws Throwable {
			buffer.push("cast(? as varchar(2000) for bit data)");
			return null;
		}

		@Override
		public Object inDate(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as timestamp)");
			return null;
		}

		@Override
		public Object inGUID(DB2ExprBuffer buffer) throws Throwable {
			buffer.push("cast(? as varchar(16) for bit data)");
			return null;
		}
	};

	@Override
	public final DB2ExprBuffer mod() {
		this.call("mod", 2);
		return this;
	}

	@Override
	protected DB2SelectBuffer newSubquery() {
		return new DB2SelectBuffer();
	}

	@Override
	protected void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q) {
		((DB2SelectBuffer) q).writeTo(sql, args);
	}

	private final void timestampadd(String interval, int mul) {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.appendBuffer(i, 0);// timestamp
		this.append("+");
		if (mul != 0) {
			this.append("(" + mul);
			this.append("*");
			this.appendBuffer(i + 1, 0);// interval
			this.append(')');
		} else {
			this.appendBuffer(i + 1, 0);// interval
		}
		this.append(' ');
		this.append(interval);
		this.endUpdate(PREC_ADD);
	}

	@Override
	public ISqlExprBuffer coalesce(int paramCount) {
		this.call("nvl", paramCount);
		return this;
	}

	final void coalesceEmptyBin(int j) {
		this.append("coalesce(");
		this.appendBuffer(j, PREC_MAX);
		this.append(",x'')");
	}

	static final String ROWCOUNT_VAR = "$ROWCOUNT";

	boolean useRowcount;

	public ISqlExprBuffer getdate() {
		int i = this.count() - 0;
		this.beginUpdate(i);
		this.append("current timestamp");
		this.endUpdate(PREC_MAX);
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
		this.call("week", 1);
		return this;
	}

	public ISqlExprBuffer dayofyear() {
		this.call("dayofyear", 1);
		return this;
	}

	public ISqlExprBuffer dayofymonth() {
		this.call("day", 1);
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
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("microsecond(");
		this.appendBuffer(i, 0);
		this.append(")/1000");
		this.endUpdate(PREC_MUL);
		return this;
	}

	public ISqlExprBuffer yearadd() {
		this.timestampadd("years", 0);
		return this;
	}

	public ISqlExprBuffer quarteradd() {
		this.timestampadd("months", 3);
		return this;
	}

	public ISqlExprBuffer monthadd() {
		this.timestampadd("months", 0);
		return this;
	}

	public ISqlExprBuffer weekadd() {
		this.timestampadd("days", 7);
		return this;
	}

	public ISqlExprBuffer dayadd() {
		this.timestampadd("days", 0);
		return this;
	}

	public ISqlExprBuffer houradd() {
		this.timestampadd("hours", 0);
		return this;
	}

	public ISqlExprBuffer minuteadd() {
		this.timestampadd("minutes", 0);
		return this;
	}

	public ISqlExprBuffer secondadd() {
		this.timestampadd("seconds", 0);
		return this;
	}

	public ISqlExprBuffer yeardiff() {
		this.call("dna_yeardiff", 2);
		return this;
	}

	public ISqlExprBuffer quarterdiff() {
		this.call("dna_quarterdiff", 2);
		return this;
	}

	public ISqlExprBuffer monthdiff() {
		this.call("dna_monthdiff", 2);
		return this;
	}

	public ISqlExprBuffer weekdiff() {
		this.call("dna_weekdiff", 2);
		return this;
	}

	public ISqlExprBuffer daydiff() {
		this.call("dna_daydiff", 2);
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
		this.call("dna_isleapyear", 1);
		return this;
	}

	public ISqlExprBuffer isleapmonth() {
		this.call("dna_isleapmonth", 1);
		return this;
	}

	public ISqlExprBuffer isleapday() {
		this.call("dna_isleapday", 1);
		return this;
	}

	public ISqlExprBuffer truncyear() {
		this.call("dna_truncyear", 1);
		return this;
	}

	public ISqlExprBuffer truncquarter() {
		this.call("dna_truncquarter", 1);
		return this;
	}

	public ISqlExprBuffer truncmonth() {
		this.call("dna_truncmonth", 1);
		return this;
	}

	public ISqlExprBuffer truncweek() {
		this.call("dna_truncweek", 1);
		return this;
	}

	public ISqlExprBuffer truncday() {
		this.call("date", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer lg() {
		this.call("log10", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer chr() {
		this.call("char", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer nchr() {
		this.call("char", 1);
		return this;
	}

	public ISqlExprBuffer lpad(int paramCount) {
		this.call("dna_lpad", paramCount);
		return this;
	}

	public ISqlExprBuffer rpad(int paramCount) {
		this.call("dna_rpad", paramCount);
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

	public ISqlExprBuffer len() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("length(");
		this.appendBuffer(i, 0);
		this.append(",codeunits16)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer indexof(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("locate(");
		this.appendBuffer(i + 1, 0);// search_str
		this.append(',');
		this.appendBuffer(i, 0);// source_str
		if (paramCount > 2) {
			this.append(',');
			this.appendBuffer(i + 2, 0);
		}
		this.append(",codeunits16)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.appendBuffer(i, PREC_MAX);
		for (int j = i + 1; j < this.count(); j++) {
			this.append("||");
			this.appendBuffer(j, PREC_MAX);
		}
		this.endUpdate(PREC_ADD);
		return this;
	}

	public ISqlExprBuffer bin_substr(int paramCount) {
		this.call("substr", paramCount);
		return this;
	}

	public ISqlExprBuffer bin_concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.coalesceEmptyBin(i);
		for (int j = i + 1; j < this.count(); j++) {
			this.append("||");
			this.nvlEmptyBin(j);
		}
		this.endUpdate(PREC_ADD);
		return this;
	}

	private final void nvlEmptyBin(int j) {
		this.append("nvl(");
		this.appendBuffer(j, PREC_MAX);
		this.append(",x'')");
	}

	public ISqlExprBuffer bin_len() {
		this.call("length", 1);
		return this;
	}

	public ISqlExprBuffer new_recid() {
		this.call("dna_newrecid", 0);
		return this;
	}

	public ISqlExprBuffer rowcount() {
		this.useRowcount = true;
		int i = this.count() - 0;
		this.beginUpdate(i);
		this.append("\"" + ROWCOUNT_VAR + "\"");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer hexstr() {
		this.call("hex", 1);
		return this;
	}

	public ISqlExprBuffer to_int() {
		this.call("integer", 1);
		return this;
	}

	public ISqlExprBuffer to_char() {
		this.call("char", 1);
		return this;
	}
	
	public ISqlExprBuffer numberstr() {
		this.call("char", 1);
		return this;
	}
	
	public ISqlExprBuffer datestr() {
		this.call("dna_datestr", 2);
		return this;
	} 

	@Override
	public ISqlExprBuffer collate_gbk() {
		this.call("dna_collate_gbk", 1);
		return this;
	}

	//²»Ö§³Öwindow
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
		}
		this.append(')');
		this.endUpdate(PREC_MAX);
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