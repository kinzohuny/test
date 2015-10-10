package servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import manage.CacheManage;
import manage.Constants;
import model.CategoryModel;
import model.ItemModel;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import utils.StringUtils;
import dao.ItemDao;

public class ManageServlet extends HttpServlet {

	private static final long serialVersionUID = -8693978090360750531L;
	private static final Logger logger = Logger.getLogger(ManageServlet.class);

	String msg = "";
	String title = "";
	String page = "";
	
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
						title = "Create Item";
					}else{
						title = "Modify Item";
					}
					page = editHandle(req, isNew);
				}else if(StringUtils.isNotEmpty(req.getParameter("save"))){
					title = "Save result";
					page = saveHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("status"))){
					title = "Status Update Result";
					page = statusHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("delete"))){
					title = "Delete Result";
					page = deleteHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("import"))){
					title = "Import Result";
					page = importHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("cleanCache"))){
					title = "Clean Cache Result";
					page = cleanHandle(req);
				}else{
					title = "Item List";
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

	private String editHandle(HttpServletRequest req, boolean isNew) throws SQLException {
		ItemModel item = null;
		if(isNew){
			item = new ItemModel();
		}else{
			String id = req.getParameter("id");
			if(StringUtils.isNotEmpty(id)){
				item = new ItemDao().queryById(id);
			}
			if(item==null){
				throw new IllegalArgumentException("id="+id+" 找不到对应的商品！");
			}
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(getEditPage(item, isNew));
		return buffer.toString();
	}

	private String saveHandle(HttpServletRequest req) {
		StringBuffer buffer = new StringBuffer();
		// TODO Auto-generated method stub
		//post提交、参数转对象入库、后台校验、js校验
		System.out.println(req.getParameter("save"));
		System.out.println(req.getParameter("isNew"));
		System.out.println(req.getParameter("id"));
		System.out.println(req.getParameter("tagid"));
		System.out.println(req.getParameter("long_title"));
		return buffer.toString();
	}

	private String statusHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		String status = req.getParameter("status");
		String ids = req.getParameter("ids");
		int i = new ItemDao().updateStatus(ids, "1".equals(status)?1:0);
		buffer.append("更新成功，已经"+("1".equals(status)?"启用":"停用")).append(i).append("条记录。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/manage');</script>");
		return buffer.toString();
	}

	private String deleteHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		String ids = req.getParameter("delete");
		int i = new ItemDao().delete(ids);
		buffer.append("删除成功，已经删除").append(i).append("条记录。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/manage');</script>");
		return buffer.toString();
	}

	private String importHandle(HttpServletRequest req) throws FileUploadException, IOException, SQLException {
		StringBuffer buffer = new StringBuffer();
		int num = doImport(req);
		
		if(num>0){
			buffer.append("导入成功，已经导入").append(num).append("条记录。<br>");
			buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
			buffer.append("<script type=\"text/javascript\">countDown(3,'/manage');</script>");
		}else{
			buffer.append("导入失败，请检查导入文件。<br>失败原因：<br>");
			buffer.append("<textarea cols=\"100\" rows=\"20\" readonly=\"readonly\">"+msg+"</textarea>");
		}
		return buffer.toString();
	}

	private String cleanHandle(HttpServletRequest req) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		CacheManage.refreshCache();
		buffer.append("缓存清理成功。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/manage');</script>");
		return buffer.toString();
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ItemModel> list = new ItemDao().queryForList(map);
		return getButtonPage()+getListPage(list)+getHiddenPage();
	}

	private int doImport(HttpServletRequest req) throws FileUploadException, IOException, SQLException {
		FileItem file = getFile(req);
		if(checkFile(file)){
			List<ItemModel> itemList = parseFile(file);
			if(itemList!=null&&!itemList.isEmpty()){
				return new ItemDao().insert(itemList);
			}
		}
		return 0;
	}

	private List<ItemModel> parseFile(FileItem file) throws IOException, SQLException {
		List<ItemModel> list = new ArrayList<ItemModel>();
		Workbook workbook = new HSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		StringBuffer errorInfo = new StringBuffer();
		if(sheet!=null){
			
			Set<Long> fileIdentifySet = new HashSet<Long>();
			Set<Long> dbIdentifySet = getDbIdentifySet();
			
			for(int i=1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				if(row==null){
					continue;
				}
				ItemModel item = new ItemModel();
				
				Long tagId = StringUtils.toLong(getCellValue(row.getCell(0)));
				if(tagId!=null||!CacheManage.getTagIdSet().contains(tagId)){
					errorInfo.append("请检查单元格["+(i+1)+",A]的值：（tagid）为空||不在tagId范围内。").append("\r\n");
				}else{
					item.setTagid(tagId);
				}
				
				String long_title = getCellValue(row.getCell(1));
				if(StringUtils.isEmpty(long_title)){
					errorInfo.append("请检查单元格["+(i+1)+",B]的值：（商品标题）为空。").append("\r\n");
				}else{
					item.setLong_title(long_title);
				}
				
				Long identify = StringUtils.toLong(getCellValue(row.getCell(2)));
				if(identify==null){
					errorInfo.append("请检查单元格["+(i+1)+",C]的值：（商品id）为空||格式不正确。").append("\r\n");
				}else if(fileIdentifySet.contains(identify)){
					errorInfo.append("请检查单元格["+(i+1)+",C]的值：（商品id）与文件内记录有重复。").append("\r\n");
				}else if(dbIdentifySet.contains(identify)){
					errorInfo.append("请检查单元格["+(i+1)+",C]的值：（商品id）与数据库记录有重复。").append("\r\n");
				}else{
					item.setIdentify(identify);
					fileIdentifySet.add(identify);
				}
				
				BigDecimal price = StringUtils.toBigDecimal(getCellValue(row.getCell(3)));
				if(price==null){
					errorInfo.append("请检查单元格["+(i+1)+",D]的值：（现价）为空||格式不正确。").append("\r\n");
				}else{
					item.setPrice(price);
				}
				
				BigDecimal cheap = StringUtils.toBigDecimal(getCellValue(row.getCell(4)));
				if(StringUtils.isNotEmpty(getCellValue(row.getCell(4)))&&cheap==null){
					errorInfo.append("请检查单元格["+(i+1)+",E]的值：（优惠券金额）格式不正确。").append("\r\n");
				}else{
					item.setCheap(cheap);
				}
				
				Integer post = StringUtils.toInteger(getCellValue(row.getCell(5)));
				if(post==null){
					errorInfo.append("请检查单元格["+(i+1)+",F]的值：（包邮）为空||格式不正确。").append("\r\n");
				}else if(post==1){
					item.setPost(1);
				}else{
					item.setPost(0);
				}
				
				Integer status = StringUtils.toInteger(getCellValue(row.getCell(6)));
				if(status==null){
					errorInfo.append("请检查单元格["+(i+1)+",G]的值：（启用）为空||格式不正确。").append("\r\n");
				}else if(status==1){
					item.setStatus(1);
				}else{
					item.setStatus(0);
				}
				
				String site = getCellValue(row.getCell(7));
				if(StringUtils.isEmpty(site)){
					errorInfo.append("请检查单元格["+(i+1)+",H]的值：（商铺名称）为空。").append("\r\n");
				}else{
					item.setSite(site);
				}
				
				String site_url = getCellValue(row.getCell(8));
				if(StringUtils.isEmpty(site_url)){
					errorInfo.append("请检查单元格["+(i+1)+",I]的值：（商铺URL）为空。").append("\r\n");
				}else{
					item.setSite_url(site_url);
				}
				
				String url = getCellValue(row.getCell(9));
				if(StringUtils.isEmpty(url)){
					errorInfo.append("请检查单元格["+(i+1)+",J]的值：（商品详情URL）为空。").append("\r\n");
				}else{
					item.setUrl(url);
				}
				
				String wapurl = getCellValue(row.getCell(10));
				item.setWapurl(StringUtils.isEmpty(wapurl)?"":wapurl);
				
				String img_url = getCellValue(row.getCell(11));
				if(StringUtils.isEmpty(img_url)){
					errorInfo.append("请检查单元格["+(i+1)+",L]的值：（商品图片URL）为空。").append("\r\n");
				}else{
					item.setImg_url(img_url);
				}
				list.add(item);
			}
		}
		workbook.close();
		if(errorInfo.length()>0){
			msg=errorInfo.toString();
			return null;
		}
		return list;
	}
	
	private String getCellValue(Cell cell){
		String value = null;
		if(cell==null){
			return null;
		}
		switch(cell.getCellType()){
			case Cell.CELL_TYPE_NUMERIC :
				value = StringUtils.formatDouble2(cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_STRING :
				value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BOOLEAN :
				value = StringUtils.toString(cell.getBooleanCellValue());
				break;
			default :
				value = "";
				break;
		}
		return value;
	}

	private Set<Long> getDbIdentifySet() throws SQLException {
		Set<Long> dbIdentifySet = new HashSet<Long>();
		List<ItemModel> list = new ItemDao().queryForList(null);
		for(ItemModel item : list){
			dbIdentifySet.add(item.getIdentify());
		}
		return dbIdentifySet;
	}

	private boolean checkFile(FileItem file) {
		if(file==null){
			msg="导入文件为空，请检查导入文件！";
			return false;
		}
		String fileName = file.getName();
		if(StringUtils.isEmpty(fileName)||!(fileName.toLowerCase().endsWith(".xls")||fileName.toLowerCase().endsWith(".xlsx"))){
			msg="只能导入xls或xlsx文件！";
			return false;
		}
		return true;
	}
	
	private FileItem getFile(HttpServletRequest req) throws FileUploadException, IOException{
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
		List<FileItem> fileList = servletFileUpload.parseRequest(req);
		if(fileList!=null&&!fileList.isEmpty()){
			return fileList.get(0);
		}
		return null;
	}

	private String getHiddenPage() {
		StringBuffer buffer = new StringBuffer();
		//删除操作表单
		
		//清空缓存表单
		
		//更新状态表单
		
		return buffer.toString();
	}
	
	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"menutable\"><tr>");
		buffer.append("<td><input type=\"button\" value=\"全选\" onclick=\"selectAll();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"全清\" onclick=\"selectNone();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"启用\" onclick=\"setStatus(1);\"></td>");
		buffer.append("<td><input type=\"button\" value=\"停用\" onclick=\"setStatus(0);\"></td>");
		buffer.append("<td><input type=\"button\" value=\"新增\" onclick=\"location.href='/manage?edit=false'\"></td>");
		buffer.append("<td><input type=\"button\" value=\"修改\" onclick=\"editItem();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"删除\" onclick=\"deleteSelected();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"清空缓存\" onclick=\"cleanCache();\"></td>");
		buffer.append("<td><input type=\"button\" value=\"下载导入模板\" onclick=\"location.href='/download/import_demo.xls'\"></td>");
		buffer.append("<form action=\"/manage?import=1\" method=\"post\" enctype=\"multipart/form-data\" onsubmit=\"return checkFile()\"><td><input type=\"submit\" value=\"导入\"></td><td><input id=\"file_select\" type=\"file\" name=\"file\"></td></form>");
		//buffer.append("<td><input type=\"button\" value=\"清空文件\" onclick=\"clearFile();\"></td>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getEditPage(ItemModel item, boolean isNew){

		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"edittable\">");
		buffer.append("<form id=\"saveItem\" action=\"/manage?save=true\" method=\"post\" onsubmit=\"return checkItem()\">");
		buffer.append("<tr><th colspan=\"3\">").append(isNew?"新增商品":"修改商品").append("</th></tr>");
		buffer.append("<tr><td>ID：</td><td><input class=\"w300\" name=\"id\" readonly=\"readonly\" value=\"").append(item.getId()==null?"":item.getId()).append("\"></td><td class=\"left\"><input type=\"hidden\" name=\"isNew\" value=\"").append(isNew).append("\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*tag_id：</td>");
		buffer.append("<td><select class=\"w300\" name=\"tagid\">");
		List<CategoryModel> cateList = CacheManage.getCategoryList();
		Long defaultTagig = 1L;
		if(item.getTagid()!=null){
			defaultTagig = item.getTagid();
		}
		for(CategoryModel cate : cateList){
			buffer.append("<option value=\"").append(cate.getTagid()).append("\"").append(defaultTagig.equals(cate.getTagid())?" selected=\"selected\"":"").append(">").append(cate.getTagid()+" "+cate.getTag_name()).append("</option>");
		}
		buffer.append("</select></td>");
		buffer.append("<td class=\"left\">固定的分类编号。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商品标题：</td><td><input class=\"w300\" name=\"long_title\" value=\"").append(item.getLong_title()==null?"":item.getLong_title()).append("\"></td><td class=\"left\">40个字符以内。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商品id：</td><td><input class=\"w300\" name=\"identify\" value=\"").append(item.getIdentify()==null?"":item.getIdentify()).append("\"></td><td class=\"left\">商品ID不能重复。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*现价：</td><td><input class=\"w300\" name=\"price\" value=\"").append(item.getPrice()==null?"":item.getPrice()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right\">优惠卷金额：</td><td><input class=\"w300\" name=\"cheap\" value=\"").append(item.getCheap()==null?"":item.getCheap()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*包邮：</td><td><select class=\"w300\" name=\"post\">")
			.append(item.getPost()!=null&&item.getPost()==1?"<option value=\"0\"> 0 否</option><option value=\"1\" selected=\"selected\">1 是</option>":"<option value=\"0\" selected=\"selected\"> 0 否</option><option value=\"1\">1 是</option>")
			.append("</select></td><td class=\"left\">是否包邮</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*启用：</td><td><select class=\"w300\" name=\"status\">")
			.append(item.getStatus()!=null&&item.getStatus()==1?"<option value=\"0\"> 0 否</option><option value=\"1\" selected=\"selected\">1 是</option>":"<option value=\"0\" selected=\"selected\"> 0 否</option><option value=\"1\">1 是</option>")
			.append("</select></td><td class=\"left\">是否启用</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商铺名称：</td><td><input class=\"w300\" name=\"site\" value=\"").append(item.getSite()==null?"":item.getSite()).append("\"></td><td class=\"left\">40个字符以内。</td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商铺url：</td><td><input class=\"w300\" name=\"site_url\" value=\"").append(item.getSite_url()==null?"":item.getSite_url()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商品详情url：</td><td><input class=\"w300\" name=\"url\" value=\"").append(item.getUrl()==null?"":item.getUrl()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right\">商品详情wap_url：</td><td><input class=\"w300\" name=\"wap_url\" value=\"").append(item.getWapurl()==null?"":item.getWapurl()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr><td class=\"right notNull\">*商品图片url：</td><td><input class=\"w300\" name=\"img_url\" value=\"").append(item.getImg_url()==null?"":item.getImg_url()).append("\"></td><td class=\"left\"></td></tr>");
		buffer.append("<tr>");
		buffer.append("<td class=\"empty\">").append(isNew?"<input type=\"submit\" class=\"w100\" value=\"保存并新增\">":"").append("</td>");
		buffer.append("<td class=\"empty\"><input type=\"submit\" class=\"w100\" value=\"保存\"></td>");
		buffer.append("<td class=\"empty\"><input type=\"button\" class=\"w100\" value=\"返回\" onclick=\"location.href='/manage'\"></td></tr>");
		buffer.append("</form>");
		buffer.append("</table>");
		
		return buffer.toString();
	}
	
	private String getListPage(List<ItemModel> list){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table class=\"gridtable\">");
		
		buffer.append("<tr>")
			.append("<th>id</th>")
			.append("<th>参数代码</th>")
			.append("<th>参数</th>")
			.append("<th>tagid</th>")
			.append("<th>tag</th>")
			.append("<th>标题</th>")
			.append("<th>商品ID</th>")
			.append("<th>现价</th>")
			.append("<th>优惠</th>")
			.append("<th>包邮</th>")
			.append("<th>启用</th>")
			.append("<th>店铺名称</th>")
			.append("<th>店铺地址</th>")
			.append("<th>商品详情地址</th>")
			.append("<th>商品详情wap地址</th>")
			.append("<th>商品图片地址</th>")
			.append("<th>创建时间</th>")
			.append("<th>修改时间</th></tr>");

		for(ItemModel model : list){
			buffer.append("<tr>");
			buffer.append("<td><input id="+model.getId()+" name=\"checkbox\" type=\"checkbox\">").append(model.getId()).append("</td>");
			buffer.append("<td>").append(model.getCategory_code()).append("</td>");
			buffer.append("<td>").append(model.getCategory_name()).append("</td>");
			buffer.append("<td>").append(model.getTagid()).append("</td>");
			buffer.append("<td>").append(model.getTag()).append("</td>");
			buffer.append("<td>").append(model.getLong_title()).append("</td>");
			buffer.append("<td>").append(model.getIdentify()).append("</td>");
			buffer.append("<td>").append(model.getPrice()).append("</td>");
			buffer.append("<td>").append(model.getCheap()==null?0:model.getCheap()).append("</td>");
			buffer.append("<td>").append(model.getPost()).append("</td>");
			buffer.append("<td>").append(model.getStatus()).append("</td>");
			buffer.append("<td>").append(model.getSite()).append("</td>");
			buffer.append("<td>").append(model.getSite_url()).append("</td>");
			buffer.append("<td>").append(model.getUrl()).append("</td>");
			buffer.append("<td>").append(model.getWapurl()==null?"":model.getWapurl()).append("</td>");
			buffer.append("<td>").append(model.getImg_url()).append("</td>");
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
