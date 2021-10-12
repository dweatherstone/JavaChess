package com.weatherstone.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.Tile;
import com.weatherstone.chess.engine.board.Move.MajorAttackMove;
import com.weatherstone.chess.engine.board.Move.MajorMove;

public class Bishop extends Piece {

	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -7, 7, 9 };

	public Bishop(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.BISHOP, pieceAlliance, piecePosition, true);
	}
	
	public Bishop(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.BISHOP, pieceAlliance, piecePosition, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {

		final List<Move> legalMoves = new ArrayList<Move>();

		for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {

			int candidateDestinationCoordinate = this.piecePosition;

			while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {

				if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)
						|| isEighthColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
					break;
				}

				candidateDestinationCoordinate += candidateCoordinateOffset;

				if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
					final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
					if (!candidateDestinationTile.isTileOccupied()) {
						legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
					} else { // is occupied
						final Piece pieceAtDestination = candidateDestinationTile.getPiece();
						final Alliance destinationPieceAlliance = pieceAtDestination.getPieceAlliance();
						if (this.pieceAlliance != destinationPieceAlliance) {
							legalMoves.add(
									new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
						}
						break;
					}
				}
			}

		}

		return ImmutableList.copyOf(legalMoves);
	}
	
	@Override
	public Bishop movePiece(final Move move) {
		return new Bishop(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
	
	@Override
	public String toString() {
		return PieceType.BISHOP.toString();
	}

	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
	}

	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
	}

}
