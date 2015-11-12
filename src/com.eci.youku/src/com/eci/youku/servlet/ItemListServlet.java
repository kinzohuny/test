package com.eci.youku.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.eci.youku.constant.Constants;
import com.eci.youku.core.CacheManage;
import com.eci.youku.dao.ItemDao;
import com.eci.youku.dao.ShopDao;
import com.eci.youku.model.ItemModel;
import com.eci.youku.util.StringUtils;

public class ItemListServlet extends HttpServlet {

	private static final long serialVersionUID = 8599583576620959269L;

	private static final Logger logger = Logger.getLogger(ItemListServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	ItemDao itemDao = new ItemDao();
	ShopDao shopDao = new ShopDao();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			try {
				
				if(StringUtils.isNotEmpty(req.getParameter("edit"))){
					boolean isNew = "true".equals(req.getParameter("edit"))?false:true;
					if(isNew){
						title = "新增商品";
					}else{
						title = "修改商品";
					}
					page = editHandle(req, isNew);
				}else if(StringUtils.isNotEmpty(req.getParameter("save"))){
					title = "保存结果";
					page = saveHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("changeStatus"))){
					title = "状态更新";
					page = statusHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("delete"))){
					title = "删除结果";
					page = deleteHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("cleanCache"))){
					title = "清理缓存";
					page = cleanHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("changeSort"))){
					title = "交换顺序";
					page = changeSortHandle(req);
				}else{
					title = "商品列表";
					page = defaultHandle(req);
				}
			} catch (Exception e) {
				title = "Exception";
				page = e.getMessage();
				StringBuffer buffer = new StringBuffer("Request args:");
				for(Object key : req.getParameterMap().keySet()){
					buffer.append("&" + key + "=" + toString(req.getParameterMap().get(key)));
				}
				logger.error(buffer.toString(), e);
			}
			
			resp.setContentType("text/html;charset=utf-8");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("<title>"+title+"</title>");
			resp.getWriter().println("<link href=\"/resource/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
			resp.getWriter().println("<script src=\"/resource/main.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<script src=\"/resource/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
			resp.getWriter().println("<body>"+page+"</body>");
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private String changeSortHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		String ids = req.getParameter("ids");
		String[] idArr = ids.split(",");
		ItemModel model1 = itemDao.queryById(idArr[0]);
		ItemModel model2 = itemDao.queryById(idArr[1]);
		Long temp = model1.getSort();
		model1.setSort(model2.getSort());
		model2.setSort(temp);
		itemDao.update(model1);
		itemDao.update(model2);
		
		refreshCache();
		buffer.append("交换顺序成功，[").append(model1.getTitle()).append("]与[").append(model2.getTitle()).append("]的记录顺序已经交换。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/item');</script>");
		return buffer.toString();
	}

	private String editHandle(HttpServletRequest req, boolean isNew) throws SQLException {
		ItemModel item = null;
		if(isNew){
			item = new ItemModel();
		}else{
			String id = req.getParameter("id");
			if(StringUtils.isNotEmpty(id)){
				item = itemDao.queryById(id);
			}
			if(item==null){
				throw new IllegalArgumentException("id="+id+" 找不到对应的商品！");
			}
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(getEditPage(item, isNew));
		return buffer.toString();
	}

	private String saveHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		boolean isNew = "true".equals(req.getParameter("isNew"))?true:false;
		msg = "";
		ItemModel item = getItemFromReq(req, isNew);
		if(StringUtils.isNotEmpty(msg)){
			buffer.append(getEditPage(item, isNew));
			msg = "保存失败！\r\n"+msg;
			buffer.append(getErrorReasonPage());
		}else{
			if(isNew){
				List<ItemModel> list = new ArrayList<ItemModel>();
				list.add(item);
				int i = itemDao.insert(list);
				refreshCache();
				if(StringUtils.isNotEmpty(req.getParameter("saveType"))){
					buffer.append(getEditPage(new ItemModel(), isNew));
					msg = ""+i+"件商品[title="+item.getTitle()+"]新增成功！";
					buffer.append(getErrorReasonPage());
				}else{
					buffer.append(i).append("件商品[title=").append(item.getTitle()).append("]新增成功。<br>");
					buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
					buffer.append("<script type=\"text/javascript\">countDown(3,'/item');</script>");
				}
			}else{
				int i = itemDao.update(item);
				refreshCache();
				buffer.append(i).append("件商品[id=").append(item.getIid()).append("&title=").append(item.getTitle()).append("]修改成功。<br>");
				buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
				buffer.append("<script type=\"text/javascript\">countDown(3,'/item');</script>");
			}
		}
		return buffer.toString();
	}

	private String statusHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		String status = req.getParameter("changeStatus");
		String ids = req.getParameter("ids");
		int i = itemDao.updateStatus(ids, "1".equals(status)?1:0);
		refreshCache();
		buffer.append("更新成功，已经"+("1".equals(status)?"启用":"停用")).append(i).append("条记录。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/item');</script>");
		return buffer.toString();
	}

	private String deleteHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		String ids = req.getParameter("delete");
		int i = itemDao.delete(ids);
		refreshCache();
		buffer.append("删除成功，已经删除").append(i).append("条记录。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/item');</script>");
		return buffer.toString();
	}

	private String cleanHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		CacheManage.refreshCache();
		buffer.append("缓存清理成功。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/item');</script>");
		return buffer.toString();
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();

		if(StringUtils.isNotEmpty(req.getParameter("iid"))){
			map.put("iidLike", "%"+req.getParameter("iid")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("sid"))){
			map.put("sidLike", "%"+req.getParameter("sid")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("title"))){
			map.put("titleLike", "%"+req.getParameter("title")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("shop_title"))){
			map.put("shop_titleLike", "%"+req.getParameter("shop_title")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("status"))){
			map.put("status", req.getParameter("status"));
		}
		if(StringUtils.isNotEmpty(req.getParameter("shop_status"))){
			map.put("shop_status", req.getParameter("shop_status"));
		}
		List<ItemModel> list = itemDao.queryForList(map);
		return getButtonPage()+getFilterPage(req)+getListPage(list);
	}
	
	private void refreshCache() throws SQLException{
		CacheManage.refreshCache();
	}
	
	private ItemModel getItemFromReq(HttpServletRequest req,Boolean isNew) throws SQLException{
		StringBuffer errorInfo = new StringBuffer();
		ItemModel item = new ItemModel();
		
		Long iid = StringUtils.toLong(req.getParameter("iid"));
		item.setIid(iid);
		if(StringUtils.isEmpty(req.getParameter("iid"))){
			errorInfo.append("【*商品id】不可为空。").append("\r\n");
		}else if(iid==null){
			errorInfo.append("【*商品id】格式不正确。[").append(req.getParameter("iid")).append("]\r\n");
		}else if(isNew&&itemDao.queryById(String.valueOf(iid))!=null){
			errorInfo.append("【*商品id】与数据库记录有重复。[").append(req.getParameter("iid")).append("]\r\n");
		}
		
		Long sid = StringUtils.toLong(req.getParameter("sid"));
		item.setSid(sid);
		if(StringUtils.isEmpty(req.getParameter("sid"))){
			errorInfo.append("【*店铺id】不可为空。").append("\r\n");
		}else if(sid==null){
			errorInfo.append("【*店铺id】格式不正确。[").append(req.getParameter("sid")).append("]\r\n");
		}else if(shopDao.queryById(String.valueOf(sid))==null){
			errorInfo.append("【*店铺id】不存在该店铺。[").append(req.getParameter("sid")).append("]\r\n");
		}
		
		String title = req.getParameter("title")==null?null:req.getParameter("title").trim();
		item.setTitle(title);
		if(StringUtils.isEmpty(title)){
			errorInfo.append("【*商品标题】不可为空。").append("\r\n");
		}else if(title.length()>100){
			errorInfo.append("【*商品标题】最长100个字符。[").append(title).append("]\r\n");
		}
		
		BigDecimal price = StringUtils.toBigDecimal(req.getParameter("price"));
		item.setPrice(price);
		if(StringUtils.isEmpty(req.getParameter("price"))){
			errorInfo.append("【*价格】不可为空。").append("\r\n");
		}else if(price==null){
			errorInfo.append("【*价格】格式不正确。[").append(req.getParameter("price")).append("]\r\n");
		}
		
		Integer status = StringUtils.toInteger(req.getParameter("status"));
		item.setStatus(status==1?1:0);
		if(StringUtils.isEmpty(req.getParameter("status"))){
			errorInfo.append("【*上线状态】不可为空。").append("\r\n");
		}

		String url = req.getParameter("url")==null?null:req.getParameter("url").trim();
		item.setUrl(url);
		if(StringUtils.isEmpty(url)){
			errorInfo.append("【*商品链接】不可为空。").append("\r\n");
		}
		
		String tk_url = req.getParameter("tk_url")==null?null:req.getParameter("tk_url").trim();
		item.setTk_url(tk_url);
		if(StringUtils.isEmpty(tk_url)){
			errorInfo.append("【*淘客链接】不可为空。").append("\r\n");
		}
		
		String pic_url = req.getParameter("pic_url")==null?null:req.getParameter("pic_url").trim();
		item.setPic_url(pic_url);
		if(StringUtils.isEmpty(pic_url)){
			errorInfo.append("【*图片链接】不可为空。").append("\r\n");
		}else if(pic_url.indexOf("cdn.hao.ad.intf.ecinsight.cn")>=0){
			errorInfo.append("【*图片链接】不应使用cdn地址。").append("\r\n");
		}
		
		Long sort = StringUtils.toLong(req.getParameter("sort"));
		item.setSort(sort);
		if(StringUtils.isEmpty(req.getParameter("sort"))){
			errorInfo.append("【*排序字段】不可为空。").append("\r\n");
		}else if(sort==null){
			errorInfo.append("【*排序字段】格式不正确。[").append(req.getParameter("sort")).append("]\r\n");
		}
		msg = errorInfo.toString();
		return item;
	}

	private Object getErrorReasonPage() {
		return "<textarea cols=\"100\" rows=\"20\" readonly=\"readonly\">"+msg+"</textarea>";
	}

	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("<td><input type=\"button\" value=\"店铺管理\" onclick=\"location.href='shop'\"></td>");
		buffer.append("<td><input type=\"button\" value=\"全选\" onclick=\"selectAll();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"全清\" onclick=\"selectNone();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"启用\" onclick=\"setStatus(1);\"></td>");
		buffer.append("<td><input type=\"button\" value=\"停用\" onclick=\"setStatus(0);\"></td>");
		buffer.append("<td><input type=\"button\" value=\"交换顺序\" onclick=\"changeSort();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"新增\" onclick=\"location.href='/item?edit=false'\"></td>");
		buffer.append("<td><input type=\"button\" value=\"修改\" onclick=\"editItem();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"删除\" onclick=\"deleteSelected();\"></td>");
		//buffer.append("<td><input type=\"button\" value=\"重建缓存\" onclick=\"cleanCache();\"></td>");
		//buffer.append("<td><input type=\"button\" value=\"下载导入模板\" onclick=\"location.href='/download/import_demo.xls'\"></td>");
		//buffer.append("<form action=\"?import=1\" method=\"post\" enctype=\"multipart/form-data\" onsubmit=\"return checkFile()\"><td><input type=\"submit\" value=\"导入\"></td><td><input id=\"file_select\" type=\"file\" name=\"file\"></td></form>");
		//buffer.append("<td><input type=\"button\" value=\"清空文件\" onclick=\"clearFile();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"退出\" onclick=\"location.href='/login?logout=true'\"></td>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getFilterPage(HttpServletRequest req){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("<form action=\"/item\" method=\"post\">");
		buffer.append("<td>店铺ID：<input class=\"val w100\" name=\"sid\" value=\"").append(req.getParameter("sid")==null?"":req.getParameter("sid")).append("\"></td>");
		buffer.append("<td>商品ID：<input class=\"val w100\" name=\"iid\" value=\"").append(req.getParameter("iid")==null?"":req.getParameter("iid")).append("\"></td>");
		buffer.append("<td>店铺状态：<input class=\"val w50\" name=\"shop_status\" value=\"").append(req.getParameter("shop_status")==null?"":req.getParameter("shop_status")).append("\"></td>");
		buffer.append("<td>商品状态：<input class=\"val w50\" name=\"status\" value=\"").append(req.getParameter("status")==null?"":req.getParameter("status")).append("\"></td>");
		buffer.append("<td>店铺标题：<input class=\"val w100\" name=\"shop_title\" value=\"").append(req.getParameter("shop_title")==null?"":req.getParameter("shop_title")).append("\"></td>");
		buffer.append("<td>商品标题：<input class=\"val w100\" name=\"title\" value=\"").append(req.getParameter("title")==null?"":req.getParameter("title")).append("\"></td>");
		buffer.append("<td><input type=\"submit\" value=\"查询\"></td>");
		buffer.append("<td><input type=\"button\" value=\"清空\" onclick=\"clearFilter();\"></td>");
		buffer.append("</from>");
		buffer.append("</tr></table>");
		return buffer.toString();
	}
	
	private String getEditPage(ItemModel item, boolean isNew){

		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"edittable\">");
		buffer.append("<form id=\"saveItem\" action=\"/item?save=true\" method=\"post\" onsubmit=\"return checkItem()\">");
		buffer.append("<tr><th colspan=\"3\">").append(isNew?"新增商品":"修改商品").append("</th></tr>");
		buffer.append("<tr><td class=\"right notNull\">*店铺id：</td><td><input class=\"w300").append(isNew?"\"":" disabled\" readonly=\"readonly\" ").append(" name=\"sid\" value=\"").append(item.getSid()==null?"":item.getSid()).append("\"></td><td class=\"left\">店铺ID。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商品id：</td><td><input class=\"w300").append(isNew?"\"":" disabled\" readonly=\"readonly\" ").append(" name=\"iid\" value=\"").append(item.getIid()==null?"":item.getIid()).append("\"></td><td class=\"left\">商品ID不能重复。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商品标题：</td><td><input class=\"w300\" name=\"title\" value=\"").append(item.getTitle()==null?"":item.getTitle()).append("\"></td><td class=\"left\">40个字符以内。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*价格：</td><td><input class=\"w300\" name=\"price\" value=\"").append(item.getPrice()==null?"":item.getPrice()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*上线状态：</td><td><select class=\"w300\" name=\"status\">")
			.append(item.getStatus()!=null&&item.getStatus()==0?"<option value=\"0\" selected=\"selected\"> 0 否</option><option value=\"1\">1 是</option>":"<option value=\"0\"> 0 否</option><option value=\"1\" selected=\"selected\">1 是</option>")
			.append("</select></td><td class=\"left\">上线状态</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商品链接：</td><td><input class=\"w300\" name=\"url\" value=\"").append(item.getUrl()==null?"":item.getUrl()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*淘客链接：</td><td><input class=\"w300\" name=\"tk_url\" value=\"").append(item.getTk_url()==null?"":item.getTk_url()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*图片链接：</td><td><input class=\"w300\" name=\"pic_url\" value=\"").append(item.getPic_url()==null?"":item.getPic_url()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*排序字段：</td><td><input class=\"w300\" name=\"sort\" value=\"").append(item.getSort()==null?"":item.getSort()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr>");
		buffer.append("<td class=\"empty\">").append(isNew?"<input type=\"submit\" class=\"w100\" value=\"保存并新增\" onclick=\"setSaveType(true);\">":"").append("</td>");
		buffer.append("<td class=\"empty\"><input type=\"submit\" class=\"w100\" value=\"保存\"></td>");
		buffer.append("<td class=\"empty\"><input type=\"button\" class=\"w100\" value=\"返回\" onclick=\"location.href='/item'\"></td></tr>");
		buffer.append("<input type=\"hidden\" id=\"saveType\" name=\"saveType\">");
		buffer.append("<input type=\"hidden\" id=\"isNew\" name=\"isNew\" value=\"").append(isNew).append("\">");
		buffer.append("</form>");
		buffer.append("</table>");
		
		return buffer.toString();
	}
	
	private String getListPage(List<ItemModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		buffer.append("共"+list.size()+"条商品记录");
		buffer.append("<tr>")
			.append("<th>排序字段</th>")
			.append("<th>商品ID</th>")
			.append("<th>店铺ID</th>")
			.append("<th>标题</th>")
			.append("<th>价格</th>")
			.append("<th>上线状态</th>")
			.append("<th>商品链接</th>")
			.append("<th>淘客链接</th>")
			.append("<th>图片链接</th>")
			.append("<th>创建时间</th>")
			.append("<th>修改时间</th></tr>");

		for(ItemModel model : list){
			buffer.append("<tr>");
			buffer.append("<td><input id="+model.getIid()+" name=\"checkbox\" type=\"checkbox\">").append(model.getSort()).append("</td>");
			buffer.append("<td>").append(model.getIid()).append("</td>");
			buffer.append("<td>").append(model.getSid()).append("</td>");
			buffer.append("<td>").append(model.getTitle()).append("</td>");
			buffer.append("<td>").append(model.getPrice()).append("</td>");
			buffer.append("<td>").append(model.getStatus()).append("</td>");
			buffer.append("<td>").append(model.getUrl()).append("</td>");
			buffer.append("<td>").append(model.getTk_url()==null?"":model.getTk_url()).append("</td>");
			buffer.append("<td>").append(model.getPic_url()).append("</td>");
			buffer.append("<td>").append(model.getCreated()).append("</td>");
			buffer.append("<td>").append(model.getUpdated()).append("</td>");
			buffer.append("</tr>");
		}

		buffer.append("</table>");
		return String.valueOf(buffer.toString());
	} 

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}
	
	private String toString(Object obj){
		StringBuffer sb = new StringBuffer();
		if(obj instanceof String[]){
			String[] array = (String[]) obj;
			String separator = "";
			for(String str : array){
				sb.append(separator).append(str);
				separator = ",";
			}
		}
		return sb.toString();
	}
}
