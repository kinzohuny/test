package com.jiuqi.dna.core.def.exp;

import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.def.table.TableReferenceDefine;

/**
 * ���ֶ����ñ��ʽ
 * 
 * @author houchunlei
 * 
 */
public interface TableFieldRefExpr extends RelationColumnRefExpr {

	/**
	 * ��ȡ�ֶζ���
	 */
	public TableFieldDefine getColumn();

	/**
	 * ��ȡ���ڵı����ö���
	 */
	public TableReferenceDefine getReference();
}