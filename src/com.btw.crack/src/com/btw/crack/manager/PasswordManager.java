package com.btw.crack.manager;

/**
 * 密码构造器
 * @author Kinzo
 *
 */
public class PasswordManager {
	
	public final static String DICT_CHAR_NUM = "0123456789";
	public final static String DICT_CHAR_LOW = "abcdefghijklmnopqrstuvwxyz";
	public final static String DICT_CHAR_SUP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final static String DICT_CHAR_SIGN = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
	public final static String DICT_CHAR_ALL;
	static{
		StringBuilder temp = new StringBuilder();
		//char{33~126};
		for(int i = 33; i <=126; i++){
			temp.append((char)i);
		}
		DICT_CHAR_ALL = temp.toString();
	}

	private int minPasswordLength;
	private int maxPasswordLength;
	private char[] dictList;
	private int length;
	private int[] passwordChars;
	
	public PasswordManager(int minPasswordLength, int maxPasswordLength, String dict){
		this.minPasswordLength = minPasswordLength>0?minPasswordLength:1;
		this.maxPasswordLength = maxPasswordLength>minPasswordLength?maxPasswordLength:minPasswordLength;
		if(dict!=null&&dict.trim().length()>0){
			this.dictList = dict.toCharArray();
		}else{
			this.dictList = DICT_CHAR_ALL.toCharArray();
		}
		length = this.minPasswordLength;
		passwordChars = new int[length];
	}
	
	public synchronized String getPassword(){
		
		String result = getPasswordStr();
		
		if(hasNext(length-1)){
			
		}else if(length<maxPasswordLength){
			length += 1;
			passwordChars = new int[length];
		}else{
			return null;
		}
		
		return result;
	}
	
	private String getPasswordStr() {
		StringBuilder builder = new StringBuilder();
		for(int i : passwordChars){
			builder.append(dictList[i]);
		}
		return builder.toString();
	}

	private boolean hasNext(int pos) {
		if(passwordChars[pos]==dictList.length-1){
			if(pos==0){
				return false;
			}else{
				passwordChars[pos]=0;
				return hasNext(pos-1);
			}
		}else{
			passwordChars[pos]+=1;
			return true;
		}
	}
}
