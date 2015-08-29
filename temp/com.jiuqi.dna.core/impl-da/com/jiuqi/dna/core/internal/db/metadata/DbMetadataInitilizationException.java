package com.jiuqi.dna.core.internal.db.metadata;

/**
 * @author Hcl
 * 
 */
public final class DbMetadataInitilizationException extends RuntimeException {

	private static final long serialVersionUID = 3093664806500479950L;

	public DbMetadataInitilizationException(String msg) {
		super("数据库元数据信息初始化异常:" + msg);
	}

	public DbMetadataInitilizationException(Throwable cause) {
		super("数据库元数据信息初始化异常.", cause);
	}
}