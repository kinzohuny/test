/**
 *
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.Digester;
import com.jiuqi.dna.core.type.TypeDetector;
import com.jiuqi.dna.core.type.Undigester;

/**
 * 长文本类型
 * 
 * @author gaojingxin
 * 
 */
public final class TextDBType extends StringType {
	public static final TextDBType TYPE = new TextDBType();

	private TextDBType() {
		super();
	}

	@Override
	public final boolean canDBTypeConvertTo(DataType target) {
		return target == this;
	}

	@Override
	public final boolean isLOB() {
		return true;
	}

	@Override
	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> caller, TUserData userData) {
		try {
			return caller.inText(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Override
	public final String toString() {
		return "text";
	}

	@Override
	public final void digestType(Digester digester) {
		digester.update(TypeCodeSet.TEXT);
	}

	static {
		DataTypeUndigester.regUndigester(new DataTypeUndigester(TypeCodeSet.TEXT) {
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