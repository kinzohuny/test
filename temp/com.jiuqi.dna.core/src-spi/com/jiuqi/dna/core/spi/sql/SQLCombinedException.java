package com.jiuqi.dna.core.spi.sql;

import com.jiuqi.dna.core.exception.NullArgumentException;

public class SQLCombinedException extends IllegalArgumentException {
	private static final long serialVersionUID = 1191081942359310507L;

	private final Exception[] list;

	public SQLCombinedException(String message, Exception[] exceptions) {
		super(message);
		if (exceptions == null) {
			throw new NullArgumentException("exceptions");
		}
		this.list = exceptions;
	}

	public Exception[] getExceptions() {
		return this.list;
	}

	@Override
	public String getMessage() {
		String msg = super.getMessage();
		if (this.list.length == 0) {
			return msg;
		}
		StringBuilder sb = new StringBuilder();
		if (msg != null) {
			sb.append(msg).append("\r\n");
		}
		for (Exception ex : this.list) {
			if (ex instanceof SQLParseException) {
				SQLParseException spe = (SQLParseException) ex;
				sb.append('ÐÐ').append(spe.line + 1).append('ÁÐ')
						.append(spe.col + 1).append(':').append(' ');
			}
			sb.append(ex.getMessage()).append("\r\n");
		}
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}
}
