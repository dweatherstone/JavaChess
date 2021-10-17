package com.weatherstone.chess.engine.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weatherstone.chess.engine.pieces.King;
import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.board.Move.MoveFactory;

public enum BoardUtils {
	
	INSTANCE;
	
	public final List<Boolean> FIRST_COLUMN = initColumn(0);
	public final List<Boolean> SECOND_COLUMN = initColumn(1);
	public final List<Boolean> SEVENTH_COLUMN = initColumn(6);
	public final List<Boolean> EIGHTH_COLUMN = initColumn(7);
	
	public final List<Boolean> FIRST_ROW = initRow(0);
	public final List<Boolean> SECOND_ROW = initRow(1);
	public final List<Boolean> THIRD_ROW = initRow(2);
	public final List<Boolean> FOURTH_ROW = initRow(3);
	public final List<Boolean> FIFTH_ROW = initRow(4);
	public final List<Boolean> SIXTH_ROW = initRow(5);
	public final List<Boolean> SEVENTH_ROW = initRow(6);
	public final List<Boolean> EIGHTH_ROW = initRow(7);
	
	public final List<String> ALGEBRAIC_NOTATION = initAlgebraicNotation();
	public final Map<String, Integer> POSITION_TO_COORDINATE = initPositionToCoordinateMap();
	
	public static final int NUM_TILES = 64;
	public static final int NUM_TILES_PER_ROW = 8;
	public static final int START_TILE_INDEX = 0;

	private static List<String> initAlgebraicNotation() {
		return Collections.unmodifiableList(Arrays.asList(
				"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
				"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
				"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
				"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
				"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
				"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
				"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
				"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
	}
	
	private Map<String, Integer> initPositionToCoordinateMap() {
		final Map<String, Integer> positionToCoordinate = new HashMap<>();
		for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
			positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
		}
		return Collections.unmodifiableMap(positionToCoordinate);
	}


	public static boolean isValidTileCoordinate(final int coordinate) {
		return coordinate >= START_TILE_INDEX && coordinate < NUM_TILES;
	}
	
	private static List<Boolean> initColumn(int columnNumber) {
		final Boolean[] column = new Boolean[NUM_TILES];
		for (int i = 0; i < column.length; i++) {
			column[i] = false;
		}
		
		do {
			column[columnNumber] = true;
			columnNumber += NUM_TILES_PER_ROW;
			
		} while (columnNumber < NUM_TILES);
		
		return Collections.unmodifiableList(Arrays.asList(column));
	}
	
	private static List<Boolean> initRow(int rowNumber) {
		final Boolean[] row = new Boolean[NUM_TILES];
		for (int i = 0; i < row.length; i++) {
			row[i] = false;
		}
		
		int start = rowNumber * NUM_TILES_PER_ROW;
		for (int i = 0; i < NUM_TILES_PER_ROW; i++) {
			row[start + i] = true;
		}
		return Collections.unmodifiableList(Arrays.asList(row));
	}

	public String getPositionAtCoordinate(final int coordinate) {
		return ALGEBRAIC_NOTATION.get(coordinate);
	}
	
	public int getCoordinateAtPosition(final String position) {
		return POSITION_TO_COORDINATE.get(position);
	}
	
	public static boolean isThreatenedBoardImmediate(final Board board) {
		return board.whitePlayer().isInCheck() || board.blackPlayer().isInCheck();
	}
	
	public static boolean kingThreat(final Move move) {
		final Board board = move.getBoard();
		final MoveTransition transition = board.currentPlayer().makeMove(move);
		return transition.getToBoard().currentPlayer().isInCheck();
	}
	
	public static boolean isKingPawnTrap(final Board board, final King king, final int frontTile) {
		final Piece piece = board.getPiece(frontTile);
		return piece != null &&
				piece.getPieceType() == Piece.PieceType.PAWN &&
				piece.getPieceAlliance() != king.getPieceAlliance();
	}
	
	public static int mvvlva (final Move move) {
		final Piece movingPiece = move.getMovedPiece();
		if (move.isAttack()) {
			final Piece attackedPiece = move.getAttackedPiece();
			return (attackedPiece.getPieceValue() - movingPiece.getPieceValue() + Piece.PieceType.KING.getPieceValue()) * 100;
		}
		return Piece.PieceType.KING.getPieceValue() - movingPiece.getPieceValue();
	}
	
	public static List<Move> lastNMoves (final Board board, int N) {
		final List<Move> moveHistory = new ArrayList<Move>();
		Move currentMove = board.getTransitionMove();
		int i = 0;
		while (currentMove != MoveFactory.getNullMove() && i < N) {
			moveHistory.add(currentMove);
			currentMove = currentMove.getBoard().getTransitionMove();
			i++;
		}
		return Collections.unmodifiableList(moveHistory);
	}
	
	public static boolean isEndGame(final Board board) {
		return board.currentPlayer().isInCheckMate() ||
				board.currentPlayer().isInStaleMate();
	}

}
