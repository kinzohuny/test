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
		int i=0,j=0;
		for(;i<5;i++){
			ChessItem ci = getU(chessItem, chessBoard);
			if(ci==null||(!chessItem.getColor().equals(ci.getColor()))){
				break;
			}
		}
		for(;j<5;j++){
			ChessItem ci = getD(chessItem, chessBoard);
			if(ci==null||(!chessItem.getColor().equals(ci.getColor()))){
				break;
			}
		}
		return i+j;
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
	
	public static ChessItem getU(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX(), chessItem.getY()-1);
	}
	
	public static ChessItem getLU(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()-1, chessItem.getY()-1);
	}
	
	public static ChessItem getL(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()-1, chessItem.getY());
	}
	
	public static ChessItem getLD(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()-1, chessItem.getY()+1);
	}
	
	public static ChessItem getD(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX(), chessItem.getY()+1);
	}
	
	public static ChessItem getRD(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()+1, chessItem.getY()+1);
	}
	
	public static ChessItem getR(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()+1, chessItem.getY());
	}
	
	public static ChessItem getRU(ChessItem chessItem, ChessBoard chessBoard){
		return chessBoard.getChess(chessItem.getX()+1, chessItem.getY()-1);
	}
}