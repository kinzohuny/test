package com.jiuqi.dna.core.da;

import java.sql.SQLException;

/**
 * SQL”Ôæ‰÷¥–– ±¥ÌŒÛ
 * 
 * @author houchunlei
 * 
 */
public class SQLExecutionException extends RuntimeException {

	private static final long serialVersionUID = -6905214814890242891L;

	public final String sql;

	public SQLExecutionException(String message, SQLException cause, String sql) {
		super(message, cause);
		this.sql = sql;
	}
}