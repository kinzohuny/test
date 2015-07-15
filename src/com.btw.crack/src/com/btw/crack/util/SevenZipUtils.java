package com.btw.crack.util;

import java.io.File;

public class SevenZipUtils {
	
	private String filePath;
	private String sevenZaPath;
	
	public SevenZipUtils(String filePath){
		this.filePath = filePath;
		sevenZaPath = get7zaPath();
	}

	public synchronized boolean isPassword(String password){
		String commandStr = new StringBuilder(sevenZaPath).append(" l \"").append(filePath).append("\" -p").append(password).toString();
		String result = CommandUtils.exeCmd(commandStr);
		return checkResult(result);
	}
	
	private synchronized boolean checkResult(String result) {
		if(result.indexOf("Listing archive:")>0){
			return true;
		}
		return false;
	}

	private String get7zaPath(){
		String realFile = new File("").getAbsolutePath() + File.separator +"7za.exe";

		if(realFile.indexOf(" ")>=0){
			SystemUtils.stop("jar file path has space, please change jar file path!");
		}
		
		if(!new File(realFile).exists()||new File(realFile).isDirectory()){
			SystemUtils.stop("can not find 7za.exe! the 7za.exe and jar must be together!");
		}
		return realFile;
	}
}
