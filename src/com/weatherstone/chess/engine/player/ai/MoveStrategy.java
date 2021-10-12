package com.weatherstone.chess.engine.player.ai;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Move;

public interface MoveStrategy {
	
	public Move execute(Board board);

}
