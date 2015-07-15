package com.btw.crack.task;

import com.btw.crack.main.MainClass;
import com.btw.crack.manager.PasswordManager;
import com.btw.crack.util.SevenZipUtils;
import com.btw.crack.util.SystemUtils;

/**
 * 密码破解任务
 * @author Kinzo
 *
 */
public class SevenZipPasswordCrackerTask implements Runnable{

	private SevenZipUtils sevenZipUtils;
	private PasswordManager passwordManager;
	private int threadId;
	
	public SevenZipPasswordCrackerTask(String file, PasswordManager passwordManager, int threadId) {
		this.sevenZipUtils = new SevenZipUtils(file);
		this.passwordManager = passwordManager;
		this.threadId = threadId;
	}
	
	@Override
	public void run() {
		try {
			MainClass.threadAdd();
			SystemUtils.info("Thread-"+threadId+" is running!");
			String password = passwordManager.getPassword();
			while (password!=null) {
				if(sevenZipUtils.isPassword(password)){
					System.out.println("password is found!!!!!!!!!!!!!! the passwod is "+password);
					SystemUtils.stopWithTimeShow();
				}else{
					SystemUtils.info(password + " is wrong!");
					password = passwordManager.getPassword();
				}
			}
			SystemUtils.info("Thread-"+threadId+" is done!");
			MainClass.threadRemove();
		} catch (Exception e) {
			SystemUtils.error(e);
		}

	}

}
