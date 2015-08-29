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
 * 语句工厂类
 * 
 * <p>
 * 仅提供测试使用
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
				throw new NullArgumentException("名称");
			}
			return new QueryStatementImpl(name);
		}

		public final MappingQueryStatementDeclare newMappingQueryStatement(
				String name, Class<?> entityClass) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (entityClass == null) {
				throw new NullArgumentException("实体类");
			}
			return new MappingQueryStatementImpl(name, entityClass);
		}

		public final InsertStatementDeclare newInsertStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (table == null) {
				throw new NullArgumentException("表定义");
			}
			return new InsertStatementImpl(name, (TableDefineImpl) table);
		}

		public final DeleteStatementDeclare newDeleteStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (table == null) {
				throw new NullArgumentException("表定义");
			}
			TableDefineImpl t = (TableDefineImpl) table;
			return new DeleteStatementImpl(name, t.name, t);
		}

		public final UpdateStatementDeclare newUpdateStatement(String name,
				TableDefine table) {
			if (name == null || name.length() == 0) {
				throw new NullArgumentException("名称");
			}
			if (table == null) {
				throw new NullArgumentException("表定义");
			}
			TableDefineImpl t = (TableDefineImpl) table;
			return new UpdateStatementImpl(name, t.name, t);
		}

	};
}
