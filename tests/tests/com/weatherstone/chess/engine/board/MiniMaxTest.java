package tests.com.weatherstone.chess.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.player.ai.MiniMax;
import com.weatherstone.chess.engine.player.ai.MoveStrategy;

class MiniMaxTest {

	@Test
	void testOpeningDepth1() {
		final Board board = Board.createStandardBoard();
		final MoveStrategy minMax = new MiniMax(1);
		minMax.execute(board);
		
	}

}
