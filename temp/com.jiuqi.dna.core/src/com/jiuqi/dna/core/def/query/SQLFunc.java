package com.jiuqi.dna.core.def.query;

import static com.jiuqi.dna.core.impl.ValueExpr.expOf;

import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.impl.AggregateFunction;
import com.jiuqi.dna.core.impl.OperateExpr;
import com.jiuqi.dna.core.impl.ScalarFunction;
import com.jiuqi.dna.core.impl.ValueExpr;

/**
 * SQL����
 * 
 * @author houchunlei
 * 
 */
public class SQLFunc {

	// ---------------------------- ������ ----------------------------

	/**
	 * �������е�����
	 * 
	 * <p>
	 * ����nullֵ,ͬcount(*)
	 * 
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xCount() {
		return OperateExpr.COUNT_ASTERISK;
	}

	/**
	 * �������е�����
	 * 
	 * <p>
	 * ͬcount(all <code>value</code>)
	 * 
	 * @param value
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xCount(Object value) {
		return new OperateExpr(AggregateFunction.COUNT_ALL, expOf(value));
	}

	/**
	 * �������е�����
	 * 
	 * <p>
	 * ͬcount(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xCountDistinct(Object value) {
		return new OperateExpr(AggregateFunction.COUNT_DISTINCT, expOf(value));
	}

	/**
	 * �������и�ֵ��ƽ��ֵ
	 * 
	 * <p>
	 * ������������,����nullֵ<br>
	 * ͬavg(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvg(Object value) {
		return new OperateExpr(AggregateFunction.AVG_ALL, expOf(value));
	}

	/**
	 * �������и�ֵ��ƽ��ֵ
	 * 
	 * <p>
	 * ������������,����nullֵ���ظ�ֵ<br>
	 * ͬavg(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvgDistinct(Object value) {
		return new OperateExpr(AggregateFunction.AVG_DISTINCT, expOf(value));
	}

	/**
	 * �������б��ʽ�ĺ�
	 * 
	 * <p>
	 * ������������,����nullֵ<br>
	 * ͬsum(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSum(Object value) {
		return new OperateExpr(AggregateFunction.SUM_ALL, expOf(value));
	}

	/**
	 * �������б��ʽ�ĺ�
	 * 
	 * <p>
	 * ������������,����nullֵ���ظ�ֵ<br>
	 * ͬsum(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSumDistinct(Object value) {
		return new OperateExpr(AggregateFunction.SUM_DISTINCT, expOf(value));
	}

	/**
	 * ���ر��ʽ�е����ֵ
	 * 
	 * <p>
	 * ͬmax(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMax(Object value) {
		return new OperateExpr(AggregateFunction.MAX, expOf(value));
	}

	/**
	 * ���ر��ʽ�е����ֵ
	 * 
	 * <p>
	 * ͬmin(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMin(Object value) {
		return new OperateExpr(AggregateFunction.MIN, expOf(value));
	}

	/**
	 * ���ط���ϼƵı�־ֵ
	 * 
	 * @param value
	 * @return
	 * @deprecated ���������ʹ��
	 */
	@Deprecated
	public static final OperateExpression xGrouping(Object value) {
		ValueExpr expr = expOf(value);
		return new OperateExpr(AggregateFunction.GROUPING, expr);
	}

	// ---------------------------- ����ʱ�亯�� ----------------------------

	/**
	 * ��ȡ��ǰϵͳ����ʱ��
	 * 
	 * <p>
	 * ��ȷ������
	 * 
	 * @return ����ʱ����ʽ
	 */
	public static final OperateExpression xGetDate() {
		return OperateExpr.GET_DATE;
	}

