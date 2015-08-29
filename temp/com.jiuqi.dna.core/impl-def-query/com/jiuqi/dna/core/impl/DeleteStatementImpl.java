package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.query.DeleteStatementDeclarator;
import com.jiuqi.dna.core.def.query.DeleteStatementDeclare;
import com.jiuqi.dna.core.internal.da.sql.render.ModifySql;

/**
 * …æ≥˝”Ôæ‰ µœ÷¿‡
 * 
 * @author houchunlei
 * 
 */
public final class DeleteStatementImpl extends ConditionalStatementImpl
		implements DeleteStatementDeclare,
		Declarative<DeleteStatementDeclarator> {

	public final DeleteStatementDeclarator getDeclarator() {
		return this.declarator;
	}

	public final MetaElementType getMetaElementType() {
		return MetaElementType.DELETE;
	}

	@Override
	public final String getXMLTagName() {
		return DeleteStatementImpl.xml_element_delete;
	}

	static final String xml_element_delete = "delete-statement";

	final DeleteStatementDeclarator declarator;

	public DeleteStatementImpl(String name, String alias, TableDefineImpl table) {
		this(name, alias, table, null);
	}

	public DeleteStatementImpl(String name, String alias,
			TableDefineImpl table, DeleteStatementDeclarator declarator) {
		super(name, alias, table);
		this.declarator = declarator;
	}

	private volatile ModifySql sql;

	@Override
	public final ModifySql getSql(DBAdapterImpl dbAdapter) {
		this.ensurePrepared();
		ModifySql sql = this.sql;
		if (sql == null) {
			synchronized (this) {
				sql = this.sql;
				if (sql == null) {
					this.sql = sql = dbAdapter.dbMetadata.deleteSqlFor(this);
				}
			}
		}
		return sql;
	}

	@Override
	public final void doPrepare(ContextImpl<?, ?, ?> context) throws Throwable {
		super.doPrepare(context);
		this.sql = null;
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitDeleteStatement(this, context);
	}
}