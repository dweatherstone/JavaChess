package com.weatherstone.chess.pgn;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.player.Player;

public interface PGNPersistence {
	
	void persistGame(Game game);
	
	Move getNextBestMove(Board board, Player player, String gameText);

}
