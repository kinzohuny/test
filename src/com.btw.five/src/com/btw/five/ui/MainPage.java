package com.btw.five.ui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.btw.five.core.ChessColor;
import com.btw.five.core.ChessItem;

public class MainPage {
	
	static JFrame baseFrame;
	static JMenuBar menuBar;
	static JButton btn_start;
	static JButton btn_end;
	static JButton btn_setup;
	static int size = 19;

	public static void main(String[] args) {
		initWindow();
		initListener();
		putChess(new ChessItem(1, 3, ChessColor.BLACK));
		putChess(new ChessItem(10, 1, ChessColor.WHITE));
	}

	private static void initListener() {
		// TODO Auto-generated method stub
		
	}

	private static void initWindow() {
		//窗口
		baseFrame = new JFrame();
		baseFrame.setSize(size*30+45, size*30+40+60);
		baseFrame.setLayout(null);
		baseFrame.setResizable(false);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		baseFrame.setVisible(true);
		//菜单
		menuBar = new JMenuBar();
		baseFrame.add(menuBar);
		menuBar.setSize(baseFrame.getWidth(), 30);
		btn_start = new JButton("开始游戏");
		btn_end = new JButton("结束游戏");
		btn_setup = new JButton("游戏设置");
		menuBar.add(btn_start);
		menuBar.add(btn_end);
		menuBar.add(btn_setup);
		
	
		//棋盘
		JTable table = new JTable(size, size);
		baseFrame.add(table);
		table.setShowGrid(true);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setLocation(20, 50);
		table.setRowHeight(30);
		table.setSize(size*30, size*30);
		table.setEnabled(false);

		JScrollPane sPane = new JScrollPane(table);
		baseFrame.add(table);
		sPane.setVisible(true);
	}
	
	public static void putChess(ChessItem item){
		ImageIcon black = new ImageIcon("resource/black.gif");
		ImageIcon white = new ImageIcon("resource/white.gif");
		
        JPanel panel=new JPanel(new BorderLayout()); 
        JLabel label = null;
        if(item.getColor().equals(ChessColor.BLACK)){
        	label=new JLabel(black); 
        }else{
        	label=new JLabel(white); 
        }
        panel.add(label,BorderLayout.CENTER); 
        panel.setSize(20, 20);
        panel.setLocation(item.getY()*30+10, item.getX()*30+40);
        panel.setVisible(true);
        baseFrame.add(panel);
        baseFrame.setVisible(true);  
	}

}
