package com.jiuqi.dna.core.internal.db.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Connections {

	private Connections() {
	}

	public static final boolean exist(Connection conn, CharSequence sql)
			throws SQLException {
		final PreparedStatement ps = conn.prepareStatement(sql.toString());
		try {
			final ResultSet rs = ps.executeQuery();
			try {
				if (rs.next()) {
					return true;
				}
				return false;
			} finally {
				rs.close();
			}
		} finally {
			ps.close();
		}
	}

	public static final boolean exist(Statement st, CharSequence sql)
			throws SQLException {
		final ResultSet rs = st.executeQuery(sql.toString());
		try {
			if (rs.next()) {
				return true;
			}
			return false;
		} finally {
			rs.close();
		}
	}

	public static final boolean exist(PreparedStatement ps) throws SQLException {
		final ResultSet rs = ps.executeQuery();
		try {
			if (rs.next()) {
				return true;
			}
			return false;
		} finally {
			rs.close();
		}
	}

	public static final int intOf(Connection conn, CharSequence sql)
			throws SQLException {
		final Statement s = conn.createStatement();
		try {
			final ResultSet rs = s.executeQuery(sql.toString());
			try {
				if (rs.next()) {
					int i = rs.getInt(1);
					return i;
				}
			} finally {
				rs.close();
			}
			return 0;
		} finally {
			s.close();
		}
	}

	public static final int intOf(PreparedStatement ps) throws SQLException {
		final ResultSet rs = ps.executeQuery();
		try {
			if (rs.next()) {
				int i = rs.getInt(1);
				return i;
			}
			return 0;
		} finally {
			rs.close();
		}
	}

	public static final long longOf(Connection conn, CharSequence sql)
			throws SQLException {
		final Statement s = conn.createStatement();
		try {
			final ResultSet rs = s.executeQuery(sql.toString());
			try {
				if (rs.next()) {
					long i = rs.getLong(1);
					return i;
				}
			} finally {
				rs.close();
			}
			return 0;
		} finally {
			s.close();
		}
	}

	public static final String stringOf(Connection conn, CharSequence sql)
			throws SQLException {
		final Statement s = conn.createStatement();
		try {
			final ResultSet rs = s.executeQuery(sql.toString());
			try {
				if (rs.next()) {
					String str = rs.getString(1);
					return str;
				}
				return null;
			} finally {
				rs.close();
			}
		} finally {
			s.close();
		}
	}

	public static final String stringOf(PreparedStatement ps)
			throws SQLException {
		final ResultSet rs = ps.executeQuery();
		try {
			if (rs.next()) {
				final String s = rs.getString(1);
				return s;
			}
			return null;
		} finally {
			rs.close();
		}
	}
}