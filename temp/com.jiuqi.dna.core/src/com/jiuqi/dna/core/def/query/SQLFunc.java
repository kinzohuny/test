package com.jiuqi.dna.core.def.query;

import static com.jiuqi.dna.core.impl.ValueExpr.expOf;

import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.impl.AggregateFunction;
import com.jiuqi.dna.core.impl.OperateExpr;
import com.jiuqi.dna.core.impl.ScalarFunction;
import com.jiuqi.dna.core.impl.ValueExpr;

/**
 * SQL函数
 * 
 * @author houchunlei
 * 
 */
public class SQLFunc {

	// ---------------------------- 集函数 ----------------------------

	/**
	 * 返回组中的项数
	 * 
	 * <p>
	 * 包括null值,同count(*)
	 * 
	 * @return 整型表达式
	 */
	public static final OperateExpression xCount() {
		return OperateExpr.COUNT_ASTERISK;
	}

	/**
	 * 返回组中的项数
	 * 
	 * <p>
	 * 同count(all <code>value</code>)
	 * 
	 * @param value
	 * @return 整型表达式
	 */
	public static final OperateExpression xCount(Object value) {
		return new OperateExpr(AggregateFunction.COUNT_ALL, expOf(value));
	}

	/**
	 * 返回组中的项数
	 * 
	 * <p>
	 * 同count(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return 整型表达式
	 */
	public static final OperateExpression xCountDistinct(Object value) {
		return new OperateExpr(AggregateFunction.COUNT_DISTINCT, expOf(value));
	}

	/**
	 * 返回组中各值的平均值
	 * 
	 * <p>
	 * 仅用于数字列,忽略null值<br>
	 * 同avg(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvg(Object value) {
		return new OperateExpr(AggregateFunction.AVG_ALL, expOf(value));
	}

	/**
	 * 返回组中各值的平均值
	 * 
	 * <p>
	 * 仅用于数字列,忽略null值及重复值<br>
	 * 同avg(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvgDistinct(Object value) {
		return new OperateExpr(AggregateFunction.AVG_DISTINCT, expOf(value));
	}

	/**
	 * 返回组中表达式的和
	 * 
	 * <p>
	 * 仅用于数字列,忽略null值<br>
	 * 同sum(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSum(Object value) {
		return new OperateExpr(AggregateFunction.SUM_ALL, expOf(value));
	}

	/**
	 * 返回组中表达式的和
	 * 
	 * <p>
	 * 仅用于数字列,忽略null值及重复值<br>
	 * 同sum(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSumDistinct(Object value) {
		return new OperateExpr(AggregateFunction.SUM_DISTINCT, expOf(value));
	}

	/**
	 * 返回表达式中的最大值
	 * 
	 * <p>
	 * 同max(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMax(Object value) {
		return new OperateExpr(AggregateFunction.MAX, expOf(value));
	}

	/**
	 * 返回表达式中的最大值
	 * 
	 * <p>
	 * 同min(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMin(Object value) {
		return new OperateExpr(AggregateFunction.MIN, expOf(value));
	}

	/**
	 * 返回分组合计的标志值
	 * 
	 * @param value
	 * @return
	 * @deprecated 不建议继续使用
	 */
	@Deprecated
	public static final OperateExpression xGrouping(Object value) {
		ValueExpr expr = expOf(value);
		return new OperateExpr(AggregateFunction.GROUPING, expr);
	}

	// ---------------------------- 日期时间函数 ----------------------------

	/**
	 * 获取当前系统日期时间
	 * 
	 * <p>
	 * 不确定函数
	 * 
	 * @return 日期时间表达式
	 */
	public static final OperateExpression xGetDate() {
		return OperateExpr.GET_DATE;
	}