	/**
	 * ��������ʱ�������
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xYearOf(Object date) {
		return new OperateExpr(ScalarFunction.YEAR_OF, expOf(date));
	}

	/**
	 * ��������ʱ�������еļ�����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xQuarterOf(Object date) {
		return new OperateExpr(ScalarFunction.QUARTER_OF, expOf(date));
	}

	/**
	 * ��������ʱ�������е�����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMonthOf(Object date) {
		return new OperateExpr(ScalarFunction.MONTH_OF, expOf(date));
	}

	/**
	 * ��������ʱ�������е�����.
	 * 
	 * <p>
	 * ����Ȼ�ܼ���.������Ϊһ�ܵĵ�һ��.
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xWeekOf(Object date) {
		return new OperateExpr(ScalarFunction.WEEK_OF, expOf(date));
	}

	/**
	 * ��������ʱ�������е�����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayOf(Object date) {
		return new OperateExpr(ScalarFunction.DAY_OF, expOf(date));
	}

	/**
	 * ��������ʱ�������е�����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayOfYear(Object date) {
		return new OperateExpr(ScalarFunction.DAY_OF_YEAR, expOf(date));
	}

	/**
	 * ��������ʱ������һ���ڵ����
	 * 
	 * <ul>
	 * <li>������,����<code>1</code>
	 * <li>����һ,����<code>2</code>
	 * <li>���ڶ�,����<code>3</code>
	 * <li>������,����<code>4</code>
	 * <li>������,����<code>5</code>
	 * <li>������,����<code>6</code>
	 * <li>������,����<code>7</code>
	 * </ul>
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayOfWeek(Object date) {
		return new OperateExpr(ScalarFunction.DAY_OF_WEEK, expOf(date));
	}

	/**
	 * ��������ʱ���Сʱ��
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xHourOf(Object date) {
		return new OperateExpr(ScalarFunction.HOUR_OF, expOf(date));
	}

	/**
	 * ��������ʱ��ķ���
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMinuteOf(Object date) {
		return new OperateExpr(ScalarFunction.MINUTE_OF, expOf(date));
	}

	/**
	 * ��������ʱ�������
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xSecondOf(Object date) {
		return new OperateExpr(ScalarFunction.SECOND_OF, expOf(date));
	}

	/**
	 * ��������ʱ�������
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMillisecondOf(Object date) {
		return new OperateExpr(ScalarFunction.MILLISECOND_OF, expOf(date));
	}

	/**
	 * ����ʱ����ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddYear(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_YEAR, expOf(date),
				expOf(interval));
	}

	/**
	 * ����ʱ����ʽ�����Լ���Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddQuarter(Object date,
			Object interval) {
		return new OperateExpr(ScalarFunction.ADD_QUARTER, expOf(date),
				expOf(interval));
	}

	/**
	 * ����ʱ����ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddMonth(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_MONTH, expOf(date),
				expOf(interval));
	}

	/**
	 * ����ʱ����ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddWeek(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_WEEK, expOf(date),
				expOf(interval));
	}

	/**
	 * ����ʱ����ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddDay(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_DAY, expOf(date),
				expOf(interval));
	}

	/**
	 * ����ʱ����ʽ������СʱΪ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddHour(Object date, Object interval) {
		return new OperateExpr(ScalarFunction.ADD_HOUR, expOf(date),
				expOf(interval));
	}

	/**
	 * ����ʱ����ʽ�����Է�Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddMinute(Object date,
			Object interval) {
		return new OperateExpr(ScalarFunction.ADD_MINUTE, expOf(date),
				expOf(interval));
	}

	/**
	 * ����ʱ����ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return ����ʱ�����ͱ��ʽ
	 */
	public static final OperateExpression xAddSecond(Object date,
			Object interval) {
		return new OperateExpr(ScalarFunction.ADD_SECOND, expOf(date),
				expOf(interval));
	}

