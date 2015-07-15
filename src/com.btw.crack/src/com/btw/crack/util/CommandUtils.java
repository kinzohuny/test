package com.btw.crack.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandUtils {
	public static String exeCmd(String commandStr) {
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(commandStr);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		} catch (Exception e) {
			SystemUtils.error(e);
		} 
		finally
		{
			if (br != null)
			{
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		String commandStr = "ping www.taobao.com";
		//String commandStr = "ipconfig";
		System.out.println(CommandUtils.exeCmd(commandStr));
	}
}

