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
//				JOptionPane.showMessageDialog(null, "��ʲô�㣡");
//				JOptionPane.showMessageDialog(null, "��������ǲ���", "��� ��", 3);
				JOptionPane.showConfirmDialog(null, "��ʲô�㣡");
				
			}
		});
		
		j.setSize(800, 600);
		j.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				System.out.println("�ſ��ˣ�"+arg0.getX()+","+arg0.getY()+"�����꣡");
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				System.out.println("�����ˣ�"+arg0.getX()+","+arg0.getY()+"�����꣡");
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				System.out.println("��ȥ�ˣ�"+arg0.getX()+","+arg0.getY()+"�����꣡");
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				System.out.println("�����ˣ�"+arg0.getX()+","+arg0.getY()+"�����꣡");
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("����ˣ�"+arg0.getX()+","+arg0.getY()+"�����꣡");
				
			}
		});
		
	
	}

	private static void init() {
		j = new JFrame();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setLayout(null);
		btn_1 = new JButton("���ǰ�ť");
//		btn_1.setLocation(10, 30);
		j.add(btn_1);
		btn_1.setBounds(20, 30, 100, 30);
		
		
		
	}

}
