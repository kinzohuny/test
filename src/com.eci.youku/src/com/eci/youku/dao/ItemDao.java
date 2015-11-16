package com.eci.youku.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eci.youku.core.DatabaseManage;
import com.eci.youku.model.ItemModel;
import com.eci.youku.util.StringUtils;



public class ItemDao {
	
	public static String SQL_QUERY = "select i.iid,i.sid,i.title,i.price,i.url,i.tk_url,i.pic_url,i.sort,i.status,i.created,i.updated,s.title as shop_title"
			+ " from youku_item i left join youku_shop s on i.sid=s.sid"
			+ " where 1=1";

	public static String SQL_INSERT = "replace into youku_item (iid,sid,title,price,url,tk_url,pic_url,sort,status,created) values";
	
	public static String SQL_DELETE = "delete from youku_item where iid in ";
	
	public static String SQL_UPDATE_STATUS = "update youku_item set status=? where iid in ";
	
	public static String SQL_UPDATE = "update youku_item"
			+ " set sid=?,title=?,price=?,url=?,tk_url=?,pic_url=?,sort=?,status=?"
			+ " where iid=?";
	
	public int delete(String ids) throws SQLException{
		if(StringUtils.isNotEmpty(ids)){
			Object[] paras = ids.split(",");
			return DatabaseManage.update(SQL_DELETE+getInSql(paras.length), paras);
		}
		return 0;
	}
	
	public int updateStatus(String ids, int status) throws SQLException{
		if(StringUtils.isNotEmpty(ids)){
			Object[] idArr = ids.split(",");
			Object[] paras = (status+","+ids).split(",");
			return DatabaseManage.update(SQL_UPDATE_STATUS+getInSql(idArr.length), paras);
		}
		return 0;
	}
	
	public int update(ItemModel model) throws SQLException{
		if(model!=null){
			Object[] paras = new Object[9];
			paras[0] = model.getSid();
			paras[1] = model.getTitle();
			paras[2] = model.getPrice();
			paras[3] = model.getUrl();
			paras[4] = model.getTk_url();
			paras[5] = model.getPic_url();
			paras[6] = model.getSort();
			paras[7] = model.getStatus();
			paras[8] = model.getIid();
			return DatabaseManage.update(SQL_UPDATE, paras);
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
	
	public int insert(List<ItemModel> list) throws SQLException{
		if(list!=null&&!list.isEmpty()){
			StringBuffer buffer = new StringBuffer();;
			for(ItemModel model : list){
				buffer.append("(");
				buffer.append(model.getIid()).append(",");
				buffer.append(model.getSid()).append(",");
				buffer.append("'").append(model.getTitle()).append("',");
				buffer.append(model.getPrice()).append(",");
				buffer.append("'").append(model.getUrl()).append("',");
				buffer.append("'").append(model.getTk_url()).append("',");
				buffer.append("'").append(model.getPic_url()).append("',");
				buffer.append(model.getSort()).append(",");
				buffer.append(model.getStatus()).append(",");
				buffer.append("now()");
				buffer.append("),");
			}
			buffer.setLength(buffer.length()-1);
			return DatabaseManage.update(SQL_INSERT+buffer.toString());
		}
		return 0;
	}
	
	public ItemModel queryById(String iid) throws SQLException {
		ItemModel item  = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("iid", iid);
		List<ItemModel> list = new ItemDao().queryForList(map);
		if(!list.isEmpty()){
			item = list.get(0);
		}
		return item;
	}
	
	public List<ItemModel> queryForList(Map<String, Object> map) throws SQLException{
		StringBuffer buffer = new StringBuffer();
		List<Object> paraList = new ArrayList<Object>();
		if(map!=null&&map.size()>0){
			for(String key : map.keySet()){
				if("sid".equalsIgnoreCase(key)){
					buffer.append(" and i.sid=?");
					paraList.add(map.get(key));
				}
				if("sidLike".equalsIgnoreCase(key)){
					buffer.append(" and i.sid like ?");
					paraList.add(map.get(key));
				}
				if("iid".equalsIgnoreCase(key)){
					buffer.append(" and i.iid=?");
					paraList.add(map.get(key));
				}
				if("iidLike".equalsIgnoreCase(key)){
					buffer.append(" and i.iid like ?");
					paraList.add(map.get(key));
				}
				if("titleLike".equalsIgnoreCase(key)){
					buffer.append(" and i.title like ?");
					paraList.add(map.get(key));
				}
				if("shop_titleLike".equalsIgnoreCase(key)){
					buffer.append(" and s.title like ?");
					paraList.add(map.get(key));
				}
				if("status".equalsIgnoreCase(key)){
					buffer.append(" and i.status=?");
					paraList.add(map.get(key));
				}
				if("shop_status".equalsIgnoreCase(key)){
					buffer.append(" and s.status=?");
					paraList.add(map.get(key));
				}
			}
		}
		buffer.append(" order by s.status desc,s.sort,i.status desc,i.sort");
		List<Map<String, Object>> resultList = DatabaseManage.queryMapList(SQL_QUERY+buffer.toString(), paraList.toArray());
		List<ItemModel> list = new ArrayList<ItemModel>();
		for(Map<String, Object> result : resultList){
			ItemModel model = new ItemModel();
			model.setIid((Long)result.get("iid"));
			model.setSid((Long)result.get("sid"));
			model.setTitle((String)result.get("title"));
			model.setPrice((BigDecimal)result.get("price"));
			model.setUrl((String)result.get("url"));
			model.setTk_url((String)result.get("tk_url"));
			model.setPic_url((String)result.get("pic_url"));
			model.setSort((Long)result.get("sort"));
			model.setStatus((Integer)result.get("status"));
			model.setCreated((Timestamp)result.get("created"));
			model.setUpdated((Timestamp)result.get("updated"));
			list.add(model);
		}
		return list;
	}
}
