package com.jiuqi.dna.core.internal.db.tool;

import java.io.File;

public final class IllegalBackupFileException extends RuntimeException {

	private static final long serialVersionUID = 2152177415447653373L;

	public IllegalBackupFileException(File f) {
		super("�Ƿ������ݿⱸ���ļ���" + f.toString() + "�����ݿ⻹ԭ��������ֹ��");
	}
}