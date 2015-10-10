package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import manage.CacheManage;
import model.ItemModel;

import org.apache.log4j.Logger;

import utils.StringUtils;
import dao.ItemDao;

public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	
	private static final Logger logger = Logger.getLogger(QueryServlet.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String result = null;
		if(StringUtils.isNotEmpty(req.getParameter("tagid"))){
			Long tagid = StringUtils.toLong(req.getParameter("tagid"));
			if(tagid!=null&&CacheManage.getTagIdSet().contains(tagid)){
				result = queryByTag(tagid);
			}
		} else if (StringUtils.isNotEmpty(req.getParameter("category"))){
			String category_code = req.getParameter("category");
			if(CacheManage.getCategoryCodeSet().contains(category_code)){
				result = queryByCategory(category_code);
			}
		}
		
		if(StringUtils.isEmpty(result)){
			result = "";
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setCharacterEncoding("UTF-8");  
		resp.setContentType("application/json; charset=utf-8"); 
		resp.getWriter().append(result);

		resp.getWriter().flush();
		resp.getWriter().close();
	}
	
	String tag_prefix = "tag_";
	private String queryByTag(Long tagid){
		
		String result = CacheManage.getItemListJson(tag_prefix+tagid);
		if(StringUtils.isEmpty(result)){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("tagid", tagid);
			result = queryFromDb(map);
			if(StringUtils.isNotEmpty(result)){
				CacheManage.setItemListJson(tag_prefix+tagid, result);
			}
		}

		return result;
	}
	
	String category_prefix = "category_";
	private String queryByCategory(String category_code){
		
		String result = CacheManage.getItemListJson(category_prefix+category_code);
		if(StringUtils.isEmpty(result)){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("category_code", category_code);
			result = queryFromDb(map);
			if(StringUtils.isNotEmpty(result)){
				CacheManage.setItemListJson(category_prefix+category_code, result);
			}
		}

		return result;
	}
	
	private String queryFromDb(Map<String, Object> map) {
		String result = "";
		map.put("status", true);
		if("all".equals(map.get("category_code"))){
			map.remove("category_code");
		}
		try {
			List<ItemModel> list = new ItemDao().queryForList(map);
			if(list!=null&&!list.isEmpty()){
				result = StringUtils.toJSON(list);
			}
		} catch (Exception e) {
			logger.error("query item list from db error! ", e);
		}
		return result;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	public static void main(String[] args) {
		System.out.println(StringUtils.toJSON(null));
	}
}
