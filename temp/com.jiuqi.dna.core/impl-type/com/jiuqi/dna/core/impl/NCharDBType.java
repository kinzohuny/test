package com.jiuqi.dna.core.impl;

import java.io.IOException;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.TypeFactory;
import com.jiuqi.dna.core.type.Undigester;

/**
 * Unicode�����ַ���
 * 
 * @author gaojingxin
 * 
 */
public final class NCharDBType extends CharsType {
	@Override
	public final boolean isFixedLength() {
		return true;
	}

	@Override
	boolean isN() {
		return true;
	}

	/**
	 * ���ݿ��Ƿ���������ת����Ŀ������
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		if (target == this) {
			return true;
		}
		if (target instanceof NCharDBType) {
			return this.length <= ((NCharDBType) target).length;
		} else if (target instanceof NVarCharDBType) {
			return this.length <= ((NVarCharDBType) target).length;
		}
		return false;
	}

	public final static DataTypeMap map = new DataTypeMap() {
		@Override
		final int keyCode(int length, int precision, int scale) {
			if (length <= 0) {
				throw new IllegalArgumentException("length <= 0");
			}
			if (length > NCHAR_LENGTH_MAX) {
				throw new IllegalArgumentException("length > " + NCHAR_LENGTH_MAX);
			}
			return length;
		}

		@Override
		final DataTypeBase newType(int length, int precision, int scale) {
			return new NCharDBType(length);
		}
	};

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inNChar(userData, this);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "nchar(" + this.length + ")";
	}

	public final static DataType tryParse(String str) {
		int length = DataTypeBase.tryParseLength(str, "nchar");
		return length > 0 ? TypeFactory.NCHAR(length) : null;
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.NCHAR_H);
		digester.update((short) this.length);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.NCHAR_H) {
			@Override
			protected DataType doUndigest(Undigester undigester)
					throws IOException {
				return TypeFactory.NCHAR(undigester.extractShort());
			}
		});
	}

	NCharDBType(int length) {
		super(length);
	}

}