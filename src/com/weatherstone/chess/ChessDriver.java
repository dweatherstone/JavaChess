package com.weatherstone.chess;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.gui.Table;

public class ChessDriver {
	
	public static final void main(String[] args) {
		Board board = Board.createStandardBoard();
		
		System.out.println(board);
		
		Table.get().show();
	}

}