	/**
	 * ������������ʱ��ļ������
	 * 
	 * @param startDate
	 *            ��ʼ������ʱ����ʽ
	 * @param endDate
	 *            ����������ʱ����ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xYearDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.YEAR_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * ������������ʱ��ļ��������
	 * 
	 * @param startDate
	 *            ��ʼ������ʱ����ʽ
	 * @param startDate
	 *            ����������ʱ����ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xQuarterDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.QUARTER_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * ������������ʱ��ļ������
	 * 
	 * @param startDate
	 *            ��ʼ������ʱ����ʽ
	 * @param startDate
	 *            ����������ʱ����ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMonthDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.MONTH_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * ������������ʱ��ļ������,����һΪһ�ܵĵ�һ��.
	 * 
	 * @param startDate
	 *            ��ʼ������ʱ����ʽ
	 * @param startDate
	 *            ����������ʱ����ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xWeekDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.WEEK_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * ������������ʱ��ļ������
	 * 
	 * @param startDate
	 *            ��ʼ������ʱ����ʽ
	 * @param startDate
	 *            ����������ʱ����ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(ScalarFunction.DAY_DIFF, expOf(startDate),
				expOf(endDate));
	}

	/**
	 * ��������ʱ��������ĵ�һ���������������ʱ��
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncYear(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_YEAR, expOf(date));
	}

	/**
	 * ��������ʱ�����ڼ��ȵĵ�һ���������������ʱ��
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncQuarter(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_QUARTER, expOf(date));
	}

	/**
	 * ��������ʱ�������µĵ�һ���������������ʱ��.
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncMonth(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_MONTH, expOf(date));
	}

	/**
	 * ��������ʱ�������ܵĵ�һ���������������ʱ��ʱ��.����Ϊһ�ܵĵ�һ��.
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncWeek(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_WEEK, expOf(date));
	}

	/**
	 * ��������ʱ�䵱��������������ʱ��
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncDay(Object date) {
		return new OperateExpr(ScalarFunction.TRUNC_DAY, expOf(date));
	}

	/**
	 * ��������ʱ���������Ƿ�Ϊ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xIsLeapYear(Object date) {
		return new OperateExpr(ScalarFunction.IS_LEAP_YEAR, expOf(date));
	}

	/**
	 * ��������ʱ���������Ƿ�Ϊ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xIsLeapMonth(Object date) {
		return new OperateExpr(ScalarFunction.IS_LEAP_MONTH, expOf(date));
	}

	/**
	 * ��������ʱ���Ƿ�Ϊ����
	 * 
	 * @param date
	 *            ����ʱ�����ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xIsLeapDay(Object date) {
		return new OperateExpr(ScalarFunction.IS_LEAP_DAY, expOf(date));
	}

	// ---------------------------- ��ѧ���� ----------------------------

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)������ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xSin(Object radians) {
		return new OperateExpr(ScalarFunction.SIN, expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)������ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xCos(Object radians) {
		return new OperateExpr(ScalarFunction.COS, expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)������ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xTan(Object radians) {
		return new OperateExpr(ScalarFunction.TAN, expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xAsin(Object radians) {
		return new OperateExpr(ScalarFunction.ASIN, expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xAcos(Object radians) {
		return new OperateExpr(ScalarFunction.ACOS, expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xAtan(Object radians) {
		return new OperateExpr(ScalarFunction.ATAN, expOf(radians));
	}

	/**
	 * ����ָ��ֵ��ָ��ֵ
	 * 
	 * @param power
	 * @return
	 */
	public static final OperateExpression xExp(Object power) {
		return new OperateExpr(ScalarFunction.EXP, expOf(power));
	}

	/**
	 * ����ָ��ֵ��ָ���ݵ�ֵ
	 * 
	 * @param base
	 *            ����
	 * @param power
	 *            ָ��
	 * @return
	 */
	public static final OperateExpression xPower(Object base, Object power) {
		return new OperateExpr(ScalarFunction.POWER, expOf(base), expOf(power));
	}

