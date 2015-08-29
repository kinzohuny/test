/**
 *
 */
package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.TypeFactory;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 定长二进制类型
 * 
 * @author gaojingxin
 * 
 */
public final class FixBinDBType extends BinDBType {
	public final static DataTypeMap map = new DataTypeMap() {
		@Override
		final int keyCode(int length, int precision, int scale) {
			if (length <= 0) {
				throw new IllegalArgumentException("length <= 0");
			}
			if (length > BIN_LENGTH_MAX) {
				throw new IllegalArgumentException("length > " + BIN_LENGTH_MAX);
			}
			return length;
		}

		@Override
		final DataTypeBase newType(int length, int precision, int scale) {
			return new FixBinDBType(length);
		}
	};

	@Override
	public final boolean isFixedLength() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inBinary(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "binary(" + this.length + ")";
	}

	public final static DataType tryParse(String str) {
		int length = DataTypeBase.tryParseLength(str, "binary");
		return length > 0 ? TypeFactory.BINARY(length) : null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.BINARY_H);
		digester.update((short) this.length);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.BINARY_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				return TypeFactory.BINARY(undigester.extractShort());
			}
		});
	}

	FixBinDBType(int length) {
		super(length);
	}

}