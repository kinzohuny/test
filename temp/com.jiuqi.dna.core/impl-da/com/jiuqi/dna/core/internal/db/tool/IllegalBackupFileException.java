package com.jiuqi.dna.core.internal.db.tool;

import java.io.File;

public final class IllegalBackupFileException extends RuntimeException {

	private static final long serialVersionUID = 2152177415447653373L;

	public IllegalBackupFileException(File f) {
		super("非法的数据库备份文件：" + f.toString() + "。数据库还原操作被中止。");
	}
}