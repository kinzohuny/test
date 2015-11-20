package com.eci.youku.servlet.assist;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

import com.eci.youku.constant.Constants;
import com.eci.youku.dao.ItemDao;
import com.eci.youku.dao.ShopDao;
import com.eci.youku.model.ItemModel;
import com.eci.youku.model.ShopModel;
import com.eci.youku.util.StringUtils;

public class ImportServlet extends HttpServlet {

	private static final long serialVersionUID = -4227992424945185378L;
	private static final Logger logger = Logger.getLogger(ImportServlet.class);

	private ShopDao shopDao = new ShopDao();
	private ItemDao itemDao = new ItemDao();
	private String errorMsg = "";
	
	List<ShopModel> shopList;
	List<ItemModel> itemList;
	
	int shopNum = 0;
	int itemNum = 0;
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		if(session.getAttribute(Constants.SESSION_IS_LOGIN)==null){
			resp.sendRedirect("/login");
		}else{
			String title = "";
			String page = "";
			try {
				title = "导入结果";
				page = importHandle(req);
			} catch (Exception e) {
				title = "Exception";
				page = e.getMessage();
				logger.error(page, e);
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
	
	private String importHandle(HttpServletRequest req) throws FileUploadException, IOException, SQLException {
		StringBuffer buffer = new StringBuffer();
		errorMsg = "";
		shopNum = 0;
		itemNum = 0;
		String result = doImport(req);
		
		if(shopNum>0 || itemNum>0){
			buffer.append("导入成功，").append(result).append("<br>");
			buffer.append("<span id=\"jumpTo\">5</span>秒后自动跳转到查询界面...");
			buffer.append("<script type=\"text/javascript\">countDown(5,'/shop');</script>");
		}else{
			buffer.append("导入失败，请检查导入文件。<br>失败原因：<br>");
			buffer.append(getErrorReasonPage());
			buffer.append("<br><input type=\"button\" onClick=\"javascript:history.back(-1);\" value=\"返回\">");
		}
		return buffer.toString();
	}
	
	private String doImport(HttpServletRequest req) throws FileUploadException, IOException, SQLException {
		FileItem file = getFileFromReq(req);
		StringBuffer buffer = new StringBuffer("");
		if(checkFile(file)){
			parseFile(file);
			if(shopList!=null&&!shopList.isEmpty()){
				shopNum = shopDao.insert(shopList);
				buffer.append("<br>店铺记录：新增"+(shopList.size()+shopList.size()-shopNum)+"条，覆盖"+(shopNum-shopList.size())+"条。"); 
			}
			if(itemList!=null&&!itemList.isEmpty()){
				itemNum = itemDao.insert(itemList);
				buffer.append("<br>商品记录：新增"+(itemList.size()+itemList.size()-itemNum)+"条，覆盖"+(itemNum-itemList.size())+"条。"); 
			}
		}
		return buffer.toString();
	}

	private void parseFile(FileItem file) throws IOException, SQLException {
		
		Workbook workbook = new HSSFWorkbook(file.getInputStream());
		StringBuffer errorInfo = new StringBuffer();
		Set<Long> fileSidSet = new HashSet<Long>();
		
		//读取excel中的店铺信息
		Sheet sheet = workbook.getSheetAt(0);
		if(sheet!=null){
			shopList = new ArrayList<ShopModel>();
			for(int i=1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				if(row==null){
					continue;
				}
				ShopModel shop = new ShopModel();
				
				Long sid = StringUtils.toLong(getCellValue(row.getCell(0)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(0)))){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",A]的值：（店铺id）为空。").append("\r\n");
				}else if(sid==null){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",A]的值：（店铺id）格式不正确。").append("\r\n");
				}else if(fileSidSet.contains(sid)){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",A]的值：（店铺id）与文件内记录有重复。").append("\r\n");
				}else{
					shop.setSid(sid);
					fileSidSet.add(sid);
				}
				
				String title = getCellValue(row.getCell(1))==null?null:getCellValue(row.getCell(1)).trim();
				if(StringUtils.isEmpty(title)){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",B]的值：（店铺名称）为空。").append("\r\n");
				}else{
					shop.setTitle(title);
				}
				
