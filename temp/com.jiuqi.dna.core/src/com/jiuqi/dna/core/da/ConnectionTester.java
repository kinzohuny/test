package com.jiuqi.dna.core.da;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import com.jiuqi.dna.core.internal.db.datasource.JdbcDriverManager;

public final class ConnectionTester {

	public static boolean test(String url, String user, String password) {
		Driver drv = JdbcDriverManager.INSTANCE.find(url);
		if (drv == null) {
			return false;
		}
		try {
			Properties props = new Properties();
			props.put("user", user);
			props.put("password", password);
			Connection conn = drv.connect(url, props);
			try {
				return true;
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			return false;
		}
	}
}
