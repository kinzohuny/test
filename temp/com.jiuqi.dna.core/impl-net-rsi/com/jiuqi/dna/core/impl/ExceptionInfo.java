package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.obja.StructClass;
import com.jiuqi.dna.core.exception.ExceptionFromRemote;

/**
 * �쳣����Ϣ���������л���Զ�̽ڵ㻹ԭ�쳣��Ϣ����
 * 
 * @author gaojingxin
 * 
 */
@StructClass
final class ExceptionInfo {
	public final StackTraceElement[] stackTrace;
	public final String message;
	public final String exceptionClassName;
	public final ExceptionInfo cause;

	final ExceptionFromRemote toException() {
		ExceptionFromRemote efr;
		if (this.cause != null) {
			efr = new ExceptionFromRemote(this.message, this.exceptionClassName, this.cause.toException());
		} else {
			efr = new ExceptionFromRemote(this.message, this.exceptionClassName);
		}
		efr.setStackTrace(this.stackTrace);
		return efr;
	}

	ExceptionInfo(Throwable e) {
		this.exceptionClassName = e.getClass().getName();
		this.stackTrace = e.getStackTrace();
		this.message = e.getMessage();
		Throwable cause = e.getCause();
		if (cause != null) {
			this.cause = new ExceptionInfo(cause);
		} else {
			this.cause = null;
		}
	}
}