	/**
	 * ����ָ��ֵ��eΪ�׵Ķ���ֵ,����Ȼ����
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLn(Object number) {
		return new OperateExpr(ScalarFunction.LN, expOf(number));
	}

	/**
	 * ����ָ��ֵ��10Ϊ�׵Ķ���ֵ
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLg(Object number) {
		return new OperateExpr(ScalarFunction.LG, expOf(number));
	}

	/**
	 * ����ָ��ֵ��ƽ����
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSqrt(Object number) {
		return new OperateExpr(ScalarFunction.SQRT, expOf(number));
	}

	/**
	 * ���ش��ڻ����ָ��ֵ����С����
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xCeil(Object number) {
		return new OperateExpr(ScalarFunction.CEIL, expOf(number));
	}

	/**
	 * ����С�ڻ����ָ��ֵ���������
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xFloor(Object number) {
		return new OperateExpr(ScalarFunction.FLOOR, expOf(number));
	}

	/**
	 * ������ӽ����ʽ������
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xRound(Object number) {
		return new OperateExpr(ScalarFunction.ROUND, expOf(number), expOf(0));
	}

	/**
	 * �����ʽ���뵽ָ���ĳ��Ȼ򾫶�
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
	 * ���ز����ķ��ź���ֵ
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSign(Object number) {
		return new OperateExpr(ScalarFunction.SIGN, expOf(number));
	}

	/**
	 * ���ز����ľ���ֵ
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xAbs(Object number) {
		return new OperateExpr(ScalarFunction.ABS, expOf(number));
	}

	// ---------------------------- �ַ������� ----------------------------

	/**
	 * ��ASCII����ת��Ϊ�ַ�
	 * 
	 * @param ascii
	 * @return
	 */
	@Deprecated
	public static final OperateExpression xChr(Object ascii) {
		return new OperateExpr(ScalarFunction.CHR, expOf(ascii));
	}

	/**
	 * ���ؾ���ָ�������������Unicode�ַ�
	 * 
	 * @param national
	 * @return
	 */
	@Deprecated
	public static final OperateExpression xNchr(Object national) {
		return new OperateExpr(ScalarFunction.NCHR, expOf(national));
	}

	/**
	 * �����ַ����ʽ���������ַ���ASCII����ֵ
	 * 
	 * @param str
	 * @return
	 */
	@Deprecated
	public static final OperateExpression xAscii(Object str) {
		return new OperateExpr(ScalarFunction.ASCII, expOf(str));
	}

	/**
	 * ����ָ���ַ������ʽ���ַ�����������ַ������ֽ���
	 * 
	 * @param str
	 *            �ַ������ʽ��������ַ������ʽ
	 * @return
	 */
	public static final OperateExpression xLen(Object str) {
		return new OperateExpr(ScalarFunction.LEN, expOf(str));
	}

	/**
	 * ����д�ַ�����ת��ΪСд�ַ����ݺ󷵻�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xLower(Object str) {
		return new OperateExpr(ScalarFunction.LOWER, expOf(str));
	}

	/**
	 * ��Сд�ַ�����ת��Ϊ��д�ַ����ݺ󷵻�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xUpper(Object str) {
		return new OperateExpr(ScalarFunction.UPPER, expOf(str));
	}

	/**
	 * ɾ���ַ����ʽ��ǰ���ո�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xLtrim(Object str) {
		return new OperateExpr(ScalarFunction.LTRIM, expOf(str));
	}

	/**
	 * ɾ���ַ����ʽ��β��ո�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xRtrim(Object str) {
		return new OperateExpr(ScalarFunction.RTRIM, expOf(str));
	}

	/**
	 * ɾ���ַ����ʽ��ǰ��Ŀո�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xTrim(Object str) {
		return new OperateExpr(ScalarFunction.TRIM, expOf(str));
	}

	/**
	 * �����ַ�����ָ�����ʽ�Ŀ�ʼλ��
	 * 
	 * @param str
	 *            �ַ���
	 * @param search
	 *            �����ַ���
	 * @return ��Ŵ�1��ʼ
	 */
	public static final OperateExpression xIndexOf(Object str, Object search) {
		return new OperateExpr(ScalarFunction.INDEXOF, expOf(str),
				expOf(search), expOf(1));
	}

