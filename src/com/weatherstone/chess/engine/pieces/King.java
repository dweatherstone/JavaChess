package com.weatherstone.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.Move.*;

public class King extends Piece {
	
	private final boolean isCastled;
	private final boolean kingSideCastleCapable;
	private final boolean queenSideCastleCapable;
	
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

	public King(final Alliance pieceAlliance, 
			final int piecePosition, 
			final boolean kingSideCastleCapable, 
			final boolean queenSideCastleCapable) {
		super(PieceType.KING, pieceAlliance, piecePosition, true);
		this.isCastled = false;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}
	
	public King(final Alliance pieceAlliance, 
			final int piecePosition, 
			final boolean isFirstMove, 
			final boolean isCastled, 
			final boolean kingSideCastleCapable, 
			final boolean queenSideCastleCapable) {
		super(PieceType.KING, pieceAlliance, piecePosition, isFirstMove);
		this.isCastled = isCastled;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		
		final List<Move> legalMoves = new ArrayList<Move>();
		
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset)
					|| isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
				continue;
			}
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {	
				final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
				if (pieceAtDestination == null) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
				} else { // is occupied
					final Alliance destinationPieceAlliance = pieceAtDestination.getPieceAlliance();
					if (this.pieceAlliance != destinationPieceAlliance) {
						legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
					}
				}
			}
		}
		
		return Collections.unmodifiableList(legalMoves);
	}
	
	@Override
	public King movePiece(final Move move) {
		return new King(this.pieceAlliance, move.getDestinationCoordinate(), false, move.isCastlingMove(), false, false);
	}
	
	@Override
	public String toString() {
		return this.pieceType.toString();
	}
	
	@Override
	public int locationBonus() {
		return this.pieceAlliance.kingBonus(this.piecePosition);
	}
	
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition)
				&& (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7);
	}
	
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition)
				&& (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9);
	}

	public boolean isCastled() {
		return isCastled;
	}

	public boolean isKingSideCastleCapable() {
		return kingSideCastleCapable;
	}

	public boolean isQueenSideCastleCapable() {
		return queenSideCastleCapable;
	}
	
	@Override
	public boolean equals (final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof King)) {
			return false;
		}
		if (!super.equals(other)) {
			return false;
		}
		final King king = (King) other;
		return isCastled == king.isCastled;
	}
	
	@Override
	public int hashCode() {
		return (31 * super.hashCode()) + (isCastled ? 1 : 0);
	}

}
