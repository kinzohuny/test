package com.jiuqi.hjz.md5.main;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.btw.jdbc.mysql.MysqlDriver;

public class test2 {
	public static void main(String[] args) {
		MysqlDriver driver = new MysqlDriver(null, null, null);
		
		for(int i=0; i<100;i++){
			driver.executeUpdate("insert into MD5 values(null,"+String.valueOf(i+1)+","+String.valueOf(i+1)+")");
		}

		;
		System.out.println(driver.executeUpdate("delete from MD5")+"Ìõ¼ÇÂ¼±»É¾³ý!");
		
		ResultSet rs = driver.executeQuery("select * from MD5");
		try {
			while(rs.next()){
				System.out.println(rs.getString("NUM")+"|"+rs.getString("SOURCE")+"|"+rs.getString("TARGET"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
