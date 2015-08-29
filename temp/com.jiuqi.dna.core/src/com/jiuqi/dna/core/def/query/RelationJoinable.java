package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableRelationDefine;

/**
 * �����ӹ�ϵ����ģ�ָʾ��ǰ�ӿڿ����������ӹ�ϵ���á�
 * 
 * ������������������ŵ����ӣ�ʼ�մ�����ߵĹ�ϵ���ô����������ö������磺
 * <ul>
 * <li>sql������磺<strong>select * from A a join B b join C c</strong>.�乹�����Ϊ:
 * <blockquote>
 * 
 * <pre>
 * QuRelationRefDeclare a = query.newReference(A);
 * QuJoinedRelationRefDeclare b = a.newJoin(B);
 * QuJoinedRelationRefDeclare c = a.newJoin(C);
 * </pre>
 * 
 * </blockquote>
 * <li>sql������磺<strong>select * from A a join (B b join C c)</strong>.�乹�����Ϊ:
 * <blockquote>
 * 
 * <pre>
 * QuRelationRefDeclare a = query.newReference(A);
 * QuJoinedRelationRefDeclare b = a.newJoin(B);
 * QuJoinedRelationRefDeclare c = b.newJoin(C);
 * </pre>
 * 
 * </blockquote>
 * </ul>
 * 
 * @deprecated �ýӿ������壬ֻӦ��ʹ�����ӽӿڡ�
 * @author houchunlei
 * 
 */
@Deprecated
public interface RelationJoinable {

	/**
	 * ���ӱ���������.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ�����
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table);

	/**
	 * ���ӱ���������.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ�����
	 * @param alias
	 *            ���ӹ�ϵ��������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table, String alias);

	/**
	 * ���ӱ���������.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ���������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table);

	/**
	 * ���ӱ���������.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ���������
	 * @param alias
	 *            ���ӹ�ϵ��������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table,
			String alias);

	/**
	 * ���ӱ���������.
	 * 
	 * @param sample
	 *            ʹ��ָ�����ϵ�������������Ӽ���������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample);

	/**
	 * ���ӱ���������.
	 * 
	 * @param sample
	 *            ʹ��ָ�����ϵ�������������Ӽ���������
	 * @param alias
	 *            ���ӹ�ϵ��������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample,
			String alias);

	/**
	 * �����Ӳ�ѯ��������.
	 * 
	 * @param query
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query);

	/**
	 * �����Ӳ�ѯ��������.
	 * 
	 * @param query
	 * @param name
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query,
			String name);

}
