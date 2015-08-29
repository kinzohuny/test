package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.Bound;
import com.jiuqi.dna.core.impl.AnalyticFunctionExpr.WindowType;
import com.jiuqi.dna.core.impl.DataTypeInternal;

/**
 * 表达式buffer.包括条件表达式和值表达式.按逆波兰式传入运算体和运算符.
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlExprBuffer extends ISqlBuffer {

	public ISqlExprBuffer predicate(SqlPredicate pred, int paramCount);

	public ISqlExprBuffer load(long val);

	public ISqlExprBuffer load(double val);

	public ISqlExprBuffer load(byte[] val);

	public ISqlExprBuffer load(boolean val);

	public ISqlExprBuffer loadStr(String val);

	public ISqlExprBuffer loadDate(long val);

	public ISqlExprBuffer loadVar(String name);

	public ISqlExprBuffer loadParam(ParameterPlaceholder placeholder);

	public ISqlExprBuffer loadParam(ParameterPlaceholder placeholder,
			DataTypeInternal refer);

	public ISqlExprBuffer loadColumnRef(String ref, String column);

	public ISqlExprBuffer loadNull(DataTypeInternal type);

	public ISqlSelectBuffer subquery();

	public ISqlExprBuffer minus();

	public ISqlExprBuffer add(int paramCount);

	public ISqlExprBuffer sub(int paramCount);

	public ISqlExprBuffer mul(int paramCount);

	public ISqlExprBuffer div(int paramCount);

	public ISqlExprBuffer mod();

	public ISqlExprBuffer searchedCase(int paramCount);

	public ISqlExprBuffer simpleCase(int paramCount);

	public ISqlExprBuffer coalesce(int paramCount);

	public ISqlExprBuffer lt();

	public ISqlExprBuffer le();

	public ISqlExprBuffer gt();

	public ISqlExprBuffer ge();

	public ISqlExprBuffer eq();

	public ISqlExprBuffer ne();

	public ISqlExprBuffer and(int paramCount);

	public ISqlExprBuffer or(int paramCount);

	public ISqlExprBuffer not();

	public ISqlExprBuffer count(int paramCount, boolean distinct);

	public ISqlExprBuffer avg(boolean distinct);

	public ISqlExprBuffer sum(boolean distinct);

	public ISqlExprBuffer max();

	public ISqlExprBuffer min();

	public ISqlExprBuffer grouping();

	public ISqlExprBuffer getdate();

	public ISqlExprBuffer year();

	public ISqlExprBuffer quarter();

	public ISqlExprBuffer month();

	public ISqlExprBuffer weekofyear();

	public ISqlExprBuffer dayofyear();

	public ISqlExprBuffer dayofymonth();

	public ISqlExprBuffer dayofweek();

	public ISqlExprBuffer hour();

	public ISqlExprBuffer minute();

	public ISqlExprBuffer second();

	public ISqlExprBuffer millisecond();

	public ISqlExprBuffer yearadd();

	public ISqlExprBuffer quarteradd();

	public ISqlExprBuffer monthadd();

	public ISqlExprBuffer weekadd();

	public ISqlExprBuffer dayadd();

	public ISqlExprBuffer houradd();

	public ISqlExprBuffer minuteadd();

	public ISqlExprBuffer secondadd();

	public ISqlExprBuffer yeardiff();

	public ISqlExprBuffer quarterdiff();

	public ISqlExprBuffer monthdiff();

	public ISqlExprBuffer weekdiff();

	public ISqlExprBuffer daydiff();

	public ISqlExprBuffer hourdiff();

	public ISqlExprBuffer minutediff();

	public ISqlExprBuffer seconddiff();

	public ISqlExprBuffer isleapyear();

	public ISqlExprBuffer isleapmonth();

	public ISqlExprBuffer isleapday();

	public ISqlExprBuffer truncyear();

	public ISqlExprBuffer truncquarter();

	public ISqlExprBuffer truncmonth();

	public ISqlExprBuffer truncweek();

	public ISqlExprBuffer truncday();

	public ISqlExprBuffer sin();

	public ISqlExprBuffer cos();

	public ISqlExprBuffer tan();

	public ISqlExprBuffer asin();

	public ISqlExprBuffer acos();

	public ISqlExprBuffer atan();

	public ISqlExprBuffer exp();

	public ISqlExprBuffer power();

	public ISqlExprBuffer lg();

	public ISqlExprBuffer ln();

	public ISqlExprBuffer sqrt();

	public ISqlExprBuffer ceil();

	public ISqlExprBuffer floor();

	public ISqlExprBuffer round(int paramCount);

	public ISqlExprBuffer sign();

	public ISqlExprBuffer abs();

	public ISqlExprBuffer chr();

	public ISqlExprBuffer nchr();

	public ISqlExprBuffer ascii();

	public ISqlExprBuffer lower();

	public ISqlExprBuffer upper();

	public ISqlExprBuffer ltrim();

	public ISqlExprBuffer rtrim();

	public ISqlExprBuffer trim();

	public ISqlExprBuffer lpad(int paramCount);

	public ISqlExprBuffer rpad(int paramCount);

	public ISqlExprBuffer indexof(int paramCount);

	public ISqlExprBuffer substr(int paramCount);

	public ISqlExprBuffer concat(int paramCount);

	public ISqlExprBuffer len();

	public ISqlExprBuffer replace();

	public ISqlExprBuffer bin_substr(int paramCount);

	public ISqlExprBuffer bin_concat(int paramCount);

	public ISqlExprBuffer bin_len();

	public ISqlExprBuffer new_recid();

	public ISqlExprBuffer rowcount();

	public ISqlExprBuffer hexstr();

	public ISqlExprBuffer numberstr();
	
	public ISqlExprBuffer datestr();
	
	public ISqlExprBuffer to_char();

	public ISqlExprBuffer to_int();

	public ISqlExprBuffer collate_gbk();

	public ISqlExprBuffer userfunction(String function, int paramCount);

	/** sum over */
	public ISqlExprBuffer analytic(String af, int partitionCount,
			int orderbyCount, int desc, WindowType type, Bound preceding,
			Bound following);
	
	/** row_number */
	public ISqlExprBuffer analytic(String af, int partitionCount,
			int orderbyCount, int desc);
}