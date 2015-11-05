package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.StringUtils;
import manage.DatabaseManage;
import model.ItemModel;



public class ItemDao {
	
	public static String SQL_QUERY = "select si.identify,si.long_title,si.url,si.wap_url,si.img_url,si.price,si.price_new,si.cheap,si.site,si.site_url,si.tag_id,sc.tag_name,si.post,si.status,sc.category_code,sc.category_name,si.created,si.updated,si.sort"
			+ " from shopchannel_item si,shopchannel_category sc"
			+ " where si.tag_id=sc.tag_id";

	public static String SQL_INSERT = "replace into shopchannel_item (identify,long_title,url,wap_url,img_url,price,price_new,cheap,site,site_url,tag_id,post,status,created,updated,sort) values";
	
	public static String SQL_DELETE = "delete from shopchannel_item where identify in ";
	
	public static String SQL_UPDATE_STATUS = "update shopchannel_item set status=? where identify in ";
	
	public static String SQL_UPDATE = "update shopchannel_item"
			+ " set long_title=?,url=?,wap_url=?,img_url=?,price=?,price_new=?,cheap=?,site=?,site_url=?,tag_id=?,post=?,status=?,updated=now(),sort=?"
			+ " where identify=?";
	
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
	
	public int update(ItemModel item) throws SQLException{
		if(item!=null){
			Object[] paras = new Object[14];
			paras[0] = item.getLong_title();
			paras[1] = item.getUrl();
			paras[2] = item.getWapurl();
			paras[3] = item.getImg_url();
			paras[4] = item.getPrice();
			paras[5] = item.getPrice_new();
			paras[6] = item.getCheap();
			paras[7] = item.getSite();
			paras[8] = item.getSite_url();
			paras[9] = item.getTagid();
			paras[10] = item.getPost();
			paras[11] = item.getStatus();
			paras[12] = item.getSort();
			paras[13] = item.getIdentify();
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
	
	public int insert(List<ItemModel> list) throws SQLException{
		if(list!=null&&!list.isEmpty()){
			StringBuffer buffer = new StringBuffer();;
			for(ItemModel item : list){
				buffer.append("(");
				buffer.append(item.getIdentify()).append(",");
				buffer.append("'").append(item.getLong_title()).append("',");
				buffer.append("'").append(item.getUrl()).append("',");
				buffer.append("'").append(item.getWapurl()).append("',");
				buffer.append("'").append(item.getImg_url()).append("',");
				buffer.append(item.getPrice()).append(",");
				buffer.append(item.getPrice_new()).append(",");
				buffer.append(item.getCheap()).append(",");
				buffer.append("'").append(item.getSite()).append("',");
				buffer.append("'").append(item.getSite_url()).append("',");
				buffer.append("'").append(item.getTagid()).append("',");
				buffer.append(item.getPost()).append(",");
				buffer.append(item.getStatus()).append(",");
				buffer.append("now(),");
				buffer.append("now(),");
				buffer.append(item.getSort());
				buffer.append("),");
			}
			buffer.setLength(buffer.length()-1);
			return DatabaseManage.executeUpdate(SQL_INSERT+buffer.toString());
		}
		return 0;
	}
	
	public ItemModel queryById(String identify) throws SQLException {
		ItemModel item  = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("identify", identify);
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
				if("status".equalsIgnoreCase(key)){
					buffer.append(" and si.status=?");
					paraList.add(map.get(key));
				}
				if("category_code".equalsIgnoreCase(key)){
					buffer.append(" and sc.category_code=?");
					paraList.add(map.get(key));
				}
				if("tagid".equalsIgnoreCase(key)){
					buffer.append(" and sc.tag_id=?");
					paraList.add(map.get(key));
				}
				if("identify".equalsIgnoreCase(key)){
					buffer.append(" and si.identify=?");
					paraList.add(map.get(key));
				}
				if("identifyLike".equalsIgnoreCase(key)){
					buffer.append(" and si.identify like ?");
					paraList.add(map.get(key));
				}
				if("siteLike".equalsIgnoreCase(key)){
					buffer.append(" and si.site like ?");
					paraList.add(map.get(key));
				}
				if("long_titleLike".equalsIgnoreCase(key)){
					buffer.append(" and si.long_title like ?");
					paraList.add(map.get(key));
				}
			}
		}
		buffer.append(" order by si.status desc,si.sort");
		ResultSet result = DatabaseManage.executeQuery(SQL_QUERY+buffer.toString(), paraList.toArray());
		List<ItemModel> list = new ArrayList<ItemModel>();
		while(result.next()){
			ItemModel item = new ItemModel();
			item.setIdentify(result.getLong("identify"));
			item.setLong_title(result.getString("long_title"));
			item.setUrl(result.getString("url"));
			item.setWapurl(result.getString("wap_url"));
			item.setImg_url(result.getString("img_url"));
			item.setPrice(result.getBigDecimal("price"));
			item.setPrice_new(result.getBigDecimal("price_new"));
			item.setCheap(result.getBigDecimal("cheap"));
			item.setSite(result.getString("site"));
			item.setSite_url(result.getString("site_url"));
			item.setTagid(result.getLong("tag_id"));
			item.setTag(result.getString("tag_name"));
			item.setPost(result.getInt("post"));
			item.setStatus(result.getInt("status"));
			item.setCategory_code(result.getString("category_code"));
			item.setCategory_name(result.getString("category_name"));
			item.setCreated(result.getTimestamp("created"));
			item.setUpdated(result.getTimestamp("updated"));
			item.setSort(result.getLong("sort"));
			list.add(item);
		}
		return list;
	}
	
	public static void main(String[] args) {
		try {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("status", false);
			map.put("category_code", "fuzhuang");
			List<ItemModel> list = new ItemDao().queryForList(map);
			System.out.println(list.size());
			for(ItemModel item : list){
				System.out.println(item.getIdentify()+"|"+
						item.getLong_title()+"|"+
						item.getUrl()+"|"+
						item.getWapurl()+"|"+
						item.getImg_url()+"|"+
						item.getPrice()+"|"+
						item.getPrice_new()+"|"+
						item.getCheap()+"|"+
						item.getSite()+"|"+
						item.getSite_url()+"|"+
						item.getTagid()+"|"+
						item.getPost()+"|"+
						item.getStatus()+"|"+
						item.getCategory_code()+"|"+
						item.getCategory_name()+"|"+
						item.getCreated()+"|"+
						item.getUpdated());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
