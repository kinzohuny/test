package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableReferenceDeclare;

/**
 * ���Թ��������ӵĹ�ϵ����
 * 
 * @author houchunlei
 * 
 */
public interface RelationRefBuildable {

	/**
	 * ���������
	 * 
	 * @param table
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDefine table);

	/**
	 * ���������
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDefine table, String name);

	/**
	 * ���������
	 * 
	 * @param table
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDeclarator table);

	/**
	 * ���������
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDeclarator table, String name);

	/**
	 * �����ѯ����
	 * 
	 * @param query
	 * @return
	 */
	public QueryReferenceDeclare newReference(DerivedQueryDefine query);

	/**
	 * �����ѯ����
	 * 
	 * @param query
	 * @param name
	 * @return
	 */
	public QueryReferenceDeclare newReference(DerivedQueryDefine query,
			String name);
}