				Integer status = StringUtils.toInteger(getCellValue(row.getCell(2)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(2)))){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",C]的值：（启用）为空。").append("\r\n");
				}else if(status==null){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",C]的值：（启用）格式不正确。").append("\r\n");
				}else if(status==1){
					shop.setStatus(1);
				}else{
					shop.setStatus(0);
				}
				
				String url = getCellValue(row.getCell(3))==null?null:getCellValue(row.getCell(3)).trim();
				if(StringUtils.isEmpty(url)){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",D]的值：（店铺链接）为空。").append("\r\n");
				}else{
					shop.setUrl(url);
				}
				
				String tk_url = getCellValue(row.getCell(4))==null?null:getCellValue(row.getCell(4)).trim();
				if(StringUtils.isEmpty(tk_url)){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",E]的值：（淘客链接）为空。").append("\r\n");
				}else{
					shop.setTk_url(tk_url);
				}
				
				String logo_url = getCellValue(row.getCell(5))==null?null:getCellValue(row.getCell(5)).trim();
				if(StringUtils.isEmpty(logo_url)){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",F]的值：（LOGO链接）为空。").append("\r\n");
				}else if(logo_url.indexOf("cdn.hao.ad.intf.ecinsight.cn")>=0){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",F]的值：（LOGO链接）不应使用cdn地址。").append("\r\n");
				}else{
					shop.setLogo_url(logo_url);
				}
				
				String pic_url = getCellValue(row.getCell(6))==null?null:getCellValue(row.getCell(6)).trim();
				if(StringUtils.isEmpty(pic_url)){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",G]的值：（图片链接）为空。").append("\r\n");
				}else if(pic_url.indexOf("cdn.hao.ad.intf.ecinsight.cn")>=0){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",G]的值：（图片链接）不应使用cdn地址。").append("\r\n");
				}else{
					shop.setPic_url(pic_url);
				}
				
				Long sort = StringUtils.toLong(getCellValue(row.getCell(7)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(7)))){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",H]的值：（排序字段）为空。").append("\r\n");
				}else if(sort==null){
					errorInfo.append("请检查【表单1】单元格["+(i+1)+",H]的值：（排序字段）只能为整数。").append("\r\n");
				}else{
					shop.setSort(sort);
				}
				shopList.add(shop);
			}
		}
		
		//读取excel中的商品信息
		sheet = workbook.getSheetAt(1);
		if(sheet!=null){
			itemList = new ArrayList<ItemModel>();
			for(int i=1;i<=sheet.getLastRowNum();i++){
				Row row = sheet.getRow(i);
				if(row==null){
					continue;
				}
				ItemModel item = new ItemModel();
				
				Long iid = StringUtils.toLong(getCellValue(row.getCell(0)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(0)))){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",A]的值：（商品id）为空。").append("\r\n");
				}else if(iid==null){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",A]的值：（商品id）格式不正确。").append("\r\n");
				}else if(fileSidSet.contains(iid)){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",A]的值：（商品id）与文件内记录有重复。").append("\r\n");
				}else{
					item.setIid(iid);
					fileSidSet.add(iid);
				}
				
				Long sid = StringUtils.toLong(getCellValue(row.getCell(1)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(1)))){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",B]的值：（店铺id）为空。").append("\r\n");
				}else if(sid==null){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",B]的值：（店铺id）格式不正确。").append("\r\n");
				}else if(!fileSidSet.contains(sid) && shopDao.queryById(sid.toString())==null){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",B]的值：（店铺id）没有对应店铺。").append("\r\n");
				}else{
					item.setSid(sid);
					fileSidSet.add(sid);
				}
				
				String title = getCellValue(row.getCell(2))==null?null:getCellValue(row.getCell(2)).trim();
				if(StringUtils.isEmpty(title)){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",C]的值：（商品名称）为空。").append("\r\n");
				}else{
					item.setTitle(title);
				}
				
				BigDecimal price = StringUtils.toBigDecimal(getCellValue(row.getCell(3)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(3)))){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",D]的值：（价格）为空。").append("\r\n");
				}else if(price==null){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",D]的值：（价格）格式不正确。").append("\r\n");
				}else{
					item.setPrice(price);
				}
				
				Integer status = StringUtils.toInteger(getCellValue(row.getCell(4)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(4)))){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",E]的值：（启用）为空。").append("\r\n");
				}else if(status==null){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",E]的值：（启用）格式不正确。").append("\r\n");
				}else if(status==1){
					item.setStatus(1);
				}else{
					item.setStatus(0);
				}
				
				String url = getCellValue(row.getCell(5))==null?null:getCellValue(row.getCell(5)).trim();
				if(StringUtils.isEmpty(url)){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",F]的值：（商品链接）为空。").append("\r\n");
				}else{
					item.setUrl(url);
				}
				
				String tk_url = getCellValue(row.getCell(6))==null?null:getCellValue(row.getCell(6)).trim();
				if(StringUtils.isEmpty(tk_url)){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",G]的值：（淘客链接）为空。").append("\r\n");
				}else{
					item.setTk_url(tk_url);
				}
				
				String pic_url = getCellValue(row.getCell(7))==null?null:getCellValue(row.getCell(7)).trim();
				if(StringUtils.isEmpty(pic_url)){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",H]的值：（图片链接）为空。").append("\r\n");
				}else if(pic_url.indexOf("cdn.hao.ad.intf.ecinsight.cn")>=0){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",H]的值：（图片链接）不应使用cdn地址。").append("\r\n");
				}else{
					item.setPic_url(pic_url);
				}
				
				Long sort = StringUtils.toLong(getCellValue(row.getCell(8)));
				if(StringUtils.isEmpty(getCellValue(row.getCell(8)))){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",J]的值：（排序字段）为空。").append("\r\n");
				}else if(sort==null){
					errorInfo.append("请检查【表单2】单元格["+(i+1)+",J]的值：（排序字段）只能为整数。").append("\r\n");
				}else{
					item.setSort(sort);
				}
				itemList.add(item);
			}
		}
		workbook.close();
		
		//读取完成，判断检查结果
		if(errorInfo.length()>0){
			errorMsg=errorInfo.toString();
			itemList =  null;
			shopList = null;
		}
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

	private boolean checkFile(FileItem file) {
		if(file==null){
			errorMsg="导入文件为空，请检查导入文件！";
			return false;
		}
		String fileName = file.getName();
		if(StringUtils.isEmpty(fileName)||!(fileName.toLowerCase().endsWith(".xls")||fileName.toLowerCase().endsWith(".xlsx"))){
			errorMsg="只能导入xls或xlsx文件！";
			return false;
		}
		return true;
	}
	
	private FileItem getFileFromReq(HttpServletRequest req) throws FileUploadException, IOException{
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
		List<FileItem> fileList = servletFileUpload.parseRequest(req);
		if(fileList!=null&&!fileList.isEmpty()){
			return fileList.get(0);
		}
		return null;
	}
	
	private Object getErrorReasonPage() {
		return "<textarea cols=\"100\" rows=\"20\" readonly=\"readonly\">"+errorMsg+"</textarea>";
	}
}
