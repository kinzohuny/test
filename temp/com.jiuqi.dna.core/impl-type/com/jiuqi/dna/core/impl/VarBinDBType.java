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
 * 变长二进制类型
 * 
 * @author gaojingxin
 * 
 */
public final class VarBinDBType extends BinDBType {
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
			return new VarBinDBType(length);
		}
	};

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inVarBinary(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "varbinary(" + this.length + ")";
	}

	public final static DataType tryParse(String str) {
		int length = DataTypeBase.tryParseLength(str, "varbinary");
		return length > 0 ? TypeFactory.VARBINARY(length) : null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.VARBINARY_H);
		digester.update((short) this.length);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.VARBINARY_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				return TypeFactory.VARBINARY(undigester.extractShort());
			}
		});
	}

	VarBinDBType(int length) {
		super(length);
	}

}