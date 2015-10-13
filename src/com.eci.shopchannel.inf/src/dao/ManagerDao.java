package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import manage.DatabaseManage;

public class ManagerDao {

	//private final static String SQL_INSERT = "insert into shopchannel_manager(name,password,status) values (?,md5(?),1)";
	private final static String SQL_VERIFY = "select count(0) from shopchannel_manager where status=1 and name=? and password=?";
	private final static String SQL_UPDATE = "update shopchannel_manager set last_login=?,last_ip=? where name=?";
	
	public int verify(String name, String password) throws SQLException{
		ResultSet result = DatabaseManage.executeQuery(SQL_VERIFY, name, password);
		int cnt = -1;
		if(result.next()){
			cnt = result.getInt(1);
		}
		return cnt;
	}
	
	public int updateLoginTime(String name, Timestamp time, String ip) throws SQLException{
		return DatabaseManage.executeUpdate(SQL_UPDATE, time, ip, name );
	}
	
	public static void main(String[] args) throws SQLException {
		int i = new ManagerDao().verify("test", "test");
		if(i>0){
			new ManagerDao().updateLoginTime("test", new Timestamp(System.currentTimeMillis()), "111.112.113.114");
		}
	}
}
