package com.jiuqi.dna.core.internal.db.metadata;

/**
 * @author Hcl
 * 
 */
public final class DbMetadataInitilizationException extends RuntimeException {

	private static final long serialVersionUID = 3093664806500479950L;

	public DbMetadataInitilizationException(String msg) {
		super("���ݿ�Ԫ������Ϣ��ʼ���쳣:" + msg);
	}

	public DbMetadataInitilizationException(Throwable cause) {
		super("���ݿ�Ԫ������Ϣ��ʼ���쳣.", cause);
	}
}