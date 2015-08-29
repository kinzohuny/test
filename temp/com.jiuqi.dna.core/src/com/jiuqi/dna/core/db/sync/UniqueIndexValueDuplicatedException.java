package com.jiuqi.dna.core.db.sync;

import com.jiuqi.dna.core.def.table.IndexDefine;

/**
 * Ψһ�����������ظ�ֵ
 * 
 * @author houchunlei
 * 
 */
public class UniqueIndexValueDuplicatedException extends TableSyncException {

	private static final long serialVersionUID = -5375976037028986120L;

	public UniqueIndexValueDuplicatedException(IndexDefine index) {
		super(index.getOwner(), message(index));
	}

	public static final String message(IndexDefine index) {
		return "Ψһ��������[" + index + "]������ֵ�ظ�.";
	}

	public static final String message0(IndexDefine index) {
		return TableSyncException.message(index.getOwner())
				+ UniqueIndexValueDuplicatedException.message(index);
	}
}