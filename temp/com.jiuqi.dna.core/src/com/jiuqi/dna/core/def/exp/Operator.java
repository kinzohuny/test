package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.impl.AggregateFunction;
import com.jiuqi.dna.core.impl.CommonOperator;
import com.jiuqi.dna.core.impl.ScalarFunction;

/**
 * ÔËËã·û»ùÀà
 * 
 * @author gaojingxin
 */
public interface Operator {

	public static final Operator ADD = CommonOperator.ADD;

	public static final Operator SUB = CommonOperator.SUB;

	public static final Operator MUL = CommonOperator.MUL;

	public static final Operator DIV = CommonOperator.DIV;

	public static final Operator MINUS = CommonOperator.MINUS;

	public static final Operator SIMPLE_CASE = CommonOperator.SIMPLE_CASE;

	public static final Operator STR_CONCAT = CommonOperator.STR_CONCAT;

	public static final Operator COALESCE = CommonOperator.COALESCE;

	public static final Operator COUNT_ALL = AggregateFunction.COUNT_ALL;

	public static final Operator COUNT_DISTINCT = AggregateFunction.COUNT_DISTINCT;

	public static final Operator COUNT_ASTERISK = AggregateFunction.COUNT_ASTERISK;

	public static final Operator AVG_ALL = AggregateFunction.AVG_ALL;

	public static final Operator AVG_DISTINCT = AggregateFunction.AVG_DISTINCT;

	public static final Operator SUM_ALL = AggregateFunction.SUM_ALL;

	public static final Operator SUM_DISTINCT = AggregateFunction.SUM_DISTINCT;

	public static final Operator MIN = AggregateFunction.MIN;

	public static final Operator MAX = AggregateFunction.MAX;

	public static final Operator GETDATE = ScalarFunction.GETDATE;

	public static final Operator YEAR_OF = ScalarFunction.YEAR_OF;

	public static final Operator QUARTER_OF = ScalarFunction.QUARTER_OF;

	public static final Operator MONTH_OF = ScalarFunction.MONTH_OF;

	public static final Operator WEEK_OF = ScalarFunction.WEEK_OF;

	public static final Operator DAY_OF = ScalarFunction.DAY_OF;

	public static final Operator DAY_OF_YEAR = ScalarFunction.DAY_OF_YEAR;

	public static final Operator DAY_OF_WEEK = ScalarFunction.DAY_OF_WEEK;

	public static final Operator HOUR_OF = ScalarFunction.HOUR_OF;

	public static final Operator MINUTE_OF = ScalarFunction.MINUTE_OF;

	public static final Operator SECOND_OF = ScalarFunction.SECOND_OF;

	public static final Operator MILLISECOND_OF = ScalarFunction.MILLISECOND_OF;

	public static final Operator ADD_YEAR = ScalarFunction.ADD_YEAR;

	public static final Operator ADD_QUARTER = ScalarFunction.ADD_QUARTER;

	public static final Operator ADD_MONTH = ScalarFunction.ADD_MONTH;

	public static final Operator ADD_WEEK = ScalarFunction.ADD_WEEK;

	public static final Operator ADD_DAY = ScalarFunction.ADD_DAY;

	public static final Operator ADD_HOUR = ScalarFunction.ADD_HOUR;

	public static final Operator ADD_MINUTE = ScalarFunction.ADD_MINUTE;

	public static final Operator ADD_SECOND = ScalarFunction.ADD_SECOND;

	public static final Operator YEAR_DIFF = ScalarFunction.YEAR_DIFF;

	public static final Operator QUARTER_DIFF = ScalarFunction.QUARTER_DIFF;

	public static final Operator MONTH_DIFF = ScalarFunction.MONTH_DIFF;

	public static final Operator DAY_DIFF = ScalarFunction.DAY_DIFF;

	public static final Operator WEEK_DIFF = ScalarFunction.WEEK_DIFF;

	public static final Operator IS_LEAP_YEAR = ScalarFunction.IS_LEAP_YEAR;

	public static final Operator IS_LEAP_MONTH = ScalarFunction.IS_LEAP_MONTH;

	public static final Operator IS_LEAP_DAY = ScalarFunction.IS_LEAP_DAY;

	public static final Operator SIN = ScalarFunction.SIN;

	public static final Operator COS = ScalarFunction.COS;

	public static final Operator TAN = ScalarFunction.TAN;

	public static final Operator ASIN = ScalarFunction.ASIN;

	public static final Operator ACOS = ScalarFunction.ACOS;

	public static final Operator ATAN = ScalarFunction.ATAN;

	public static final Operator POWER = ScalarFunction.POWER;

	public static final Operator EXP = ScalarFunction.EXP;

	public static final Operator LOG10 = ScalarFunction.LG;

	public static final Operator SQRT = ScalarFunction.SQRT;

	public static final Operator CEIL = ScalarFunction.CEIL;

	public static final Operator FLOOR = ScalarFunction.FLOOR;

	public static final Operator ROUND = ScalarFunction.ROUND;

	public static final Operator SIGN = ScalarFunction.SIGN;

	public static final Operator ABS = ScalarFunction.ABS;

	public static final Operator CHR = ScalarFunction.CHR;

	public static final Operator NCHR = ScalarFunction.NCHR;

	public static final Operator ASCII = ScalarFunction.EXP;

	public static final Operator LEN = ScalarFunction.LEN;

	public static final Operator INDEXOF = ScalarFunction.INDEXOF;

	public static final Operator UPPER = ScalarFunction.UPPER;

	public static final Operator LOWER = ScalarFunction.LOWER;

	public static final Operator LTRIM = ScalarFunction.LTRIM;

	public static final Operator RTRIM = ScalarFunction.RTRIM;

	public static final Operator TRIM = ScalarFunction.TRIM;

	public static final Operator REPLACE = ScalarFunction.REPLACE;

	public static final Operator SUBSTR = ScalarFunction.SUBSTR;

	public static final Operator COLLATE_GBK = ScalarFunction.COLLATE_GBK;
}