	/**
	 * �����ַ�����ָ�����ʽ�Ŀ�ʼλ��
	 * 
	 * @param str
	 *            �ַ���
	 * @param search
	 *            �����ַ���
	 * @param position
	 *            ��ʼ����λ��
	 * @return ��Ŵ�1��ʼ
	 */
	public static final OperateExpression xIndexOf(Object str, Object search,
			Object position) {
		return new OperateExpr(ScalarFunction.INDEXOF, expOf(str),
				expOf(search), expOf(position));
	}

	/**
	 * �����ַ���������Ʊ��ʽ���Ӵ�
	 * 
	 * @param str
	 *            �ַ�����������ַ������ʽ
	 * @param position
	 *            ��ʼ�ضϵ�λ��
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position) {
		return new OperateExpr(ScalarFunction.SUBSTR, expOf(str),
				expOf(position));
	}

	/**
	 * �����ַ���������Ʊ��ʽ���Ӵ�
	 * 
	 * @param str
	 *            �ַ�����������ַ������ʽ
	 * @param position
	 *            ��ʼ�ضϵ�λ��
	 * @param length
	 *            �ضϵĳ���
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position,
			Object length) {
		return new OperateExpr(ScalarFunction.SUBSTR, expOf(str),
				expOf(position), expOf(length));
	}

	/**
	 * ʹ���ַ����滻ָ���ַ����е�ƥ���
	 * 
	 * @param str
	 *            Ҫ�������ַ���
	 * @param search
	 *            Ҫ���ҵ��ַ���
	 * @param replacement
	 *            �滻���ַ���
	 * @return
	 */
	public static final OperateExpression xReplace(Object str, Object search,
			Object replacement) {
		return new OperateExpr(ScalarFunction.REPLACE, expOf(str),
				expOf(search), expOf(replacement));
	}

	/**
	 * �ַ���str�����ո��ַ�������length,��str���ȴ���length,��ض�str��length.
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static final OperateExpression xLpad(Object str, Object length) {
		return new OperateExpr(ScalarFunction.LPAD, expOf(str), expOf(length));
	}

	/**
	 * �ַ���str����pad�ַ���������length,��str���ȴ���length,��ض�str��length.
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
	 * �ַ���str�ұ���ո��ַ�������length,��str���ȴ���length,��ض�str��length.
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static final OperateExpression xRpad(Object str, Object length) {
		return new OperateExpr(ScalarFunction.RPAD, expOf(str), expOf(length));
	}

	/**
	 * �ַ���str�ұ��pad�ַ���������length,��str���ȴ���length,��ض�str��length.
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
	 * Ŀ����ʽת��Ϊ�ַ���
	 * 
	 * @param value
	 *            �ַ���,������,����ʱ��,GUID,��ֵ���ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xToChar(Object value) {
		return new OperateExpr(ScalarFunction.TO_CHAR, expOf(value));
	}

	/**
	 * Ŀ����ʽת��Ϊ�ַ���
	 * 
	 * @param value
	 *            ����ʱ��
	 * @param fmt ���ڸ�ʽ
	 * @return
	 */
	public static final OperateExpression xToChar(Object value, Object fmt) {
		return new OperateExpr(ScalarFunction.TO_CHAR, expOf(value), expOf(fmt));
	}
	
	/**
	 * ����RECID
	 * 
	 * <p>
	 * ��ȷ������
	 * 
	 * @return
	 */
	public static final OperateExpression xNewRecid() {
		return OperateExpr.NEW_RECID;
	}

	/**
	 * �ַ���ת��Ϊ����
	 * 
	 * @param str
	 *            �ַ������ʽ
	 * @return
	 */
	public static final OperateExpression xToInt(Object str) {
		return new OperateExpr(ScalarFunction.TO_INT, expOf(str));
	}

	/**
	 * �ַ���ת��ΪGBK�ַ�����ƴ��������ţ����Ϊ�����ƴ����͡�
	 * 
	 * <p>
	 * ��������ʱ����Ҫ�����ֶα����ַ�����������š�
	 * 
	 * <p>
	 * ���������Ǵ�Сд���С�
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
