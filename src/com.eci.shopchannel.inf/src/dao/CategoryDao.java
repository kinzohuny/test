package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import manage.DatabaseManage;
import model.CategoryModel;

public class CategoryDao {

	public static String SQL_QUERY = "select tag_id,tag_name,category_code,category_name,created,updated"
			+ " from shopchannel_category";
	
	public List<CategoryModel> queryForList(Map<String, Object> map) throws SQLException{

		ResultSet result = DatabaseManage.executeQuery(SQL_QUERY);
		List<CategoryModel> list = new ArrayList<CategoryModel>();
		while(result.next()){
			CategoryModel category = new CategoryModel();
			category.setTagid(result.getLong("tag_id"));
			category.setTag_name(result.getString("tag_name"));
			category.setCategory_code(result.getString("category_code"));
			category.setCategory_name(result.getString("category_name"));
			category.setCreated(result.getTimestamp("created"));
			category.setUpdated(result.getTimestamp("updated"));
			list.add(category);
		}
		return list;
	}
	
	public static void main(String[] args) {

		try {
			List<CategoryModel> list = new CategoryDao().queryForList(null);
			System.out.println(list.size());
			for(CategoryModel category : list){
				System.out.println(category.getTagid()+"|"+
						category.getTag_name()+"|"+
						category.getCategory_code()+"|"+
						category.getCategory_name()+"|"+
						category.getCreated()+"|"+
						category.getUpdated());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
