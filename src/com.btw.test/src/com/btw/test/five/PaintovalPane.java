package com.btw.test.five;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class PaintovalPane extends JPanel {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		JFrame f = new PaintovalFrame();
		f.show();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.pink);
		g.fillOval(100, 100, 200, 200);

	}
}

@SuppressWarnings("serial")
class PaintovalFrame extends JFrame {
	public PaintovalFrame() {
		setTitle("lamp");
		setSize(500, 500);
		addWindowListener(new WindowAdapter() {
			@SuppressWarnings("unused")
			public void WindowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Container contentPane = getContentPane();
		contentPane.add(new PaintovalPane());
	}
}


