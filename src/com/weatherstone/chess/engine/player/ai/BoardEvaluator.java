package com.weatherstone.chess.engine.player.ai;

import com.weatherstone.chess.engine.board.Board;

public interface BoardEvaluator {
	
	public int evaluate(Board board, int depth);

}
