package com.jiuqi.dna.core.spi.da;

import com.jiuqi.dna.core.exception.NullArgumentException;

/**
 * dnaSql«Î«Ûª˘¿‡
 * 
 * @author gaojingxin
 * 
 */
public abstract class NewByDnaSql {
	public final String dnaSql;

	NewByDnaSql(String dnaSql) {
		if (dnaSql == null || dnaSql.length() == 0) {
			throw new NullArgumentException("dnaSql");
		}
		this.dnaSql = dnaSql;

	}
}
