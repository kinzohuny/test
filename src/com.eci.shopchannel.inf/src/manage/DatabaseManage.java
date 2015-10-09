package manage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import utils.StringUtils;

public class DatabaseManage {
	
	private final static Logger logger = Logger.getLogger(DatabaseManage.class);
	
	private static Connection connect;
	private final static String url = "jdbc:mysql://"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_IP)+":"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_PORT)+"/"+PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_DATABASE);
	private final static String user = PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_USERNAME);
	private final static String password = PropertiesManage.getProperties(Constants.PROPERTIES_MYSQL_PASSWORD);
	
	public synchronized static ResultSet executeQuery(String sql,Object... paras) throws SQLException, ClassNotFoundException{
		PreparedStatement pstmt = getPreparedStatement(sql, paras);
		logger.debug(sql);
		logger.debug(StringUtils.toSting(paras));
		return pstmt.executeQuery();
	}
	
	public synchronized static int executeUpdate(String sql,Object... paras) throws SQLException, ClassNotFoundException{
		PreparedStatement pstmt = getPreparedStatement(sql, paras);
		logger.debug(sql);
		logger.debug(StringUtils.toSting(paras));
		return pstmt.executeUpdate();
	}
	
	private synchronized static PreparedStatement getPreparedStatement(String sql,Object... paras) throws SQLException, ClassNotFoundException{
		PreparedStatement pstmt = null;
		pstmt = getConnect().prepareStatement(sql);
		for(int i = 0;i<paras.length;i++){
			pstmt.setObject(i+1, paras[i]);
		}
		return pstmt;
	}
	
	private synchronized static Connection getConnect() throws ClassNotFoundException, SQLException {
		if(connect==null){
			Class.forName("com.mysql.jdbc.Driver"); 
			connect = DriverManager.getConnection(url, user, password);
		} 
		return connect;
	}
	
}
