package com.jiuqi.dna.core.impl;

import java.sql.Date;

import com.jiuqi.dna.core.type.DataObjectTranslator;

final class DOT_SqlDate implements DataObjectTranslator<Date, Long> {
	final static short VERSION = 0x0100;

	private DOT_SqlDate() {

	}

	public final boolean supportAssign() {
		return false;
	}

	public Long toDelegateObject(Date date) {
		return date.getTime();
	}

	public short getVersion() {
		return VERSION;
	}

	public final Date resolveInstance(Date destHint, Long time, short version,
			boolean forSerial) {
		final long t = time;
		if (destHint != null && destHint.getClass() == Date.class && destHint.getTime() == t) {
			return destHint;
		}
		return new Date(t);
	}

	public final void recoverData(Date dest, Long delegate, short version,
			boolean forSerial) {
		// do nothing
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
