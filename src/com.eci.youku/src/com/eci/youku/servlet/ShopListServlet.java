package com.eci.youku.servlet;

import java.io.IOException;
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
import com.eci.youku.dao.ShopDao;
import com.eci.youku.model.ShopModel;
import com.eci.youku.util.StringUtils;

public class ShopListServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	private static final Logger logger = Logger.getLogger(ShopListServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	ShopDao shopDao = new ShopDao();
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else if(StringUtils.isNotEmpty(req.getParameter("logout"))){
			session.setAttribute(Constants.SESSION_IS_LOGIN, null);
			resp.sendRedirect("/login");
		}else{
			try {
				if(StringUtils.isNotEmpty(req.getParameter("edit"))){
					boolean isNew = "true".equals(req.getParameter("edit"))?false:true;
					if(isNew){
						title = "新增店铺";
					}else{
						title = "修改店铺";
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
					title = "店铺管理";
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
		ShopModel model1 = shopDao.queryById(idArr[0]);
		ShopModel model2 = shopDao.queryById(idArr[1]);
		Long temp = model1.getSort();
		model1.setSort(model2.getSort());
		model2.setSort(temp);
		shopDao.update(model1);
		shopDao.update(model2);
		
		refreshCache();
		buffer.append("交换顺序成功，[").append(model1.getTitle()).append("]与[").append(model2.getTitle()).append("]的记录顺序已经交换。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/shop');</script>");
		return buffer.toString();
	}

	private String editHandle(HttpServletRequest req, boolean isNew) throws SQLException {
		ShopModel item = null;
		if(isNew){
			item = new ShopModel();
		}else{
			String id = req.getParameter("id");
			if(StringUtils.isNotEmpty(id)){
				item = shopDao.queryById(id);
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
		ShopModel item = getItemFromReq(req, isNew);
		if(StringUtils.isNotEmpty(msg)){
			buffer.append(getEditPage(item, isNew));
			msg = "保存失败！\r\n"+msg;
			buffer.append(getErrorReasonPage());
		}else{
			if(isNew){
				List<ShopModel> list = new ArrayList<ShopModel>();
				list.add(item);
				int i = shopDao.insert(list);
				refreshCache();
				if(StringUtils.isNotEmpty(req.getParameter("saveType"))){
					buffer.append(getEditPage(new ShopModel(), isNew));
					msg = ""+i+"件商品[title="+item.getTitle()+"]新增成功！";
					buffer.append(getErrorReasonPage());
				}else{
					buffer.append(i).append("件商品[title=").append(item.getTitle()).append("]新增成功。<br>");
					buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
					buffer.append("<script type=\"text/javascript\">countDown(3,'/shop');</script>");
				}
			}else{
				int i = shopDao.update(item);
				refreshCache();
				buffer.append(i).append("件商品[id=").append(item.getSid()).append("&title=").append(item.getTitle()).append("]修改成功。<br>");
				buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
				buffer.append("<script type=\"text/javascript\">countDown(3,'/shop');</script>");
			}
		}
		return buffer.toString();
	}

	private String statusHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		String status = req.getParameter("changeStatus");
		String ids = req.getParameter("ids");
		int i = shopDao.updateStatus(ids, "1".equals(status)?1:0);
		refreshCache();
		buffer.append("更新成功，已经"+("1".equals(status)?"启用":"停用")).append(i).append("条记录。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/shop');</script>");
		return buffer.toString();
	}

	private String deleteHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		String ids = req.getParameter("delete");
		int i = shopDao.delete(ids);
		refreshCache();
		buffer.append("删除成功，已经删除").append(i).append("条记录。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/shop');</script>");
		return buffer.toString();
	}

	private String cleanHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		CacheManage.refreshCache();
		buffer.append("缓存清理成功。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/shop');</script>");
		return buffer.toString();
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(req.getParameter("sid"))){
			map.put("sidLike", "%"+req.getParameter("sid")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("title"))){
			map.put("titleLike", "%"+req.getParameter("title")+"%");
		}
		if(StringUtils.isNotEmpty(req.getParameter("status"))){
			map.put("status", req.getParameter("status"));
		}
		List<ShopModel> list = shopDao.queryForList(map);
		return getButtonPage()+getFilterPage(req)+getListPage(list);
	}
	
	private void refreshCache() throws SQLException{
		CacheManage.refreshCache();
	}
	
	private ShopModel getItemFromReq(HttpServletRequest req,Boolean isNew) throws SQLException{
		StringBuffer errorInfo = new StringBuffer();
		ShopModel model = new ShopModel();
		
		Long sid = StringUtils.toLong(req.getParameter("sid"));
		model.setSid(sid);
		if(StringUtils.isEmpty(req.getParameter("sid"))){
			errorInfo.append("【*店铺id】不可为空。").append("\r\n");
		}else if(sid==null){
			errorInfo.append("【*店铺id】格式不正确。[").append(req.getParameter("sid")).append("]\r\n");
		}else if(isNew&&shopDao.queryById(String.valueOf(sid))!=null){
			errorInfo.append("【*店铺id】与数据库记录有重复。[").append(req.getParameter("sid")).append("]\r\n");
		}
		
		String title = req.getParameter("title")==null?null:req.getParameter("title").trim();
		model.setTitle(title);
		if(StringUtils.isEmpty(title)){
			errorInfo.append("【*店铺标题】不可为空。").append("\r\n");
		}else if(title.length()>100){
			errorInfo.append("【*店铺标题】最长100个字符。[").append(title).append("]\r\n");
		}
		
		Integer status = StringUtils.toInteger(req.getParameter("status"));
		model.setStatus(status==1?1:0);
		if(StringUtils.isEmpty(req.getParameter("status"))){
			errorInfo.append("【*上线状态】不可为空。").append("\r\n");
		}

		String url = req.getParameter("url")==null?null:req.getParameter("url").trim();
		model.setUrl(url);
		if(StringUtils.isEmpty(url)){
			errorInfo.append("【*店铺链接】不可为空。").append("\r\n");
		}
		
		String tk_url = req.getParameter("tk_url")==null?null:req.getParameter("tk_url").trim();
		model.setTk_url(tk_url);
		if(StringUtils.isEmpty(tk_url)){
			errorInfo.append("【*淘客链接】不可为空。").append("\r\n");
		}
		
		String logo_url = req.getParameter("logo_url")==null?null:req.getParameter("logo_url").trim();
		model.setLogo_url(logo_url);
		if(StringUtils.isEmpty(logo_url)){
			errorInfo.append("【*LOGO链接】不可为空。").append("\r\n");
		}else if(logo_url.indexOf("cdn.hao.ad.intf.ecinsight.cn")>=0){
			errorInfo.append("【*LOGO链接】不应使用cdn地址。").append("\r\n");
		}
		
		String pic_url = req.getParameter("pic_url")==null?null:req.getParameter("pic_url").trim();
		model.setPic_url(pic_url);
		if(StringUtils.isEmpty(pic_url)){
			errorInfo.append("【*商品链接】不可为空。").append("\r\n");
		}else if(pic_url.indexOf("cdn.hao.ad.intf.ecinsight.cn")>=0){
			errorInfo.append("【*商品链接】不应使用cdn地址。").append("\r\n");
		}
		
		Long sort = StringUtils.toLong(req.getParameter("sort"));
		model.setSort(sort);
		if(StringUtils.isEmpty(req.getParameter("sort"))){
			errorInfo.append("【*排序字段】不可为空。").append("\r\n");
		}else if(sort==null){
			errorInfo.append("【*排序字段】格式不正确。[").append(req.getParameter("sort")).append("]\r\n");
		}
		msg = errorInfo.toString();
		return model;
	}

	private Object getErrorReasonPage() {
		return "<textarea cols=\"100\" rows=\"20\" readonly=\"readonly\">"+msg+"</textarea>";
	}

	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("<td><input type=\"button\" value=\"商品管理\" onclick=\"location.href='item'\"></td>");
		buffer.append("<td><input type=\"button\" value=\"全选\" onclick=\"selectAll();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"全清\" onclick=\"selectNone();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"启用\" onclick=\"setStatus(1);\"></td>");
		buffer.append("<td><input type=\"button\" value=\"停用\" onclick=\"setStatus(0);\"></td>");
		buffer.append("<td><input type=\"button\" value=\"交换顺序\" onclick=\"changeSort();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"新增\" onclick=\"location.href='/shop?edit=false'\"></td>");
		buffer.append("<td><input type=\"button\" value=\"修改\" onclick=\"editItem();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"删除\" onclick=\"deleteSelected();\"></td>");
		//buffer.append("<td><input type=\"button\" value=\"重建缓存\" onclick=\"cleanCache();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"下载导入模板\" onclick=\"location.href='/download/import_demo.xls'\"></td>");
		buffer.append("<form action=\"/import\" method=\"post\" enctype=\"multipart/form-data\" onsubmit=\"return checkFile()\"><td><input type=\"submit\" value=\"导入\"></td><td><input id=\"file_select\" type=\"file\" name=\"file\"></td></form>");
		//buffer.append("<td><input type=\"button\" value=\"清空文件\" onclick=\"clearFile();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"退出\" onclick=\"location.href='/login?logout=true'\"></td>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getFilterPage(HttpServletRequest req){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("<form action=\"/shop\" method=\"post\">");
		buffer.append("<td>店铺ID：<input class=\"val w100\" name=\"sid\" value=\"").append(req.getParameter("sid")==null?"":req.getParameter("sid")).append("\"></td>");
		buffer.append("<td>店铺名称：<input class=\"val w100\" name=\"title\" value=\"").append(req.getParameter("title")==null?"":req.getParameter("title")).append("\"></td>");
		buffer.append("<td>状态：<input class=\"val w50\" name=\"status\" value=\"").append(req.getParameter("status")==null?"":req.getParameter("status")).append("\"></td>");
		buffer.append("<td><input type=\"submit\" value=\"查询\"></td>");
		buffer.append("<td><input type=\"button\" value=\"清空\" onclick=\"clearFilter();\"></td>");
		buffer.append("</from>");
		buffer.append("</tr></table>");
		return buffer.toString();
	}
	
	private String getEditPage(ShopModel model, boolean isNew){

		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"edittable\">");
		buffer.append("<form id=\"saveItem\" action=\"/shop?save=true\" method=\"post\" onsubmit=\"return checkItem()\">");
		buffer.append("<tr><th colspan=\"3\">").append(isNew?"新增店铺":"修改店铺").append("</th></tr>");
		buffer.append("<tr><td class=\"right notNull\">*店铺id：</td><td><input class=\"w300").append(isNew?"\"":" disabled\" readonly=\"readonly\" ").append(" name=\"sid\" value=\"").append(model.getSid()==null?"":model.getSid()).append("\"></td><td class=\"left\">店铺ID不能重复。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*店铺名称：</td><td><input class=\"w300\" name=\"title\" value=\"").append(model.getTitle()==null?"":model.getTitle()).append("\"></td><td class=\"left\">40个字符以内。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*上线状态：</td><td><select class=\"w300\" name=\"status\">")
			.append(model.getStatus()!=null&&model.getStatus()==0?"<option value=\"0\" selected=\"selected\"> 0 否</option><option value=\"1\">1 是</option>":"<option value=\"0\"> 0 否</option><option value=\"1\" selected=\"selected\">1 是</option>")
			.append("</select></td><td class=\"left\">上线状态</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*店铺链接：</td><td><input class=\"w300\" name=\"url\" value=\"").append(model.getUrl()==null?"":model.getUrl()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*淘客链接：</td><td><input class=\"w300\" name=\"tk_url\" value=\"").append(model.getTk_url()==null?"":model.getTk_url()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*LOGO链接：</td><td><input class=\"w300\" name=\"logo_url\" value=\"").append(model.getLogo_url()==null?"":model.getLogo_url()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*图片链接：</td><td><input class=\"w300\" name=\"pic_url\" value=\"").append(model.getPic_url()==null?"":model.getPic_url()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*排序字段：</td><td><input class=\"w300\" name=\"sort\" value=\"").append(model.getSort()==null?"":model.getSort()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr>");
		buffer.append("<td class=\"empty\">").append(isNew?"<input type=\"submit\" class=\"w100\" value=\"保存并新增\" onclick=\"setSaveType(true);\">":"").append("</td>");
		buffer.append("<td class=\"empty\"><input type=\"submit\" class=\"w100\" value=\"保存\"></td>");
		buffer.append("<td class=\"empty\"><input type=\"button\" class=\"w100\" value=\"返回\" onclick=\"location.href='/shop'\"></td></tr>");
		buffer.append("<input type=\"hidden\" id=\"saveType\" name=\"saveType\">");
		buffer.append("<input type=\"hidden\" id=\"isNew\" name=\"isNew\" value=\"").append(isNew).append("\">");
		buffer.append("</form>");
		buffer.append("</table>");
		
		return buffer.toString();
	}
	
	private String getListPage(List<ShopModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		buffer.append("共"+list.size()+"家店铺");
		buffer.append("<tr>")
			.append("<th>排序字段</th>")
			.append("<th>店铺ID</th>")
			.append("<th>店铺名称</th>")
			.append("<th>上线状态</th>")
			.append("<th>商品管理</th>")
			.append("<th>商品数量</th>")
			.append("<th>上线数量</th>")
			.append("<th>店铺链接</th>")
			.append("<th>淘客链接</th>")
			.append("<th>LOGO链接</th>")
			.append("<th>图片链接</th>")
			.append("<th>创建时间</th>")
			.append("<th>更新时间</th></tr>");

		for(ShopModel model : list){
			buffer.append("<tr>");
			buffer.append("<td><input id="+model.getSid()+" name=\"checkbox\" type=\"checkbox\">").append(model.getSort()).append("</td>");
			buffer.append("<td>").append(model.getSid()).append("</td>");
			buffer.append("<td>").append(model.getTitle()).append("</td>");
			buffer.append("<td>").append(model.getStatus()).append("</td>");
//			buffer.append("<td><form action=\"item?sid=").append(model.getSid()).append("\"><input type=\"submit\" value=\"查看\"></form></td>");
			buffer.append("<td><a href=\"item?sid=").append(model.getSid()).append("\">查看</a></td>");
			buffer.append("<td>").append(model.getItem_num()).append("</td>");
			buffer.append("<td>").append(model.getItem_on_num()).append("</td>");
			buffer.append("<td>").append(model.getUrl()).append("</td>");
			buffer.append("<td>").append(model.getTk_url()==null?"":model.getTk_url()).append("</td>");
			buffer.append("<td>").append(model.getLogo_url()).append("</td>");
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
