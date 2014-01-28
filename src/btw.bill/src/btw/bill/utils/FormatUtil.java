package btw.bill.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {

	public static String timeToCompleteString(long time){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(time);
	}
	
	public static String timeToDate(long time){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		return sdf.format(time);
	}
	
	public static long getNow(){
		return new Date().getTime();
	}
	
	
	private static final String STR_FORMAT = "000000";
	public static String formatSN(int sn){
		DecimalFormat df = new DecimalFormat(STR_FORMAT);
	    return df.format(sn);
	}
}
