package com.jiuqi.dna.core.da;

import com.jiuqi.dna.core.LifeHandle;
import com.jiuqi.dna.core.def.obja.StructDefine;
import com.jiuqi.dna.core.def.query.DeleteStatementDeclare;
import com.jiuqi.dna.core.def.query.InsertStatementDeclare;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDeclare;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDefine;
import com.jiuqi.dna.core.def.query.ModifyStatementDeclarator;
import com.jiuqi.dna.core.def.query.ModifyStatementDefine;
import com.jiuqi.dna.core.def.query.ORMDeclarator;
import com.jiuqi.dna.core.def.query.QueryStatementDeclarator;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.def.query.QueryStatementDefine;
import com.jiuqi.dna.core.def.query.StatementDeclarator;
import com.jiuqi.dna.core.def.query.StatementDeclare;
import com.jiuqi.dna.core.def.query.StatementDefine;
import com.jiuqi.dna.core.def.query.StoredProcedureDeclarator;
import com.jiuqi.dna.core.def.query.StoredProcedureDefine;
import com.jiuqi.dna.core.def.query.UpdateStatementDeclare;
import com.jiuqi.dna.core.def.table.EntityTableDeclarator;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.type.GUID;

/**
 * 数据库适配器<br>
 * 
 * 
 * @author houchunlei
 * 
 */
public interface DBAdapter extends LifeHandle {

	/**
	 * 数据库是否可访问
	 */
	public boolean isDBAccessible();

	/**
	 * 获取新的递增的全局不重复的RECID，用于表记录的插入
	 */
	public GUID newRECID();

	/**
	 * 获得新的递增的本数据库不重复的行版本号
	 */
	public long newRECVER();

	/**
	 * 创建查询定义
	 */
	public QueryStatementDeclare newQueryStatement();

	/**
	 * 以指定查询定义为样本，复制返回一个新的查询定义。
	 * 
	 * @param sample
	 * @return
	 */
	public QueryStatementDeclare newQueryStatement(QueryStatementDefine sample);

	/**
	 * 创建插入语句定义
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDefine table);

	/**
	 * 创建插入语句定义
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDeclarator table);

	/**
	 * 创建删除语句定义
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDefine table);

	/**
	 * 创建删除语句定义
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDeclarator table);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table,
			String name);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table,
			String name);

	/**
	 * 创建ORM查询语句
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass);

	/**
	 * 创建ORM查询语句
	 * 
	 * @param entityClass
	 * @param name
	 * @return
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass, String name);

	/**
	 * 创建ORM查询语句
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			EntityTableDeclarator<?> table);

	/**
	 * 创建ORM查询语句
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model);

	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model, String name);

	/**
	 * 解析DNA-SQL
	 * 
	 * @param dnaSql
	 *            DNA-SQL文本，可以是String、StringBuilder、StringBuffer。
	 * @return 解析后的语句对象
	 */
	public StatementDeclare parseStatement(CharSequence dnaSql);

	/**
	 * 解析DNA-SQL
	 * 
	 * @param dnaSql
	 *            DNA-SQL文本，可以是String、StringBuilder、StringBuffer。
	 * @param clz
	 *            语句定义类型
	 * @return 解析后的语句对象
	 */
	public <TStatement extends StatementDeclare> TStatement parseStatement(
			CharSequence dnaSql, Class<TStatement> clz);

	/**
	 * 执行查询，返回记录集。
	 * 
	 * <p>
	 * 查询结果会被一次装载到内存，查询结果过大时，尽量使用带限定返回的查询或迭代查询。
	 * 
	 * @param query
	 *            查询定义。
	 * @param argValues
	 *            参数值。
	 * @return 记录集
	 */
	public RecordSet openQuery(QueryStatementDefine query, Object... argValues);

	/**
	 * 执行查询，返回记录集。
	 * 
	 * <p>
	 * 查询结果会被一次装载到内存，查询结果过大时，尽量使用带限定返回的查询或迭代查询。
	 * 
	 * @param query
	 *            查询定义。
	 * @param argValues
	 *            参数值。
	 * @return 记录集
	 */
	public RecordSet openQuery(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * 执行带行限定的查询,返回记录集.查询结果会被一次装载到内存.
	 * 
	 * @param query
	 *            查询定义。
	 * @param offset
	 *            从指定偏移量开始返回结果，从第1行开始返回则偏移量为0。
	 * @param rowCount
	 *            总返回行数。
	 * @param argValues
	 *            参数值。
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDefine query, long offset,
			long rowCount, Object... argValues);

