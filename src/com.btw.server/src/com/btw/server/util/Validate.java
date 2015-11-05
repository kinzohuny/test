package com.btw.server.util;

public class Validate {

	public static final String Email = "[\\w[.-]]+@[\\w[.-]]+\\.[\\w]+";

	public static final String MOBILE = "^1(3|4|5|7|8)\\d{9}$";

	public static final String PHONE = "^((\\(\\d{3}\\))|(\\d{3}\\-))?(\\(0\\d{2,3}\\)|0\\d{2,3}-)?[1-9]\\d{6,7}$";

	public static final String ZIP_CODE = "^[0-9]\\d{5}$";

	public static final String USER_NAME = "^\\w+$";

	public static boolean isEmail(String email) {
		return email.matches(Email);
	}

	public static boolean isPass(String pass) {
		return !StringUtils.contentChinese(pass) && pass.length() > 5
				&& pass.length() < 30;
	}

	public static boolean isUserName(String userName) {
		return (userName.matches(USER_NAME) && userName.length() > 3 && userName
				.length() < 31)
				|| isEmail(userName);
	}

	public static boolean isMobile(String mobile) {
		if(mobile==null){
			return false;
		}
		return mobile.matches(MOBILE);
	}

	public static boolean isPhone(String phone) {
		return phone.matches(PHONE);
	}

	public static boolean isZipCode(String zipCode) {
		return zipCode.matches(ZIP_CODE);
	}
	
}
