package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

/**
 * 数据库兼容性异常
 * 
 * @author houchunlei
 * 
 */
public final class DatabaseCompatibleException extends RuntimeException {

	private static final long serialVersionUID = -5769548820965047061L;

	public DatabaseCompatibleException(DbMetadata metadata, String message) {
		super("在数据库[" + metadata.toString() + "]发生兼容性异常：" + message + "。");
	}
}