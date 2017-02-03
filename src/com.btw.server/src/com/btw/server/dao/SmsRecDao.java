package com.btw.server.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btw.server.core.DatabaseManage;
import com.btw.server.model.SmsRecModel;

public class SmsRecDao {
	
	public static String SQL_QUERY_TOSEND_LIST = "select *"
			+ " from sms_rec"
			+ " where 1=1 and status=0 and send_time < now()";
	
	public static String SQL_QUERY_LIST = "select *"
			+ " from sms_rec"
			+ " where 1=1"
			+ " order by updated desc";
	
	public static String SQL_UPDATE_STATUS = "update sms_rec"
			+ " set status = ?, updated = now()"
			+ " where id = ?";
	
	public static String SQL_UPDATE_RESULT = "update sms_rec"
			+ " set status = ?, result = ?, updated = now()"
			+ " where id = ?";
	
	public static String SQL_INSERT = "insert into sms_rec (mobile,tid,args,content,status,result,created,updated,send_time)"
			+ " values(?,?,?,?,?,?,now(),now(),?)";
	
	public List<SmsRecModel> queryList() throws SQLException{
		return DatabaseManage.queryList(SmsRecModel.class, SQL_QUERY_LIST);
	}
	
	public List<SmsRecModel> queryToSendList() throws SQLException{
		return DatabaseManage.queryList(SmsRecModel.class, SQL_QUERY_TOSEND_LIST);
	}
	
	public int updateStatus(SmsRecModel smsModel) throws SQLException{
		return DatabaseManage.update(SQL_UPDATE_STATUS, smsModel.getStatus(), smsModel.getId());
	}
	
	public int updateResult(SmsRecModel smsModel) throws SQLException{
		return DatabaseManage.update(SQL_UPDATE_RESULT, smsModel.getStatus(), smsModel.getResult(), smsModel.getId());
	}
	
	public int insertList(List<SmsRecModel> list) throws SQLException{
		Object[][] paras = new Object[list.size()][11];
		for(int i=0;i<list.size();i++){
			SmsRecModel model = list.get(i);
			paras[i][0] = model.getMobile();
			paras[i][1] = model.getTid();
			paras[i][3] = model.getArgs();
			paras[i][4] = model.getContent();
			paras[i][5] = model.getStatus();
			paras[i][6] = model.getResult();
			paras[i][10] = model.getSend_time();
		}
		return DatabaseManage.updateBatch(SQL_INSERT, paras);
	}
	
	public int insert(SmsRecModel model) throws SQLException{

		List<SmsRecModel> list = new ArrayList<SmsRecModel>();
		list.add(model);
		return insertList(list);
	}
	
}
