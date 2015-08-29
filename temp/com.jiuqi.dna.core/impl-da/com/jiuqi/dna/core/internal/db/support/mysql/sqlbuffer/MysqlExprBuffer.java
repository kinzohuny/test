package com.jiuqi.dna.core.internal.db.support.mysql.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.type.DateParser;

class MysqlExprBuffer extends SqlExprBuffer {

	static final String quote(String name) {
		return "`" + name + "`";
	}

	@Override
	public final MysqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		if (val == null || val.length == 0) {
			this.append("unhex('')");
		} else {
			this.append("0x");
			super.bytesToBuffer(val);
		}
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public final MysqlExprBuffer loadDate(long val) {
		this.push("'" + DateParser.format(val, DateParser.FORMAT_DATE_TIME_MS) + "'");
		return this;
	}

	@Override
	public final MysqlExprBuffer loadColumnRef(String table, String column) {
		if (table == null || table.length() == 0) {
			this.push(quote(column));
		} else {
			this.push(quote(table) + "." + quote(column));
		}
		return this;
	}

	private final void timestampadd(String unit, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("timestampadd(");
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

	private final void timestampdiff(String unit, int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("timestampdiff(");
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
		this.append("hex(coalesce(");
		this.appendBuffer(i, PREC_MAX);
		this.append(",binary''))");
	}

	@Override
	protected final ISqlSelectBuffer newSubquery() {
		return new MysqlSelectBuffer(this.command);
	}

	@Override
	protected final void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q) {
		((MysqlSelectBuffer) q).writeTo(sql, args);
	}

	final MysqlCommandBuffer command;

	MysqlExprBuffer(MysqlCommandBuffer command) {
		this.command = command;
	}

	@Override
	public ISqlExprBuffer grouping() {
		// XXX
		this.load(-1);
		return this;
	}

	public ISqlExprBuffer getdate() {
		this.call("now", 0);
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
		this.call("dna_week", 1);
		return this;
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
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("microsecond(");
		this.appendBuffer(i, 0);
		this.append(")/1000");
		this.endUpdate(PREC_MUL);
		return this;
	}

	public ISqlExprBuffer yearadd() {
		this.timestampadd("year", 2);
		return this;
	}

	public ISqlExprBuffer quarteradd() {
		this.timestampadd("quarter", 2);
		return this;
	}

	public ISqlExprBuffer monthadd() {
		this.timestampadd("month", 2);
		return this;
	}

	public ISqlExprBuffer weekadd() {
		this.timestampadd("week", 2);
		return this;
	}

	public ISqlExprBuffer dayadd() {
		this.timestampadd("day", 2);

		return this;
	}

	public ISqlExprBuffer houradd() {
		this.timestampadd("hour", 2);

		return this;
	}

	public ISqlExprBuffer minuteadd() {
		this.timestampadd("minute", 2);

		return this;
	}

	public ISqlExprBuffer secondadd() {
		this.timestampadd("second", 2);
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
		this.timestampdiff("hour", 2);
		return this;
	}

	public ISqlExprBuffer minutediff() {
		this.timestampdiff("minute", 2);
		return this;
	}

	public ISqlExprBuffer seconddiff() {
		this.timestampdiff("second", 2);
		return this;
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
		this.call("dna_truncday", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer lg() {
		this.call("log10", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer chr() {
		// CORE2.5
		this.call("char", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer nchr() {
		// CORE2.5
		this.call("char", 1);
		return this;
	}

	public ISqlExprBuffer lpad(int paramCount) {
		if (paramCount == 2) {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("lpad(");
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(", ' ')");
			this.endUpdate(PREC_MAX);
			return this;
		} else {
			this.call("lpad", 3);
		}
		return this;
	}

	public ISqlExprBuffer rpad(int paramCount) {
		if (paramCount == 2) {
			int i = this.count() - paramCount;
			this.beginUpdate(i);
			this.append("rpad(");
			this.appendBuffer(i, 0);
			this.append(',');
			this.appendBuffer(i + 1, 0);
			this.append(", ' ')");
			this.endUpdate(PREC_MAX);
			return this;
		} else {
			this.call("rpad", 3);
		}
		return this;
	}

	public ISqlExprBuffer indexof(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("locate(");
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
		this.call("char_length", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("concat(");
		this.coalesceEmptyStr(i);
		for (int j = i + 1; j < this.count(); j++) {
			this.append(',');
			this.coalesceEmptyStr(j);
		}
		this.append(")");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer bin_substr(int paramCount) {
		// CORE2.5
		this.call("substr", paramCount);
		return this;
	}

	public ISqlExprBuffer bin_concat(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("unhex(concat(");
		this.coalesceEmptyBin(i);
		for (int j = i + 1; j < this.count(); j++) {
			this.append(',');
			this.coalesceEmptyBin(j);
		}
		this.append("))");
		this.endUpdate(PREC_MAX);
		return this;
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
		this.call("row_count", 0);
		return this;
	}

	public ISqlExprBuffer hexstr() {
		this.call("hex", 1);
		return this;
	}

	public ISqlExprBuffer to_char() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("cast(");
		this.appendBuffer(i, 0);
		this.append(" as char)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer numberstr() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("cast(");
		this.appendBuffer(i, 0);
		this.append(" as char)");
		this.endUpdate(PREC_MAX);
		return this;
	}
	
	public ISqlExprBuffer datestr() {
		this.call("dna_datestr", 2);
		return this;
	}
	
	public ISqlExprBuffer to_int() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("cast(");
		this.appendBuffer(i, 0);
		this.append(" as decimal)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer collate_gbk() {
		this.call("dna_collate_gbk", 1);
		return this;
	}

}
