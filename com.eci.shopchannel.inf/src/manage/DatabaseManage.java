package manage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManage {
	
	private static Connection connect;
	private final static String url = "jdbc:mysql://localhost:3306/test";
	private final static String user = "test";
	private final static String password = "test";
	
	public synchronized static ResultSet executeQuery(String sql,Object... paras) throws SQLException, ClassNotFoundException{
		PreparedStatement pstmt = getPreparedStatement(sql, paras);
		return pstmt.executeQuery();
	}
	
	public synchronized static int executeUpdate(String sql,Object... paras) throws SQLException, ClassNotFoundException{
		PreparedStatement pstmt = getPreparedStatement(sql, paras);
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
