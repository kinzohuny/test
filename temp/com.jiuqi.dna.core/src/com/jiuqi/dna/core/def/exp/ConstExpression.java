package com.jiuqi.dna.core.def.exp;

import java.util.Date;

import com.jiuqi.dna.core.impl.BooleanConstExpr;
import com.jiuqi.dna.core.impl.ByteConstExpr;
import com.jiuqi.dna.core.impl.BytesConstExpr;
import com.jiuqi.dna.core.impl.ConstExpr;
import com.jiuqi.dna.core.impl.DateConstExpr;
import com.jiuqi.dna.core.impl.DoubleConstExpr;
import com.jiuqi.dna.core.impl.FloatConstExpr;
import com.jiuqi.dna.core.impl.GUIDConstExpr;
import com.jiuqi.dna.core.impl.IntConstExpr;
import com.jiuqi.dna.core.impl.LongConstExpr;
import com.jiuqi.dna.core.impl.ShortConstExpr;
import com.jiuqi.dna.core.impl.StringConstExpr;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.ReadableValue;

/**
 * 常量表达式
 * 
 * @author gaojingxin
 * 
 */
public interface ConstExpression extends ValueExpression, ReadableValue {

	public static final ConstExpressionBuilder builder = new ConstExpression.ConstExpressionBuilder() {

		public final ConstExpression expOf(Object value) {
			return ConstExpr.expOf(value);
		}

		public ConstExpression expOf(boolean value) {
			return BooleanConstExpr.valueOf(value);
		}

		public ConstExpression expOf(byte value) {
			return ByteConstExpr.valueOf(value);
		}

		public ConstExpression expOf(short value) {
			return ShortConstExpr.valueOf(value);
		}

		public ConstExpression expOf(char value) {
			return StringConstExpr.valueOf(String.valueOf(value));
		}

		public ConstExpression expOf(int value) {
			return IntConstExpr.valueOf(value);
		}

		public ConstExpression expOf(long value) {
			return LongConstExpr.valueOf(value);
		}

		public ConstExpression expOf(float value) {
			return FloatConstExpr.valueOf(value);
		}

		public ConstExpression expOf(double value) {
			return DoubleConstExpr.valueOf(value);
		}

		public ConstExpression expOf(byte[] value) {
			return BytesConstExpr.valueOf(value);
		}

		public ConstExpression expOf(String value) {
			return StringConstExpr.valueOf(value);
		}

		public ConstExpression expOf(Date value) {
			return DateConstExpr.valueOf(value.getTime());
		}

		public ConstExpression expOfDate(long value) {
			return DateConstExpr.valueOf(value);
		}

		public ConstExpression expOf(GUID value) {
			return GUIDConstExpr.valueOf(value);
		}
	};

	public static final ConstExpression TRUE = builder.expOf(true);

	public static final ConstExpression FALSE = builder.expOf(false);

	/**
	 * 常量表达式构造器
	 * 
	 * @author gaojingxin
	 */
	public static interface ConstExpressionBuilder extends
			ValueExpressionBuilder {

		public ConstExpression expOf(Object value);

		public ConstExpression expOf(boolean value);

		public ConstExpression expOf(byte value);

		public ConstExpression expOf(short value);

		public ConstExpression expOf(char value);

		public ConstExpression expOf(int value);

		public ConstExpression expOf(long value);

		public ConstExpression expOf(float value);

		public ConstExpression expOf(double value);

		public ConstExpression expOf(byte[] value);

		public ConstExpression expOf(String value);

		public ConstExpression expOf(Date value);

		public ConstExpression expOfDate(long value);

		public ConstExpression expOf(GUID value);
	}
}