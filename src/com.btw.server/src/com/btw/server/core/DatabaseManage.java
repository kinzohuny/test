package com.btw.server.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.btw.server.util.ServerUtils;

public class DatabaseManage {
	
	private final static Logger logger = Logger.getLogger(DatabaseManage.class);
	
	private static DataSource dataSource = null;
	private final static QueryRunner queryRunner = new QueryRunner();
	
	private DatabaseManage(){
		
	}
	
	static {
		
		logger.info("dataSource init start...");
		
		InputStream inputStream = null;
		Properties properties = new Properties();
		try {
			inputStream = ServerUtils.getFileInputStream("conf"+File.separator+"database.properties");
			//inputStream = ClassLoader.getSystemResourceAsStream("config/properties/database.properties");
			properties.load(inputStream);
			dataSource = BasicDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			logger.fatal("dataSource init error! system exit", e);
			System.exit(1);
		} finally {
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}

		logger.info("dataSource init success!");
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
			connection.close();
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
			connection.close();
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
			connection.close();
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
			connection.close();
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
			connection.close();
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
			connection.close();
		}
		
		return result;
	}
	
	private synchronized static Connection getConnect() throws SQLException {
		return dataSource.getConnection();
	}
	
}
