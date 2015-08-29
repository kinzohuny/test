package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.auth.Actor;
import com.jiuqi.dna.core.def.table.TableDeclare;
import com.jiuqi.dna.core.def.table.TableFieldDeclare;
import com.jiuqi.dna.core.type.DataType;
import com.jiuqi.dna.core.type.TypeFactory;

final class AuthorityDataBaseConstant {

	static final TableFieldDefine TABLE_USER_FIELD_NAME = new TableFieldDefine("name", TypeFactory.VARCHAR(Actor.MAX_NAME_LENGTH * 2));
	static final TableFieldDefine TABLE_USER_FIELD_TITLE = new TableFieldDefine("title", TypeFactory.VARCHAR(Actor.MAX_TITLE_LENGTH * 2));
	static final TableFieldDefine TABLE_USER_FIELD_STATE = new TableFieldDefine("state", TypeFactory.VARCHAR(16));
	static final TableFieldDefine TABLE_USER_FIELD_DESCRIPTION = new TableFieldDefine("description", TypeFactory.VARCHAR(Actor.MAX_DESCRIPTION_LENGTH * 2));
	static final TableFieldDefine TABLE_USER_FIELD_LEVEL = new TableFieldDefine("level", TypeFactory.VARCHAR(4));
	static final TableFieldDefine TABLE_USER_FIELD_PASSWORD = new TableFieldDefine("password", TypeFactory.GUID);
	static final TableFieldDefine TABLE_USER_FIELD_PRIORITY = new TableFieldDefine("priorityInfo", TypeFactory.INT);

	static final TableFieldDefine TABLE_ROLE_FIELD_NAME = new TableFieldDefine("name", TypeFactory.VARCHAR(Actor.MAX_NAME_LENGTH * 2));
	static final TableFieldDefine TABLE_ROLE_FIELD_TITLE = new TableFieldDefine("title", TypeFactory.VARCHAR(Actor.MAX_TITLE_LENGTH * 2));
	static final TableFieldDefine TABLE_ROLE_FIELD_STATE = new TableFieldDefine("state", TypeFactory.VARCHAR(16));
	static final TableFieldDefine TABLE_ROLE_FIELD_DESCRIPTION = new TableFieldDefine("description", TypeFactory.VARCHAR(Actor.MAX_DESCRIPTION_LENGTH * 2));

	static final TableFieldDefine TABLE_RA_FIELD_ACTORID = new TableFieldDefine("actorID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_RA_FIELD_ORGID = new TableFieldDefine("orgID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_RA_FIELD_ROLEID = new TableFieldDefine("roleID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_RA_FIELD_PRIORITY = new TableFieldDefine("priority", TypeFactory.INT);

	static final TableFieldDefine TABLE_UOM_FIELD_ACTORID = new TableFieldDefine("actorID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_UOM_FIELD_ORGID = new TableFieldDefine("orgID", TypeFactory.GUID);

	static final TableFieldDefine TABLE_ACL_FIELD_ACTORID = new TableFieldDefine("actorID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_ACL_FIELD_ORGID = new TableFieldDefine("orgID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_ACL_FIELD_GROUPID = new TableFieldDefine("resCategoryID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_ACL_FIELD_RESOURCEID = new TableFieldDefine("resourceID", TypeFactory.GUID);
	static final TableFieldDefine TABLE_ACL_FIELD_CODE = new TableFieldDefine("authorityCode", TypeFactory.INT);

	static final class TableFieldDefine {

		private TableFieldDefine(final String name, final DataType type) {
			this.name = name;
			this.type = type;
		}

		final TableFieldDeclare toTableField(final TableDeclare table) {
			return table.newField(this.name, this.type);
		}

		final String name;

		final DataType type;

	}

	private AuthorityDataBaseConstant() {
	}
}