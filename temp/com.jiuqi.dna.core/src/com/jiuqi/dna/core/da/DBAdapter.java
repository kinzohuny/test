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
 * ���ݿ�������<br>
 * 
 * 
 * @author houchunlei
 * 
 */
public interface DBAdapter extends LifeHandle {

	/**
	 * ���ݿ��Ƿ�ɷ���
	 */
	public boolean isDBAccessible();

	/**
	 * ��ȡ�µĵ�����ȫ�ֲ��ظ���RECID�����ڱ��¼�Ĳ���
	 */
	public GUID newRECID();

	/**
	 * ����µĵ����ı����ݿⲻ�ظ����а汾��
	 */
	public long newRECVER();

	/**
	 * ������ѯ����
	 */
	public QueryStatementDeclare newQueryStatement();

	/**
	 * ��ָ����ѯ����Ϊ���������Ʒ���һ���µĲ�ѯ���塣
	 * 
	 * @param sample
	 * @return
	 */
	public QueryStatementDeclare newQueryStatement(QueryStatementDefine sample);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDefine table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDeclarator table);

	/**
	 * ����ɾ����䶨��
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDefine table);

	/**
	 * ����ɾ����䶨��
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDeclarator table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table,
			String name);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table);

	/**
	 * ����������䶨��
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table,
			String name);

	/**
	 * ����ORM��ѯ���
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass);

	/**
	 * ����ORM��ѯ���
	 * 
	 * @param entityClass
	 * @param name
	 * @return
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass, String name);

	/**
	 * ����ORM��ѯ���
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			EntityTableDeclarator<?> table);

	/**
	 * ����ORM��ѯ���
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model);

	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model, String name);

	/**
	 * ����DNA-SQL
	 * 
	 * @param dnaSql
	 *            DNA-SQL�ı���������String��StringBuilder��StringBuffer��
	 * @return �������������
	 */
	public StatementDeclare parseStatement(CharSequence dnaSql);

	/**
	 * ����DNA-SQL
	 * 
	 * @param dnaSql
	 *            DNA-SQL�ı���������String��StringBuilder��StringBuffer��
	 * @param clz
	 *            ��䶨������
	 * @return �������������
	 */
	public <TStatement extends StatementDeclare> TStatement parseStatement(
			CharSequence dnaSql, Class<TStatement> clz);

	/**
	 * ִ�в�ѯ�����ؼ�¼����
	 * 
	 * <p>
	 * ��ѯ����ᱻһ��װ�ص��ڴ棬��ѯ�������ʱ������ʹ�ô��޶����صĲ�ѯ�������ѯ��
	 * 
	 * @param query
	 *            ��ѯ���塣
	 * @param argValues
	 *            ����ֵ��
	 * @return ��¼��
	 */
	public RecordSet openQuery(QueryStatementDefine query, Object... argValues);

	/**
	 * ִ�в�ѯ�����ؼ�¼����
	 * 
	 * <p>
	 * ��ѯ����ᱻһ��װ�ص��ڴ棬��ѯ�������ʱ������ʹ�ô��޶����صĲ�ѯ�������ѯ��
	 * 
	 * @param query
	 *            ��ѯ���塣
	 * @param argValues
	 *            ����ֵ��
	 * @return ��¼��
	 */
	public RecordSet openQuery(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,���ؼ�¼��.��ѯ����ᱻһ��װ�ص��ڴ�.
	 * 
	 * @param query
	 *            ��ѯ���塣
	 * @param offset
	 *            ��ָ��ƫ������ʼ���ؽ�����ӵ�1�п�ʼ������ƫ����Ϊ0��
	 * @param rowCount
	 *            �ܷ���������
	 * @param argValues
	 *            ����ֵ��
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDefine query, long offset,
			long rowCount, Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,���ؼ�¼��.��ѯ����ᱻһ��װ�ص��ڴ�.
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param offset
	 *            ��ָ��ƫ������ʼ���ؽ��,�ӵ�1�п�ʼ������ƫ����Ϊ0.
	 * @param rowCount
	 *            �ܷ�������.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDeclarator query,
			long offset, long rowCount, Object... argValues);

	/**
	 * ִ�в�ѯ,ʹ��ָ���������������.
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param action
	 *            ��¼��������.
	 * @param argValues
	 *            ����ֵ.
	 */
	public void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues);

	/**
	 * ִ�в�ѯ,ʹ��ָ���������������.
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param action
	 *            ��¼��������.
	 * @param argValues
	 *            ����ֵ.
	 */
	public void iterateQuery(QueryStatementDeclarator query,
			RecordIterateAction action, Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,ʹ��ָ���������������.
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param action
	 *            ��¼��������.
	 * @param offset
	 *            ��ָ��ƫ������ʼ�������,�ӵ�1�п�ʼ������ƫ����Ϊ0.
	 * @param rowCount
	 *            �ܷ�������.
	 * @param argValues
	 *            ����ֵ.
	 */
	public void iterateQueryLimit(QueryStatementDefine query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * ִ�д����޶��Ĳ�ѯ,ʹ��ָ���������������.
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param action
	 *            ��¼��������.
	 * @param offset
	 *            ��ָ��ƫ������ʼ�������,�ӵ�1�п�ʼ������ƫ����Ϊ0.
	 * @param rowCount
	 *            �ܷ�������.
	 * @param argValues
	 *            ����ֵ.
	 */
	public void iterateQueryLimit(QueryStatementDeclarator query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * ִ�в�ѯ,���ؽ����һ�е�һ�е�ֵ
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public Object executeScalar(QueryStatementDefine query, Object... argValues);

	/**
	 * ִ�в�ѯ,���ؽ����һ�е�һ�е�ֵ
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public Object executeScalar(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public int rowCountOf(QueryStatementDefine query, Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public int rowCountOf(QueryStatementDeclarator query, Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public long rowCountOfL(QueryStatementDefine query, Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param query
	 *            ��ѯ����.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public long rowCountOfL(QueryStatementDeclarator query, Object... argValues);

	/**
	 * ִ�����ݿ�������
	 * 
	 * @param statement
	 *            ������䶨��.
	 * @param argValues
	 *            ����ֵ.
	 * @return ���¼���
	 */
	public int executeUpdate(ModifyStatementDefine statement,
			Object... argValues);

	/**
	 * ִ�����ݿ�������
	 * 
	 * @param statement
	 *            ������䶨��.
	 * @param argValues
	 *            ����ֵ.
	 * @return
	 */
	public int executeUpdate(ModifyStatementDeclarator<?> statement,
			Object... argValues);

	/**
	 * ִ�д洢����.
	 * 
	 * @param procedure
	 *            �洢���̶���.
	 * @param argValues
	 *            ����ֵ.
	 * @deprecated ʹ��executeProcedure.
	 */
	@Deprecated
	public void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues);

	/**
	 * ִ�д洢����.
	 * 
	 * @param procedure
	 *            �洢���̶���.
	 * @param argValues
	 *            ����ֵ.
	 * @deprecated ʹ��executeProcedure.
	 */
	@Deprecated
	public void executeUpdate(StoredProcedureDeclarator procedure,
			Object... argValues);

	/**
	 * ִ�д洢����,���ؽ����.
	 * 
	 * @param procedure
	 *            �洢���̶���.
	 * @param argValues
	 *            ����ֵ.
	 * @return �洢���̲����ؽ�����򷵻س���0������.
	 */
	public RecordSet[] executeProcedure(StoredProcedureDefine procedure,
			Object... argValues);

	/**
	 * ִ�д洢����,���ؽ����.
	 * 
	 * @param procedure
	 *            �洢���̶���.
	 * @param argValues
	 *            ����ֵ.
	 * @return �洢���̲����ؽ�����򷵻س���0������.
	 */
	public RecordSet[] executeProcedure(StoredProcedureDeclarator procedure,
			Object... argValues);

	/**
	 * ׼�����ݿ���䶨��
	 * 
	 * @param statement
	 *            ���ݿ��������
	 * @return ���ݿ�ִ������
	 */
	public DBCommand prepareStatement(StatementDefine statement);

	/**
	 * ׼�����ݿ���䶨��
	 * 
	 * @param statement
	 *            ���ݿ���䶨��
	 * @return ���ݿ�ִ������
	 */
	public DBCommand prepareStatement(StatementDeclarator<?> statement);

	/**
	 * ����DNA-SQL׼�����ݿ���䶨�壬���Է������ݿ�
	 * 
	 * @param DNA-SQL
	 *            DNA-SQL�����Ϳ�����String,StringBuilder,CharBuffer��
	 * @return �������ݿ���ʶ���
	 */
	public DBCommand prepareStatement(CharSequence dnaSql);

	/**
	 * ����ʵ����������
	 * 
	 * @param <TEntity>
	 *            ʵ������
	 * @param orm
	 *            ʵ��ӳ��
	 * @return ���ط�����
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			ORMDeclarator<TEntity> orm);

	/**
	 * ����ʵ����������
	 * 
	 * @param <TEntity>
	 * @param entityClass
	 * @param mappingQuery
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			Class<TEntity> entityClass, MappingQueryStatementDefine mappingQuery);

	/**
	 * ����ʵ����������
	 * 
	 * @param mappingQuery
	 *            ʵ���ѯ����
	 * @return
	 */
	public ORMAccessor<Object> newORMAccessor(
			MappingQueryStatementDefine mappingQuery);

	/**
	 * ����ʵ����������
	 * 
	 * @param <TEntity>
	 * @param table
	 *            ʵ���������
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			EntityTableDeclarator<TEntity> table);

	/**
	 * ��ȡ��ǰ���ݿ�Select�Ӿ�֧�ֵ���������
	 * 
	 * @return
	 */
	public int getMaxColumnsInSelect();

	/**
	 * ��ǰ���ݿ����ӵ����ݿ��Ʒ
	 * 
	 * @return
	 */
	public DbProduct dbProduct();
}