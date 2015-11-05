package com.btw.server.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.btw.server.core.DatabaseManage;
import com.btw.server.model.PcModel;

public class PcDao {

	  private static final String SQL_QUERY = "select id,server_name,server_ip,created,updated from btw_pc";
	  private static final String SQL_UPDATE = "update btw_pc set server_ip=?,updated=now() where server_name=?";
	  private static final String SQL_INSERT_UPDATE = "insert into btw_pc (server_name,server_ip,created) "
	  		+ "values (?,?,now()) "
	  		+ "on duplicate key update server_ip=?,updated=now();";

	  public static List<PcModel> queryForList(Map<String, Object> map)
	    throws SQLException
	  {
		List<Map<String, Object>> resultList = DatabaseManage.executeQuery(SQL_QUERY);
	    List<PcModel> list = new ArrayList<PcModel>();
	    if(!resultList.isEmpty()){
	    	for(Map<String, Object> result : resultList){
	  	      PcModel item = new PcModel();
		      item.setId((Integer)result.get("id"));
		      item.setServer_name((String)result.get("server_name"));
		      item.setServer_ip((String)result.get("server_ip"));
		      item.setCreated((Timestamp)result.get("created"));
		      item.setUpdated((Timestamp)result.get("updated"));
		      list.add(item);
	    	}
	    }
	    
	    return list;
	  }

	  public static int insertUpdatePc(PcModel model) throws SQLException{
		  return DatabaseManage.executeUpdate(SQL_INSERT_UPDATE, new Object[] { model.getServer_name(), model.getServer_ip(), model.getServer_ip() });
	  }
	  
	  public static int updatePc(PcModel model) throws SQLException {
	    return DatabaseManage.executeUpdate(SQL_UPDATE, new Object[] { model.getServer_ip(), model.getServer_name() });
	  }
}
