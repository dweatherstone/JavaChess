package com.weatherstone.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.Move.PawnMove;
import com.weatherstone.chess.engine.board.Move.PawnJump;
import com.weatherstone.chess.engine.board.Move.PawnAttackMove;
import com.weatherstone.chess.engine.board.Move.PawnEnPassantAttackMove;
import com.weatherstone.chess.engine.board.Move.PawnPromotion;

public class Pawn extends Piece {

	private final static int[] CANDIDATE_MOVE_COORDINATES = { 7, 8, 9, 16 };

	public Pawn(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.PAWN, pieceAlliance, piecePosition, true);
	}

	public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.PAWN, pieceAlliance, piecePosition, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<Move>();

		for (final int currentCoordinateOffset : CANDIDATE_MOVE_COORDINATES) {
			final int candidateDestinationCoordinate = this.piecePosition
					+ (currentCoordinateOffset * this.pieceAlliance.getDirection());

			if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				continue;
			}

			if (currentCoordinateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
				if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
					legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
				} else {
					legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
				}
			
			} else if (currentCoordinateOffset == 16 && this.isFirstMove()
					&& ((BoardUtils.SECOND_ROW[this.piecePosition] && this.pieceAlliance.isBlack())
							|| BoardUtils.SEVENTH_ROW[this.piecePosition] && this.pieceAlliance.isWhite())) {
				final int behindCandidateDestinationCoordinate = this.piecePosition
						+ (this.pieceAlliance.getDirection() * 8);
				if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
						&& !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
				}
			} else if (currentCoordinateOffset == 7
					&& !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite())
							|| (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
				if(board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
							legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
						} else {
							legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}
					}
				} else if (board.getEnPassantPawn() != null) {
					if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
							legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}
					}
				}
			} else if (currentCoordinateOffset == 9
					& !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())
							|| (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()))) {
				if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
					}
				} else if (board.getEnPassantPawn() != null) {
					if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
							if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
								legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate)));
							} else {
								legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
							}
						}
					}
				}
			}
		}

		return ImmutableList.copyOf(legalMoves);
	}
	
	public Piece getPromotionPiece() {
		return new Queen(this.pieceAlliance, this.piecePosition, false);
	}

	@Override
	public Pawn movePiece(final Move move) {
		return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}

	@Override
	public String toString() {
		return PieceType.PAWN.toString();
	}

}
