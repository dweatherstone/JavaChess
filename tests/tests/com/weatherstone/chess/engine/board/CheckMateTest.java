package tests.com.weatherstone.chess.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.engine.pieces.*;
import com.weatherstone.chess.engine.player.ai.MiniMax;
import com.weatherstone.chess.engine.player.ai.MoveStrategy;
import com.weatherstone.chess.engine.Alliance;

class CheckMateTest {

	@Test
	void testFoolsMate() {
		final Board board = Board.createStandardBoard();
		final MoveTransition t1 = board.currentPlayer().makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("f2"), BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));
		assertTrue(t1.getMoveStatus().isDone());
		final MoveTransition t2 = t1.getToBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"), BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));
		assertTrue(t2.getMoveStatus().isDone());
		final MoveTransition t3 = t2.getToBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"), BoardUtils.INSTANCE.getCoordinateAtPosition("g4")));
		assertTrue(t3.getMoveStatus().isDone());
		final MoveTransition t4 = t3.getToBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"), BoardUtils.INSTANCE.getCoordinateAtPosition("h4")));
		assertTrue(t4.getMoveStatus().isDone());
		
		assertTrue(t4.getToBoard().currentPlayer().isInCheckMate());
	}
	
	@Test
	void testFoolsMateMiniMax() {
		final Board board = Board.createStandardBoard();
		final MoveTransition t1 = board.currentPlayer().makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("f2"), BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));
		assertTrue(t1.getMoveStatus().isDone());
		final MoveTransition t2 = t1.getToBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"), BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));
		assertTrue(t2.getMoveStatus().isDone());
		final MoveTransition t3 = t2.getToBoard().currentPlayer().makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"), BoardUtils.INSTANCE.getCoordinateAtPosition("g4")));
		assertTrue(t3.getMoveStatus().isDone());
		final MoveStrategy strategy = new MiniMax(4);
		final Move aiMove = strategy.execute(t3.getToBoard());
		final Move bestMove = Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"), BoardUtils.INSTANCE.getCoordinateAtPosition("h4"));
		assertEquals(aiMove, bestMove);
	}
	
	@Test
    public void testScholarsMate() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("a7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("a6")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("a6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("a5")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c4")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("a5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("a4")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f3"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f7")));

        assertTrue(t7.getMoveStatus().isDone());
        assertTrue(t7.getToBoard().currentPlayer().isInCheckMate());

    }
	
	@Test
    public void testLegalsMate() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c4")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d6")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g4")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c3")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g6")));

        assertTrue(t8.getMoveStatus().isDone());

        final MoveTransition t9 = t8.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f3"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t9.getMoveStatus().isDone());

        final MoveTransition t10 = t9.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d1")));

        assertTrue(t10.getMoveStatus().isDone());

        final MoveTransition t11 = t10.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f7")));

        assertTrue(t11.getMoveStatus().isDone());

        final MoveTransition t12 = t11.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t11.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e7")));

        assertTrue(t12.getMoveStatus().isDone());

        final MoveTransition t13 = t12.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t12.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c3"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d5")));

        assertTrue(t13.getMoveStatus().isDone());
        assertTrue(t13.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testSevenMoveMate() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d4")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d6")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e7")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d6")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t8.getMoveStatus().isDone());

        final MoveTransition t9 = t8.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e2")));

        assertTrue(t9.getMoveStatus().isDone());

        final MoveTransition t10 = t9.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g2")));

        assertTrue(t10.getMoveStatus().isDone());

        final MoveTransition t11 = t10.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c7")));

        assertTrue(t11.getMoveStatus().isDone());

        final MoveTransition t12 = t11.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t11.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("h1")));

        assertTrue(t12.getMoveStatus().isDone());

        final MoveTransition t13 = t12.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t12.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d8")));

        assertTrue(t13.getMoveStatus().isDone());
        assertTrue(t13.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testGrecoGame() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("d2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("d4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f6")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d2")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g4")));


        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("h2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("h3")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e3")));

        assertTrue(t8.getMoveStatus().isDone());

        final MoveTransition t9 = t8.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e3")));

        assertTrue(t9.getMoveStatus().isDone());

        final MoveTransition t10 = t9.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("h4")));

        assertTrue(t10.getMoveStatus().isDone());

        final MoveTransition t11 = t10.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g3")));

        assertTrue(t11.getMoveStatus().isDone());

        final MoveTransition t12 = t11.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t11.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("h4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g3")));

        assertTrue(t12.getMoveStatus().isDone());
        assertTrue(t12.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testOlympicGame() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c6")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d5")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c3")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c3"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d7")));

        assertTrue(t8.getMoveStatus().isDone());

        final MoveTransition t9 = t8.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e2")));

        assertTrue(t9.getMoveStatus().isDone());

        final MoveTransition t10 = t9.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f6")));

        assertTrue(t10.getMoveStatus().isDone());

        final MoveTransition t11 = t10.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d6")));

        assertTrue(t11.getMoveStatus().isDone());
        assertTrue(t11.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testAnotherGame() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c6")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c4")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d4")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f3"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g5")));

        assertTrue(t8.getMoveStatus().isDone());

        final MoveTransition t9 = t8.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f7")));

        assertTrue(t9.getMoveStatus().isDone());

        final MoveTransition t10 = t9.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g2")));

        assertTrue(t10.getMoveStatus().isDone());

        final MoveTransition t11 = t10.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("h1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f1")));

        assertTrue(t11.getMoveStatus().isDone());

        final MoveTransition t12 = t11.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t11.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t12.getMoveStatus().isDone());

        final MoveTransition t13 = t12.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t12.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e2")));

        assertTrue(t13.getMoveStatus().isDone());

        final MoveTransition t14 = t13.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t13.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t14.getMoveStatus().isDone());
        assertTrue(t14.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testSmotheredMate() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e2")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c6")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c3")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d4")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g3")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t8.getMoveStatus().isDone());
        assertTrue(t8.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testHippopotamusMate() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e2")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("h4")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c3")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c6")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g3")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("h4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g5")));


        assertTrue(t8.getMoveStatus().isDone());

        final MoveTransition t9 = t8.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d4")));

        assertTrue(t9.getMoveStatus().isDone());

        final MoveTransition t10 = t9.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d4")));

        assertTrue(t10.getMoveStatus().isDone());

        final MoveTransition t11 = t10.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g5")));

        assertTrue(t11.getMoveStatus().isDone());

        final MoveTransition t12 = t11.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t11.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t12.getMoveStatus().isDone());
        assertTrue(t12.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testBlackburneShillingMate() {

        final Board board = Board.createStandardBoard();

        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveTransition t4 = t3.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("b8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c6")));

        assertTrue(t4.getMoveStatus().isDone());

        final MoveTransition t5 = t4.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("c4")));

        assertTrue(t5.getMoveStatus().isDone());

        final MoveTransition t6 = t5.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c6"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("d4")));

        assertTrue(t6.getMoveStatus().isDone());

        final MoveTransition t7 = t6.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f3"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));

        assertTrue(t7.getMoveStatus().isDone());

        final MoveTransition t8 = t7.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g5")));

        assertTrue(t8.getMoveStatus().isDone());

        final MoveTransition t9 = t8.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f7")));

        assertTrue(t9.getMoveStatus().isDone());

        final MoveTransition t10 = t9.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g5"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("g2")));

        assertTrue(t10.getMoveStatus().isDone());

        final MoveTransition t11 = t10.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("h1"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f1")));

        assertTrue(t11.getMoveStatus().isDone());

        final MoveTransition t12 = t11.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t11.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g2"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));

        assertTrue(t12.getMoveStatus().isDone());

        final MoveTransition t13 = t12.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t12.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("c4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("e2")));

        assertTrue(t13.getMoveStatus().isDone());

        final MoveTransition t14 = t13.getToBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t13.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d4"),
                        BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));

        assertTrue(t14.getMoveStatus().isDone());
        assertTrue(t14.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testAnastasiaMate() {

        final Board.Builder builder = new Board.Builder();

        // Black Layout
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Rook(Alliance.BLACK, 5));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new King(Alliance.BLACK, 15, false, false));
        // White Layout
        builder.setPiece(new Knight(Alliance.WHITE, 12));
        builder.setPiece(new Rook(Alliance.WHITE, 27));
        builder.setPiece(new Pawn(Alliance.WHITE, 41));
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new King(Alliance.WHITE, 62, false, false));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);

        final Board board = builder.build();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("d5"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("h5")));

        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInCheckMate());
    }

    @Test
    public void testTwoBishopMate() {

        final Board.Builder builder = new Board.Builder();

        builder.setPiece(new King(Alliance.BLACK, 7, false, false));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));
        builder.setPiece(new Pawn(Alliance.BLACK, 17));
        // White Layout
        builder.setPiece(new Bishop(Alliance.WHITE, 40));
        builder.setPiece(new Bishop(Alliance.WHITE, 48));
        builder.setPiece(new King(Alliance.WHITE, 53, false, false));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);

        final Board board = builder.build();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("a3"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("b2")));

        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInCheckMate());
    }

    @Test
    public void testQueenRookMate() {

        final Board.Builder builder = new Board.Builder();

        // Black Layout
        builder.setPiece(new King(Alliance.BLACK, 5, false, false));
        // White Layout
        builder.setPiece(new Rook(Alliance.WHITE, 9));
        builder.setPiece(new Queen(Alliance.WHITE, 16));
        builder.setPiece(new King(Alliance.WHITE, 59, false, false));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);

        final Board board = builder.build();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("a6"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("a8")));

        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testQueenKnightMate() {

        final Board.Builder builder = new Board.Builder();

        // Black Layout
        builder.setPiece(new King(Alliance.BLACK, 4, false, false));
        // White Layout
        builder.setPiece(new Queen(Alliance.WHITE, 15));
        builder.setPiece(new Knight(Alliance.WHITE, 29));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new King(Alliance.WHITE, 60, false, false));
        // Set the current player
        builder.setMoveMaker(Alliance.WHITE);

        final Board board = builder.build();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("h7"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("e7")));

        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInCheckMate());

    }

    @Test
    public void testBackRankMate() {

        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(new King(Alliance.BLACK, 4, false, false));
        builder.setPiece(new Rook(Alliance.BLACK, 18));
        // White Layout
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new King(Alliance.WHITE, 62, false, false));
        // Set the current player
        builder.setMoveMaker(Alliance.BLACK);

        final Board board = builder.build();

        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("c6"),
                                BoardUtils.INSTANCE.getCoordinateAtPosition("c1")));

        assertTrue(t1.getMoveStatus().isDone());
        assertTrue(t1.getToBoard().currentPlayer().isInCheckMate());

    }

}
