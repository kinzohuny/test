package com.btw.test.five;

import java.awt.Color;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class practice {
	public static void main(String[] args) {
		JFrame f = new JFrame("������������");
		// ���ڴ�С
		f.setSize(260, 280);
		Point point = new Point(350, 200);
		f.setLocation(point);
		int grids = 8;
		int gridsize = 30;
		for (int i = 0; i < grids; i++) {
			for (int j = 0; j < grids; j++) {
				JLabel l = new JLabel(); // ���ɱ�ǩʵ��
				l.setSize(gridsize, gridsize); // ���ñ�ǩ��С
				l.setLocation(i * gridsize, j * gridsize); // ���ñ�ǩλ��
				if ((i + j) % 2 == 0) { // ��С���������͸պ���ż��ʱ��
					l.setBackground(Color.black); // ���÷���Ϊ��ɫ
					l.setOpaque(true); // ����Ϊ��͸��
				} else {
					l.setBackground(Color.white); // ���÷���Ϊ��ɫ
					l.setOpaque(true); // ����Ϊ��͸��
				}
				l.setBorder(BorderFactory.createLineBorder(Color.black));
				// ���ñ߽�Ϊ��ɫ

				f.add(l); // ��ӱ�ǩ
			}
		}
		f.setVisible(true);
	}
}
