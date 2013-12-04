package com.jiuqi.hjz.transform;

public class ToChineseMoney {
	
	public static void main(String[] args) {
		String s = "34567890.1234";
		ToChineseMoney toChineseStr = new ToChineseMoney(s);
		
		System.out.println(s+"��"+toChineseStr.getChineseMoney());
	}
	
	String money;
	
	public ToChineseMoney(String money) {
		try {
			Double.valueOf(money);
			this.money = money;
		} catch (Exception e) {
			System.out.println("�������"+money+"����ת��������");
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
			return "��";
		}else{
			return toChineseChar(back[0])+"��"+toChineseChar(back[1])+"��";
		}

	}
	
	private String getUnit(int i){
		switch (i) {
		case 0:
			return "Ԫ";
		case 1:
			return "ʰ";
		case 2:
			return "��";
		case 3:
			return "Ǫ";
		case 4:
			return "��";
		case 5:
			return "ʰ";
		case 6:
			return "��";
		case 7:
			return "Ǫ";
		case 8:
			return "��";
		case 9:
			return "ʰ";
		case 10:
			return "��";
		case 11:
			return "Ǫ";
		case 12:
			return "Ǫ";
		}
		return "������";
	}
	
	private String toChineseChar(char c){
		switch (c) {
		case '0':
			return "��";
		case '1':
			return "Ҽ";
		case '2':
			return "��";
		case '3':
			return "��";
		case '4':
			return "��";
		case '5':
			return "��";
		case '6':
			return "½";
		case '7':
			return "��";
		case '8':
			return "��";
		case '9':
			return "��";
		default:
			return null;
		}
	}
}
