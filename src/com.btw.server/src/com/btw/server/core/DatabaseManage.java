package com.btw.server.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.btw.server.constant.Constants;
import com.btw.server.util.StringUtils;

public class DatabaseManage {
	
	private final static Logger logger = Logger.getLogger(DatabaseManage.class);
	
	private static ConnectionPool connectionPool;
	private final static String driverClassName = "com.mysql.jdbc.Driver";
	private final static String characterEncoding="UTF-8";
	
	private static String url;
	private static String username;
	private static String password;
	private static int initConn;
	private static int incConn;
	private static int maxConn;
	
	public static void initDatabase() throws IOException, SQLException {

		logger.info("database init start...");
		if(!PropertiesManage.isReady()){
			PropertiesManage.initProperties();
		}
		url = "jdbc:mysql://"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_IP)
				+":"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_PORT)
				+"/"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_DATABASE)
				+"?useUnicode=true&characterEncoding="+characterEncoding;
		username = PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_USERNAME);
		password = PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_PASSWORD);
		initConn = StringUtils.toInt(PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_INIT_CONNECTIONS));
		incConn = StringUtils.toInt(PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_INC_CONNECTIONS));
		maxConn = StringUtils.toInt(PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_MAX_CONNECTIONS));
		Connection connect = getConnect();
		if(connect!=null){
			returnConnect(connect);
			logger.info("database init success!");
		}else{
			throw new RuntimeException("can not connect to "+url);
		}

	}
	
	public static List<Map<String, Object>> executeQuery(String sql,Object... paras) throws SQLException{
		ResultSet resultSet = null;
		Connection connection = getConnect();
		PreparedStatement pstmt = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			pstmt = connection.prepareStatement(sql);
			for(int i = 0;i<paras.length;i++){
				pstmt.setObject(i+1, paras[i]);
			}
			logger.debug(pstmt.toString());
			resultSet = pstmt.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			while(resultSet.next()){
				Map<String,Object> map = new HashMap<String, Object>();
				for(int i=1;i<=metaData.getColumnCount();i++){
					map.put(metaData.getColumnLabel(i), resultSet.getObject(i));
				}
				result.add(map);
			}
		} catch (SQLException e) {
			logger.error(pstmt.toString());
			throw e;
		} finally {
			resultSet.close();
			pstmt.close();
			returnConnect(connection);
		}
		logger.debug("result:"+result.size());
		return result;
	}
	
	public static int executeUpdate(String sql,Object... paras) throws SQLException{
		int result = 0;
		Connection connection = getConnect();
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(sql);
			for(int i = 0;i<paras.length;i++){
				pstmt.setObject(i+1, paras[i]);
			}
			logger.debug(pstmt.toString());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(pstmt.toString());
			throw e;
		} finally {
			pstmt.close();
			returnConnect(connection);
		}
		logger.debug("result:"+result);
		return result;
	}
	
	public static int executeBatch(String sql,Object[][] paras) throws SQLException{
		int result = 0;
		Connection connection = getConnect();
		PreparedStatement pstmt = null;
		try {
			pstmt = connection.prepareStatement(sql);
			int index = 1;
			for(Object[] item : paras){
				index = 1;
				for(Object field : item){
					pstmt.setObject(index++, field);
				}
				pstmt.addBatch();
			}
			logger.debug(pstmt.toString());
			int[] resultArr = pstmt.executeBatch();
			for (int i = 0; i < resultArr.length; i++) {
				result += resultArr[i];
			}
			returnConnect(connection);
		} catch (SQLException e) {
			logger.error(pstmt.toString());
			throw e;
		} finally {
			pstmt.close();
			returnConnect(connection);
		}
		logger.debug("result:"+result);
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
