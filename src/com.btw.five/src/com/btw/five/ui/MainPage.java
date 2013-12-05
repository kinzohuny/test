package com.btw.five.ui;

import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
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
		putChess(15, 30);
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
	
	public static void putChess(int x,int y){
		ChessItem item = new ChessItem(x, y, ChessColor.BLACK);
		Container contentPane = baseFrame.getContentPane();
		contentPane.add(new ChessItemUI());
		contentPane.show();
	}

}
