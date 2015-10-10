package manage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import utils.StringUtils;

public class DatabaseManage {
	
	private final static Logger logger = Logger.getLogger(DatabaseManage.class);
	
	private static ConnectionPool connectionPool;
	private final static String driverClassName = "com.mysql.jdbc.Driver";
	private final static String url = "jdbc:mysql://"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_IP)+":"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_PORT)+"/"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_DATABASE);
	private final static String username = PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_USERNAME);
	private final static String password = PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_PASSWORD);
	private final static int initConn = StringUtils.toInt(PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_INIT_CONNECTIONS));
	private final static int incConn = StringUtils.toInt(PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_INC_CONNECTIONS));
	private final static int maxConn = StringUtils.toInt(PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_MAX_CONNECTIONS));
	
	
	public synchronized static ResultSet executeQuery(String sql,Object... paras) throws SQLException{
		ResultSet result = null;
		Connection connection = getConnect();
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			for(int i = 0;i<paras.length;i++){
				pstmt.setObject(i+1, paras[i]);
			}
			logger.debug(sql);
			logger.debug(StringUtils.arrayToSting(paras));
			result = pstmt.executeQuery();
			returnConnect(connection);
		} catch (SQLException e) {
			returnConnect(connection);
			throw e;
		}
		return result;
	}
	
	public synchronized static int executeUpdate(String sql,Object... paras) throws SQLException{
		int result = 0;
		Connection connection = getConnect();
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			for(int i = 0;i<paras.length;i++){
				pstmt.setObject(i+1, paras[i]);
			}
			logger.debug(sql);
			logger.debug(StringUtils.arrayToSting(paras));
			result = pstmt.executeUpdate();
			returnConnect(connection);
		} catch (SQLException e) {
			returnConnect(connection);
			throw e;
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
