package com.jiuqi.dna.core.internal.db.support.kingbase.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.type.DateParser;

class KingbaseExprBuffer extends SqlExprBuffer {

	private String targetAlias;
	private String alternateAlias;

	static final String quote(String name) {
		return new StringBuffer().append('"').append(name).append('"').toString();
	}

	public void replace(String targetAlias, String alternateAlias) {
		this.targetAlias = targetAlias;
		this.alternateAlias = alternateAlias;
	}

	@Override
	protected ISqlSelectBuffer newSubquery() {
		KingbaseSelectBuffer sub = new KingbaseSelectBuffer();
		sub.replace(this.targetAlias, this.alternateAlias);
		return sub;
	}

	@Override
	protected void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q) {
		((KingbaseSelectBuffer) q).writeTo(sql, args);
	}

	// JDBC

	@Override
	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("X'");
		this.bytesToBuffer(val);
		this.append("'");
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer loadDate(long val) {
		this.push(new StringBuffer("timestamp '").append(DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS)).append("'").toString());
		return this;
	}

	@Override
	public ISqlExprBuffer loadColumnRef(String ref, String column) {
		ref = quote(ref);
		if (this.targetAlias != null && ref.equals(this.targetAlias)) {
			ref = this.alternateAlias;
		}
		this.push(new StringBuffer().append(ref).append('.').append(quote(column)).toString());
		return this;
	}

	@Override
	public ISqlExprBuffer loadVar(String name) {
		return super.loadVar(quote(name));
	}

	@Override
	public ISqlExprBuffer mod() {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("(to_number(");
		this.appendBuffer(i, 0);
		this.append(")%to_number(");
		this.appendBuffer(i + 1, 0);
		this.append("))");
		this.endUpdate(PREC_ADD);
		return this;
	}

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

	private final void newGUID(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("decoding(sys_guid(),'hex')");
		this.endUpdate(PREC_MAX);
	}

	private final void timestampadd(String unit) {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("dateadd(");
		this.append(unit);
		this.append(',');
		// interval
		this.appendBuffer(i + 1, 0);
		this.append(',');
		// ts
		this.appendBuffer(i, 0);
		this.append(")");
		this.endUpdate(PREC_MAX);
	}

	private final void datePart(String unit) {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("datePart(");
		this.append(unit);
		this.append(',');
		// interval
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void dateTrunc(String unit) {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("date_trunc(");
		this.append(unit);
		this.append(',');
		// interval
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_MAX);
	}

	private final void timestampdiff(String unit, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(0);
		this.append("datediff(");
		this.append(unit);
		this.append(',');
		// startTs
		this.appendBuffer(i, 0);
		this.append(',');
		// endTs
		this.appendBuffer(i + 1, 0);
		this.append(")");
		this.endUpdate(PREC_MAX);
	}

	final void coalesceEmptyBin(int i) {
		this.append("encode(coalesce(");
		this.appendBuffer(i, PREC_MAX);
		this.append(",X''),'hex')");
	}

	@Override
	public ISqlExprBuffer grouping() {
		// HCL
		this.load(-1);
		return this;
	}

	public ISqlExprBuffer getdate() {
		this.call("now", 0);
		return this;
	}

	public ISqlExprBuffer year() {
		this.datePart("'year'");
		return this;
	}

	public ISqlExprBuffer quarter() {
		this.datePart("'quarter'");
		return this;
	}

	public ISqlExprBuffer month() {
		this.datePart("'month'");
		return this;
	}

	public ISqlExprBuffer weekofyear() {
		this.datePart("'week'");
		return this;
	}

	public ISqlExprBuffer dayofyear() {
		this.datePart("'dayofyear'");
		return this;
	}

	public ISqlExprBuffer dayofymonth() {
		this.datePart("'day'");
		return this;
	}

	public ISqlExprBuffer dayofweek() {
		this.datePart("'weekday'");
		return this;
	}

	public ISqlExprBuffer hour() {
		this.datePart("'hour'");
		return this;
	}

	public ISqlExprBuffer minute() {
		this.datePart("'minute'");
		return this;
	}

	public ISqlExprBuffer second() {
		this.datePart("'second'");
		return this;
	}

	public ISqlExprBuffer millisecond() {
		this.datePart("'ms'");
		return this;
	}

	public ISqlExprBuffer yearadd() {
		this.timestampadd("'year'");
		return this;
	}

	public ISqlExprBuffer quarteradd() {
		this.timestampadd("'quarter'");
		return this;
	}

	public ISqlExprBuffer monthadd() {
		this.timestampadd("'month'");
		return this;
	}

	public ISqlExprBuffer weekadd() {
		this.timestampadd("'week'");
		return this;
	}

	public ISqlExprBuffer dayadd() {
		this.timestampadd("'day'");
		return this;
	}

	public ISqlExprBuffer houradd() {
		this.timestampadd("'hour'");
		return this;
	}

	public ISqlExprBuffer minuteadd() {
		this.timestampadd("'minute'");
		return this;
	}

	public ISqlExprBuffer secondadd() {
		this.timestampadd("'second'");
		return this;
	}

	public ISqlExprBuffer yeardiff() {
		this.timestampdiff("'year'", 2);
		return this;
	}

	public ISqlExprBuffer quarterdiff() {
		this.timestampdiff("'quarter'", 2);
		return this;
	}

	public ISqlExprBuffer monthdiff() {
		this.timestampdiff("'month'", 2);
		return this;
	}

	public ISqlExprBuffer weekdiff() {
		// HCL
		this.timestampdiff("'week'", 2);
		return this;
	}

	public ISqlExprBuffer daydiff() {
		this.timestampdiff("'day'", 2);
		return this;
	}

	public ISqlExprBuffer hourdiff() {
		this.timestampdiff("'hour'", 2);
		return this;
	}

	public ISqlExprBuffer minutediff() {
		this.timestampdiff("'minute'", 2);
		return this;
	}

	public ISqlExprBuffer seconddiff() {
		this.timestampdiff("'second'", 2);
		return this;
	}

	public ISqlExprBuffer isleapyear() {
		this.call("public.dna.isleapyear", 1);
		return this;
	}

	public ISqlExprBuffer isleapmonth() {
		this.call("public.dna.isleapmonth", 1);
		return this;
	}

	public ISqlExprBuffer isleapday() {
		this.call("public.dna.isleapday", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer collate_gbk() {
		this.call("public.dna.collate_gbk", 1);
		return this;
	}

	public ISqlExprBuffer truncyear() {
		this.dateTrunc("'year'");
		return this;
	}

	public ISqlExprBuffer truncquarter() {
		this.dateTrunc("'quarter'");
		return this;
	}

	public ISqlExprBuffer truncmonth() {
		this.dateTrunc("'month'");
		return this;
	}

	public ISqlExprBuffer truncweek() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("dateadd('day',-1,date_trunc('week',");
		// this.append(',');
		this.appendBuffer(i, 0);
		this.append("))");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer truncday() {
		this.dateTrunc("'day'");
		return this;
	}

	@Override
	public ISqlExprBuffer lg() {
		this.call("log", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer nchr() {
		this.call("chr", 1);
		return this;
	}

	public ISqlExprBuffer lpad(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("lpad(");
		if (paramCount == 2) {
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(",' '");
			this.append(')');
		} else {
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(',');
			this.appendBuffer(i + 2, 0);
			this.append(')');
		}
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer rpad(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("rpad(");
		if (paramCount == 2) {
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(",' '");
			this.append(')');
		} else {
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(',');
			this.appendBuffer(i + 2, 0);
			this.append(')');
		}
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer indexof(int paramCount) {
		this.call("instr", paramCount);
		return this;
	}

	public ISqlExprBuffer len() {
		this.call("char_length", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.coalesceEmptyStr(i);
		for (int j = i + 1; j < this.count(); j++) {
			this.append("||");
			this.coalesceEmptyStr(j);
		}
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer bin_substr(int paramCount) {
		// HCL
		this.call("substr", paramCount);
		return this;
	}

	public ISqlExprBuffer bin_concat(int paramCount) {
		// To change
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("decoding(concat(");
		this.coalesceEmptyBin(i);
		for (int j = i + 1; j < this.count(); j++) {
			this.append(',');
			this.coalesceEmptyBin(j);
		}
		this.append("),'hex')");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer bin_len() {
		this.call("length", 1);
		return this;
	}

	public ISqlExprBuffer new_recid() {
		this.newGUID(0);
		return this;
	}

	public ISqlExprBuffer rowcount() {
		this.call("row_count", 0);
		return this;
	}

	public ISqlExprBuffer hexstr() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("encode(");
		this.appendBuffer(i, 0);
		this.append(',');
		this.append("'hex'");
		this.append(")");
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
		throw new UnsupportedOperationException();
	}

	public ISqlExprBuffer to_int() {
		this.call("to_number", 1);
		return this;
	}
}