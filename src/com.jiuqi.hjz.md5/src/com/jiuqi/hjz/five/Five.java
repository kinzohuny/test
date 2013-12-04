package com.jiuqi.hjz.five;

import java.util.Arrays;
import java.util.Scanner;

public class Five {

	public static final int chessboardNum = 12;
	public static final String black = "●";
	public static final String white = "○";
	public static final String[] tab = { "┼", "┌", "┐", "└", "┘", "┬", "├",
			"┤", "┴" };
	public static final String tabStr = "┌┬┐├┼┤└┴┘";
	public static String[][] chessboard;

	public static void main(String[] args) {
		initChessboard();
		showChessboard();
		start();
	}

	private static void start() {
		System.out.println("黑方先手？(y/n)");
		Scanner scan = new Scanner(System.in);
		String input = scan.next();
		if ("y".equals(input) || "Y".equals(input) || "yes".equals(input)
				|| "YES".equals(input)) {
			play(true);
		} else if ("n".equals(input) || "N".equals(input) || "no".equals(input)
				|| "NO".equals(input)) {
			play(false);
		} else {
			System.out.println("输入错误！");
			start();
		}

	}

	private static void play(boolean blackFirst) {
		if (!(chess(blackFirst ? black : white)||check())) {
			play(!blackFirst);
		}
	}

	private static boolean check() {
		for (int i = 0; i < chessboardNum; i++) {
			for (int j = 0; j < chessboardNum; j++) {
				if (tabStr.indexOf(chessboard[i][j]) > -1) {
					return false;
				}
			}
		}
		System.out.println("棋盘已满，游戏结束！");
		return true;
	}

	private static boolean chess(String string) {
		boolean isError = false;
		String errorMsg = "";
		int x = 0, y = 0;
		System.out.println(string + ",请输入落子位置：（例如： Ff）");
		Scanner scan = new Scanner(System.in);
		char[] site = scan.next().toCharArray();
		try {
			x = (site[0]-64>0&&site[0]-64<=26)?site[0]-64:site[0]-96;
			y = (site[1]-64>0&&site[1]-64<=26)?site[1]-64:site[1]-96;
		} catch (Exception e) {
			isError = true;
			errorMsg = e.getMessage();
		}
		if(!isError&&(x < 1 || y < 1 || x > chessboardNum || y > chessboardNum)){
			isError = true;
			errorMsg = "落子位置错误,没有该位置";
		}
		if(!isError&&tabStr.indexOf(chessboard[x][y])<0){
			isError = true;
			errorMsg = "该位置已经有子！";
		}
		
		if (!isError) {
			chessboard[x][y] = string;
			showChessboard();
			return isWin(x,y,string);
		} else {
			System.out.println(errorMsg);
			return chess(string);
		}
	}

	private static boolean isWin(int x, int y, String string) {
		//左斜
//		if(1==1){
//		
//		//右斜
//		}else if(1==1){
//			
//		//水平
//		}else if(1==1){
//		
//		//垂直
//		}else if(1==1){
//			
//		}
		return false;
	}

	private static void showChessboard() {
		for (String[] row : chessboard) {
			System.out.println();
			for (String col : row) {
				System.out.print(col);
			}
		}
		System.out.println();
	}

	private static void initChessboard() {
		chessboard = new String[chessboardNum+1][chessboardNum+1];
		for (String[] row : chessboard) {
			Arrays.fill(row, tab[0]);
		}
		chessboard[1][1] = tab[1];
		chessboard[1][chessboardNum] = tab[2];
		chessboard[chessboardNum][1] = tab[3];
		chessboard[chessboardNum][chessboardNum] = tab[4];
		for (int i = 2; i < chessboardNum; i++) {
			chessboard[1][i] = tab[5];
			chessboard[i][1] = tab[6];
			chessboard[i][chessboardNum] = tab[7];
			chessboard[chessboardNum][i] = tab[8];
		}
		for(int i =1;i<chessboardNum+1;i++){
			chessboard[0][i] = ""+(char)('a'-1+i);
			chessboard[i][0] = ""+(char)('A'-1+i);
		}

	}
}
