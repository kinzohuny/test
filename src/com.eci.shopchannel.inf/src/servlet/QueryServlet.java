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
		
		String category_code = null;
		for(Object key : req.getParameterMap().keySet()){
			if("category".equalsIgnoreCase(String.valueOf(key))){
				category_code = ((String[])req.getParameterMap().get(key))[0];
				break;
			}
		}
		
		String result = query(category_code);

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setCharacterEncoding("UTF-8");  
		resp.setContentType("application/json; charset=utf-8"); 
		resp.getWriter().append(result);

		resp.getWriter().flush();
		resp.getWriter().close();
	}
	
	private String query(String category_code){
		String result = "";
		
		if(StringUtils.isNotEmpty(category_code)&&CacheManage.getCategoryCodeSet().contains(category_code)){
			result = CacheManage.getItemListJson(category_code);
			if(StringUtils.isEmpty(result)){
				result = queryFromDb(category_code);
				if(StringUtils.isNotEmpty(result)){
					CacheManage.setItemListJson(category_code, result);
				}
			}
		}

		return result;
	}
	
	private String queryFromDb(String category_code) {
		String result = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", true);
		if(!"all".equals(category_code)){
			map.put("category_code", category_code);
		}
		try {
			List<ItemModel> list = new ItemDao().queryForList(map);
			logger.info("::::query from db!category_code="+category_code);
			if(list!=null&&!list.isEmpty()){
				result = StringUtils.string2Unicode(StringUtils.toJSON(list));
			}
		} catch (Exception e) {
			logger.error("query item list from db error! category_code="+category_code, e);
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
