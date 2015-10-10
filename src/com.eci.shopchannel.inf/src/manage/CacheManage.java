package manage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.CategoryModel;
import dao.CategoryDao;

public class CacheManage {
	
	private static final String CATEGORY_CODE_SET="CATEGORY_CODE_SET";
	private static final String TAG_ID_SET="TAG_ID_SET";
	private static final String CATEGORY_LIST="CATEGORY_LIST";

	public static void initCache() throws SQLException{
		initCategoryCodeSet();
	}
	
	public static void refreshCache() throws SQLException{
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
	
	@SuppressWarnings("unchecked")
	public static Set<String> getTagIdSet(){
		Object obj = CachePool.getInstance().get(TAG_ID_SET);
		if(obj!=null&&obj instanceof Set){
			return (Set<String>)obj;
		}
		return new HashSet<String>();
	}
	
	@SuppressWarnings("unchecked")
	public static List<CategoryModel> getCategoryList(){
		Object obj = CachePool.getInstance().get(CATEGORY_LIST);
		if(obj!=null&&obj instanceof List){
			return (List<CategoryModel>)obj;
		}
		return new ArrayList<CategoryModel>();
	}
	
	private static void initCategoryCodeSet() throws SQLException{
		Set<String> category_set = new HashSet<String>();
		Set<String> tag_set = new HashSet<String>();
		List<CategoryModel> list = new CategoryDao().queryForList(null);
		if(list!=null && !list.isEmpty()){
			category_set.add("all");
			for(CategoryModel category : list){
				category_set.add(category.getCategory_code());
				tag_set.add(String.valueOf(category.getTagid()));
			}
		}
		CachePool.getInstance().add(CATEGORY_CODE_SET, category_set);
		CachePool.getInstance().add(TAG_ID_SET, tag_set);
		CachePool.getInstance().add(CATEGORY_LIST, list);
	}
	
}
