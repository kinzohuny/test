package com.btw.test.five;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JFrameTest {

	public static void main(String[] args) {
		
		init();
		initListener();
		show();
		
	}
	
	static JFrame j;
	static JButton btn_1;
	private static void show() {
		j.setVisible(true);
		
	}

	private static void initListener() {
		
		btn_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				JOptionPane.showMessageDialog(null, "点什么点！");
//				JOptionPane.showMessageDialog(null, "不想混了是不？", "想混 不", 3);
				JOptionPane.showConfirmDialog(null, "点什么点！");
				
			}
		});
		
		j.setSize(800, 600);
		j.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				System.out.println("放开了（"+arg0.getX()+","+arg0.getY()+"）坐标！");
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				System.out.println("按下了（"+arg0.getX()+","+arg0.getY()+"）坐标！");
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				System.out.println("出去了（"+arg0.getX()+","+arg0.getY()+"）坐标！");
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				System.out.println("进来了（"+arg0.getX()+","+arg0.getY()+"）坐标！");
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("点击了（"+arg0.getX()+","+arg0.getY()+"）坐标！");
				
			}
		});
		
	
	}

	private static void init() {
		j = new JFrame();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setLayout(null);
		btn_1 = new JButton("我是按钮");
//		btn_1.setLocation(10, 30);
		j.add(btn_1);
		btn_1.setBounds(20, 30, 100, 30);
		
		
		
	}

}
