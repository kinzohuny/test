package com.jiuqi.dna.core.def;

import com.jiuqi.dna.core.misc.MissingObjectException;

/**
 * ’“≤ªµΩ
 * 
 * @author gaojingxin
 * 
 */
public class MissingDefineException extends MissingObjectException {

	private static final long serialVersionUID = 1L;

	public MissingDefineException() {
		super();
	}

	public MissingDefineException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingDefineException(String message) {
		super(message);
	}

	public MissingDefineException(Throwable cause) {
		super(cause);
	}
}
