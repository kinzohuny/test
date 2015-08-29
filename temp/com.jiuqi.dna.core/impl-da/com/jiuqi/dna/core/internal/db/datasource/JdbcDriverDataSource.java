package com.jiuqi.dna.core.internal.db.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.jiuqi.dna.core.internal.db.metadata.DbMetadata;

final class JdbcDriverDataSource implements DataSource {

	final Driver driver;

	private final Properties props;

	final String url;

	JdbcDriverDataSource(JdbcDriverProvider provider, String url, String usr,
			String pwd) {
		this.url = url;
		this.driver = provider.driver;
		this.props = (Properties) provider.props.clone();
		setUserAndPassword(this.props, usr, pwd);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	public void setLoginTimeout(int seconds) throws SQLException {
	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public Logger getParentLogger() {
		return null;
	}

	public <T> T unwrap(Class<T> iface) {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) {
		return false;
	}

	public final Connection getConnection() throws SQLException {
		return this.driver.connect(this.url, this.props);
	}

	public final Connection getConnection(String username, String password)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	static final String JDBC_PROP_USER = "user";
	static final String JDBC_PROP_PASSWORD = "password";
	static final String JDBC_PROP_LOGIN_TIMEOUT = "loginTimeout";

	static final void setUserAndPassword(Properties props, String usr,
			String pwd) {
		props.put(JDBC_PROP_USER, usr);
		props.put(JDBC_PROP_PASSWORD, pwd);
	}

	final void initPropsUsingConfig(ComboDataSourceConf config) {
		if (config.loginTimeoutS > 0) {
			this.props.put(JDBC_PROP_LOGIN_TIMEOUT, Integer.toString(config.loginTimeoutS));
		} else {
			this.props.remove(JDBC_PROP_LOGIN_TIMEOUT);
		}
	}

	final void initPropsUsingMetadata(DbMetadata dbMetadata) {
		dbMetadata.initConnectionProps(this.props);
	}
}