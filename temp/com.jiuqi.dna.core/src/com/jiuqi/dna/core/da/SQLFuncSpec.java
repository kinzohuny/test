package com.jiuqi.dna.core.da;

import com.jiuqi.dna.core.def.exp.OperateExpression;
import com.jiuqi.dna.core.def.exp.ValueExpression;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.ScalarFunction;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.DataType;

/**
 * DNA-SQL֧�ֺ����Ĺ��
 * 
 * @author houchunlei
 * 
 */
public enum SQLFuncSpec implements IFuncSpec {

	getdate(ScalarFunction.GETDATE),

	yearof(ScalarFunction.YEAR_OF),

	quarterof(ScalarFunction.QUARTER_OF),

	monthof(ScalarFunction.MONTH_OF),

	weekof(ScalarFunction.WEEK_OF),

	dayof(ScalarFunction.DAY_OF),

	dayofyear(ScalarFunction.DAY_OF_YEAR),

	dayofweek(ScalarFunction.DAY_OF_WEEK),

	hourof(ScalarFunction.HOUR_OF),

	minuteof(ScalarFunction.MINUTE_OF),

	secondof(ScalarFunction.SECOND_OF),

	millisecondof(ScalarFunction.MILLISECOND_OF),

	addyear(ScalarFunction.ADD_YEAR),

	addquarter(ScalarFunction.ADD_QUARTER),

	addmonth(ScalarFunction.ADD_MONTH),

	addweek(ScalarFunction.ADD_WEEK),

	addday(ScalarFunction.ADD_DAY),

	addhour(ScalarFunction.ADD_HOUR),

	addminute(ScalarFunction.ADD_MINUTE),

	addsecond(ScalarFunction.ADD_SECOND),

	yeardiff(ScalarFunction.YEAR_DIFF),

	quarterdiff(ScalarFunction.QUARTER_DIFF),

	monthdiff(ScalarFunction.MONTH_DIFF),

	daydiff(ScalarFunction.DAY_DIFF),

	weekdiff(ScalarFunction.WEEK_DIFF),

	truncyear(ScalarFunction.TRUNC_YEAR),

	truncquarter(ScalarFunction.TRUNC_QUARTER),

	truncmonth(ScalarFunction.TRUNC_MONTH),

	truncweek(ScalarFunction.TRUNC_WEEK),

	truncday(ScalarFunction.TRUNC_DAY),

	isleapyear(ScalarFunction.IS_LEAP_YEAR),

	isleapmonth(ScalarFunction.IS_LEAP_MONTH),

	isleapday(ScalarFunction.IS_LEAP_DAY),

	sin(ScalarFunction.SIN),

	cos(ScalarFunction.COS),

	tan(ScalarFunction.TAN),

	asin(ScalarFunction.ASIN),

	acos(ScalarFunction.ACOS),

	atan(ScalarFunction.ATAN),

	exp(ScalarFunction.EXP),

	power(ScalarFunction.POWER),

	ln(ScalarFunction.LN),

	lg(ScalarFunction.LG),

	sqrt(ScalarFunction.SQRT),

	ceil(ScalarFunction.CEIL),

	floor(ScalarFunction.FLOOR),

	round(ScalarFunction.ROUND),

	sign(ScalarFunction.SIGN),

	abs(ScalarFunction.ABS),

	chr(ScalarFunction.CHR),

	nchr(ScalarFunction.NCHR),

	ascii(ScalarFunction.ASCII),

	len(ScalarFunction.LEN),

	indexof(ScalarFunction.INDEXOF),

	lower(ScalarFunction.LOWER),

	upper(ScalarFunction.UPPER),

	ltrim(ScalarFunction.LTRIM),

	rtrim(ScalarFunction.RTRIM),

	trim(ScalarFunction.TRIM),

	substr(ScalarFunction.SUBSTR),

	replace(ScalarFunction.REPLACE),

	to_char(ScalarFunction.TO_CHAR),

	new_recid(ScalarFunction.NEW_RECID),

	to_int(ScalarFunction.TO_INT),

	lpad(ScalarFunction.LPAD),

	rpad(ScalarFunction.RPAD),

	collate_gbk(ScalarFunction.COLLATE_GBK);

	private ScalarFunction function;

	private SQLFuncSpec(ScalarFunction function) {
		this.function = function;
	}

	public final SQLFuncPattern accept(DataType[] types) {
		for (SQLFuncPattern p : this.function.patterns) {
			if (p.accept(types)) {
				return p;
			}
		}
		return null;
	}

	public final SQLFuncPattern getPattern(int i) {
		return this.function.patterns[i];
	}

	public final int size() {
		return this.function.patterns.length;
	}

	/**
	 * �����Ĳ������˵��
	 * 
	 * @author houchunlei
	 * 
	 */
	public static class ArgumentSpec {

		/**
		 * Ĭ�ϵı�־��
		 */
		public final String str;

		/**
		 * ����˵��
		 */
		public final String description;

		/**
		 * ��������������
		 */
		public final DataType type;

		public ArgumentSpec(String str, String description, DataType type) {
			this.str = str;
			this.description = description;
			this.type = type;
		}

		public boolean accept(DataType type) {
			AssignCapability ac = this.type.isAssignableFrom(type);
			if (ac == AssignCapability.SAME || ac == AssignCapability.IMPLICIT) {
				return true;
			}
			return false;
		}

		public static final ArgumentSpec[] EMPTY_ARRAY = new ArgumentSpec[] {};
	}

	public static abstract class SQLFuncPattern {

		/**
		 * ģʽ��˵��
		 */
		public final String description;

		/**
		 * ģʽ�ķ���ֵ����
		 */
		public final DataType type;

		/**
		 * ģʽ�Ĳ�����Ϣ
		 */
		public final ArgumentSpec[] args;

		public SQLFuncPattern(String description, DataType type,
				ArgumentSpec... args) {
			this.description = description;
			this.type = type;
			if (args == null) {
				this.args = ArgumentSpec.EMPTY_ARRAY;
			} else {
				this.args = args;
			}
		}

		/**
		 * �����Ƿ����ָ�����������б�Ĳ���ֵ
		 * 
		 * @param types
		 * @return
		 */
		public final boolean accept(DataType[] types) {
			if (types == null) {
				throw new NullArgumentException("���������б�Ϊ�ա�");
			}
			if (types.length != this.args.length) {
				return false;
			}
			final int c = types.length;
			for (int i = 0; i < c; i++) {
				if (this.args[i].accept(types[i])) {
					continue;
				}
				return false;
			}
			return true;
		}

		public final boolean accept(ValueExpression[] values) {
			if (values == null) {
				throw new NullArgumentException("���������б�Ϊ�ա�");
			}
			if (values.length != this.args.length) {
				return false;
			}
			final int c = values.length;
			for (int i = 0; i < c; i++) {
				if (this.args[i].accept(values[i].getType())) {
					continue;
				}
				return false;
			}
			return true;
		}

		/**
		 * ������ʽ������ǰȷ�Ϻ����ӿ�ָ���Ĳ���ǩ�������ܡ�
		 * 
		 * @param values
		 * @return
		 */
		public abstract OperateExpression expOf(Object[] values);

	}

	public static final boolean contains(String name) {
		try {
			return SQLFuncSpec.valueOf(name.toLowerCase()) != null;
		} catch (Throwable e) {
			return false;
		}
	}

	public final String functionName() {
		return this.name();
	}

}