package com.btw.server.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;

public class StringUtils {

	public static String md5(String source) {

		StringBuffer sb = new StringBuffer(32);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(source.getBytes("utf-8"));
			
			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
		} catch (Exception e) {
			// logger.error(“Can not encode the string ‘” + source + “‘ to MD5!”, e);
			return null;
		}

		return sb.toString();
	}
	
	public static String toJSON(Object obj){
		return JSON.toJSONString(obj);
	}
	
	public static <T> T fromJSON(Class<T> clazz, String jsonObject){
		return JSON.parseObject(jsonObject, clazz);
	}
	
	public static String arrayToSting(Object[] objectArray){
		if(objectArray==null){
			return null;
		}
		if(objectArray.length==0){
			return "[]";
		}
		StringBuffer buffer = new StringBuffer("[");
		for(Object object : objectArray){
			buffer.append(object==null?"":String.valueOf(object)).append(",");
		}
		buffer.setLength(buffer.length()-1);
		buffer.append("]");
		return buffer.toString();
	}
	
	public static String toString(Object obj){
		if(obj==null){
			return null;
		}else{
			try {
				return String.valueOf(obj);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * if can not convert, return null;
	 * @param str
	 * @return
	 */
	public static Long toLong(String str){
		if(isEmpty(str)){
			return null;
		}else{
			try {
				return Long.valueOf(str);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * if can not convert, return null;
	 * @param str
	 * @return
	 */
	public static Integer toInteger(String str){
		if(isEmpty(str)){
			return null;
		}else{
			try {
				return Integer.valueOf(str);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * if can not convert, return 0;
	 * @param str
	 * @return
	 */
	public static int toInt(String str){
		Integer i = toInteger(str);
		return i==null?0:i;
	}
	
	public static BigDecimal toBigDecimal(String str){
		if(isEmpty(str)){
			return null;
		}else{
			try {
				return new BigDecimal(str);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	public static String formatDouble2(double d){
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		return decimalFormat.format(d);
	}

    private static final String SPACE = "[ ]";

    /**
     * 字符串替换(src=源,oldstr=要替换的内容,newstr为要替换为的内容)
     * 
     * @param src
     * @param oldstr
     * @param newstr
     * @return
     */
    public static String replace(String src, String oldstr, String newstr) {
        StringBuffer dest = new StringBuffer();
        int beginIndex = 0;
        int endIndex = 0;
        while (true) {
            endIndex = src.indexOf(oldstr, beginIndex);
            if (endIndex >= 0) {
                dest.append(src.substring(beginIndex, endIndex));
                dest.append(newstr);
                beginIndex = endIndex + oldstr.length();
            } else {
                dest.append(src.substring(beginIndex));
                break;
            }
        }
        return dest.toString();
    }

    public static boolean isNumberMatches(String str) {
        return str.matches("[0-9]+");
    }

    public static boolean isVarcharMatches(String str) {
        return str.matches("[a-zA-Z]+");
    }

    /**
     * 判断在source中是否含有pattern
     * 
     * @param source
     * @param pattern
     * @return boolean
     */
    public static boolean containsDate(String source, String pattern) {
        if (source == null || pattern == null) {
            return false;
        }
        source = source.trim();
        pattern = pattern.trim();
        if (source.indexOf(pattern) == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证二位浮点
     * 
     * @param number
     * @return
     */
    public static boolean isFloat(String number) {
        String eL = "^(?!0(\\d|\\.0+$|$))\\d+(\\.\\d{0,9})?$";// 浮点数
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(number);
        boolean b = m.matches();
        return b;
    }

    /**
     * 判断是否为数字
     * 
     * @param number
     *            java.lang.String
     * @return java.lang.boolean
     */
    public static boolean isNumber(String number) {
        if (isEmpty(number) == true)
            return false;
        // if (number.length() > 9)
        // return false;
        byte[] tempbyte = number.getBytes();
        for (int i = 0; i < number.length(); i++) {
            if ((tempbyte[i] < 48) || (tempbyte[i] > 57)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为字符
     * 
     * @param string
     *            java.lang.String
     * @return java.lang.boolean
     */
    public static boolean isChar(String string) {
        if (isEmpty(string) == true)
            return false;
        byte[] tempbyte = string.getBytes();
        for (int i = 0; i < string.length(); i++) {
            if ((tempbyte[i] < 48) || ((tempbyte[i] > 57) & (tempbyte[i] < 65)) || (tempbyte[i] > 122)
                    || ((tempbyte[i] > 90) & (tempbyte[i] < 97))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLetter(String string) {
        if (isEmpty(string) == true)
            return false;
        byte[] tempbyte = string.getBytes();
        for (int i = 0; i < string.length(); i++) {
            if ((tempbyte[i] < 65) || (tempbyte[i] > 122) || ((tempbyte[i] > 90) & (tempbyte[i] < 97))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 正则表达式匹配
     * 
     * @param string
     *            java.lang.String
     * @param pattern
     *            java.lang.String
     * @return java.lang.boolean
     */
    public static boolean regex(String string, String pattern) {
        Pattern p = null;
        Matcher m = null;
        try {
            p = java.util.regex.Pattern.compile(pattern);
            m = p.matcher(string);
            if (m.find())
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断是否为空
     * 
     * @param string
     *            java.lang.String
     * @return java.lang.boolean
     */
    public static boolean isEmpty(String string) {
        return (string == null || string.equals("") == true) ? true : false;
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    /**
     * 字符比较
     * 
     * @param string1
     *            java.lang.String
     * @param string2
     *            java.lang.String
     * @return java.lang.boolean
     */
    public static boolean isSame(String string1, String string2) {
        return (string1 != null && string1.equalsIgnoreCase(string2) == true) ? true : false;
    }

    /**
     * 全角字符转半角
     * 
     * @param str
     * @return
     */
    public static String converFullStr(String str) {
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 提取用逗号,空格,中文顿号分隔的字符
     * 
     * @param str
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List segmentationStr(String str) {
        str = converFullStr(str);
        List result = new ArrayList();
        str = str.replaceAll(" ", ",");
        str = str.replaceAll("、", ",");
        StringTokenizer strToken = new StringTokenizer(str, ",");
        while (strToken.hasMoreElements()) {
            result.add(strToken.nextToken());
        }
        return result;
    }

    /**
     * 用指定分割符分割字符串
     * 
     * @param str
     *            待分割的字符串
     * @param separator
     *            分割符
     * @return List
     */
    public static List<Object> segmentationStr(String str, String separator) {
        List<Object> result = new ArrayList<Object>();
        StringTokenizer token = new StringTokenizer(str, separator);
        while (token.hasMoreElements()) {
            result.add(token.nextToken());
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
	public static String mapToString(Map map) {
        StringBuffer re = new StringBuffer();
        java.util.Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            // entry.getKey() 返回与此项对应的键
            // entry.getValue() 返回与此项对应的值
            if (null != entry && null != entry.getKey() && !isEmpty(entry.getKey().toString())) {
                re.append(strfilter(entry.getKey().toString(), SPACE));
            }
            if (null != entry && null != entry.getValue() && !isEmpty(entry.getValue().toString())) {
                re.append("=").append(strfilter(entry.getValue().toString(), SPACE));
            }
            re.append(";");
        }
        return re.toString();
    }

    /**
     * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
     * 
     * @param c
     *            需要判断的字符
     * @return boolean, 返回true,Ascill字符
     */
    public static boolean isLetter(char c) {
        int k = 0x80;
        return c / k == 0 ? true : false;
    }

    /**
     * 判断字符串中是否包含中文字符
     * 
     * @param str
     *            待判断的字符串
     * @return boolean
     */
    public static boolean contentChinese(String str) {
        boolean res = false;
        if (str == null || str == "")
            return false;
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (!isLetter(c[i])) {
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
     * 
     * @param str
     *            需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int length(String str) {
        if (str == null)
            return 0;
        char[] c = str.toCharArray();
        int len = 0;
        for (int i = 0; i < c.length; i++) {
            len++;
            if (!isLetter(c[i]))
                len++;
        }
        return len;
    }

    /**
     * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位
     * 
     * @param origin
     *            原始字符串
     * @param len
     *            截取长度(一个汉字长度按2算的)
     * @return String 返回的字符串
     */
    public static String substring(String origin, int len) {
        if (origin == null || origin.equals("") || len < 1)
            return "";
        byte[] strByte = new byte[len];
        if (len > length(origin)) {
            return origin;
        }
        System.arraycopy(origin.getBytes(), 0, strByte, 0, len);
        int count = 0;
        for (int i = 0; i < len; i++) {
            int value = (int) strByte[i];
            if (value < 0) {
                count++;
            }
        }
        if (count % 2 != 0) {
            len = (len == 1) ? ++len : --len;
        }
        return new String(strByte, 0, len);
    }

    public static int getRandom(int from, int to) {
        return (int) (Math.random() * to) + from;
    }

    /**
     * 数值是否有效，当iVal!=null && iVal>0返回true
     * 
     * @param iVal
     * @return
     */
    public static boolean isValidInteger(Integer iVal) {
        if (iVal != null && iVal > 0)
            return true;
        return false;
    }

    /**
     * 数值是否无数效，当iVal==null || iVal<=0返回true
     * 
     * @param iVal
     * @return
     */
    public static boolean isNotValidInteger(Integer iVal) {
        return !isValidInteger(iVal);
    }

    public static String strfilter(String args, String regex) {
        // String regEx =
        // "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？『』]";
        if (isEmpty(regex)) {
            regex = "[`@#$%^&*()=|{}\\[\\]<>/?~@#￥%&*（）——|{}【】‘；”“’？『』]";
        }
        if (isEmpty(args)) {
            args = "";
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(args);
        return m.replaceAll("").trim();
    }

    public static <K, V extends Number> Map<String, V> sortMap(Map<String, V> map) {
        class TempMap<M, N> {
            private M key;
            private N value;

            private M getKey() {
                return key;
            }

            private void setKey(M key) {
                this.key = key;
            }

            private N getValue() {
                return value;
            }

            private void setValue(N value) {
                this.value = value;
            }
        }

        List<TempMap<String, V>> list = new ArrayList<TempMap<String, V>>();
        for (Iterator<String> i = map.keySet().iterator(); i.hasNext();) {
            TempMap<String, V> temp = new TempMap<String, V>();
            String key = i.next();
            temp.setKey(key);
            temp.setValue(map.get(key));
            list.add(temp);
        }

        Collections.sort(list, new Comparator<TempMap<String, V>>() {
            public int compare(TempMap<String, V> o1, TempMap<String, V> o2) {
                if (o1.getValue() == o2.getValue()) {
                    return o2.getKey().compareTo(o1.getKey());
                } else {
                    return (int) (o2.getValue().doubleValue() - o1.getValue().doubleValue());
                }
            }
        });

        Map<String, V> sortMap = new LinkedHashMap<String, V>();
        for (int i = 0, k = list.size(); i < k; i++) {
            TempMap<String, V> rt = list.get(i);
            sortMap.put(rt.getKey(), rt.getValue());
        }
        return sortMap;
    }

    public void print(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    /**
     * 数组排序 考虑到排序的效率的问题的方法
     * 
     * @param arr
     */
    public static void arraySort(int[] arr) {
        int k, temp;
        for (int i = 0; i < arr.length; i++) {
            k = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[k]) {
                    k = j;
                }
            }
            if (k != i) {
                temp = arr[i];
                arr[i] = arr[k];
                arr[k] = temp;
            }
        }
    }

    /**
     * 验证数组元素是否相等
     * 
     * @param i1
     * @param i2
     * @return
     */
    public static Boolean arrayEquals(int[] i1, int[] i2) {
        Boolean bResult = false;
        if (i1.length != i2.length) {
            return bResult;
        } else {
            arraySort(i1);
            arraySort(i2);
            Boolean bIsSame = true;
            for (int i = 0; i < i1.length; i++) {
                if (i1[i] != i2[i]) {
                    bIsSame = false;
                    break;
                }
            }
            bResult = bIsSame;
        }
        return bResult;
    }
	
}
