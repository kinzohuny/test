package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.da.SQLFuncSpec.ArgumentSpec;
import com.jiuqi.dna.core.da.SQLFuncSpec.SQLFuncPattern;
import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.def.query.SQLFunc;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer.DB2ExprBuffer;
import com.jiuqi.dna.core.type.DataType;

public enum ScalarFunction implements OperatorIntrl {

	GETDATE(new SQLFuncPattern("获取服务器端当前日期时间", DateType.TYPE) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xGetDate();
		}
	}) {

		@Override
		public final boolean isNonDeterministic() {
			return true;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			buffer.getdate();
		}
	},

	YEAR_OF(
			new SQLFuncPattern("获取日期表达式年数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xYearOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.year();
		}
	},

	QUARTER_OF(
			new SQLFuncPattern("返回日期表达式在年中的季度数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xQuarterOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.quarter();
		}

	},

	MONTH_OF(
			new SQLFuncPattern("返回日期表达式在年中的月数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xMonthOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.month();
		}
	},

	WEEK_OF(
			new SQLFuncPattern("返回日期表达式的在年中的周数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xWeekOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.weekofyear();
		}
	},

	DAY_OF(
			new SQLFuncPattern("返回日期表达式的在月中的天数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xDayOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.dayofymonth();
		}
	},

	DAY_OF_YEAR(
			new SQLFuncPattern("返回日期表达式的在年中的天数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xDayOfYear(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.dayofyear();
		}
	},

	DAY_OF_WEEK(
			new SQLFuncPattern("返回日期表达式的在周中的天数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xDayOfWeek(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.dayofweek();
		}
	},

	HOUR_OF(
			new SQLFuncPattern("返回日期表达式的小时数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xHourOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.hour();
		}
	},

	MINUTE_OF(
			new SQLFuncPattern("返回日期表达式的分数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xMinuteOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.minute();
		}
	},

	SECOND_OF(
			new SQLFuncPattern("返回日期表达式的秒数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSecondOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.second();
		}

	},

	MILLISECOND_OF(
			new SQLFuncPattern("返回日期表达式的毫秒数", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xMillisecondOf(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.millisecond();
		}
	},

	ADD_YEAR(
			new SQLFuncPattern("日期表达式增加以年为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddYear(values[0], values[1]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.yearadd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.yearadd();
			}
		}
	},

	ADD_QUARTER(
			new SQLFuncPattern("日期表达式增加以季度为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddQuarter(values[0], values[1]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.quarteradd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.quarteradd();
			}
		}
	},

	ADD_MONTH(
			new SQLFuncPattern("日期表达式增加以月为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddMonth(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.monthadd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.monthadd();
			}
		}

	},

	ADD_WEEK(
			new SQLFuncPattern("日期表达式增加以周为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddWeek(values[0], values[1]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.weekadd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.weekadd();
			}
		}
	},

	ADD_DAY(
			new SQLFuncPattern("日期表达式增加以天为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddDay(values[0], values[1]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.dayadd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.dayadd();
			}
		}
	},

	ADD_HOUR(
			new SQLFuncPattern("日期表达式增加以小时为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddHour(values[0], values[1]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.houradd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.houradd();
			}
		}
	},

	ADD_MINUTE(
			new SQLFuncPattern("日期表达式增加以分为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddMinute(values[0], values[1]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.minuteadd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.minuteadd();
			}
		}
	},

	ADD_SECOND(
			new SQLFuncPattern("日期表达式增加以秒为单位的时间间隔", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAddSecond(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			if (buffer instanceof DB2ExprBuffer && expr.values[0] instanceof ArgumentRefExpr) {
				ArgumentRefExpr are = (ArgumentRefExpr) expr.values[0];
				are.renderUsingRefer(buffer, DateType.TYPE);
				expr.values[1].render(buffer, usages);
				buffer.secondadd();
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				buffer.secondadd();
			}
		}
	},

	YEAR_DIFF(
			new SQLFuncPattern("计算两个日期的间隔年数", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xYearDiff(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.yeardiff();
		}

	},

	QUARTER_DIFF(
			new SQLFuncPattern("计算两个日期的间隔季度数", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xQuarterDiff(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.quarterdiff();
		}
	},

	MONTH_DIFF(
			new SQLFuncPattern("计算两个日期的间隔月数", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xMonthDiff(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.monthdiff();
		}
	},

	DAY_DIFF(
			new SQLFuncPattern("计算两个日期的间隔天数", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xDayDiff(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.daydiff();
		}
	},

	WEEK_DIFF(
			new SQLFuncPattern("计算两个日期的间隔周数", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xWeekDiff(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.weekdiff();
		}

	},

	TRUNC_YEAR(
			new SQLFuncPattern("返回日期时间所在年的第一天的零分零秒的日期时间", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xTruncYear(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.truncyear();
		}

	},

	TRUNC_QUARTER(
			new SQLFuncPattern("返回日期时间所在季度的第一天的零分零秒的日期时间", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xTruncQuarter(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.truncquarter();
		}

	},

	TRUNC_MONTH(
			new SQLFuncPattern("返回日期时间所在月的第一天的零分零秒的日期时间.", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xTruncMonth(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.truncmonth();
		}

	},

	TRUNC_WEEK(
			new SQLFuncPattern("返回日期时间所在周的第一天的零分零秒的日期时间时间.周天为一周的第一天.", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xTruncWeek(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.truncweek();
		}

	},

	TRUNC_DAY(
			new SQLFuncPattern("截取日期到当天的零分零秒", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xTruncDay(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.truncday();
		}

	},

	IS_LEAP_YEAR(
			new SQLFuncPattern("日期是否闰年", BooleanType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIsLeapYear(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.isleapyear();
		}

	},

	IS_LEAP_MONTH(
			new SQLFuncPattern("日期是否闰月", BooleanType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIsLeapMonth(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.isleapmonth();
		}

	},

	IS_LEAP_DAY(
			new SQLFuncPattern("日期是否闰日", BooleanType.TYPE, ScalarFuncArgumentSpecConstants.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIsLeapDay(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.isleapday();
		}
	},

	SIN(
			new SQLFuncPattern("返回角度(以弧度为单位)的正弦值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSin(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.sin();
		}
	},

	COS(
			new SQLFuncPattern("返回角度(以弧度为单位)的余弦值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xCos(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.cos();
		}
	},

	TAN(
			new SQLFuncPattern("返回角度(以弧度为单位)的正切值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xTan(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.tan();
		}
	},

	ASIN(
			new SQLFuncPattern("返回角度(以弧度为单位)的反正弦值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAsin(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.asin();
		}
	},

	ACOS(
			new SQLFuncPattern("返回角度(以弧度为单位)的反余弦值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAcos(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.acos();
		}
	},

	ATAN(
			new SQLFuncPattern("返回角度(以弧度为单位)的反正切值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAtan(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.atan();
		}
	},

	EXP(
			new SQLFuncPattern("返回指定值的指数值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.power) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xExp(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.exp();
		}
	},

	LN(
			new SQLFuncPattern("返回指定值以e为底的对数值,即自然对数", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLn(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.ln();
		}
	},

	LG(
			new SQLFuncPattern("返回指定值以10为底的对数值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLg(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.lg();
		}
	},

	POWER(
			new SQLFuncPattern("返回指定值的指定幂的值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.base, ScalarFuncArgumentSpecConstants.power) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xPower(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			buffer.power();
		}
	},

	SQRT(
			new SQLFuncPattern("返回指定值的平方根", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSqrt(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.sqrt();
		}
	},

	CEIL(
			new SQLFuncPattern("返回大于或等于指定值的最小整数", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xCeil(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.ceil();
		}

	},

	FLOOR(
			new SQLFuncPattern("返回小于或等于指定值的最大整数", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xFloor(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.floor();
		}

	},

	ROUND(
			new SQLFuncPattern("返回最接近表达式的整数", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRound(values[0], Integer.valueOf(0));
				}

			},
			new SQLFuncPattern("返回舍入到精度的数字", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number, ScalarFuncArgumentSpecConstants.scale) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRound(values[0], values[1]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			for (ValueExpr value : expr.values) {
				value.render(buffer, usages);
			}
			buffer.round(expr.values.length);
		}
	},

	SIGN(
			new SQLFuncPattern("返回参数的符号函数值", IntType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSign(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.sign();
		}
	},

	ABS(
			new SQLFuncPattern("返回参数的绝对值", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAbs(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.abs();
		}
	},

	// --------------------------- sql character ---------------------------

	CHR(
			new SQLFuncPattern("将ASCII代码转换为字符", StringType.TYPE, new ArgumentSpec("ascii", "ascii值", IntType.TYPE)) {

				@SuppressWarnings("deprecation")
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xChr(values[0]);
				}

			}) {

		// private final DataTypeBase char1 = CharDBType.map.get(1, 0, 0);
		// ExprUtl.checkNonDecimalNumber(values[0], this.toString());
		// TODO 实际上oracle中,该函数返回varchar2(1),sqlserver是返回char(1)
		// return this.char1;

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.chr();
		}
	},

	NCHR(
			new SQLFuncPattern("返回具有指定的整数代码的Unicode字符", StringType.TYPE, new ArgumentSpec("unicode", "unicode值", IntType.TYPE)) {

				@SuppressWarnings("deprecation")
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xNchr(values[0]);
				}

			}) {

		// final DataTypeBase nchar1 = NCharDBType.map.get(1, 0, 0);
		//
		// public final DataTypeInternal checkValues(ValueExpr[] values) {
		// expect(this, values, 1);
		// ExprUtl.checkNonDecimalNumber(values[0], this.toString());
		// return this.nchar1;
		// }

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.nchr();
		}
	},

	ASCII(
			new SQLFuncPattern("返回字符表达式中最左侧的字符的ASCII代码值", IntType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@SuppressWarnings("deprecation")
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xAscii(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.ascii();
		}

	},

	LEN(
			new SQLFuncPattern("返回指定字符串表达式的字符数", IntType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			},
			new SQLFuncPattern("返回指定二进制字符串表达式的字节数", IntType.TYPE, ScalarFuncArgumentSpecConstants.bytes) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			}) {

		// public final final DataTypeInternal checkValues(ValueExpr[] values) {
		// expect(this, values, 1);
		// DataType type = values[0].getType().getRootType();
		// if (type != BytesType.TYPE && type != StringType.TYPE) {
		// throw new IllegalArgumentException("参数类型错误");
		// }
		// return IntType.TYPE;
		// }

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			DataType type = expr.values[0].getType().getRootType();
			if (type == BytesType.TYPE) {
				expr.values[0].render(buffer, usages);
				buffer.bin_len();
			} else {
				expr.values[0].render(buffer, usages);
				buffer.len();
			}
		}
	},

	INDEXOF(
			new SQLFuncPattern("返回查找字符串(search)在指定字符串(str)中第一出现的序号(从1开始)", IntType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.search) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIndexOf(values[0], values[1]);
				}
			},
			new SQLFuncPattern("返回查找字符串(search)在指定字符串(str)中第一出现的序号(从1开始)", IntType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.search, ScalarFuncArgumentSpecConstants.start_position) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIndexOf(values[0], values[1], values[2]);
				}
			}) {

		// public final DataTypeInternal checkValues(ValueExpr[] values) {
		// expect(this, values, 2, 3);
		// this.stringOrNull(values[0]);
		// this.stringOrNull(values[1]);
		// if (values.length == 3) {
		// ExprUtl.checkNonDecimalNumber(values[2], this.toString());
		// }
		// return IntType.TYPE;
		// }

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			if (expr.values.length == 3) {
				expr.values[2].render(buffer, usages);
			}
			buffer.indexof(expr.values.length);
		}

	},

	UPPER(
			new SQLFuncPattern("将小写字符数据转换为大写字符数据后返回", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xUpper(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.upper();
		}

	},

	LOWER(
			new SQLFuncPattern("将大写字符数据转换为小写字符数据后返回", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLower(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.lower();
		}

	},

	LTRIM(
			new SQLFuncPattern("删除字符表达式的前导空格", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLtrim(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.ltrim();
		}

	},

	RTRIM(
			new SQLFuncPattern("删除字符表达式的尾随空格", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRtrim(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.rtrim();
		}

	},

	TRIM(
			new SQLFuncPattern("删除字符表达式的前后的空格", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xTrim(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.trim();
		}

	},

	REPLACE(
			new SQLFuncPattern("替换原字符串(str)中的所有搜索字符串(search)为新字符串(replace)", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.search, ScalarFuncArgumentSpecConstants.replace) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xReplace(values[0], values[1], values[2]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			expr.values[2].render(buffer, usages);
			buffer.replace();
		}

	},

	SUBSTR(
			new SQLFuncPattern("返回字符串表达式的子串", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.start_position) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSubstr(values[0], values[1]);
				}

			},
			new SQLFuncPattern("返回字符串表达式的子串", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.start_position, ScalarFuncArgumentSpecConstants.substr_length) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSubstr(values[0], values[1], values[2]);
				}

			},
			new SQLFuncPattern("返回二进制字符串表达式的子串", BytesType.TYPE, ScalarFuncArgumentSpecConstants.bytes, ScalarFuncArgumentSpecConstants.start_position) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSubstr(values[0], values[1]);
				}

			},
			new SQLFuncPattern("返回字符串表达式的子串", BytesType.TYPE, ScalarFuncArgumentSpecConstants.bytes, ScalarFuncArgumentSpecConstants.start_position, ScalarFuncArgumentSpecConstants.substr_length) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSubstr(values[0], values[1], values[2]);
				}

			}) {

		// TODO
		// public final DataTypeInternal checkValues(ValueExpr[] values) {
		// expect(this, values, 2, 3);
		// ExprUtl.checkNonDecimalNumber(values[1], this.toString());
		// if (values.length > 2) {
		// ExprUtl.checkNonDecimalNumber(values[2], this.toString());
		// }
		// DataTypeInternal type = values[0].getType();
		// if (type.getRootType() == BytesType.TYPE
		// || type.getRootType() == StringType.TYPE) {
		// return type;
		// }
		// throw new InvalidExpressionException(this.toString(), "字符串或二进制串",
		// type);
		// }

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			DataType type = expr.values[0].getType().getRootType();
			if (type == BytesType.TYPE) {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				if (expr.values.length == 3) {
					expr.values[2].render(buffer, usages);
				}
				buffer.bin_substr(expr.values.length);
			} else {
				expr.values[0].render(buffer, usages);
				expr.values[1].render(buffer, usages);
				if (expr.values.length == 3) {
					expr.values[2].render(buffer, usages);
				}
				buffer.substr(expr.values.length);
			}
		}

	},

	LPAD(
			new SQLFuncPattern("字符串左边填充", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLpad(values[0], values[1]);
				}

			},
			new SQLFuncPattern("字符串左边填充", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length, ScalarFuncArgumentSpecConstants.pad_str) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLpad(values[0], values[1], values[2]);
				}
			}) {

		// public final DataTypeInternal checkValues(ValueExpr[] values) {
		// expect(this, values, 2, 3);
		// DataTypeInternal r = this.stringOrNull(values[0]);
		// ExprUtl.checkNonDecimalNumber(values[1], this.toString());
		// if (values.length == 3) {
		// r = ExprUtl.higherPrecedence(r, this.stringOrNull(values[2]));
		// }
		// return r;
		// }

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			if (expr.values.length == 3) {
				expr.values[2].render(buffer, usages);
			}
			buffer.lpad(expr.values.length);
		}

	},

	RPAD(
			new SQLFuncPattern("字符串右边填充", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRpad(values[0], values[1]);
				}

			},
			new SQLFuncPattern("字符串右边填充", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length, ScalarFuncArgumentSpecConstants.pad_str) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRpad(values[0], values[1], values[2]);
				}
			}) {

		// public final DataTypeInternal checkValues(ValueExpr[] values) {
		// expect(this, values, 2, 3);
		// DataTypeInternal r = this.stringOrNull(values[0]);
		// ExprUtl.checkNonDecimalNumber(values[1], this.toString());
		// if (values.length == 3) {
		// r = ExprUtl.higherPrecedence(r, this.stringOrNull(values[2]));
		// }
		// return r;
		// }

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			expr.values[1].render(buffer, usages);
			if (expr.values.length == 3) {
				expr.values[2].render(buffer, usages);
			}
			buffer.rpad(expr.values.length);
		}
	},

	TO_CHAR(
			new SQLFuncPattern("数值(number)转换为数据库编码类型的字符串", StringType.TYPE, ScalarFuncArgumentSpecConstants.number) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("日期(date)转换为数据库编码类型的字符串", StringType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("日期(date)转换为格式化的字符串", StringType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.string) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0], values[1]);
				}
			},
			new SQLFuncPattern("GUID转换为数据库编码类型的十六进制字符串", StringType.TYPE, new ArgumentSpec("guid", "GUID", GUIDType.TYPE)) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("二进制字符串(bytes)转换为数据库编码类型的十六进制字符串", StringType.TYPE, new ArgumentSpec("bytes", "二进制字符串", BytesType.TYPE)) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("字符串(str)转换为数据库编码类型的字符串", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			ValueExpr value = expr.values[0];
			DataTypeInternal type = value.getType().getRootType();
			expr.values[0].render(buffer, usages);
			if (type instanceof NumberType) {
				buffer.numberstr();
			} else if (type == DateType.TYPE){
				if (expr.values.length > 1) {
					expr.values[1].render(buffer, usages);
					buffer.datestr();
				} else {
					buffer.to_char();
				}
			} else if (type == BooleanType.TYPE  || type == StringType.TYPE) {
				buffer.to_char();
			} else if (type == BytesType.TYPE || type == GUIDType.TYPE) {
				buffer.hexstr();
			} else {
				throw new UnsupportedOperationException();
			}
		}

	},

	NEW_RECID(new SQLFuncPattern("创建GUID类型值", GUIDType.TYPE) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xNewRecid();
		}
	}) {

		@Override
		public final boolean isNonDeterministic() {
			return true;
		}

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			buffer.new_recid();
		}

	},

	TO_INT(
			new SQLFuncPattern("转换为整型数值", IntType.TYPE, ScalarFuncArgumentSpecConstants.string) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToInt(values[0]);
				}
			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.to_int();
		}

	},

	COLLATE_GBK(
			new SQLFuncPattern("转换为gbk字符集的拼音排序规则", BytesType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xCollateGBK(values[0]);
				}

			}) {

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.collate_gbk();
		}

	};

	public final SQLFuncPattern patterns[];

	private ScalarFunction(SQLFuncPattern... patterns) {
		this.patterns = patterns;
	}

	public final DataTypeInternal checkValues(ValueExpr[] values) {
		for (SQLFuncPattern pattern : this.patterns) {
			if (pattern.accept(values)) {
				return (DataTypeInternal) pattern.type;
			}
		}
		throw new UnsupportedOperationException("系统函数[" + this.name() + "]不支持传入的参数列表。");
	}

	public boolean isNonDeterministic() {
		return false;
	}

}
