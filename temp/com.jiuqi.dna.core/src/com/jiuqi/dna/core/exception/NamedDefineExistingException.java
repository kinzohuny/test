package com.jiuqi.dna.core.exception;

import com.jiuqi.dna.core.def.NamedDefine;

public final class NamedDefineExistingException extends CoreException {

	public NamedDefineExistingException(String msg) {
		super(msg);
	}

	public NamedDefineExistingException(NamedDefine define) {
		super("����Ϊ[" + define.getName() + "]��Ԫ���Ѿ�����.");
	}

	private static final long serialVersionUID = -3289360691045446371L;

}
