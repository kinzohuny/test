package com.btw.five.core;

public class ChessBoard {

	private static int size;
	private static ChessItem[][] chessBoard;

	public ChessBoard() {
		size = 9;
		initChessBoard();
	}

	public ChessBoard(int size) {
		ChessBoard.size = size;
		initChessBoard();
	}

	private void initChessBoard() {
		chessBoard = new ChessItem[size][size];
	}
	
	public ChessItem[][] getChessBoard(){
		return chessBoard;
	}
	
	public int getSize(){
		return size;
	}
	
	public ChessItem getChess(int x, int y){
		if(x<0||y<0||x>=size||y>=size){
			return null;
		}
		return chessBoard[x][y];
	}
	
	public void putChess(ChessItem chessItem){
		chessBoard[chessItem.getX()][chessItem.getY()] = chessItem;
	}
}
