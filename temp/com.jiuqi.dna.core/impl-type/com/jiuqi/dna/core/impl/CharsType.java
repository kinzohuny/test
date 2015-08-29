package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataType;

/**
 * ���ݿ��ַ�����
 * 
 * @author gaojingxin
 * 
 */
public abstract class CharsType extends StringType {

	public final static int CHAR_LENGTH_MAX = 2000;
	public final static int NCHAR_LENGTH_MAX = 1000;

	public static DataType tryParse(String str) {
		DataType type = NVarCharDBType.tryParse(str);
		if (type == null) {
			type = VarCharDBType.tryParse(str);
			if (type == null) {
				type = NCharDBType.tryParse(str);
				if (type == null) {
					type = CharDBType.tryParse(str);
				}
			}
		}
		return type;
	}

	public final int length;

	@Override
	public final int getMaxLength() {
		return this.length;
	}

	@Override
	final void regThisDataTypeInConstructor() {
	}

	public CharsType(int length) {
		super();
		this.length = length;
		regDataType(this);
	}

	@Override
	public final boolean isDBType() {
		return true;
	}
}