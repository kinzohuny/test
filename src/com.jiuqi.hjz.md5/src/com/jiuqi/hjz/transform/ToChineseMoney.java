package com.jiuqi.hjz.transform;

public class ToChineseMoney {
	
	public static void main(String[] args) {
		String s = "34567890.1234";
		ToChineseMoney toChineseStr = new ToChineseMoney(s);
		
		System.out.println(s+"£º"+toChineseStr.getChineseMoney());
	}
	
	String money;
	
	public ToChineseMoney(String money) {
		try {
			Double.valueOf(money);
			this.money = money;
		} catch (Exception e) {
			System.out.println("ÊäÈë´íÎó£¡"+money+"²»ÄÜ×ª»»³ÉÊı×Ö");
		}
	}
	
	public String getChineseMoney(){
		
		return getFront()+getBack();
	}
	
	private String getFront(){
		String frontStr = "";
		char[] front = money.split("\\.")[0].toCharArray();
		for(int i = 0;front.length-i>0;i++){
			frontStr =  toChineseChar(front[front.length-1-i])+getUnit(i)+frontStr ;
		}
		return frontStr;
	}
	
	private String getBack(){
		char[] back = money.split("\\.")[1].toCharArray();
		if(back == null||back.length==0||back.length>=2&&back[0]=='0'&&back[1]==00){
			return "Õû";
		}else{
			return toChineseChar(back[0])+"½Ç"+toChineseChar(back[1])+"·Ö";
		}

	}
	
	private String getUnit(int i){
		switch (i) {
		case 0:
			return "Ôª";
		case 1:
			return "Ê°";
		case 2:
			return "°Û";
		case 3:
			return "Çª";
		case 4:
			return "Íò";
		case 5:
			return "Ê°";
		case 6:
			return "°Û";
		case 7:
			return "Çª";
		case 8:
			return "ÒÚ";
		case 9:
			return "Ê°";
		case 10:
			return "°Û";
		case 11:
			return "Çª";
		case 12:
			return "Çª";
		}
		return "³ö´íÁË";
	}
	
	private String toChineseChar(char c){
		switch (c) {
		case '0':
			return "Áã";
		case '1':
			return "Ò¼";
		case '2':
			return "·¡";
		case '3':
			return "Èş";
		case '4':
			return "ËÁ";
		case '5':
			return "Îé";
		case '6':
			return "Â½";
		case '7':
			return "Æâ";
		case '8':
			return "°Æ";
		case '9':
			return "¾Á";
		default:
			return null;
		}
	}
}
