package servlet;

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

import manage.CacheManage;
import manage.Constants;
import model.ItemModel;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
				if(StringUtils.isNotEmpty(req.getParameter("create"))){
					title = "Create Item";
					page = createHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("update"))){
					title = "Update Item";
					page = updateHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("save"))){
					title = "Save result";
					page = saveHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("delete"))){
					title = "Delete Result";
					page = deleteHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("import"))){
					title = "Import Result";
					page = importHandle(req);
				}else if(StringUtils.isNotEmpty(req.getParameter("clean"))){
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

	private String createHandle(HttpServletRequest req) {
		StringBuffer buffer = new StringBuffer();
		
		// TODO Auto-generated method stub
		return buffer.toString();
	}

	private String updateHandle(HttpServletRequest req) {
		StringBuffer buffer = new StringBuffer();
		
		// TODO Auto-generated method stub
		return buffer.toString();
	}

	private String saveHandle(HttpServletRequest req) {
		StringBuffer buffer = new StringBuffer();
		
		// TODO Auto-generated method stub
		return buffer.toString();
	}

	private String deleteHandle(HttpServletRequest req) throws SQLException, ClassNotFoundException {
		StringBuffer buffer = new StringBuffer();
		String ids = req.getParameter("delete");
		int i = new ItemDao().delete(ids);
		buffer.append("删除成功，已经删除").append(i).append("条记录。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/manage');</script>");
		return buffer.toString();
	}

	private String importHandle(HttpServletRequest req) throws FileUploadException, IOException, SQLException, ClassNotFoundException {
		StringBuffer buffer = new StringBuffer();
		int num = doImport(req);
		
		if(num>0){
			buffer.append("导入成功，已经导入").append(num).append("条记录。<br>");
			buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
			buffer.append("<script type=\"text/javascript\">countDown(3,'/manage');</script>");
		}else{
			buffer.append("导入失败，请检查导入文件。<br>失败原因：<br>");
			buffer.append("<em class=\"error\">"+msg+"</em>");
		}
		return buffer.toString();
	}

	private String cleanHandle(HttpServletRequest req) throws SQLException, ClassNotFoundException {
		StringBuffer buffer = new StringBuffer();
		CacheManage.refreshCache();
		buffer.append("缓存清理成功。<br>");
		buffer.append("<span id=\"jumpTo\">3</span>秒后自动跳转到查询界面...");
		buffer.append("<script type=\"text/javascript\">countDown(3,'/manage');</script>");
		return buffer.toString();
	}

	private String defaultHandle(HttpServletRequest req) throws SQLException, ClassNotFoundException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ItemModel> list = new ItemDao().queryForList(map);
		return getButtonPage()+getListPage(list);
	}

	private int doImport(HttpServletRequest req) throws FileUploadException, IOException, SQLException, ClassNotFoundException {
		FileItem file = getFile(req);
		if(checkFile(file)){
			List<ItemModel> itemList = parseFile(file);
			if(itemList!=null&&!itemList.isEmpty()){
//				return new ItemDao().insert(itemList);
				return itemList.size();
			}
		}
		return 0;
	}

	private List<ItemModel> parseFile(FileItem file) throws IOException {
		List<ItemModel> list = new ArrayList<ItemModel>();
		Workbook workbook = new HSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		if(sheet!=null){
			for(int i=0;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				if(row==null){
					continue;
				}
				ItemModel item = new ItemModel();
				item.setLong_title(row.getCell(1).getStringCellValue());
				list.add(item);
				for(int j=0;j<row.getLastCellNum();j++){
					System.out.print(row.getCell(j)+",");
				}
				System.out.println();
			}
		}else{
			workbook.close();
			msg="找不到Excel中的第一个Sheet，请检查导入文件！";
			return null;
		}
		workbook.close();
		return list;
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
	
	private String getButtonPage(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table><tr>");
		buffer.append("<td><input type=\"button\" value=\"全选\" onclick=\"selectAll();\"></input></td>");
		buffer.append("<td><input type=\"button\" value=\"全清\" onclick=\"selectNone();\"></input></td>");
		buffer.append("<td><input type=\"button\" value=\"新增\"></input></td>");
		buffer.append("<td><input type=\"button\" value=\"修改\"></input></td>");
		buffer.append("<td><input type=\"button\" value=\"删除\" onclick=\"deleteSelected();\"></input></td>");
		buffer.append("<td><input type=\"button\" value=\"下载导入模板\" onclick=\"location.href='/download/import_demo.xls'\"></input></td>");
		buffer.append("<td><form action=\"/manage?import=1\" method=\"post\" enctype=\"multipart/form-data\" onsubmit=\"return checkFile()\"><input type=\"submit\" value=\"导入\"><input id=\"file_select\" type=\"file\" name=\"file\"></form></td>");
		buffer.append("<td><input type=\"button\" value=\"清空文件\" onclick=\"clearFile();\"></input></td>");
		buffer.append("<td><input type=\"button\" value=\"清空缓存\" onclick=\"cleanCache();\"></input></td>");
		buffer.append("</tr></table>");
		
		return buffer.toString();
	}
	
	private String getEditPage(ItemModel model){
		StringBuffer buffer = new StringBuffer();
		
		
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
			buffer.append("<td><input id="+model.getId()+" name=\"checkbox\" type=\"checkbox\">").append(model.getId()).append("</input></td>");
			buffer.append("<td>").append(model.getCategory_code()).append("</td>");
			buffer.append("<td>").append(model.getCategory_name()).append("</td>");
			buffer.append("<td>").append(model.getTagid()).append("</td>");
			buffer.append("<td>").append(model.getTag()).append("</td>");
			buffer.append("<td>").append(model.getLong_title()).append("</td>");
			buffer.append("<td>").append(model.getIdentify()).append("</td>");
			buffer.append("<td>").append(model.getPrice()).append("</td>");
			buffer.append("<td>").append(model.getCheap()).append("</td>");
			buffer.append("<td>").append(model.getPost()).append("</td>");
			buffer.append("<td>").append(model.getStatus()).append("</td>");
			buffer.append("<td>").append(model.getSite()).append("</td>");
			buffer.append("<td>").append(model.getSite_url()).append("</td>");
			buffer.append("<td>").append(model.getUrl()).append("</td>");
			buffer.append("<td>").append(model.getWapurl()).append("</td>");
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
