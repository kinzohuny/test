package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * 本地化信息的
 * 
 * @author gaojingxin
 * 
 */
final class LocaleInfoImpl {

	private short[] frmtInfo;
	final int localeKey;
	final String message;
	final LocaleInfoImpl next;

	LocaleInfoImpl(int localeKey, String message, LocaleInfoImpl next) {
		if (message == null || message.length() == 0) {
			throw new NullArgumentException("message");
		}
		this.message = message;
		this.localeKey = localeKey;
		this.next = next;
	}

	final short[] ensureFormatInfo(InfoDefineImpl infoDefine) {
		short[] frmtInfo = this.frmtInfo;
		if (frmtInfo == null) {
			this.frmtInfo = frmtInfo = infoDefine.buildFrmtInfo(this.message, false);
		}
		return frmtInfo;
	}
}
