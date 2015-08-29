/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.type.AssignCapability;
import com.jiuqi.dna.core.type.Convert;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.GUID;
import com.jiuqi.dna.core.type.SequenceDataType;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.Undigester;

/**
 * GUID类型
 * 
 * @author gaojingxin
 * 
 */
public final class GUIDType extends ObjectDataTypeBase {

	public static final GUIDType TYPE = new GUIDType();

	@Override
	public final DataTypeInternal getRootType() {
		return BytesType.TYPE;
	}

	@Override
	protected final GUID calcTypeID() {
		return calcNativeTypeID(ENTRY_TYPE_GUID);
	}

	private GUIDType() {
		super(GUID.class);
	}

	@Override
	public final boolean isBytes() {
		return true;
	}

	@Override
	public Object convert(Object from) {
		if (from == null || from.getClass() == GUID.class) {
			return from;
		}
		return Convert.toGUID(from);
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	@Override
	public final AssignCapability isAssignableFrom(DataType another) {
		if (another == null) {
			throw new NullArgumentException("类型");
		}
		return another.detect(assignbility, this);
	}

	@Override
	public final Object assignNoCheckSrc(Object src, Object dest,
			OBJAContext objaContext, DataTranslatorHelper<?, ?> dth) {
		return src;
	}

	private static final TypeDetector<AssignCapability, DataType> assignbility = new AssignbilityBase() {

		@Override
		public AssignCapability inGUID(DataType to) throws Throwable {
			return AssignCapability.SAME;
		}

		@Override
		public AssignCapability inString(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inBytes(DataType to, SequenceDataType type)
				throws Throwable {
			return AssignCapability.CONVERT;
		}

		@Override
		public AssignCapability inNull(DataType to) throws Throwable {
			return AssignCapability.IMPLICIT;
		}
	};

	@Override
	public final String toString() {
		return "guid";
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inGUID(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.GUID);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.GUID) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	// //////////////////////////////////////////
	// / NEW IO Serialization
	// //////////////////////////////////////////
	@Override
	public final boolean nioSerializeData(final NSerializer serializer,
			final Object object) {
		return serializer.writeGUIDData((GUID) object);
	}

}