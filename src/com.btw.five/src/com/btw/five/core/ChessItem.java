package com.btw.five.core;

public class ChessItem {

	private int x;
	private int y;
	private ChessColor color;
	
	public ChessItem(int x, int y, ChessColor color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public ChessColor getColor(){
		return color;
	}
}