	/**
	 * 执行带行限定的查询,返回记录集.查询结果会被一次装载到内存.
	 * 
	 * @param query
	 *            查询定义.
	 * @param offset
	 *            从指定偏移量开始返回结果,从第1行开始返回则偏移量为0.
	 * @param rowCount
	 *            总返回行数.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDeclarator query,
			long offset, long rowCount, Object... argValues);

	/**
	 * 执行查询,使用指定动作遍历结果集.
	 * 
	 * @param query
	 *            查询定义.
	 * @param action
	 *            记录遍历动作.
	 * @param argValues
	 *            参数值.
	 */
	public void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues);

	/**
	 * 执行查询,使用指定动作遍历结果集.
	 * 
	 * @param query
	 *            查询定义.
	 * @param action
	 *            记录遍历动作.
	 * @param argValues
	 *            参数值.
	 */
	public void iterateQuery(QueryStatementDeclarator query,
			RecordIterateAction action, Object... argValues);

	/**
	 * 执行带行限定的查询,使用指定动作遍历结果集.
	 * 
	 * @param query
	 *            查询定义.
	 * @param action
	 *            记录遍历动作.
	 * @param offset
	 *            从指定偏移量开始遍历结果,从第1行开始遍历则偏移量为0.
	 * @param rowCount
	 *            总返回行数.
	 * @param argValues
	 *            参数值.
	 */
	public void iterateQueryLimit(QueryStatementDefine query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * 执行带行限定的查询,使用指定动作遍历结果集.
	 * 
	 * @param query
	 *            查询定义.
	 * @param action
	 *            记录遍历动作.
	 * @param offset
	 *            从指定偏移量开始遍历结果,从第1行开始遍历则偏移量为0.
	 * @param rowCount
	 *            总返回行数.
	 * @param argValues
	 *            参数值.
	 */
	public void iterateQueryLimit(QueryStatementDeclarator query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * 执行查询,返回结果第一行第一列的值
	 * 
	 * @param query
	 *            查询定义.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public Object executeScalar(QueryStatementDefine query, Object... argValues);

	/**
	 * 执行查询,返回结果第一行第一列的值
	 * 
	 * @param query
	 *            查询定义.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public Object executeScalar(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 *            查询定义.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public int rowCountOf(QueryStatementDefine query, Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 *            查询定义.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public int rowCountOf(QueryStatementDeclarator query, Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 *            查询定义.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public long rowCountOfL(QueryStatementDefine query, Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 *            查询定义.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public long rowCountOfL(QueryStatementDeclarator query, Object... argValues);

	/**
	 * 执行数据库更新语句
	 * 
	 * @param statement
	 *            更新语句定义.
	 * @param argValues
	 *            参数值.
	 * @return 更新计数
	 */
	public int executeUpdate(ModifyStatementDefine statement,
			Object... argValues);

	/**
	 * 执行数据库更新语句
	 * 
	 * @param statement
	 *            更新语句定义.
	 * @param argValues
	 *            参数值.
	 * @return
	 */
	public int executeUpdate(ModifyStatementDeclarator<?> statement,
			Object... argValues);

	/**
	 * 执行存储过程.
	 * 
	 * @param procedure
	 *            存储过程定义.
	 * @param argValues
	 *            参数值.
	 * @deprecated 使用executeProcedure.
	 */
	@Deprecated
	public void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues);

	/**
	 * 执行存储过程.
	 * 
	 * @param procedure
	 *            存储过程定义.
	 * @param argValues
	 *            参数值.
	 * @deprecated 使用executeProcedure.
	 */
	@Deprecated
	public void executeUpdate(StoredProcedureDeclarator procedure,
			Object... argValues);

	/**
	 * 执行存储过程,返回结果集.
	 * 
	 * @param procedure
	 *            存储过程定义.
	 * @param argValues
	 *            参数值.
	 * @return 存储过程不返回结果集则返回长度0的数组.
	 */
	public RecordSet[] executeProcedure(StoredProcedureDefine procedure,
			Object... argValues);

	/**
	 * 执行存储过程,返回结果集.
	 * 
	 * @param procedure
	 *            存储过程定义.
	 * @param argValues
	 *            参数值.
	 * @return 存储过程不返回结果集则返回长度0的数组.
	 */
	public RecordSet[] executeProcedure(StoredProcedureDeclarator procedure,
			Object... argValues);

	/**
	 * 准备数据库语句定义
	 * 
	 * @param statement
	 *            数据库语句声明
	 * @return 数据库执行命令
	 */
	public DBCommand prepareStatement(StatementDefine statement);

	/**
	 * 准备数据库语句定义
	 * 
	 * @param statement
	 *            数据库语句定义
	 * @return 数据库执行命令
	 */
	public DBCommand prepareStatement(StatementDeclarator<?> statement);

	/**
	 * 根据DNA-SQL准备数据库语句定义，用以访问数据库
	 * 
	 * @param DNA-SQL
	 *            DNA-SQL，类型可以是String,StringBuilder,CharBuffer等
	 * @return 返回数据库访问对象
	 */
	public DBCommand prepareStatement(CharSequence dnaSql);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param <TEntity>
	 *            实体类型
	 * @param orm
	 *            实体映射
	 * @return 返回访问器
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			ORMDeclarator<TEntity> orm);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param <TEntity>
	 * @param entityClass
	 * @param mappingQuery
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			Class<TEntity> entityClass, MappingQueryStatementDefine mappingQuery);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param mappingQuery
	 *            实体查询定义
	 * @return
	 */
	public ORMAccessor<Object> newORMAccessor(
			MappingQueryStatementDefine mappingQuery);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param <TEntity>
	 * @param table
	 *            实体表声明器
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			EntityTableDeclarator<TEntity> table);

	/**
	 * 获取当前数据库Select子句支持的最大输出列
	 * 
	 * @return
	 */
	public int getMaxColumnsInSelect();

	/**
	 * 当前数据库连接的数据库产品
	 * 
	 * @return
	 */
	public DbProduct dbProduct();
}