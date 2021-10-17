package com.weatherstone.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.Move.MajorAttackMove;
import com.weatherstone.chess.engine.board.Move.MajorMove;

public class Queen extends Piece {

	private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

	public Queen(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.QUEEN, pieceAlliance, piecePosition, true);
	}
	
	public Queen(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.QUEEN, pieceAlliance, piecePosition, isFirstMove);
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
					final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
					if (pieceAtDestination  == null) {
						legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
					} else { // is occupied
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

		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override
	public Queen movePiece(final Move move) {
		return new Queen(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}
	
	@Override
	public String toString() {
		return this.pieceType.toString();
	}

	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition)
				&& (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
	}

	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition)
				&& (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
	}

	@Override
	public int locationBonus() {
		return this.pieceAlliance.queenBonus(this.piecePosition);
	}

}