	/**
	 * 返回日期时间的年数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xYearOf(Object date) {
		return new OperateExpr(ScalarFunction.YEAR_OF, expOf(date));
	}

	/**
	 * 返回日期时间在年中的季度数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xQuarterOf(Object date) {
		return new OperateExpr(ScalarFunction.QUARTER_OF, expOf(date));
	}

	/**
	 * 返回日期时间在年中的月数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMonthOf(Object date) {
		return new OperateExpr(ScalarFunction.MONTH_OF, expOf(date));
	}

	/**
	 * 返回日期时间在年中的周数.
	 * 
	 * <p>
	 * 以自然周计算.星期天为一周的第一天.
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xWeekOf(Object date) {
		return new OperateExpr(ScalarFunction.WEEK_OF, expOf(date));
	}

	/**
	 * 返回日期时间在月中的天数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayOf(Object date) {
		return new OperateExpr(ScalarFunction.DAY_OF, expOf(date));
	}

	/**
	 * 返回日期时间在年中的天数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayOfYear(Object date) {
		return new OperateExpr(ScalarFunction.DAY_OF_YEAR, expOf(date));
	}

	/**
	 * 返回日期时间与在一周内的序号
	 * 
	 * <ul>
	 * <li>星期天,返回<code>1</code>
	 * <li>星期一,返回<code>2</code>
	 * <li>星期二,返回<code>3</code>
	 * <li>星期三,返回<code>4</code>
	 * <li>星期四,返回<code>5</code>
	 * <li>星期五,返回<code>6</code>
	 * <li>星期六,返回<code>7</code>
	 * </ul>
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayOfWeek(Object date) {
		return new OperateExpr(ScalarFunction.DAY_OF_WEEK, expOf(date));
	}

	/**
	 * 返回日期时间的小时数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xHourOf(Object date) {
		return new OperateExpr(ScalarFunction.HOUR_OF, expOf(date));
	}

	/**
	 * 返回日期时间的分数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMinuteOf(Object date) {
		return new OperateExpr(ScalarFunction.MINUTE_OF, expOf(date));
	}

	/**
	 * 返回日期时间的秒数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xSecondOf(Object date) {
		return new OperateExpr(ScalarFunction.SECOND_OF, expOf(date));
	}

	/**
	 * 返回日期时间毫秒数
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMillisecondOf(Object date) {
		return new OperateExpr(ScalarFunction.MILLISECOND_OF, expOf(date));
	}

	/**
	 * 日期时间表达式增加以年为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddYear(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_YEAR, expOf(date),
				expOf(interval));
	}

	/**
	 * 日期时间表达式增加以季度为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddQuarter(Object date,
			Object interval) {
		return new OperateExpr(ScalarFunction.ADD_QUARTER, expOf(date),
				expOf(interval));
	}

	/**
	 * 日期时间表达式增加以月为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddMonth(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_MONTH, expOf(date),
				expOf(interval));
	}

	/**
	 * 日期时间表达式增加以周为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddWeek(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_WEEK, expOf(date),
				expOf(interval));
	}

	/**
	 * 日期时间表达式增加以天为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddDay(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_DAY, expOf(date),
				expOf(interval));
	}

	/**
	 * 日期时间表达式增加以小时为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddHour(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_HOUR, expOf(date),
				expOf(interval));
	}

	/**
	 * 日期时间表达式增加以分为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddMinute(Object date,
			Object interval) {
		return new OperateExpr(ScalarFunction.ADD_MINUTE, expOf(date),
				expOf(interval));
	}

	/**
	 * 日期时间表达式增加以秒为单位的时间间隔
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期时间类型表达式
	 */
	public static final OperateExpression xAddSecond(Object date,
			Object interval) {
		return new OperateExpr(ScalarFunction.ADD_SECOND, expOf(date),
				expOf(interval));
	}

