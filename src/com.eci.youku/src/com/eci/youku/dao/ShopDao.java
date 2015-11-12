package com.eci.youku.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eci.youku.core.DatabaseManage;
import com.eci.youku.model.ShopModel;
import com.eci.youku.util.StringUtils;



public class ShopDao {
	
	public static String SQL_QUERY = "select s.sid,s.title,s.url,s.tk_url,s.logo_url,s.pic_url,s.sort,s.status,s.created,s.updated,count(i.iid) as item_num,cast(sum(i.status) as signed) as item_on_num"
			+ " from youku_shop s left join youku_item i on s.sid=i.sid"
			+ " where 1=1";

	public static String SQL_INSERT = "replace into youku_shop (sid,title,url,tk_url,logo_url,pic_url,sort,status,created,updated) values";
	
	public static String SQL_DELETE = "delete from youku_shop where sid in ";
	
	public static String SQL_UPDATE_STATUS = "update youku_shop set status=? where sid in ";
	
	public static String SQL_UPDATE = "update youku_shop"
			+ " set title=?,url=?,tk_url=?,logo_url=?,pic_url=?,sort=?,status=?,updated=now()"
			+ " where sid=?";
	
	public int delete(String ids) throws SQLException{
		if(StringUtils.isNotEmpty(ids)){
			Object[] paras = ids.split(",");
			return DatabaseManage.executeUpdate(SQL_DELETE+getInSql(paras.length), paras);
		}
		return 0;
	}
	
	public int updateStatus(String ids, int status) throws SQLException{
		if(StringUtils.isNotEmpty(ids)){
			Object[] idArr = ids.split(",");
			Object[] paras = (status+","+ids).split(",");
			return DatabaseManage.executeUpdate(SQL_UPDATE_STATUS+getInSql(idArr.length), paras);
		}
		return 0;
	}
	
	public int update(ShopModel model) throws SQLException{
		if(model!=null){
			Object[] paras = new Object[8];
			paras[0] = model.getTitle();
			paras[1] = model.getUrl();
			paras[2] = model.getTk_url();
			paras[3] = model.getLogo_url();
			paras[4] = model.getPic_url();
			paras[5] = model.getSort();
			paras[6] = model.getStatus();
			paras[7] = model.getSid();
			return DatabaseManage.executeUpdate(SQL_UPDATE, paras);
		}
		return 0;
	}
	
	private String getInSql(int num){
		StringBuffer buffer = new StringBuffer("(");
		for(int i = 0; i < num; i++){
			buffer.append("?,");
		}
		buffer.setLength(buffer.length()-1);
		buffer.append(")");
		return buffer.toString();
	}
	
	public int insert(List<ShopModel> list) throws SQLException{
		if(list!=null&&!list.isEmpty()){
			StringBuffer buffer = new StringBuffer();;
			for(ShopModel model : list){
				buffer.append("(");
				buffer.append(model.getSid()).append(",");
				buffer.append("'").append(model.getTitle()).append("',");
				buffer.append("'").append(model.getUrl()).append("',");
				buffer.append("'").append(model.getTk_url()).append("',");
				buffer.append("'").append(model.getLogo_url()).append("',");
				buffer.append("'").append(model.getPic_url()).append("',");
				buffer.append(model.getSort()).append(",");
				buffer.append(model.getStatus()).append(",");
				buffer.append("now(),");
				buffer.append("now()");
				buffer.append("),");
			}
			buffer.setLength(buffer.length()-1);
			return DatabaseManage.executeUpdate(SQL_INSERT+buffer.toString());
		}
		return 0;
	}
	
	public ShopModel queryById(String sid) throws SQLException {
		ShopModel model  = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sid", sid);
		List<ShopModel> list = queryForList(map);
		if(!list.isEmpty()){
			model = list.get(0);
		}
		return model;
	}
	
	public List<ShopModel> queryForList(Map<String, Object> map) throws SQLException{
		StringBuffer buffer = new StringBuffer();
		List<Object> paraList = new ArrayList<Object>();
		if(map!=null&&map.size()>0){
			for(String key : map.keySet()){
				if("status".equalsIgnoreCase(key)){
					buffer.append(" and s.status=?");
					paraList.add(map.get(key));
				}
				if("sid".equalsIgnoreCase(key)){
					buffer.append(" and s.sid=?");
					paraList.add(map.get(key));
				}
				if("sidLike".equalsIgnoreCase(key)){
					buffer.append(" and s.sid like ?");
					paraList.add(map.get(key));
				}
				if("titleLike".equalsIgnoreCase(key)){
					buffer.append(" and s.title like ?");
					paraList.add(map.get(key));
				}
			}
		}
		buffer.append(" group by s.sid order by s.status desc,s.sort");
		List<Map<String, Object>> resultList = DatabaseManage.executeQuery(SQL_QUERY+buffer.toString(), paraList.toArray());
		List<ShopModel> list = new ArrayList<ShopModel>();
		for(Map<String, Object> result : resultList){
			ShopModel model = new ShopModel();

			model.setSid((Long)result.get("sid"));
			model.setTitle((String)result.get("title"));
			model.setUrl((String)result.get("url"));
			model.setTk_url((String)result.get("tk_url"));
			model.setLogo_url((String)result.get("logo_url"));
			model.setPic_url((String)result.get("pic_url"));
			model.setSort((Long)result.get("sort"));
			model.setStatus((Integer)result.get("status"));
			model.setCreated((Timestamp)result.get("created"));
			model.setUpdated((Timestamp)result.get("updated"));
			model.setItem_num((Long)result.get("item_num"));
			model.setItem_on_num((Long)result.get("item_on_num"));
			
			list.add(model);
		}
		return list;
	}
}
