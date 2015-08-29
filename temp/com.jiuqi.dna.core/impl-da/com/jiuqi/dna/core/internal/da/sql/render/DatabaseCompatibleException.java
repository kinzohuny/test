package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

/**
 * ���ݿ�������쳣
 * 
 * @author houchunlei
 * 
 */
public final class DatabaseCompatibleException extends RuntimeException {

	private static final long serialVersionUID = -5769548820965047061L;

	public DatabaseCompatibleException(DbMetadata metadata, String message) {
		super("�����ݿ�[" + metadata.toString() + "]�����������쳣��" + message + "��");
	}
}