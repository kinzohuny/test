package com.btw.five.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.btw.five.core.ChessBoard;
import com.btw.five.core.ChessColor;
import com.btw.five.core.ChessItem;

public class MainPage {
	
	static JFrame baseFrame;
	static JMenuBar menuBar;
	static JButton btn_start;
	static JButton btn_end;
	static JButton btn_setup;
	static JScrollPane sPane;
	static JTable table;
	static JLabel lbl_state;
	static int size = 19;
	static boolean isBlackTurn = true;
	static ChessItem item;
	static ChessBoard chessBoard;

	public static void main(String[] args) {
		initWindow();
		initListener();
		initChessBoard();
	}

	private static void initChessBoard() {
		chessBoard = new ChessBoard(size);
		
	}

	private static void initListener() {
		table.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int xx = arg0.getX();
				int yy = arg0.getY();
				if ((xx % 30 < 10 || xx % 30 > 20) && yy % 30 < 10
						|| yy % 30 > 20) {
					int x = xx / 30 + ((xx % 30) > 15 ? 1 : 0);
					int y = yy / 30 + ((yy % 30) > 15 ? 1 : 0);
					item = new ChessItem(x, y, isBlackTurn?ChessColor.BLACK:ChessColor.WHITE);
					if(chessBoard.getChess(x, y)==null){
						putChess(item);
						lbl_state.setText("��һ����"+(isBlackTurn?"����":"����")+"("+y+","+x+")����ǰ��"+((isBlackTurn = !isBlackTurn)?"����":"����"));
					}
				}
			}
		});
		
	}

	private static void initWindow() {
		//����
		baseFrame = new JFrame();
		baseFrame.setSize(size*30+45, size*30+40+60+45);
		baseFrame.setLayout(null);
		baseFrame.setResizable(false);
		baseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		baseFrame.setVisible(true);
		//�˵�
		menuBar = new JMenuBar();
		baseFrame.add(menuBar);
		menuBar.setSize(baseFrame.getWidth(), 30);
		btn_start = new JButton("��ʼ��Ϸ");
		btn_end = new JButton("������Ϸ");
		btn_setup = new JButton("��Ϸ����");
		menuBar.add(btn_start);
		menuBar.add(btn_end);
		menuBar.add(btn_setup);
		//����
		table = new JTable(size, size);
		baseFrame.add(table);
		table.setShowGrid(true);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setLocation(20, 50);
		table.setRowHeight(30);
		table.setSize(size*30, size*30);
		table.setEnabled(false);
		sPane = new JScrollPane(table);
		baseFrame.add(table);
		sPane.setVisible(true);
		//״̬��
		lbl_state = new JLabel();
		baseFrame.add(lbl_state);
		lbl_state.setLocation(20, 50+size*30+15);
		lbl_state.setSize(size*30, 30);;
		lbl_state.setText("��Ϸδ��ʼ��");
		lbl_state.setVisible(true);
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
        panel.setLocation(item.getX()*30+10, item.getY()*30+40);
        panel.setVisible(true);
        baseFrame.add(panel);
        baseFrame.setVisible(true);  
        chessBoard.putChess(item);
	}

}
