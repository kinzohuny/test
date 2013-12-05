package com.btw.five.ui;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainPage {
	
	static JFrame baseFrame;
	static JMenuBar menuBar;
	static JMenuItem mi_start;

	public static void main(String[] args) {
		initWindow();
		initListener();
	}

	private static void initListener() {
		// TODO Auto-generated method stub
		
	}

	private static void initWindow() {
		initFrame();
		initChessBoard();
		baseFrame.setVisible(true);
	}

	private static void initFrame() {
		baseFrame = new JFrame();
		baseFrame.setSize(800, 800);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		menuBar = new JMenuBar();
		JMenuItem mi_start = new JMenuItem();
		mi_start.setText("¿ªÊ¼ÓÎÏ·");
		menuBar.add(mi_start);
		baseFrame.add(menuBar);

		GridBagLayout gridLayout = new GridBagLayout();
	}

	private static void initChessBoard() {
		// TODO Auto-generated method stub
		
	}


}
