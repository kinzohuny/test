package com.eci.youku.data.push.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.eci.youku.data.push.core.ConnectionPool;


public class TBDbUtils {
	
	private final static Logger logger = Logger.getLogger(TBDbUtils.class);
	
	private static ConnectionPool connectionPool;
	private static QueryRunner queryRunner = new QueryRunner();
	
//	private final static String driverClassName = "com.mysql.jdbc.Driver";
	private final static String driverClassName = "net.sf.log4jdbc.DriverSpy";
	
	private final static String url = "jdbc:log4jdbc:mysql://conn0633f3a5.mysql.rds.aliyuncs.com:3306/my_db?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true";
	private final static String username = "usr0633f3a5";
	private final static String password = "poincoo123_abc";
	
//	private final static String url = "jdbc:log4jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true";
//	private final static String username = "test";
//	private final static String password = "test";
	
	private final static int initConn = 1;
	private final static int incConn = 1;
	private final static int maxConn = 10;
	
	public static void initDatabase() throws IOException, SQLException {

		logger.info("database init start...");
		Connection connect = getConnect();
		if(connect!=null){
			returnConnect(connect);
			logger.info("database init success!");
		}else{
			throw new RuntimeException("can not connect to "+url);
		}

	}
	
	public static <T> T queryOne(Class<T> clazz, String sql, Object... paras) throws SQLException{
		Connection connection = getConnect();
		T result;
		
		try {
			result = queryRunner.query(connection, sql, new ScalarHandler<T>(), paras);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			returnConnect(connection);
		}
		
		return result;
	}
	
	public static <T> T queryObject(Class<T> clazz, String sql, Object... paras) throws SQLException{
		Connection connection = getConnect();
		T result;
		
		try {
			result = queryRunner.query(connection, sql, new BeanHandler<T>(clazz), paras);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			returnConnect(connection);
		}
		
		return result;
	}
	
	public static <T> List<T> queryList(Class<T> clazz, String sql, Object... paras) throws SQLException{
		Connection connection = getConnect();
		List<T> resultList;
		
		try {
			resultList = queryRunner.query(connection, sql, new BeanListHandler<T>(clazz), paras);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			returnConnect(connection);
		}
		
		return resultList;
	}
	
	public static List<Map<String, Object>> queryMapList(String sql, Object... paras) throws SQLException{
		Connection connection = getConnect();
		List<Map<String, Object>> resultList;
		
		try {
			resultList = queryRunner.query(connection, sql, new MapListHandler(), paras);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			returnConnect(connection);
		}
		
		return resultList;
	}
	
	public static int update(String sql, Object... paras) throws SQLException{
		Connection connection = getConnect();
		int result = 0;
		
		try {
			result = queryRunner.update(connection, sql, paras);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			returnConnect(connection);
		}
		
		return result;
	}
	
	public static int updateBatch(String sql, Object[][] paras) throws SQLException{
		Connection connection = getConnect();
		int result = 0;
		
		try {
			int [] results = queryRunner.batch(connection, sql, paras);
			for(int i :results){
				result += i;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			returnConnect(connection);
		}
		
		return result;
	}
	
	private synchronized static void returnConnect(Connection connection) {
		if(connectionPool==null){
			connectionPool = new ConnectionPool(driverClassName, url, username, password);
			connectionPool.setTestTable("dual");
			if(initConn>0&&incConn>0&&maxConn>0&&maxConn>initConn){
				connectionPool.setInitialConnections(initConn);
				connectionPool.setIncrementalConnections(incConn);;
				connectionPool.setMaxConnections(maxConn);;
			}
			try {
				connectionPool.createPool();
			} catch (Exception e) {
				logger.fatal("can not create connection pool!system will exit..", e);
				System.exit(1);
			}
		} 
		connectionPool.returnConnection(connection);;
	}
	
	private synchronized static Connection getConnect() throws SQLException {
		if(connectionPool==null){
			connectionPool = new ConnectionPool(driverClassName, url, username, password);
			connectionPool.setTestTable("dual");
			if(initConn>0&&incConn>0&&maxConn>0&&maxConn>initConn){
				connectionPool.setInitialConnections(initConn);
				connectionPool.setIncrementalConnections(incConn);;
				connectionPool.setMaxConnections(maxConn);;
			}
			try {
				connectionPool.createPool();
			} catch (Exception e) {
				logger.fatal("can not create connection pool!system will exit..", e);
				System.exit(1);
			}
		} 
		return connectionPool.getConnection();
	}
	
}
