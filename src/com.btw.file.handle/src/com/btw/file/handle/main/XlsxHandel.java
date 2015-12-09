package com.btw.file.handle.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.btw.file.handle.utils.XlsxUtils;

public class XlsxHandel {

	public final static String filePath = "D:\\temp\\tmp\\pped2k整理.xlsx";

	public static void main(String[] args) throws UnsupportedEncodingException {
		// 读取Excel
		XSSFWorkbook workbook = null;
		try {
			workbook = XlsxUtils.readWorkbook(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (workbook != null) {
			// 读取第一个页签的内容
			XSSFSheet sheet = workbook.getSheetAt(0);
			String url;
			String urlName;
			String name;

			for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet
					.getLastRowNum(); rowNum++) {
				XSSFRow row = sheet.getRow(rowNum);
				// 第1列为空，第2列不为空时
				if ((row.getCell(1) != null
						&& !isEmpty(row.getCell(1).getStringCellValue()) && (row
						.getCell(0) == null || isEmpty(row.getCell(0)
						.getStringCellValue())))) {
					url = row.getCell(1).getStringCellValue();
					urlName = url.split("\\|")[2];
					name = URLDecoder.decode(urlName, "utf8");
					row.createCell(0).setCellValue(name);
					
					System.out.println(rowNum+"|"+urlName+"|"+name);
				}
			}


			try {
				// 保存
				XlsxUtils.writeWorkbook(workbook, filePath+"_1");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isEmpty(String s) {
		if(s==null || s.trim().length()==0){
			return true;
		}
		return false;
	}
}