	/**
	 * 计算两个日期时间的间隔年数
	 * 
	 * @param startDate
	 *            开始的日期时间表达式
	 * @param endDate
	 *            结束的日期时间表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xYearDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.YEAR_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * 计算两个日期时间的间隔季度数
	 * 
	 * @param startDate
	 *            开始的日期时间表达式
	 * @param startDate
	 *            结束的日期时间表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xQuarterDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.QUARTER_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * 计算两个日期时间的间隔月数
	 * 
	 * @param startDate
	 *            开始的日期时间表达式
	 * @param startDate
	 *            结束的日期时间表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMonthDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.MONTH_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * 计算两个日期时间的间隔周数,星期一为一周的第一天.
	 * 
	 * @param startDate
	 *            开始的日期时间表达式
	 * @param startDate
	 *            结束的日期时间表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xWeekDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.WEEK_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * 计算两个日期时间的间隔天数
	 * 
	 * @param startDate
	 *            开始的日期时间表达式
	 * @param startDate
	 *            结束的日期时间表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.DAY_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * 返回日期时间所在年的第一天的零分零秒的日期时间
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncYear(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_YEAR, expOf(date));
	}

	/**
	 * 返回日期时间所在季度的第一天的零分零秒的日期时间
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncQuarter(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_QUARTER, expOf(date));
	}

	/**
	 * 返回日期时间所在月的第一天的零分零秒的日期时间.
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncMonth(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_MONTH, expOf(date));
	}

	/**
	 * 返回日期时间所在周的第一天的零分零秒的日期时间时间.周天为一周的第一天.
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncWeek(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_WEEK, expOf(date));
	}

	/**
	 * 返回日期时间当天零分零秒的日期时间
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncDay(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_DAY, expOf(date));
	}

	/**
	 * 返回日期时间所在年是否为闰年
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xIsLeapYear(Object date) {
		return new OperateExpr(ScalarFunction.IS_LEAP_YEAR, expOf(date));
	}

	/**
	 * 返回日期时间所在月是否为闰月
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xIsLeapMonth(Object date) {
		return new OperateExpr(ScalarFunction.IS_LEAP_MONTH, expOf(date));
	}

	/**
	 * 返回日期时间是否为闰日
	 * 
	 * @param date
	 *            日期时间类型表达式
	 * @return
	 */
	public static final OperateExpression xIsLeapDay(Object date) {
		return new OperateExpr(ScalarFunction.IS_LEAP_DAY, expOf(date));
	}

	// ---------------------------- 数学函数 ----------------------------

	/**
	 * 返回角度(以弧度为单位)的正弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xSin(Object radians) {
		return new OperateExpr(ScalarFunction.SIN, expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的余弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xCos(Object radians) {
		return new OperateExpr(ScalarFunction.COS, expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的正切值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xTan(Object radians) {
		return new OperateExpr(ScalarFunction.TAN, expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的反正弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xAsin(Object radians) {
		return new OperateExpr(ScalarFunction.ASIN, expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的反余弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xAcos(Object radians) {
		return new OperateExpr(ScalarFunction.ACOS, expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的反正切值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xAtan(Object radians) {
		return new OperateExpr(ScalarFunction.ATAN, expOf(radians));
	}

	/**
	 * 返回指定值的指数值
	 * 
	 * @param power
	 * @return
	 */
	public static final OperateExpression xExp(Object power) {
		return new OperateExpr(ScalarFunction.EXP, expOf(power));
	}

	/**
	 * 返回指定值的指定幂的值
	 * 
	 * @param base
	 *            底数
	 * @param power
	 *            指数
	 * @return
	 */
	public static final OperateExpression xPower(Object base, Object power) {
		return new OperateExpr(ScalarFunction.POWER, expOf(base), expOf(power));
	}

	/**
	 * 返回指定值以e为底的对数值,即自然对数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLn(Object number) {
		return new OperateExpr(ScalarFunction.LN, expOf(number));
	}

	/**
	 * 返回指定值以10为底的对数值
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLg(Object number) {
		return new OperateExpr(ScalarFunction.LG, expOf(number));
	}

	/**
	 * 返回指定值的平方根
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSqrt(Object number) {
		return new OperateExpr(ScalarFunction.SQRT, expOf(number));
	}

	/**
	 * 返回大于或等于指定值的最小整数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xCeil(Object number) {
		return new OperateExpr(ScalarFunction.CEIL, expOf(number));
	}

	/**
	 * 返回小于或等于指定值的最大整数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xFloor(Object number) {
		return new OperateExpr(ScalarFunction.FLOOR, expOf(number));
	}

	/**
	 * 返回最接近表达式的整数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xRound(Object number) {
		return new OperateExpr(ScalarFunction.ROUND, expOf(number), expOf(0));
	}

	/**
	 * 将表达式舍入到指定的长度或精度
	 * 
	 * @param number
	 * @param length
	 * @return
	 */
	public static final OperateExpression xRound(Object number, Object length) {
		return new OperateExpr(ScalarFunction.ROUND, expOf(number),
				expOf(length));
	}

