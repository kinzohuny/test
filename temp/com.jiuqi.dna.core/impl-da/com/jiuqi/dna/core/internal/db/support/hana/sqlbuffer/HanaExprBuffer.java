package com.jiuqi.dna.core.internal.db.support.hana.sqlbuffer;

import java.util.List;

import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlSelectBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ParameterPlaceholder;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlExprBuffer;
import com.jiuqi.dna.core.internal.da.sqlbuffer.SqlStringBuffer;
import com.jiuqi.dna.core.type.DateParser;

public class HanaExprBuffer extends SqlExprBuffer {

	final HanaCommandBuffer command;

	public HanaExprBuffer(HanaCommandBuffer command) {
		this.command = command;
	}

	@Override
	public ISqlExprBuffer loadColumnRef(String ref, String column) {
		this.push(quote(ref) + "." + quote(column));
		return this;
	}

	@Override
	public ISqlExprBuffer loadDate(long val) {
		this.push("to_timestamp('" + DateParser.format(val, DateParser.FORMAT_DATE_TIME) + "')");
		return this;
	}

	@Override
	public ISqlExprBuffer load(byte[] val) {
		this.beginUpdate(this.count());
		this.append("hextobin('");
		this.bytesToBuffer(val);
		this.append("\')");
		this.endUpdate(PREC_MAX);
		return this;
	}

	@Override
	public ISqlExprBuffer loadStr(String val) {
		this.push("N'" + escape(val) + "'");
		return this;
	}

	static final String quote(String name) {
		return "\"" + name + "\"";
	}

	@Override
	public ISqlExprBuffer mod() {
		this.call("mod", 2);
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
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("to_int(substr(quarter(");
		this.appendBuffer(i, 0);
		this.append("),7))");
		this.endUpdate(PREC_MAX);
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
		this.call("dayofmonth", 1);
		return this;
	}

	public ISqlExprBuffer dayofweek() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("map(weekday(");
		this.appendBuffer(i, 0);
		this.append("),6,1,0,2,1,3,2,4,3,5,4,6,5,7)");
		this.endUpdate(PREC_MAX);
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
		this.push(Integer.toString(0));
		return this;
	}

	public ISqlExprBuffer yearadd() {
		this.call("add_years", 2);
		return this;
	}

	public ISqlExprBuffer quarteradd() {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("add_months(");
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, PREC_MUL);
		this.append("*3)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer monthadd() {
		this.call("add_months", 2);
		return this;
	}

	public ISqlExprBuffer weekadd() {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("add_days(");
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, PREC_MUL);
		this.append("*7)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer dayadd() {
		this.call("add_days", 2);
		return this;
	}

	public ISqlExprBuffer houradd() {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("add_seconds(");
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, PREC_MUL);
		this.append("*3600)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer minuteadd() {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("add_seconds(");
		this.appendBuffer(i, 0);
		this.append(',');
		this.appendBuffer(i + 1, PREC_ADD);
		this.append("*60)");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer secondadd() {
		this.call("add_seconds", 2);
		return this;
	}

	public ISqlExprBuffer yeardiff() {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("year(");
		this.appendBuffer(i + 1, 0);
		this.append(")-year(");
		this.appendBuffer(i, 0);
		this.append(')');
		this.endUpdate(PREC_ADD);
		return this;
	}

	public ISqlExprBuffer quarterdiff() {
		// HANA Auto-generated method stub
		return null;
	}

	public ISqlExprBuffer monthdiff() {
		int i = this.count() - 2;
		this.beginUpdate(i);
		this.append("(year(");
		this.appendBuffer(i + 1, 0);
		this.append(")-year(");
		this.appendBuffer(i, 0);
		this.append("))*12+month(");
		this.appendBuffer(i + 1, 0);
		this.append(")-month(");
		this.appendBuffer(i, 0);
		this.append(")");
		this.endUpdate(PREC_ADD);
		return this;
	}

	public ISqlExprBuffer weekdiff() {
		// HANA Auto-generated method stub
		return null;
	}

	public ISqlExprBuffer daydiff() {
		this.call("days_between", 2);
		return this;
	}

	public ISqlExprBuffer hourdiff() {
		// HANA Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer minutediff() {
		// HANA Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer seconddiff() {
		this.call("seconds_between", 2);
		return this;
	}

	public ISqlExprBuffer isleapyear() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("case when mod(year(");
		this.appendBuffer(i, 0);
		this.append("),400)=0 then 1 when mod(year(");
		this.appendBuffer(i, 0);
		this.append("),100)=0 then 0 when mod(year(");
		this.appendBuffer(i, 0);
		this.append("),4)=0 then 1 else 0 end");
		return this;
	}

	public ISqlExprBuffer isleapmonth() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("case when last_day(");
		this.appendBuffer(i, 0);
		this.append(")=29 then 1 else 0 end");
		return this;
	}

	public ISqlExprBuffer isleapday() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("case when month(");
		this.appendBuffer(i, 0);
		this.append(")=2 and dayofmonth(");
		this.appendBuffer(i, 0);
		this.append(")=29 then 1 else 0 end");
		return this;
	}

	public ISqlExprBuffer truncyear() {
		// HANA Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer truncquarter() {
		// HANA Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer truncmonth() {
		// HANA Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer truncweek() {
		// HANA Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer truncday() {
		int i = this.count() - 1;
		this.beginUpdate(i);
		this.append("to_seconddate(to_date(");
		this.appendBuffer(i, 0);
		this.append("))");
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

	public ISqlExprBuffer indexof(int paramCount) {
		// HANA
		this.call("locate", paramCount);
		return this;
	}

	public ISqlExprBuffer len() {
		this.call("length", 1);
		return this;
	}

	@Override
	public ISqlExprBuffer substr(int paramCount) {
		this.call("substring", paramCount);
		return this;
	}

	public ISqlExprBuffer bin_substr(int paramCount) {
		int i = this.count() - paramCount;
		this.beginUpdate(i);
		this.append("substring(");
		this.appendBuffer(i, 0);
		this.append(",2*(");
		this.appendBuffer(i + 1, 0);
		this.append(")-1");
		if (paramCount > 2) {
			this.append(",2*(");
			this.appendBuffer(i + 2, 0);
			this.append(")");
		}
		this.append(")");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer bin_concat(int paramCount) {
		this.binary("||", PREC_ADD, paramCount);
		return this;
	}

	public ISqlExprBuffer bin_len() {
		this.call("length", 1);
		return this;
	}

	public ISqlExprBuffer new_recid() {
		int i = this.count() - 0;
		this.beginUpdate(i);
		this.append("sysuuid");
		this.endUpdate(PREC_MAX);
		return this;
	}

	public ISqlExprBuffer rowcount() {
		// HANA Auto-generated method stub
		return this;
	}

	public ISqlExprBuffer hexstr() {
		// HANA Auto-generated method stub
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

	public ISqlExprBuffer to_char() {
		this.call("to_nchar", 1);
		return this;
	}
	
	public ISqlExprBuffer numberstr() {
		this.call("to_nchar", 1);
		return this;
	}

	public ISqlExprBuffer datestr() {
		throw new UnsupportedOperationException();
	}
	
	public ISqlExprBuffer to_int() {
		this.call("to_int", 1);
		return this;
	}

	@Override
	protected ISqlSelectBuffer newSubquery() {
		return new HanaSelectBuffer(this.command);
	}

	@Override
	protected void writeSubquery(SqlStringBuffer sql,
			List<ParameterPlaceholder> args, ISqlSelectBuffer q) {
		((HanaSelectBuffer) q).writeTo(sql, args);
	}

}
