package com.btw.file.handle.main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.btw.file.handle.utils.XlsUtils;

public class XlsHandel {

	public final static String filePath = "D:\\temp\\tmp\\pped2k整理.xls";

	public static void main(String[] args) throws UnsupportedEncodingException {
		// 读取Excel
		HSSFWorkbook workbook = null;
		try {
			workbook = XlsUtils.readWorkbook(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (workbook != null) {
			// 读取第一个页签的内容
			HSSFSheet sheet = workbook.getSheetAt(0);
			String url;
			String urlName;
			String name;

			for (int rowNum = sheet.getFirstRowNum(); rowNum <= sheet
					.getLastRowNum(); rowNum++) {
				HSSFRow row = sheet.getRow(rowNum);
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
				XlsUtils.writeWorkbook(workbook, filePath+"_1");
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
