package com.jiuqi.dna.core.internal.db.support.mysql.sync;

import com.jiuqi.dna.core.internal.db.sync.DbColumn;
import com.jiuqi.dna.core.internal.db.sync.TypeAlterability;
import com.jiuqi.dna.core.type.DataType;

final class MysqlColumn extends
		DbColumn<MysqlTable, MysqlColumn, MysqlDataType, MysqlIndex> {

	MysqlColumn(MysqlTable table, String name) {
		super(table, name);
	}

	String charset;

	final boolean national() {
		if (this.charset.equals("utf8")) {
			return true;
		} else if (this.charset.equals("gbk")) {
			return false;
		}
		throw new UnsupportedOperationException("不支持的字符编码" + this.charset);
	}

	@Override
	protected final TypeAlterability typeAlterable(DataType type) {
		return this.type.typeAlterable(this, type);
	}

	final void defineType(Appendable s, MysqlColumn column) {
		column.type.define(column, s);
	}
}