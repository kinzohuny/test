package com.jiuqi.dna.core.spi.sql;

/**
 * SQL�����쳣 (�����ʷ��������������)
 * 
 * @author niuhaifeng
 * 
 */
public abstract class SQLParseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public int line;
	public int col;

	public SQLParseException(int line, int col, String message) {
		super(message);
		this.line = line;
		this.col = col;
	}

	public SQLParseException(int line, int col, Throwable ex) {
		super(ex);
		this.line = line;
		this.col = col;
	}

	public SQLParseException(int line, int col, String message, Throwable ex) {
		super(message, ex);
		this.line = line;
		this.col = col;
	}

	public SQLParseException(Throwable ex) {
		super(ex);
	}

	public SQLParseException(String message, Throwable ex) {
		super(message, ex);
	}

	public abstract SQLErrorCode getErrorCode();
}
