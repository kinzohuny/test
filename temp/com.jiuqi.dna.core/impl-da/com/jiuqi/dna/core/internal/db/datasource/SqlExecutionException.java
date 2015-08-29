package com.jiuqi.dna.core.internal.db.datasource;

import java.sql.SQLException;

public class SqlExecutionException extends RuntimeException {

	private static final long serialVersionUID = -1897525191364729924L;

	public final String nativeSql;

	public SqlExecutionException(String nativeSql, SQLException cause,
			String message) {
		super(message(nativeSql, message), cause);
		this.nativeSql = nativeSql;
	}

	static final String message(String nativeSql, String message) {
		return message != null && message.length() > 0 ? "SQLÓï¾äÖ´ĞĞ´íÎó£¨" + message + "£©£º" + nativeSql : "SQLÓï¾äÖ´ĞĞ´íÎó£º" + nativeSql;
	}
}