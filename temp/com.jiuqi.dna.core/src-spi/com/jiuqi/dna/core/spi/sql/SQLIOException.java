package com.jiuqi.dna.core.spi.sql;

import java.io.IOException;

/**
 * IO�쳣 (������װ��IOException)
 * 
 * @author niuhaifeng
 * 
 */
public class SQLIOException extends SQLParseException {
	private static final long serialVersionUID = 1L;

	@Override
	public SQLErrorCode getErrorCode() {
		return SQLErrorCode.IO_ERROR;
	}

	public SQLIOException(IOException ex) {
		super(0, 0, ex);
	}
}
