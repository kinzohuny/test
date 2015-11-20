package com.eci.youku.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eci.youku.core.DatabaseManage;
import com.eci.youku.model.VipRecModel;

public class VipRecDao {

	private static final String SQL_QUERY_LIST = "select * from youku_vip_rec order by created desc";
	
	private static final String SQL_QUERY_TORUNLIST = "select * from youku_vip_rec where vip_status = 0";
	
	private static final String SQL_INSERT = "insert into youku_vip_rec (mobile,vip_type,vip_status,vip_back,vip_mobile,vip_password,vip_errmsg,vip_time,sms_status,sms_errormsg,sms_time,isManual,created)"
			+ " values (?,?,?,?,?,?,?,?,?,?,?,?,now());";

	private static final String SQL_UPDATE_VIP_STATUS = "update youku_vip_rec set vip_status = ?"
			+ " where id = ?";
	
	private static final String SQL_UPDATE = "update youku_vip_rec"
			+ " set mobile=?,vip_type=?,vip_status=?,vip_back=?,vip_mobile=?,vip_password=?,vip_errmsg=?,vip_time=?,sms_status=?,sms_errormsg=?,sms_time=?"
			+ " where id=?";
	
	public List<VipRecModel> queryList() throws SQLException{
		return DatabaseManage.queryList(VipRecModel.class, SQL_QUERY_LIST);
	}
	
	public List<VipRecModel> queryToRunList() throws SQLException{
		return DatabaseManage.queryList(VipRecModel.class, SQL_QUERY_TORUNLIST);
	}
	
	public int updateVipStatus(List<VipRecModel> list, int status) throws SQLException{
		Object[][] paras = new Object[list.size()][2];
		for(int i=0;i<list.size();i++){
			paras[i][0]=status;
			paras[i][1]=list.get(i).getId();
		}
		return DatabaseManage.updateBatch(SQL_UPDATE_VIP_STATUS, paras);
	}
	
	public int insertList(List<VipRecModel> list) throws SQLException{
		Object[][] paras = new Object[list.size()][12];
		for(int i=0;i<list.size();i++){
			VipRecModel model = list.get(i);
			paras[i][0] = model.getMobile();
			paras[i][1] = model.getVip_type();
			paras[i][2] = model.getVip_status();
			paras[i][3] = model.getVip_back();
			paras[i][4] = model.getVip_mobile();
			paras[i][5] = model.getVip_password();
			paras[i][6] = model.getVip_errmsg();
			paras[i][7] = model.getVip_time();
			paras[i][8] = model.getSms_status();
			paras[i][9] = model.getSms_errormsg();
			paras[i][10] = model.getSms_time();
			paras[i][11] = model.getIsManual();
		}
		return DatabaseManage.updateBatch(SQL_INSERT, paras);
	}
	
	public int insert(VipRecModel model) throws SQLException{

		List<VipRecModel> list = new ArrayList<VipRecModel>();
		list.add(model);
		return insertList(list);
	}
	
	public int update(VipRecModel model) throws SQLException{
		Object[] paras = new Object[12];
		paras[0] = model.getMobile();
		paras[1] = model.getVip_type();
		paras[2] = model.getVip_status();
		paras[3] = model.getVip_back();
		paras[4] = model.getVip_mobile();
		paras[5] = model.getVip_password();
		paras[6] = model.getVip_errmsg();
		paras[7] = model.getVip_time();
		paras[8] = model.getSms_status();
		paras[9] = model.getSms_errormsg();
		paras[10] = model.getSms_time();
		paras[11] = model.getId();
		return DatabaseManage.update(SQL_UPDATE, paras);
	}
}
