package com.eci.youku.data.push.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 日期工具类
 * 
 * @Version: 1.0
 * @ProjectName:bm_airui
 * @Filename: DateUtils.java
 * @PackageName: cn.com.bmks.util
 * @Author: kevin
 * @Email: kevin@ecinsight.com.cn
 * @Date:2015年8月21日下午3:51:56
 * @Copyright (c) 2015, services@ecinsight.com.cn All Rights Reserved.
 *
 */
public class DateUtils {

    /** 定义常量 **/
    public static final String DATE_YEARMM = "yyyyMM";
    public static final String DATE_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_DAY = "yyyy-MM-dd";

    public static final Date now() {
        return new Date();
    }

    /**
     * 按pattern格式，格式化当前日期
     * 
     * @param pattern
     * @return
     */
    public static final String now(String pattern) {
        return format(now(), pattern);
    }

    /**
     * 返回yyyy-MM-dd HH:mm:ss格式当前日期
     * 
     * @param date
     * @return
     */
    public static final String format(Date date) {
        return format(date, DATE_FULL);
    }

    public static final SimpleDateFormat createSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static final String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = createSimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 按照 pattern 格式 ，将source转换成Date类型
     * 
     * @param source
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static final Date parse(String source, String pattern) throws ParseException {
        SimpleDateFormat sdf = createSimpleDateFormat(pattern);
        return sdf.parse(source);
    }

    /**
     * 默认转换成 yyyy-MM-dd HH:mm:ss 格式日期
     * 
     * @param String
     *            source 待格式化字符串
     * @return Date 类型
     * @throws ParseException
     */
    public static final Date parse(String source) throws ParseException {
        return parse(source, DATE_FULL);
    }

    /**
     * 将指定的日期转换成Unix时间戳，单位毫秒
     * 
     * @param String
     *            date 需要转换的日期 yyyy-MM-dd HH:mm:ss
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp(String date) throws ParseException {
        long timestamp = 0;
        timestamp = createSimpleDateFormat(DATE_FULL).parse(date).getTime();
        return timestamp;
    }

    /**
     * 将指定的日期转换成Unix时间戳
     * 
     * @param String
     *            date 需要转换的日期 yyyy-MM-dd
     * @return long 时间戳
     */
    public static long dateToUnixTimestamp(String date, String pattern) throws ParseException {
        long timestamp = 0;
        timestamp = createSimpleDateFormat(pattern).parse(date).getTime();
        return timestamp;
    }

    /**
     * 将指定的日期转换成Unix时间戳，单位秒
     * 
     * @param String
     *            date 需要转换的日期 yyyy-MM-dd HH:mm:ss
     * @return long 时间戳
     */
    public static long dateToUnixTimeSeconds(String date) throws ParseException {
        long timestamp = 0;
        timestamp = dateToUnixTimestamp(date) / 1000;
        return timestamp;
    }

    /**
     * 将指定的日期转换成Unix时间戳，单位秒
     * 
     * @param String
     *            date 需要转换的日期 yyyy-MM-dd HH:mm:ss
     * @return long 时间戳
     */
    public static long dateToUnixTimeSeconds(String date, String pattern) throws ParseException {
        long timestamp = 0;
        timestamp = dateToUnixTimestamp(date, pattern) / 1000;
        return timestamp;
    }

    /**
     * 按照format的格式,格式化时间
     * 
     * @param date
     * @param format
     * @return
     */
    public static String fotmatDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * 将时间转化为unix时间戳格式
     * 
     * @param date
     * @return
     */
    public static Long unixTime(Date date) {
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        return time.getTimeInMillis() / 1000;
    }

