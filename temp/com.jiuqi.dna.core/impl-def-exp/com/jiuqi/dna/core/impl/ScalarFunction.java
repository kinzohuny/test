package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.da.SQLFuncSpec.ArgumentSpec;
import com.jiuqi.dna.core.da.SQLFuncSpec.SQLFuncPattern;
import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.def.query.SQLFunc;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlExprBuffer;
import com.jiuqi.dna.core.internal.db.support.db2.sqlbuffer.DB2ExprBuffer;
import com.jiuqi.dna.core.type.DataType;

public enum ScalarFunction implements OperatorIntrl {

	GETDATE(new SQLFuncPattern("��ȡ�������˵�ǰ����ʱ��", DateType.TYPE) {
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
			new SQLFuncPattern("��ȡ���ڱ��ʽ����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�������ڱ��ʽ�����еļ�����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�������ڱ��ʽ�����е�����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
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
			new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
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
			new SQLFuncPattern("�������ڱ��ʽ�������е�����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�������ڱ��ʽ��Сʱ��", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
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
			new SQLFuncPattern("�������ڱ��ʽ�ķ���", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
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
			new SQLFuncPattern("�������ڱ��ʽ������", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
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
			new SQLFuncPattern("�������ڱ��ʽ�ĺ�����", IntType.TYPE, ScalarFuncArgumentSpecConstants.date) {
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
			new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
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
			new SQLFuncPattern("���ڱ��ʽ�����Լ���Ϊ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
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
			new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {

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
			new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
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
			new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
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
			new SQLFuncPattern("���ڱ��ʽ������СʱΪ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {
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
			new SQLFuncPattern("���ڱ��ʽ�����Է�Ϊ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {

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
			new SQLFuncPattern("���ڱ��ʽ��������Ϊ��λ��ʱ����", DateType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.interval) {

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
			new SQLFuncPattern("�����������ڵļ������", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

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
			new SQLFuncPattern("�����������ڵļ��������", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

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
			new SQLFuncPattern("�����������ڵļ������", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

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
			new SQLFuncPattern("�����������ڵļ������", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

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
			new SQLFuncPattern("�����������ڵļ������", IntType.TYPE, ScalarFuncArgumentSpecConstants.startdate, ScalarFuncArgumentSpecConstants.enddate) {

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
			new SQLFuncPattern("��������ʱ��������ĵ�һ���������������ʱ��", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("��������ʱ�����ڼ��ȵĵ�һ���������������ʱ��", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("��������ʱ�������µĵ�һ���������������ʱ��.", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("��������ʱ�������ܵĵ�һ���������������ʱ��ʱ��.����Ϊһ�ܵĵ�һ��.", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("��ȡ���ڵ�������������", DateType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�����Ƿ�����", BooleanType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�����Ƿ�����", BooleanType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("�����Ƿ�����", BooleanType.TYPE, ScalarFuncArgumentSpecConstants.date) {

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
			new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)������ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

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
			new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)������ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

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
			new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)������ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

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
			new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

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
			new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

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
			new SQLFuncPattern("���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.radians) {

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
			new SQLFuncPattern("����ָ��ֵ��ָ��ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.power) {

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
			new SQLFuncPattern("����ָ��ֵ��eΪ�׵Ķ���ֵ,����Ȼ����", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

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
			new SQLFuncPattern("����ָ��ֵ��10Ϊ�׵Ķ���ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

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
			new SQLFuncPattern("����ָ��ֵ��ָ���ݵ�ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.base, ScalarFuncArgumentSpecConstants.power) {

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
			new SQLFuncPattern("����ָ��ֵ��ƽ����", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

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
			new SQLFuncPattern("���ش��ڻ����ָ��ֵ����С����", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

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
			new SQLFuncPattern("����С�ڻ����ָ��ֵ���������", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

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
			new SQLFuncPattern("������ӽ����ʽ������", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRound(values[0], Integer.valueOf(0));
				}

			},
			new SQLFuncPattern("�������뵽���ȵ�����", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number, ScalarFuncArgumentSpecConstants.scale) {

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
			new SQLFuncPattern("���ز����ķ��ź���ֵ", IntType.TYPE, ScalarFuncArgumentSpecConstants.number) {

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
			new SQLFuncPattern("���ز����ľ���ֵ", DoubleType.TYPE, ScalarFuncArgumentSpecConstants.number) {

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
			new SQLFuncPattern("��ASCII����ת��Ϊ�ַ�", StringType.TYPE, new ArgumentSpec("ascii", "asciiֵ", IntType.TYPE)) {

				@SuppressWarnings("deprecation")
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xChr(values[0]);
				}

			}) {

		// private final DataTypeBase char1 = CharDBType.map.get(1, 0, 0);
		// ExprUtl.checkNonDecimalNumber(values[0], this.toString());
		// TODO ʵ����oracle��,�ú�������varchar2(1),sqlserver�Ƿ���char(1)
		// return this.char1;

		public final void render(ISqlExprBuffer buffer, TableUsages usages,
				OperateExpr expr) {
			expr.values[0].render(buffer, usages);
			buffer.chr();
		}
	},

	NCHR(
			new SQLFuncPattern("���ؾ���ָ�������������Unicode�ַ�", StringType.TYPE, new ArgumentSpec("unicode", "unicodeֵ", IntType.TYPE)) {

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
			new SQLFuncPattern("�����ַ����ʽ���������ַ���ASCII����ֵ", IntType.TYPE, ScalarFuncArgumentSpecConstants.string) {

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
			new SQLFuncPattern("����ָ���ַ������ʽ���ַ���", IntType.TYPE, ScalarFuncArgumentSpecConstants.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			},
			new SQLFuncPattern("����ָ���������ַ������ʽ���ֽ���", IntType.TYPE, ScalarFuncArgumentSpecConstants.bytes) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			}) {

		// public final final DataTypeInternal checkValues(ValueExpr[] values) {
		// expect(this, values, 1);
		// DataType type = values[0].getType().getRootType();
		// if (type != BytesType.TYPE && type != StringType.TYPE) {
		// throw new IllegalArgumentException("�������ʹ���");
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
			new SQLFuncPattern("���ز����ַ���(search)��ָ���ַ���(str)�е�һ���ֵ����(��1��ʼ)", IntType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.search) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIndexOf(values[0], values[1]);
				}
			},
			new SQLFuncPattern("���ز����ַ���(search)��ָ���ַ���(str)�е�һ���ֵ����(��1��ʼ)", IntType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.search, ScalarFuncArgumentSpecConstants.start_position) {

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
			new SQLFuncPattern("��Сд�ַ�����ת��Ϊ��д�ַ����ݺ󷵻�", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

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
			new SQLFuncPattern("����д�ַ�����ת��ΪСд�ַ����ݺ󷵻�", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

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
			new SQLFuncPattern("ɾ���ַ����ʽ��ǰ���ո�", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

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
			new SQLFuncPattern("ɾ���ַ����ʽ��β��ո�", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

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
			new SQLFuncPattern("ɾ���ַ����ʽ��ǰ��Ŀո�", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {

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
			new SQLFuncPattern("�滻ԭ�ַ���(str)�е����������ַ���(search)Ϊ���ַ���(replace)", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.search, ScalarFuncArgumentSpecConstants.replace) {
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
			new SQLFuncPattern("�����ַ������ʽ���Ӵ�", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.start_position) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSubstr(values[0], values[1]);
				}

			},
			new SQLFuncPattern("�����ַ������ʽ���Ӵ�", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.start_position, ScalarFuncArgumentSpecConstants.substr_length) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSubstr(values[0], values[1], values[2]);
				}

			},
			new SQLFuncPattern("���ض������ַ������ʽ���Ӵ�", BytesType.TYPE, ScalarFuncArgumentSpecConstants.bytes, ScalarFuncArgumentSpecConstants.start_position) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xSubstr(values[0], values[1]);
				}

			},
			new SQLFuncPattern("�����ַ������ʽ���Ӵ�", BytesType.TYPE, ScalarFuncArgumentSpecConstants.bytes, ScalarFuncArgumentSpecConstants.start_position, ScalarFuncArgumentSpecConstants.substr_length) {

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
		// throw new InvalidExpressionException(this.toString(), "�ַ���������ƴ�",
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
			new SQLFuncPattern("�ַ���������", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLpad(values[0], values[1]);
				}

			},
			new SQLFuncPattern("�ַ���������", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length, ScalarFuncArgumentSpecConstants.pad_str) {

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
			new SQLFuncPattern("�ַ����ұ����", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRpad(values[0], values[1]);
				}

			},
			new SQLFuncPattern("�ַ����ұ����", StringType.TYPE, ScalarFuncArgumentSpecConstants.string, ScalarFuncArgumentSpecConstants.pad_length, ScalarFuncArgumentSpecConstants.pad_str) {

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
			new SQLFuncPattern("��ֵ(number)ת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE, ScalarFuncArgumentSpecConstants.number) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("����(date)ת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE, ScalarFuncArgumentSpecConstants.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("����(date)ת��Ϊ��ʽ�����ַ���", StringType.TYPE, ScalarFuncArgumentSpecConstants.date, ScalarFuncArgumentSpecConstants.string) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0], values[1]);
				}
			},
			new SQLFuncPattern("GUIDת��Ϊ���ݿ�������͵�ʮ�������ַ���", StringType.TYPE, new ArgumentSpec("guid", "GUID", GUIDType.TYPE)) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("�������ַ���(bytes)ת��Ϊ���ݿ�������͵�ʮ�������ַ���", StringType.TYPE, new ArgumentSpec("bytes", "�������ַ���", BytesType.TYPE)) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToChar(values[0]);
				}
			},
			new SQLFuncPattern("�ַ���(str)ת��Ϊ���ݿ�������͵��ַ���", StringType.TYPE, ScalarFuncArgumentSpecConstants.string) {
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

	NEW_RECID(new SQLFuncPattern("����GUID����ֵ", GUIDType.TYPE) {

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
			new SQLFuncPattern("ת��Ϊ������ֵ", IntType.TYPE, ScalarFuncArgumentSpecConstants.string) {
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
			new SQLFuncPattern("ת��Ϊgbk�ַ�����ƴ���������", BytesType.TYPE, ScalarFuncArgumentSpecConstants.string) {

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
		throw new UnsupportedOperationException("ϵͳ����[" + this.name() + "]��֧�ִ���Ĳ����б�");
	}

	public boolean isNonDeterministic() {
		return false;
	}

}
