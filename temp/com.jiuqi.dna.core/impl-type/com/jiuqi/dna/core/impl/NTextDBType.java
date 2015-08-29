package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.Undigester;

/**
 * Unicode���ı�
 * 
 * @author gaojingxin
 * 
 */
public final class NTextDBType extends StringType {
	public static final NTextDBType TYPE = new NTextDBType();

	private NTextDBType() {
		super();
	}

	/**
	 * ���ݿ��Ƿ�������ת����Ŀ������
	 */
	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	/**
	 * �Ƿ��Ǵ����
	 */
	@Override
	public final boolean isLOB() {
		return true;
	}

	@Override
	boolean isN() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inNText(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final String toString() {
		return "ntext";
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.NTEXT);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.NTEXT) {
			@Override
			protected DataType doUndigest(Undigester undigester) {
				return TYPE;
			}
		});
	}

	@Override
	public final boolean isDBType() {
		return true;
	}

}