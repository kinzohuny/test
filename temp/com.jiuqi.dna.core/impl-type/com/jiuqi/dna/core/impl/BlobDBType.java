package com.jiuqi.dna.core.impl;

import java.sql.Types;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeDetector;

/**
 * 数据库大对象类型
 * 
 * @author gaojingxin
 * 
 */
public final class BlobDBType extends BytesType {

	public static final BlobDBType TYPE = new BlobDBType();

	private BlobDBType() {
		super();
	}

	/**
	 * 数据库是否允许本类转换成目标类型
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	/**
	 * 是否是大对象
	 */
	@Override
	public final boolean isLOB() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inBlob(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public String toString() {
		return "blob";
	}

	protected int getSqlType() {
		return Types.BLOB;
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

}