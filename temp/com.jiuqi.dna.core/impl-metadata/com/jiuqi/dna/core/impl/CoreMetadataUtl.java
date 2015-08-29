package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.arg.ArgumentDefine;
import com.jiuqi.dna.core.def.query.DeleteStatementDefine;
import com.jiuqi.dna.core.def.query.InsertStatementDefine;
import com.jiuqi.dna.core.def.query.UpdateStatementDefine;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.GUID;

final class CoreMetadataUtl {

	private final DeleteStatementDefine deleteUsingRecid;
	private final DeleteStatementDefine deleteUsingKey;
	private final UpdateStatementDefine updateUsingRecid;
	private final InsertStatementDefine insert;

	CoreMetadataUtl(TD_CoreMetaData coreMetadata) {
		this.deleteUsingRecid = deleteUsingRecid(coreMetadata);
		this.deleteUsingKey = deleteUsingKey(coreMetadata);
		this.insert = insert(coreMetadata);
		this.updateUsingRecid = updateUsingRecid(coreMetadata);
	}

	final int delete(ContextImpl<?, ?, ?> context, GUID recid) {
		if (recid == null) {
			return -1;
		}
		return context.executeUpdate(this.deleteUsingRecid, recid);
	}

	final boolean save(ContextImpl<?, ?, ?> context, TableDefineImpl table) {
		SXElement xml = SXElement.newDoc();
		table.renderInto(xml);
		final String content = xml.toString();
		final GUID md5 = GUID.MD5Of(content);
		if (table.id != null) {
			int r = context.executeUpdate(this.updateUsingRecid, table.id, context.newRECVER(), MetaElementType.TABLE.name(), table.name, null, content, md5);
			if (r == 0) {
				return context.executeUpdate(this.insert, table.id, context.newRECVER(), MetaElementType.TABLE.name(), table.name, null, content, md5) == 1;
			} else if (r == 1) {
				return true;
			} else {
				throw new IllegalStateException();
			}
		} else {
			context.executeUpdate(this.deleteUsingKey, MetaElementType.TABLE.name(), table.name);
			table.id = context.newRECID();
			return context.executeUpdate(this.insert, table.id, context.newRECVER(), MetaElementType.TABLE.name(), table.name, null, content, md5) == 1;
		}
	}

	static final DeleteStatementDefine deleteUsingRecid(
			TD_CoreMetaData coreMetadata) {
		TableDefineImpl table = (TableDefineImpl) coreMetadata.getDefine();
		DeleteStatementImpl delete = new DeleteStatementImpl("core", "T", table);
		delete.setCondition(delete.expOf(coreMetadata.f_RECID).xEq(delete.newArgument(coreMetadata.f_RECID)));
		return delete;
	}

	static final DeleteStatementDefine deleteUsingKey(
			TD_CoreMetaData coreMetadata) {
		TableDefineImpl table = (TableDefineImpl) coreMetadata.getDefine();
		DeleteStatementImpl delete = new DeleteStatementImpl("core", "T", table);
		ArgumentDefine kind = delete.newArgument(coreMetadata.f_kind);
		ArgumentDefine name = delete.newArgument(coreMetadata.f_name);
		delete.setCondition(delete.expOf(coreMetadata.f_kind).xEq(kind).and(delete.expOf(coreMetadata.f_name).xEq(name)));
		return delete;
	}

	static final InsertStatementDefine insert(TD_CoreMetaData coreMetadata) {
		TableDefineImpl table = (TableDefineImpl) coreMetadata.getDefine();
		InsertStatementImpl insert = new InsertStatementImpl("core", table);
		insert.assignArgument(coreMetadata.f_RECID);
		insert.assignArgument(coreMetadata.f_RECVER);
		insert.assignArgument(coreMetadata.f_kind);
		insert.assignArgument(coreMetadata.f_name);
		insert.assignArgument(coreMetadata.f_space);
		insert.assignArgument(coreMetadata.f_xml);
		insert.assignArgument(coreMetadata.f_md5);
		return insert;
	}

	static final UpdateStatementDefine updateUsingRecid(
			TD_CoreMetaData coreMetadata) {
		TableDefineImpl table = (TableDefineImpl) coreMetadata.getDefine();
		UpdateStatementImpl update = new UpdateStatementImpl("core", "T", table);
		ArgumentDefine recid = update.newArgument(coreMetadata.f_RECID);
		update.assignArgument(coreMetadata.f_RECVER);
		update.assignArgument(coreMetadata.f_kind);
		update.assignArgument(coreMetadata.f_name);
		update.assignArgument(coreMetadata.f_space);
		update.assignArgument(coreMetadata.f_xml);
		update.assignArgument(coreMetadata.f_md5);
		update.setCondition(update.expOf(coreMetadata.f_RECID).xEq(recid));
		return update;
	}
}
