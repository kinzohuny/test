package com.btw.query.taobao.shop.utils;

import java.util.Calendar;

public class DateUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		int i =  c.get(Calendar.YEAR);
		 System.out.println(i);

	}

}
