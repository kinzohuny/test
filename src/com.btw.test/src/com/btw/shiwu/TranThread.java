package com.btw.shiwu;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.btw.test.jdbc.MysqlDriver;

public class TranThread extends Thread {

	@Override
	public void run() {
		MysqlDriver driver = new MysqlDriver("jdbc:mysql://localhost:3306/test", "root", null);
		
		try {
			driver.setAutoCommit(false);
			
			driver.executeUpdate("update test set field2=field2-1 where a=1");
			ResultSet rs = driver.executeQuery("select * from test where a=1");
			while(rs.next()){
				System.out.println(rs.getString("a")+"|"+rs.getString("field2"));
			}
			driver.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
