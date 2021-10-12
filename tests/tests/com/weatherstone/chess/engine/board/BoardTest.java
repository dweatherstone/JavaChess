package tests.com.weatherstone.chess.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Board.Builder;
import com.weatherstone.chess.engine.board.BoardUtils;
// import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.engine.board.Move.MoveFactory;
// import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.pieces.*;

class BoardTest {

	@Test
	public void initialBoard() {
		final Board board = Board.createStandardBoard();
		assertEquals(board.currentPlayer().getLegalMoves().size(), 20);
		assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
		assertFalse(board.currentPlayer().isInCheck());
		assertFalse(board.currentPlayer().isInCheckMate());
		assertFalse(board.currentPlayer().isCastled());
		//assertTrue(board.currentPlayer().isKingSideCastleCapable());
		//assertTrue(board.currentPlayer().isQueenSideCastleCapable());
		assertEquals(board.currentPlayer(), board.whitePlayer());
		assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
		assertFalse(board.currentPlayer().getOpponent().isInCheck());
		assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
		assertFalse(board.currentPlayer().getOpponent().isCastled());
		//assertTrue(board.currentPlayer().getOpponent().isKingSideCastleCapable());
		//assertTrue(board.currentPlayer().getOpponent().isQueenSideCastleCapable());
		//assertEquals(new StandardBoardEvaluator().evaluate(board, 0), 0);
	}
	/*
	@Test
    public void testPlainKingMove() {

        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new King(Alliance.BLACK, 4, false, false));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        // White Layout
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new King(Alliance.WHITE, 60, false, false));
        builder.setMoveMaker(Alliance.WHITE);
        // Set the current player
        final Board board = builder.build();
        System.out.println(board);

        assertEquals(board.whitePlayer().getLegalMoves().size(), 6);
        assertEquals(board.blackPlayer().getLegalMoves().size(), 6);
        assertFalse(board.currentPlayer().isInCheck());
        assertFalse(board.currentPlayer().isInCheckMate());
        assertFalse(board.currentPlayer().getOpponent().isInCheck());
        assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
        assertEquals(board.currentPlayer(), board.whitePlayer());
        assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
//        BoardEvaluator evaluator = StandardBoardEvaluator.get();
//        System.out.println(evaluator.evaluate(board, 0));
//        assertEquals(StandardBoardEvaluator.get().evaluate(board, 0), 0);

        final Move move = MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("e1"),
                BoardUtils.getCoordinateAtPosition("f1"));

        final MoveTransition moveTransition = board.currentPlayer()
                .makeMove(move);

        assertEquals(moveTransition.getTransitionMove(), move);
        assertEquals(moveTransition.getFromBoard(), board);
        assertEquals(moveTransition.getToBoard().currentPlayer(), moveTransition.getToBoard().blackPlayer());

        assertTrue(moveTransition.getMoveStatus().isDone());
        assertEquals(moveTransition.getToBoard().whitePlayer().getPlayerKing().getPiecePosition(), 61);
        System.out.println(moveTransition.getToBoard());

    }
	*/
	@Test
	public void testBoardConsistency() {
		final Board board = Board.createStandardBoard();
		assertEquals(board.whitePlayer(), board.currentPlayer());
		
		final MoveTransition t1 = board.currentPlayer().makeMove(MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("e2"), BoardUtils.getCoordinateAtPosition("e4")));
		final MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t1.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("e7"), BoardUtils.getCoordinateAtPosition("e5")));
		final MoveTransition t3 = t2.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t2.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("g1"), BoardUtils.getCoordinateAtPosition("f3")));
		final MoveTransition t4 = t3.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t3.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("d7"), BoardUtils.getCoordinateAtPosition("d5")));
		final MoveTransition t5 = t4.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t4.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("e4"), BoardUtils.getCoordinateAtPosition("d5")));
		final MoveTransition t6 = t5.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t5.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("d8"), BoardUtils.getCoordinateAtPosition("d5")));
		final MoveTransition t7 = t6.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t6.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("f3"), BoardUtils.getCoordinateAtPosition("g5")));
		final MoveTransition t8 = t7.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t7.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("f7"), BoardUtils.getCoordinateAtPosition("f6")));
		final MoveTransition t9 = t8.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t8.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("d1"), BoardUtils.getCoordinateAtPosition("h5")));
		final MoveTransition t10 = t9.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t9.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("g7"), BoardUtils.getCoordinateAtPosition("g6")));
		final MoveTransition t11 = t10.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t10.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("h5"), BoardUtils.getCoordinateAtPosition("h4")));
		final MoveTransition t12 = t11.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t11.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("f6"), BoardUtils.getCoordinateAtPosition("g5")));
		final MoveTransition t13 = t12.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t12.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("h4"), BoardUtils.getCoordinateAtPosition("g5")));
		final MoveTransition t14 = t13.getTransitionBoard().currentPlayer().makeMove(MoveFactory.createMove(t13.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("d5"), BoardUtils.getCoordinateAtPosition("e4")));
		
		assertTrue(t14.getTransitionBoard().whitePlayer().getActivePieces().size() == calculateActivesFor(t14.getTransitionBoard(), Alliance.WHITE));
		assertTrue(t14.getTransitionBoard().blackPlayer().getActivePieces().size() == calculateActivesFor(t14.getTransitionBoard(), Alliance.BLACK));
	}
	
	@Test
    public void testInvalidBoard() {

        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));
        // White Layout
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        //white to move
        builder.setMoveMaker(Alliance.WHITE);
        //build the board     
        Assertions.assertThrows(RuntimeException.class, () -> builder.build());
    }
	
	@Test
	public void testAlgebraicNotation() {
		assertEquals("a8", BoardUtils.getPositionAtCoordinate(0));
		assertEquals("b8", BoardUtils.getPositionAtCoordinate(1));
		assertEquals("c8", BoardUtils.getPositionAtCoordinate(2));
		assertEquals("d8", BoardUtils.getPositionAtCoordinate(3));
		assertEquals("e8", BoardUtils.getPositionAtCoordinate(4));
		assertEquals("f8", BoardUtils.getPositionAtCoordinate(5));
		assertEquals("g8", BoardUtils.getPositionAtCoordinate(6));
		assertEquals("h8", BoardUtils.getPositionAtCoordinate(7));
	}
	
	private static int calculateActivesFor(final Board board, final Alliance alliance) {
		int count = 0;
		for (final Piece p : board.getAllPieces()) {
			if (p.getPieceAlliance().equals(alliance)) {
				count++;
			}
		}
		return count;
	}

}
