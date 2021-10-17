package tests.com.weatherstone.chess.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.engine.board.Board.Builder;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move.MoveFactory;
import com.weatherstone.chess.engine.pieces.Bishop;
import com.weatherstone.chess.engine.pieces.King;
import com.weatherstone.chess.engine.pieces.Pawn;

class StaleMateCheck {

	@Test
	void testAnandKramnikStaleMate() {
		final Builder builder = new Builder();
		// Black layout
		builder.setPiece(new Pawn(Alliance.BLACK, 14));
		builder.setPiece(new Pawn(Alliance.BLACK, 21));
		builder.setPiece(new King(Alliance.BLACK, 36, false, false));
		// White layout
		builder.setPiece(new Pawn(Alliance.WHITE, 29));
		builder.setPiece(new Pawn(Alliance.WHITE, 39));
		builder.setPiece(new King(Alliance.WHITE, 31, false, false));
		// Set the current player
		builder.setMoveMaker(Alliance.BLACK);
		Board board = builder.build();
		assertFalse(board.currentPlayer().isInStaleMate());
		final MoveTransition t1 = board.currentPlayer().makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e4"), BoardUtils.INSTANCE.getCoordinateAtPosition("f5")));
		assertTrue(t1.getMoveStatus().isDone());
		assertTrue(t1.getToBoard().currentPlayer().isInStaleMate());
		assertFalse(t1.getToBoard().currentPlayer().isInCheck());
		assertFalse(t1.getToBoard().currentPlayer().isInCheckMate());
		
	}
	
	@Test
	void testAnonymousStaleMate() {
		final Builder builder = new Builder();
		// Black layout
		builder.setPiece(new King(Alliance.BLACK, 2, false, false));
		// White layout
		builder.setPiece(new Pawn(Alliance.WHITE, 10));
		builder.setPiece(new King(Alliance.WHITE, 26, false, false));
		// Set the current player
		builder.setMoveMaker(Alliance.WHITE);
		Board board = builder.build();
		assertFalse(board.currentPlayer().isInStaleMate());
		final MoveTransition t1 = board.currentPlayer().makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("c5"), BoardUtils.INSTANCE.getCoordinateAtPosition("c6")));
		assertTrue(t1.getMoveStatus().isDone());
		assertTrue(t1.getToBoard().currentPlayer().isInStaleMate());
		assertFalse(t1.getToBoard().currentPlayer().isInCheck());
		assertFalse(t1.getToBoard().currentPlayer().isInCheckMate());
	}
	
	@Test
	void testAnonymousStaleMate2() {
		final Builder builder = new Builder();
		// Black layout
		builder.setPiece(new King(Alliance.BLACK, 0, false, false));
		// White layout
		builder.setPiece(new Pawn(Alliance.WHITE, 16));
		builder.setPiece(new King(Alliance.WHITE, 17, false, false));
		builder.setPiece(new Bishop(Alliance.WHITE, 19));
		// Set the current player
		builder.setMoveMaker(Alliance.WHITE);
		Board board = builder.build();
		assertFalse(board.currentPlayer().isInStaleMate());
		final MoveTransition t1 = board.currentPlayer().makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("a6"), BoardUtils.INSTANCE.getCoordinateAtPosition("a7")));
		assertTrue(t1.getMoveStatus().isDone());
		assertTrue(t1.getToBoard().currentPlayer().isInStaleMate());
		assertFalse(t1.getToBoard().currentPlayer().isInCheck());
		assertFalse(t1.getToBoard().currentPlayer().isInCheckMate());
	}

}
