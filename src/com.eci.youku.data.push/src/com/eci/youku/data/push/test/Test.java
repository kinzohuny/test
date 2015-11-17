package com.eci.youku.data.push.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

public class Test {

	public static void main(String[] args) throws IOException, SQLException {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 365);
		System.out.println(c.get(Calendar.YEAR)+"_"+c.get(Calendar.MONTH)+"_"+c.get(Calendar.DAY_OF_MONTH));
		
		System.out.println(getMinuteToHour(22));
	}
	
	private static long getMinuteToHour(int hour){
		Calendar now = Calendar.getInstance();
		Calendar next = Calendar.getInstance();
		next.clear();
		next.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH),
				hour, 0, 0);
		if(now.get(Calendar.HOUR_OF_DAY)>=hour){
			next.add(Calendar.DATE, 1);
		}
		return (next.getTimeInMillis()-now.getTimeInMillis())/1000/60;
	}
}
