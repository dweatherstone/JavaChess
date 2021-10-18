package tests.com.weatherstone.chess.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.pgn.FenUltils;

class FENParserTest {

	@Test
	void testWriteFEN1() {
		final Board board = Board.createStandardBoard();
		final String fenString = FenUltils.createFENFromGame(board);
		assertEquals(fenString, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}
	
	@Test
	void testWriteFEN2() {
		final Board board = Board.createStandardBoard();
		final MoveTransition t1 = board.currentPlayer().makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"), BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));
		assertTrue(t1.getMoveStatus().isDone());
		final String fenString = FenUltils.createFENFromGame(t1.getToBoard());
		assertEquals(fenString, "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
		final MoveTransition t2 = t1.getToBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c7"), BoardUtils.INSTANCE.getCoordinateAtPosition("c5")));
		assertTrue(t2.getMoveStatus().isDone());
		final String fenString2 = FenUltils.createFENFromGame(t2.getToBoard());
		assertEquals(fenString2, "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 1");
	}

}
