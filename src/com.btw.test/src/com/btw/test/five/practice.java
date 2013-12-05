package com.btw.test.five;

import java.awt.Color;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class practice {
	public static void main(String[] args) {
		JFrame f = new JFrame("国际象棋棋盘");
		// 窗口大小
		f.setSize(260, 280);
		Point point = new Point(350, 200);
		f.setLocation(point);
		int grids = 8;
		int gridsize = 30;
		for (int i = 0; i < grids; i++) {
			for (int j = 0; j < grids; j++) {
				JLabel l = new JLabel(); // 生成标签实例
				l.setSize(gridsize, gridsize); // 设置标签大小
				l.setLocation(i * gridsize, j * gridsize); // 设置标签位置
				if ((i + j) % 2 == 0) { // 当小方格的坐标和刚好是偶数时，
					l.setBackground(Color.black); // 设置方格为黑色
					l.setOpaque(true); // 设置为不透明
				} else {
					l.setBackground(Color.white); // 设置方格为白色
					l.setOpaque(true); // 设置为不透明
				}
				l.setBorder(BorderFactory.createLineBorder(Color.black));
				// 设置边界为黑色

				f.add(l); // 添加标签
			}
		}
		f.setVisible(true);
	}
}
