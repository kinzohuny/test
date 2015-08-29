package com.jiuqi.dna.core.def.query;

import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableRelationDefine;

/**
 * 可连接关系定义的，指示当前接口可以增加连接关系引用。
 * 
 * 对于连续多个不带括号的连接，始终从最左边的关系引用创建连接引用对象。例如：
 * <ul>
 * <li>sql语句例如：<strong>select * from A a join B b join C c</strong>.其构造过程为:
 * <blockquote>
 * 
 * <pre>
 * QuRelationRefDeclare a = query.newReference(A);
 * QuJoinedRelationRefDeclare b = a.newJoin(B);
 * QuJoinedRelationRefDeclare c = a.newJoin(C);
 * </pre>
 * 
 * </blockquote>
 * <li>sql语句例如：<strong>select * from A a join (B b join C c)</strong>.其构造过程为:
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
 * @deprecated 该接口无意义，只应该使用其子接口。
 * @author houchunlei
 * 
 */
@Deprecated
public interface RelationJoinable {

	/**
	 * 增加表连接引用.
	 * 
	 * @param table
	 *            连接的目标表定义
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table);

	/**
	 * 增加表连接引用.
	 * 
	 * @param table
	 *            连接的目标表定义
	 * @param alias
	 *            连接关系引用名称
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table, String alias);

	/**
	 * 增加表连接引用.
	 * 
	 * @param table
	 *            连接的目标表声明器
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table);

	/**
	 * 增加表连接引用.
	 * 
	 * @param table
	 *            连接的目标表声明器
	 * @param alias
	 *            连接关系引用名称
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table,
			String alias);

	/**
	 * 增加表连接引用.
	 * 
	 * @param sample
	 *            使用指定表关系定义来构造连接及连接条件
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample);

	/**
	 * 增加表连接引用.
	 * 
	 * @param sample
	 *            使用指定表关系定义来构造连接及连接条件
	 * @param alias
	 *            连接关系引用名称
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample,
			String alias);

	/**
	 * 增加子查询连接引用.
	 * 
	 * @param query
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query);

	/**
	 * 增加子查询连接引用.
	 * 
	 * @param query
	 * @param name
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query,
			String name);

}
