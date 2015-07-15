package com.btw.crack.main;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.btw.crack.manager.PasswordManager;
import com.btw.crack.task.SevenZipPasswordCrackerTask;
import com.btw.crack.util.StringUtils;
import com.btw.crack.util.SystemUtils;

public class MainClass {

	public static void main(String[] args) {
		try {
			checkArgs(args);
			start();
		} catch (Exception e) {
			SystemUtils.error(e);
		}
			
	}
	

	private static String filePath = null;
	private static int minPasswordLength = 1;
	private static int maxPasswordLength = 6;
	private static String dict = PasswordManager.DICT_CHAR_NUM;
	private static int threadNum = 10;
	private static int runningNum = 0;
	
	private static void showHelp(String message){
		StringBuilder builder = new StringBuilder();
		builder.append("========********========********========\n");
		builder.append("args demo:\n");
		builder.append("C:\\test\\abc.7z -min3 -max5 -dict0123456789abcABC -tnum10\n");
		builder.append("first arg must be file path.\n");
		builder.append("-min:min length of password,default 1.\n");
		builder.append("-max:min length of password,default 6.\n");
		builder.append("-dict：password dict,default only number.\n");
		builder.append("-dict enum：-dict=[num|low|sup|sign|all].\n");
		builder.append("-tnum:the num of thread,default 10.\n");
		builder.append("========********========********========\n");
		
		System.out.println(message);
		SystemUtils.stop(builder.toString());
	}
	
	private static void checkArgs(String[] args) {
		if(args == null||args.length == 0){
			showHelp("args is null!");
		}
		
		if(StringUtils.isNotEmpty(args[0])){
			filePath = args[0];
			if(args[0].indexOf(File.separator) < 0){
				filePath = new File("").getAbsolutePath()+File.separator+args[0];
			}			
			File file = new File(filePath);
			if(!file.exists()||file.isDirectory()){
				showHelp("file ["+args[0]+"] is not exist!");
			}
		}
		
		if(args.length>1){
			for(int i =1; i < args.length; i++){
				String arg = args[i];
				if(StringUtils.isNotEmpty(arg)){
					String argLowerCase = args[i].toLowerCase();
					if(isMin(arg, argLowerCase)){
						continue;
					}
					if(isMax(arg, argLowerCase)){
						continue;
					}
					if(isDict(arg, argLowerCase)){
						continue;
					}
					if(isTNum(arg, argLowerCase)){
						continue;
					}
					if(isDebug(arg, argLowerCase)){
						continue;
					}
					showHelp("[" + arg + "] is unknown arg!");
				}
			}
		}
		
	}
	private static boolean isDebug(String arg, String argLowerCase) {
		if(argLowerCase.equals("-debug")){
			SystemUtils.isDebug = true;
			return true;
		}
		return false;
	}

	private static boolean isTNum(String arg, String argLowerCase) {
		if(argLowerCase.startsWith("-tnum")){
			try {
				threadNum = Integer.valueOf(arg.substring(5));
				return true;
			} catch (Exception e) {
				showHelp("["+arg+"] can not set to be thread num!");
			}
		}
		return false;
	}

	private static boolean isDict(String arg, String argLowerCase) {
		if(argLowerCase.startsWith("-dict=")){
			String dict_enum = argLowerCase.substring(6);
			if("num".equals(dict_enum)){
				return true;
			}
			if("low".equals(dict_enum)){
				dict = PasswordManager.DICT_CHAR_LOW;
				return true;
			}
			if("sup".equals(dict_enum)){
				dict = PasswordManager.DICT_CHAR_SUP;
				return true;
			}
			if("sign".equals(dict_enum)){
				dict = PasswordManager.DICT_CHAR_SIGN;
				return true;
			}
			if("all".equals(dict_enum)){
				dict = PasswordManager.DICT_CHAR_ALL;
				return true;
			}
		}
		if(argLowerCase.startsWith("-dict")){
			dict = arg.substring(5);
			return true;
		}
		return false;
	}

	private static boolean isMax(String arg, String argLowerCase) {
		if(argLowerCase.startsWith("-max")){
			try {
				maxPasswordLength = Integer.valueOf(arg.substring(4));
				return true;
			} catch (Exception e) {
				showHelp("["+arg+"] can not set to max password length!");
			}
		}
		return false;
	}

	private static boolean isMin(String arg, String argLowerCase) {
		if(argLowerCase.startsWith("-min")){
			try {
				minPasswordLength = Integer.valueOf(arg.substring(4));
				return true;
			} catch (Exception e) {
				showHelp("["+arg+"] can not set to min password length!");
			}
		}
		return false;
	}

	private static void start(){
		PasswordManager passwordManager = new PasswordManager(minPasswordLength, maxPasswordLength, dict);
		ScheduledExecutorService service = new ScheduledThreadPoolExecutor(threadNum);
		System.out.println("start time : " + StringUtils.getNowStr());
		for(int i = 0; i < threadNum; i++){
			service.execute(new SevenZipPasswordCrackerTask(filePath, passwordManager, i));
		}
		System.out.println(threadNum+" thread is running!");
	}
	
	public synchronized static void threadAdd(){
		runningNum += 1;
	}
	
	public synchronized static void threadRemove(){
		runningNum -= 1;
		if(runningNum==0){
			SystemUtils.stopWithTimeShow();
		}
	}
}
