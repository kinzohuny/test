package com.btw.web.utils;

import java.net.InetAddress;

public class ServerUtils {

	private static String ip;
	
	public static String getIp() {
		if (ip == null) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ip;
	}
}
