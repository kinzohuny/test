package com.jiuqi.dna.core.da;

import java.sql.SQLException;

/**
 * 唯一性约束冲突异常
 * 
 * @author houchunlei
 * 
 */
public class SQLUniqueConstraintViolationException extends
		SQLExecutionException {

	private static final long serialVersionUID = 3928173182863151327L;

	public SQLUniqueConstraintViolationException(String message,
			SQLException cause, String sql) {
		super(message, cause, sql);
	}
}