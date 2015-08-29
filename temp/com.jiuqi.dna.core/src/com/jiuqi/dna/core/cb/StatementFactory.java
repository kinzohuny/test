package com.jiuqi.dna.core.cb;

import com.jiuqi.dna.core.def.query.DeleteStatementDeclare;
import com.jiuqi.dna.core.def.query.InsertStatementDeclare;
import com.jiuqi.dna.core.def.query.MappingQueryStatementDeclare;
import com.jiuqi.dna.core.def.query.QueryStatementDeclare;
import com.jiuqi.dna.core.def.query.UpdateStatementDeclare;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.InsertStatementImpl;
import com.jiuqi.dna.core.impl.MappingQueryStatementImpl;
import com.jiuqi.dna.core.impl.QueryStatementImpl;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.impl.UpdateStatementImpl;

/**
 * ��乤����
 * 
 * <p>
 * ���ṩ����ʹ��
 * 
 * @author houchunlei
 * 
 */
public interface StatementFactory {

	public QueryStatementDeclare newQueryStatement(String name);

	public MappingQueryStatementDeclare newMappingQueryStatement(String name,
			Class<?> entityClass);

	public InsertStatementDeclare newInsertStatement(String name,
			TableDefine table);

	public DeleteStatementDeclare newDeleteStatement(String name,
			TableDefine table);

	public UpdateStatementDeclare newUpdateStatement(String name,
			TableDefine table);

	public static final StatementFactory instance = new StatementFactory() {

		public final QueryStatementDeclare newQueryStatement(String name) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("����");
			}
			return new QueryStatementImpl(name);
		}

		public final MappingQueryStatementDeclare newMappingQueryStatement(
				String name, Class<?> entityClass) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("����");
			}
			if (entityClass == null) {
				throw new NullArgumentException("ʵ����");
			}
			return new MappingQueryStatementImpl(name, entityClass);
		}

		public final InsertStatementDeclare newInsertStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("����");
			}
			if (table == null) {
				throw new NullArgumentException("����");
			}
			return new InsertStatementImpl(name, (TableDefineImpl) table);
		}

		public final DeleteStatementDeclare newDeleteStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("����");
			}
			if (table == null) {
				throw new NullArgumentException("����");
			}
			TableDefineImpl t = (TableDefineImpl) table;
			return new DeleteStatementImpl(name, t.name, t);
		}

		public final UpdateStatementDeclare newUpdateStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("����");
			}
			if (table == null) {
				throw new NullArgumentException("����");
			}
			TableDefineImpl t = (TableDefineImpl) table;
			return new UpdateStatementImpl(name, t.name, t);
		}

	};
}