	/**
	 * 返回参数的符号函数值
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSign(Object number) {
		return new OperateExpr(ScalarFunction.SIGN, expOf(number));
	}

	/**
	 * 返回参数的绝对值
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xAbs(Object number) {
		return new OperateExpr(ScalarFunction.ABS, expOf(number));
	}

	// ---------------------------- 字符串函数 ----------------------------

	/**
	 * 将ASCII代码转换为字符
	 * 
	 * @param ascii
	 * @return
	 */
	@Deprecated
	public static final OperateExpression xChr(Object ascii) {
		return new OperateExpr(ScalarFunction.CHR, expOf(ascii));
	}

	/**
	 * 返回具有指定的整数代码的Unicode字符
	 * 
	 * @param national
	 * @return
	 */
	@Deprecated
	public static final OperateExpression xNchr(Object national) {
		return new OperateExpr(ScalarFunction.NCHR, expOf(national));
	}

	/**
	 * 返回字符表达式中最左侧的字符的ASCII代码值
	 * 
	 * @param str
	 * @return
	 */
	@Deprecated
	public static final OperateExpression xAscii(Object str) {
		return new OperateExpr(ScalarFunction.ASCII, expOf(str));
	}

	/**
	 * 返回指定字符串表达式的字符数或二进制字符串的字节数
	 * 
	 * @param str
	 *            字符串表达式或二进制字符串表达式
	 * @return
	 */
	public static final OperateExpression xLen(Object str) {
		return new OperateExpr(ScalarFunction.LEN, expOf(str));
	}

	/**
	 * 将大写字符数据转换为小写字符数据后返回
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xLower(Object str) {
		return new OperateExpr(ScalarFunction.LOWER, expOf(str));
	}

	/**
	 * 将小写字符数据转换为大写字符数据后返回
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xUpper(Object str) {
		return new OperateExpr(ScalarFunction.UPPER, expOf(str));
	}

	/**
	 * 删除字符表达式的前导空格
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xLtrim(Object str) {
		return new OperateExpr(ScalarFunction.LTRIM, expOf(str));
	}

	/**
	 * 删除字符表达式的尾随空格
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xRtrim(Object str) {
		return new OperateExpr(ScalarFunction.RTRIM, expOf(str));
	}

	/**
	 * 删除字符表达式的前后的空格
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xTrim(Object str) {
		return new OperateExpr(ScalarFunction.TRIM, expOf(str));
	}

	/**
	 * 返回字符串中指定表达式的开始位置
	 * 
	 * @param str
	 *            字符串
	 * @param search
	 *            查找字符串
	 * @return 序号从1开始
	 */
	public static final OperateExpression xIndexOf(Object str, Object search) {
		return new OperateExpr(ScalarFunction.INDEXOF, expOf(str),
				expOf(search), expOf(1));
	}

	/**
	 * 返回字符串中指定表达式的开始位置
	 * 
	 * @param str
	 *            字符串
	 * @param search
	 *            查找字符串
	 * @param position
	 *            开始查找位置
	 * @return 序号从1开始
	 */
	public static final OperateExpression xIndexOf(Object str, Object search,
			Object position) {
		return new OperateExpr(ScalarFunction.INDEXOF, expOf(str),
				expOf(search), expOf(position));
	}

