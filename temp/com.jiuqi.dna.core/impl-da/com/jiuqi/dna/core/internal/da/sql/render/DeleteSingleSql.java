package com.jiuqi.dna.core.internal.da.sql.render;

import com.jiuqi.dna.core.impl.DeleteStatementImpl;
import com.jiuqi.dna.core.impl.MoJoinedRelationRef;
import com.jiuqi.dna.core.impl.TableDefineImpl;
import com.jiuqi.dna.core.internal.da.sql.execute.SimpleModifySql;
import com.jiuqi.dna.core.internal.da.sqlbuffer.ISqlDeleteBuffer;
import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

public final class DeleteSingleSql extends SimpleModifySql {

	public DeleteSingleSql(DbMetadata dbMetadata, DeleteStatementImpl delete,
			DeleteStatementStatusVisitor visitor) {
		final TableDefineImpl target = delete.moTableRef.target;
		ISqlDeleteBuffer buffer = dbMetadata.sqlbuffers().delete(target.primary.namedb(), Render.aliasOf(delete.moTableRef, target.primary));
		MoJoinedRelationRef join = delete.moTableRef.getJoins();
		if (join != null) {
			join.render(buffer.target(), visitor);
		}
		if (delete.getCondition() != null) {
			delete.getCondition().render(buffer.where(), visitor);
		}
		this.build(buffer);
	}
}