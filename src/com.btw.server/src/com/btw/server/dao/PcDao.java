package com.btw.server.dao;

import java.sql.SQLException;
import java.util.List;

import com.btw.server.core.DatabaseManage;
import com.btw.server.model.PcModel;

public class PcDao {

	  private static final String SQL_QUERY = "select id,server_name,server_ip,created,updated from btw_pc";
	  private static final String SQL_UPDATE = "update btw_pc set server_ip=?,updated=now() where server_name=?";
	  private static final String SQL_INSERT_UPDATE = "insert into btw_pc (server_name,server_ip,created) "
	  		+ "values (?,?,now()) "
	  		+ "on duplicate key update server_ip=?,updated=now();";

	  public static List<PcModel> queryForList() throws SQLException {
		 
		  return DatabaseManage.queryList(PcModel.class, SQL_QUERY);
	  }

	  public static int insertUpdatePc(PcModel model) throws SQLException{
		  return DatabaseManage.update(SQL_INSERT_UPDATE, model.getServer_name(), model.getServer_ip(), model.getServer_ip());
	  }
	  
	  public static int updatePc(PcModel model) throws SQLException {
	    return DatabaseManage.update(SQL_UPDATE, model.getServer_ip(), model.getServer_name() );
	  }
}