	/**
	 * 返回字符串或二进制表达式的子串
	 * 
	 * @param str
	 *            字符串或二进制字符串表达式
	 * @param position
	 *            开始截断的位置
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position) {
		return new OperateExpr(ScalarFunction.SUBSTR, expOf(str),
				expOf(position));
	}

	/**
	 * 返回字符串或二进制表达式的子串
	 * 
	 * @param str
	 *            字符串或二进制字符串表达式
	 * @param position
	 *            开始截断的位置
	 * @param length
	 *            截断的长度
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position,
			Object length) {
		return new OperateExpr(ScalarFunction.SUBSTR, expOf(str),
				expOf(position), expOf(length));
	}

	/**
	 * 使用字符串替换指定字符串中的匹配段
	 * 
	 * @param str
	 *            要搜索的字符串
	 * @param search
	 *            要查找的字符串
	 * @param replacement
	 *            替换的字符串
	 * @return
	 */
	public static final OperateExpression xReplace(Object str, Object search,
			Object replacement) {
		return new OperateExpr(ScalarFunction.REPLACE, expOf(str),
				expOf(search), expOf(replacement));
	}

	/**
	 * 字符串str左边填补空格字符到长度length,若str长度大于length,则截断str到length.
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static final OperateExpression xLpad(Object str, Object length) {
		return new OperateExpr(ScalarFunction.LPAD, expOf(str), expOf(length));
	}

	/**
	 * 字符串str左边填补pad字符串到长度length,若str长度大于length,则截断str到length.
	 * 
	 * @param str
	 * @param length
	 * @param pad
	 * @return
	 */
	public static final OperateExpression xLpad(Object str, Object length,
			Object pad) {
		return new OperateExpr(ScalarFunction.LPAD, expOf(str), expOf(length),
				expOf(pad));
	}

	/**
	 * 字符串str右边填补空格字符到长度length,若str长度大于length,则截断str到length.
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static final OperateExpression xRpad(Object str, Object length) {
		return new OperateExpr(ScalarFunction.RPAD, expOf(str), expOf(length));
	}

	/**
	 * 字符串str右边填补pad字符串到长度length,若str长度大于length,则截断str到length.
	 * 
	 * @param str
	 * @param length
	 * @param pad
	 * @return
	 */
	public static final OperateExpression xRpad(Object str, Object length,
			Object pad) {
		return new OperateExpr(ScalarFunction.RPAD, expOf(str), expOf(length),
				expOf(pad));
	}

	/**
	 * 目标表达式转换为字符串
	 * 
	 * @param value
	 *            字符串,二进制,日期时间,GUID,数值类型表达式
	 * @return
	 */
	public static final OperateExpression xToChar(Object value) {
		return new OperateExpr(ScalarFunction.TO_CHAR, expOf(value));
	}

	/**
	 * 目标表达式转换为字符串
	 * 
	 * @param value
	 *            日期时间
	 * @param fmt 日期格式
	 * @return
	 */
	public static final OperateExpression xToChar(Object value, Object fmt) {
		return new OperateExpr(ScalarFunction.TO_CHAR, expOf(value), expOf(fmt));
	}
	
	/**
	 * 创建RECID
	 * 
	 * <p>
	 * 不确定函数
	 * 
	 * @return
	 */
	public static final OperateExpression xNewRecid() {
		return OperateExpr.NEW_RECID;
	}

	/**
	 * 字符串转换为整型
	 * 
	 * @param str
	 *            字符串表达式
	 * @return
	 */
	public static final OperateExpression xToInt(Object str) {
		return new OperateExpr(ScalarFunction.TO_INT, expOf(str));
	}

	/**
	 * 字符串转换为GBK字符集的拼音排序序号，序号为二进制串类型。
	 * 
	 * <p>
	 * 数据量大时，需要增加字段保存字符串的排序序号。
	 * 
	 * <p>
	 * 排序结果不是大小写敏感。
	 * 
	 * @param str
	 * @return
	 */
	public static final OperateExpression xCollateGBK(Object str) {
		return new OperateExpr(ScalarFunction.COLLATE_GBK, expOf(str));
	}

	private SQLFunc() {
	}
}
