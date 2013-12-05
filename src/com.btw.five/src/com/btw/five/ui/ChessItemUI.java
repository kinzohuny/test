package com.btw.five.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.btw.five.core.ChessItem;

//@SuppressWarnings("serial")
public class ChessItemUI extends JPanel {

	int x;
	int y;
	Color color;
	
	public ChessItemUI(){
		JPanel panel=new JPanel(new BorderLayout()); 
		URL url=getClass().getResource("/resource/black.gif"); 
		JLabel label=new JLabel(new ImageIcon(url)); 
		panel.add(label,BorderLayout.CENTER);
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
