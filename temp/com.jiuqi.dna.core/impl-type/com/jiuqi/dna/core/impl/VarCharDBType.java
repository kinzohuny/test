package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.TypeFactory;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 变长字符串类型
 * 
 * @author gaojingxin
 * 
 */
public final class VarCharDBType extends CharsType {
	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		if (target == this) {
			return true;
		}
		if (target instanceof CharDBType) {
			return this.length <= ((CharDBType) target).length;
		} else if (target instanceof VarCharDBType) {
			return this.length <= ((VarCharDBType) target).length;
		}
		return false;
	}

	public final static DataTypeMap map = new DataTypeMap() {
		@Override
		final int keyCode(int length, int precision, int scale) {
			if (length <= 0) {
				throw new IllegalArgumentException("length <= 0");
			}
			if (length > CHAR_LENGTH_MAX) {
				throw new IllegalArgumentException("length > " + CHAR_LENGTH_MAX);
			}
			return length;
		}

		@Override
		final DataTypeBase newType(int length, int precision, int scale) {
			return new VarCharDBType(length);
		}
	};

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inVarChar(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "varchar(" + this.length + ")";
	}

	public final static DataType tryParse(String str) {
		int length = DataTypeBase.tryParseLength(str, "varchar");
		return length > 0 ? TypeFactory.VARCHAR(length) : null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.VARCHAR_H);
		digester.update((short) this.length);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.VARCHAR_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				return TypeFactory.VARCHAR(undigester.extractShort());
			}
		});
	}

	VarCharDBType(int length) {
		super(length);
	}

}