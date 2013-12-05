package com.btw.five.core;

public class ChessRule{
	
	private static ChessColor winner = null;
	
	public static boolean isOver(ChessItem chessItem, ChessBoard chessBoard){
		if(isFull(chessBoard)){
			return true;
		}
		return false;
	}
	
	public static int getUDNum(ChessItem chessItem, ChessBoard chessBoard){
		return 0;
	}
	
	public static int getLRNum(ChessItem chessItem, ChessBoard chessBoard){
		return 0;
	}
	
	public static int getLURDNum(ChessItem chessItem, ChessBoard chessBoard){
		return 0;
	}
	
	public static int getRULDNum(ChessItem chessItem, ChessBoard chessBoard){
		return 0;
	}
	
	private static boolean isFull(ChessBoard chessBoard){
		for(ChessItem[] row : chessBoard.getChessBoard()){
			for(ChessItem item : row){
				if(item == null){
					return true;
				}
			}
		}
		return false;
	}
	
	public static ChessColor getWinner(){
		return winner;
	}
	
	public ChessItem getU(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX(), chessItem.getY()-1);
	}
	
	public ChessItem getLU(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()-1, chessItem.getY()-1);
	}
	
	public ChessItem getL(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()-1, chessItem.getY());
	}
	
	public ChessItem getLD(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()-1, chessItem.getY()+1);
	}
	
	public ChessItem getD(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX(), chessItem.getY()+1);
	}
	
	public ChessItem getRD(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()+1, chessItem.getY()+1);
	}
	
	public ChessItem getR(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()+1, chessItem.getY());
	}
	
	public ChessItem getRU(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()+1, chessItem.getY()-1);
	}
}