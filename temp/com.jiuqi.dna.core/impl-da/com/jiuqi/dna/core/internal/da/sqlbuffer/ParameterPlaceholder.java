package com.jiuqi.dna.core.internal.da.sqlbuffer;

import java.sql.SQLException;
import java.util.ArrayList;

import com.jiuqi.dna.core.internal.db.datasource.PreparedStatementWrap;

/**
 * dnasql的保留变量,在编译成数据库本地sql时,可能会被使用多次.
 * 
 * @author houchunlei
 * 
 */
public class ParameterPlaceholder {

	public ParameterPlaceholder() {
	}

	public final void setInt(PreparedStatementWrap pstmt,
			ArrayList<ParameterPlaceholder> parameters, int value)
			throws SQLException {
		for (int i = 0, jdbc = 1; i < parameters.size(); i++, jdbc++) {
			if (this == parameters.get(i)) {
				pstmt.setInt(jdbc, value);
			}
		}
	}

	public final void setLong(PreparedStatementWrap pstmt,
			ArrayList<ParameterPlaceholder> parameters, long value)
			throws SQLException {
		for (int i = 0, jdbc = 1; i < parameters.size(); i++, jdbc++) {
			if (this == parameters.get(i)) {
				pstmt.setLong(jdbc, value);
			}
		}
	}

	public final void setBytes(PreparedStatementWrap pstmt,
			ArrayList<ParameterPlaceholder> parameters, byte[] value)
			throws SQLException {
		for (int i = 0, jdbc = 1; i < parameters.size(); i++, jdbc++) {
			if (this == parameters.get(i)) {
				pstmt.setBytes(jdbc, value);
			}
		}
	}
}