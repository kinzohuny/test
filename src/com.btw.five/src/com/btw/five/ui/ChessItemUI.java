package com.btw.five.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.btw.five.core.ChessItem;

//@SuppressWarnings("serial")
public class ChessItemUI extends JPanel {

	int x;
	int y;
	Color color;
	
	public ChessItemUI(){
		this.x = 20;
		this.y = 20;
		this.color = Color.black;
	}
	
	public ChessItemUI(ChessItem item){
		this.x = item.getX();
		this.y = item.getY();
		this.color = Color.black;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.pink);
		g.fillOval(x, y, 200, 200);

	}
}
