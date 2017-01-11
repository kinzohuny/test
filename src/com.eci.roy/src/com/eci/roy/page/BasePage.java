package com.eci.roy.page;

public abstract class BasePage {

	private StringBuffer PAGE = new StringBuffer();
	private StringBuffer BODY = new StringBuffer();
	
	public BasePage(String title){
		init(title);
	}
	
	public void append(String body){
		BODY.append(body);
	}
	
	public String show(){
		PAGE.append("<body>");
		PAGE.append(BODY);
		PAGE.append("</body>");
		return PAGE.toString();
	}

	private void init(String title) {
		PAGE.append("<head>");
		PAGE.append("<title>"+title+"</title>");
		PAGE.append("<link href=\"/resource/main.css\" rel=\"stylesheet\" type=\"text/css\"></link>");
		PAGE.append("<script src=\"/resource/main.js\" type=\"text/javascript\"></script>");
		PAGE.append("<script src=\"/resource/jquery-1.11.1.js\" type=\"text/javascript\"></script>");
		PAGE.append("</head>");
	}
}
