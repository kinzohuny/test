package manage;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.CategoryDao;
import model.CategoryModel;

public class CacheManage {
	
	private static final String CATEGORY_CODE_SET="category_code_set";

	public static void initCache() throws SQLException, ClassNotFoundException{
		setCategoryCodeSet();
	}
	
	public static void refreshCache() throws SQLException, ClassNotFoundException{
		CachePool.getInstance().ReLoadCache();
		initCache();
	}
	
	public static String getItemListJson(String category_code){
		Object obj = CachePool.getInstance().get(category_code);
		if(obj instanceof String){
			return (String)obj;
		}
		return null;
	}
	
	public static void setItemListJson(String category_code, String itemListJson){
		CachePool.getInstance().add(category_code, itemListJson);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<String> getCategoryCodeSet(){
		Object obj = CachePool.getInstance().get(CATEGORY_CODE_SET);
		if(obj!=null&&obj instanceof Set){
			return (Set<String>)obj;
		}
		return new HashSet<String>();
	}
	
	private static void setCategoryCodeSet() throws SQLException, ClassNotFoundException{
		Set<String> set = new HashSet<String>();
		List<CategoryModel> list = new CategoryDao().queryForList(null);
		if(list!=null && !list.isEmpty()){
			set.add("all");
			for(CategoryModel category : list){
				set.add(category.getCategory_code());
			}
		}
		CachePool.getInstance().add(CATEGORY_CODE_SET, set);
	}
	
}