    /**
     * 获取当前时间的字符串形式(yyyyMMddHHmmss)
     * 
     * @return
     */
    public static String getNowStr() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return formatter.format(date);
    }

    /**
     * 获取当前时间的字符串形式(yyyy-MM-dd HH:mm:ss)
     * 
     * @return
     */
    public static String getNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    /**
     * 取得指定格式的当前时间
     * 
     * @param format
     * @return
     */
    public static String getFormatNow(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(new Date());
    }

    /**
     * 取得距离当前时间day天的时间的字符串形式
     * 
     * @param day
     * @return
     */
    public static String getNow(int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar right = Calendar.getInstance();
        right.add(Calendar.DAY_OF_MONTH, -day);
        Date date = right.getTime();
        return formatter.format(date);
    }

    /**
     * 取得距离当前时间day天的时间的字符串形式
     * 
     * @param day
     * @return
     */
    public static String getDayOfMinute(int min) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar right = Calendar.getInstance();
        // right.add(Calendar.MINUTE, -min);
        right.add(Calendar.SECOND, -(min * 60));
        Date date = right.getTime();
        return formatter.format(date);
    }

    public static String getLastWeek(int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar right = Calendar.getInstance();
        right.add(Calendar.DAY_OF_MONTH, -day);
        Date date = right.getTime();
        return formatter.format(date);
    }

    public static String getMonth(int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar right = Calendar.getInstance();
        right.add(Calendar.MONTH, day);
        Date date = right.getTime();
        return formatter.format(date);
    }

    public static String addDay(Date date, int d) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, d);
        return formatter.format(cal.getTime());
    }

    public static Date addTime(Date date, int d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, d);
        return cal.getTime();
    }

    /**
     * 根据strdate生成时间(strdate为时间格式字符串 yyyy-MM-dd HH:mm:ss)
     * 
     * @param strdate
     * @return
     */
    public static Date str2date(String strdate, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = df.parse(strdate);
        } catch (ParseException e) {
        }
        return date;
    }

    public static Timestamp str2Timestamp(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return new Timestamp(df.parse(time).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date str2date(String strdate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(strdate);
        } catch (ParseException e) {
        }
        return date;
    }

    public static boolean isDate(String strDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        try {
            df.parse(strDate);
        } catch (ParseException e) {
            return false;
        }
        return true;

    }

    public static boolean isDate(String strDate, String format) {
        DateFormat df = new SimpleDateFormat(format);
        df.setLenient(false);
        try {
            df.parse(strDate);
        } catch (ParseException e) {
            return false;
        }
        return true;

    }

    public static Date str2dateOfMD(String strdate) {
        DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
        Date date = null;
        try {
            date = df.parse(strdate);
        } catch (ParseException e) {
        }
        return date;
    }

    /**
     * 获取时间差
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        String result = "";
        Date datenow = new Date();
        long t = (datenow.getTime() - date.getTime()) / 1000;
        long d = t / (24 * 3600);
        long h = t % (24 * 3600) / 3600;
        long s = t % 3600;
        long m = t % 3600 / 60;
        if (s > 0 && m == 0) {
            result = s + "秒前";
        } else if (m > 0)
            result = m + "分钟前";
        if (m >= 30)
            result = "半小时前";
        if (h > 0)
            result = h + "小时前";
        if (d > 0)
            result = d + "天前";
        if (d > 3)
            result = fotmatDate(date, "yy-MM-dd");
        return result;
    }

    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     * 
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }

    public static void main(String[] args) {
        Date sDate = DateUtils.str2date("2015-10-01", DateUtils.DATE_DAY);
        Date eDate = DateUtils.str2date("2015-10-15", DateUtils.DATE_DAY);

        List<Date> dates = DateUtils.getDatesBetweenTwoDate(sDate, eDate);

        Map<String, Object> rsMap = new HashMap<String, Object>();
        if (dates != null && dates.size() > 0) {
            int arrSize = dates.size();
            Object[] refDate = new Object[arrSize];
            Object[] newUser = new Object[arrSize];
            Object[] cancelUser = new Object[arrSize];
            Object[] netGrowthUser = new Object[arrSize];
            Object[] cumulateUser = new Object[arrSize];
            for (int i = 0; i < arrSize; i++) {
                Date dt = dates.get(i);
                refDate[i] = DateUtils.format(dt, DateUtils.DATE_DAY);
                newUser[i] = 0;
                cancelUser[i] = 0;
                netGrowthUser[i] = 0;
                cumulateUser[i] = 0;
            }
            rsMap.put("refDates", refDate);
            rsMap.put("newUser", newUser);
            rsMap.put("cancelUser", cancelUser);
            rsMap.put("netGrowthUser", netGrowthUser);
            rsMap.put("cumulateUser", cumulateUser);
        }
        System.out.println(JSON.toJSONString(rsMap));
    }
}
