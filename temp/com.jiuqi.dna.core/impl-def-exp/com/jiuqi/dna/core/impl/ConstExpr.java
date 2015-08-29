package com.jiuqi.dna.core.impl;

import java.util.Date;

import com.jiuqi.dna.core.def.arg.ArgumentableDefine;
import com.jiuqi.dna.core.def.exp.ConstExpression;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.TypeDetectorBase;

public abstract class ConstExpr extends ValueExpr implements ConstExpression {

	public static final ConstExpr expOf(Object object) {
		if (object == null) {
			return NullExpr.NULL;
		} else if (object instanceof ConstExpr) {
			return (ConstExpr) object;
		} else if (object instanceof GUID) {
			return GUIDConstExpr.valueOf((GUID) object);
		} else if (object instanceof Integer) {
			return IntConstExpr.valueOf(((Integer) object).intValue());
		} else if (object instanceof String || object instanceof Character) {
			return StringConstExpr.valueOf((String) object);
		} else if (object instanceof Date) {
			return DateConstExpr.valueOf(((Date) object).getTime());
		} else if (object instanceof byte[]) {
			return BytesConstExpr.valueOf((byte[]) object);
		} else if (object instanceof Byte) {
			return ByteConstExpr.valueOf(((Byte) object).byteValue());
		} else if (object instanceof Short) {
			return ShortConstExpr.valueOf(((Short) object).shortValue());
		} else if (object instanceof Long) {
			return LongConstExpr.valueOf(((Long) object).longValue());
		} else if (object instanceof Boolean) {
			return BooleanConstExpr.valueOf(((Boolean) object).booleanValue());
		} else if (object instanceof Float) {
			return FloatConstExpr.valueOf(((Float) object).floatValue());
		} else if (object instanceof Double) {
			return DoubleConstExpr.valueOf(((Double) object).doubleValue());
		}
		throw new UnsupportedOperationException("不支持的常量值:" + object);
	}

	public static ConstExpr constOf(DataType type, String str) {
		if (type == null) {
			throw new NullArgumentException("类型");
		}
		DataTypeBase t = (DataTypeBase) type;
		return t.detect(parser, str);
	}

	@Override
	public final String getXMLTagName() {
		return ConstExpr.xml_element_const;
	}

	@Override
	public final void render(SXElement element) {
		element.setAttribute(xml_attr_type, this.getType().toString());
		element.setAttribute(xml_attr_value, this.getString());
	}

	static final String xml_element_const = "const";
	static final String xml_attr_type = "type";
	static final String xml_attr_value = "value";

	@Override
	protected final ConstExpr clone(RelationRefDomain domain,
			ArgumentableDefine arguments) {
		return this;
	}

	@Override
	protected final ConstExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		return this;
	}

	static final ConstExpr loadConst(SXElement element) {
		DataType type = element.getAsType(xml_attr_type, null);
		return type.detect(parser, element.getString(xml_attr_value));
	}

	public static final TypeDetector<ConstExpr, Object> parser = new TypeDetectorBase<ConstExpr, Object>() {

		@Override
		public ConstExpr inString(Object value, SequenceDataType type)
				throws Throwable {
			return StringConstExpr.valueOf(Convert.toString(value));
		}

		@Override
		public ConstExpr inShort(Object value) throws Throwable {
			return ShortConstExpr.valueOf(Convert.toShort(value));
		}

		@Override
		public ConstExpr inLong(Object value) throws Throwable {
			return LongConstExpr.valueOf(Convert.toLong(value));
		}

		@Override
		public ConstExpr inInt(Object value) throws Throwable {
			return IntConstExpr.valueOf(Convert.toInt(value));
		}

		@Override
		public ConstExpr inGUID(Object value) throws Throwable {
			return GUIDConstExpr.valueOf(Convert.toGUID(value));
		}

		@Override
		public ConstExpr inFloat(Object value) throws Throwable {
			return FloatConstExpr.valueOf(Convert.toFloat(value));
		}

		@Override
		public ConstExpr inDouble(Object value) throws Throwable {
			return DoubleConstExpr.valueOf(Convert.toDouble(value));
		}

		@Override
		public ConstExpr inDate(Object value) throws Throwable {
			return DateConstExpr.valueOf(Convert.toDate(value));
		}

		@Override
		public ConstExpr inBytes(Object value, SequenceDataType type)
				throws Throwable {
			return BytesConstExpr.valueOf(Convert.toBytes(value));
		}

		@Override
		public ConstExpr inByte(Object value) throws Throwable {
			return ByteConstExpr.valueOf(Convert.toByte(value));
		}

		@Override
		public ConstExpr inBoolean(Object value) throws Throwable {
			return BooleanConstExpr.valueOf(Convert.toBoolean(value));
		}

	};

	// static final TypeDetectorBase<ConstExpr, Object> BLANK = new
	// TypeDetectorBase<ConstExpr, Object>() {
	//
	// @Override
	// public ConstExpr inBoolean(Object userData) throws Throwable {
	// return BooleanConstExpr.FALSE;
	// }
	//
	// @Override
	// public ConstExpr inByte(Object userData) throws Throwable {
	// return ByteConstExpr.ZERO_BYTE;
	// }
	//
	// @Override
	// public ConstExpr inShort(Object userData) throws Throwable {
	// return ShortConstExpr.ZERO_SHORT;
	// }
	//
	// @Override
	// public ConstExpr inInt(Object userData) throws Throwable {
	// return IntConstExpr.ZERO_INT;
	// }
	//
	// @Override
	// public ConstExpr inLong(Object userData) throws Throwable {
	// return LongConstExpr.ZERO_LONG;
	// }
	//
	// @Override
	// public ConstExpr inDate(Object userData) throws Throwable {
	// return DateConstExpr.ZERO;
	// }
	//
	// @Override
	// public ConstExpr inFloat(Object userData) throws Throwable {
	// return FloatConstExpr.ZERO_FLOAT;
	// }
	//
	// @Override
	// public ConstExpr inDouble(Object userData) throws Throwable {
	// return DoubleConstExpr.ZERO_DOUBLE;
	// }
	//
	// @Override
	// public ConstExpr inString(Object userData, SequenceDataType type)
	// throws Throwable {
	// return StringConstExpr.EMPTY;
	// }
	//
	// @Override
	// public ConstExpr inBytes(Object userData, SequenceDataType type)
	// throws Throwable {
	// return BytesConstExpr.EMPTY;
	// }
	// };
}