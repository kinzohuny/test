package com.btw.file.handle.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxUtils {

	public static XSSFWorkbook readWorkbook(String filePath) throws IOException{

		InputStream is = null;
		XSSFWorkbook wb = null;
		try {
			is = new FileInputStream(filePath);
			wb = new XSSFWorkbook(is);
		} finally{
			if(is!=null){
				is.close();
			}
		}
		return wb;
	}
	
	public static void writeWorkbook(XSSFWorkbook wb ,String filePath) throws IOException{

		FileOutputStream fis = null;
		try {
			fis = new FileOutputStream(filePath);
			wb.write(fis);
		} finally {
			if(fis!=null){
				fis.close();
			}
		}
	}
	
	public static void main(String[] args) {
		String filePath = "D:"+File.separatorChar+"temp"+File.separatorChar;
		String fileName = "database.xls";
		XSSFWorkbook wb = null;
		try {
			wb = readWorkbook(filePath+fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(wb!=null){
			System.out.println("there is "+wb.getSheetAt(0).getLastRowNum()+" rows!");
			handle(wb);
			System.out.println("there is "+wb.getSheetAt(0).getLastRowNum()+" rows!");
			try {
				writeWorkbook(wb, filePath+"1"+fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("file is null!");
		}
		
	}
	
	public static void handle(XSSFWorkbook wb){
		XSSFSheet result = wb.createSheet("result");
		XSSFSheet sheet = wb.getSheetAt(0);
		int j = 0;
		int x = -1;
		int y = -1;
		for(int i = sheet.getFirstRowNum();i<sheet.getLastRowNum();i++){
			XSSFRow row = sheet.getRow(i);
			if(row.getCell(0)==null||row.getCell(0).getStringCellValue()==null||"".equals(row.getCell(0).getStringCellValue())){
				XSSFRow resultRow = result.createRow(j++);
				rowCopy(row, resultRow);
				resultRow.createCell(1);
				if(x==-1){
					x = resultRow.getRowNum();
				}
				y = resultRow.getRowNum();
			}else{
				XSSFRow resultRow1 = result.createRow(j++);
				rowCopy(row, resultRow1);
				resultRow1.createCell(2);
				resultRow1.createCell(3);
				resultRow1.createCell(4);
				XSSFRow resultRow2 = result.createRow(j++);
				rowCopy(row, resultRow2);
				resultRow2.createCell(0);
				resultRow2.createCell(1);
				
				
				if(y-x>0){
					result.groupRow(x, y);
					x=-1;
					y=-1;
				}
				x = resultRow2.getRowNum();
			}
		}
//		HSSFRow row = sheet.createRow(1);
//		row.createCell(0).setCellValue("haha");
		
	} 
	
	private static void rowCopy(XSSFRow src, XSSFRow target){
		for(int i = src.getFirstCellNum(); i < src.getLastCellNum(); i++){
			if(src.getCell(i)!=null){
				target.createCell(i).setCellValue(src.getCell(i).getStringCellValue());
			}
		}
	}
}
