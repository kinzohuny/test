package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.query.ModifyStatementDeclarator;
import com.jiuqi.dna.core.def.query.StatementDeclarator;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.spi.sql.SQLOutput;
import com.jiuqi.dna.core.spi.sql.SQLParseException;

/**
 * SQL脚本
 * 
 * @author niuhaifeng
 * 
 */
class SQLScript {
	private static class VisitorContext {
		public Object returnValue;
		public final ObjectQuerier querier;
		public final DeclaratorBase declarator;

		public VisitorContext(ObjectQuerier querier, DeclaratorBase declarator) {
			this.querier = querier;
			this.declarator = declarator;
		}
	}

	final NStatement statement;
	final static boolean debug = Boolean.getBoolean("com.jiuqi.dna.debug.dnasql");
	private final SQLOutput out;

	public SQLScript(NStatement statement, SQLOutput out) {
		if (statement == null) {
			throw new NullArgumentException("statement");
		}
		this.statement = statement;
		this.out = out;
		if (debug) {
			try {
				System.out.println(RenderVisitor.render(this.statement));
			} catch (Throwable e) {
				System.err.println("DNA-SQL语句存在错误，不能显示解析结果");
			}
		}
	}

	public Object prepare(ObjectQuerier oQuerier, DeclaratorBase declarator) {
		NStatement s = this.statement;
		if (s != null) {
			try {
				VisitorContext visitorContext = new VisitorContext(oQuerier, declarator);
				s.accept(visitorContext, this.visitor);
				return visitorContext.returnValue;
			} catch (SQLParseException ex) {
				this.out.raise(ex);
			}
		}
		return null;
	}

	private VisitorBase<VisitorContext> visitor = new VisitorBase<VisitorContext>() {
		private boolean isRestrict(VisitorContext visitorContext) {
			// return visitorContext.declarator == null;
			return false;
		}

		private void visitQuery(VisitorContext visitorContext, SQLVisitable stmt) {
			SQLQueryContext c = new SQLQueryContext(visitorContext.querier, this.isRestrict(visitorContext), (StatementDeclarator<?>) visitorContext.declarator);
			stmt.accept(c, SQLQueryVisitor.VISITOR);
			visitorContext.returnValue = c.rootStmt;
		}

		@Override
		public void visitQueryDeclare(VisitorContext visitorContext,
				NQueryDeclare q) {
			this.visitQuery(visitorContext, q);
		}

		@Override
		public void visitOrmDeclare(VisitorContext visitorContext, NOrmDeclare o) {
			this.visitQuery(visitorContext, o);
		}

		@Override
		public void visitOrmOverride(VisitorContext visitorContext,
				NOrmOverride o) {
			this.visitQuery(visitorContext, o);
		}

		private void visitModify(VisitorContext visitorContext,
				SQLVisitable stmt) {
			SQLModifyContext c = new SQLModifyContext(visitorContext.querier, this.isRestrict(visitorContext), (ModifyStatementDeclarator<?>) visitorContext.declarator);
			stmt.accept(c, SQLModifyVisitor.VISITOR);
			visitorContext.returnValue = c.rootStmt;
		}

		@Override
		public void visitInsertDeclare(VisitorContext visitorContext,
				NInsertDeclare i) {
			this.visitModify(visitorContext, i);
		}

		@Override
		public void visitUpdateDeclare(VisitorContext visitorContext,
				NUpdateDeclare u) {
			this.visitModify(visitorContext, u);
		}

		@Override
		public void visitDeleteDeclare(VisitorContext visitorContext,
				NDeleteDeclare d) {
			this.visitModify(visitorContext, d);
		}

		@Override
		public void visitTableDeclare(VisitorContext visitorContext,
				NTableDeclare t) {
			SQLTableContext c = new SQLTableContext(visitorContext.querier, (TableDeclarator) visitorContext.declarator);
			t.accept(c, SQLTableVisitor.VISITOR);
			visitorContext.returnValue = c.rootStmt;
		}

		@Override
		public void visitAbstractTableDeclare(VisitorContext visitorContext,
				NAbstractTableDeclare t) {
			throw new UnsupportedOperationException("抽象表不能实例化");
		}
	};

	/**
	 * 返回第一个语句，如果没有语句，返回null
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends NStatement> T content(Class<T> statementClass) {
		if (this.statement == null) {
			throw new IllegalStateException();
		}
		if (statementClass != null && !statementClass.isInstance(this.statement)) {
			throw new IllegalArgumentException("D&A Sql 所定义的类型与要求不符：[" + statementClass.getName() + "]类型");
		}
		return (T) this.statement;
	}

	@Override
	public String toString() {
		return this.statement.toString();
	}
}
