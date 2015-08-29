package com.jiuqi.dna.core.internal.da.sqlbuffer;

import com.jiuqi.dna.core.def.table.TableJoinType;

/**
 * ��ϵ����buffer
 * 
 * @author niuhaifeng
 * 
 */
public interface ISqlRelationRefBuffer extends ISqlBuffer {

	/**
	 * ���ӱ�
	 * 
	 * @param table
	 *            unquoted����
	 * @param alias
	 *            unquoted����
	 * @param type
	 *            ��������
	 * @return
	 */
	public ISqlJoinedTableRefBuffer joinTable(String table, String alias,
			TableJoinType type);

	/**
	 * ���Ӳ�ѯ
	 * 
	 * @param alias
	 *            unquoted����
	 * @param type
	 *            ��������
	 * @return
	 */
	public ISqlJoinedQueryRefBuffer joinQuery(String alias, TableJoinType type);

	/**
	 * ����with
	 * 
	 * @param target
	 *            unquoted��with����
	 * @param alias
	 *            unquoted����
	 * @param type
	 *            ��������
	 * @return
	 */
	public ISqlJoinedWithRefBuffer joinWith(String target, String alias,
			TableJoinType type);
}
