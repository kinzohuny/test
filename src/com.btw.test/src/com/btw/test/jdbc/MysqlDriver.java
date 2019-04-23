package com.btw.test.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlDriver {
	
	public MysqlDriver(String url, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			// Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.println("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		if (url == null||url.equals("")) {
			url = "jdbc:mysql://localhost:3306/test";
		}
		if (user == null||user.equals("")) {
			user = "test";
		}
		if (password == null||password.equals("")) {
			password = "test";
		}
		try {
			connect = DriverManager.getConnection(url, user, password);
			System.out.println("Success connect Mysql server!");
		} catch (Exception e) {
			System.out.println("Creat connection error!");
			e.printStackTrace();
		}
	}
	
	Connection connect;
	
	public ResultSet executeQuery(String sql,Object... paras){
		ResultSet rs = null;
		try {
			PreparedStatement pstmt = getPreparedStatement(sql, paras);
			rs = pstmt.executeQuery();
		} catch (Exception e) {
			System.out.println("Execute query error!");
			e.printStackTrace();
		}
		return rs;
	}
	
	public int executeUpdate(String sql,Object... paras){
		int i = 0;
		try {
			PreparedStatement pstmt = getPreparedStatement(sql, paras);
			i = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("Execute update error!");
			e.printStackTrace();
		}
		return i;
	}
	
	private PreparedStatement getPreparedStatement(String sql,Object... paras){
		PreparedStatement pstmt = null;
		try {
			pstmt = connect.prepareStatement(sql);
			for(int i = 0;i<paras.length;i++){
				pstmt.setObject(i+1, paras[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pstmt;
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		connect.setAutoCommit(autoCommit);
	}

	public void commit() throws SQLException {
		connect.commit();
	}
}
