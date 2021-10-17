package com.weatherstone.chess.engine.pieces;

import java.util.Collection;

import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Move;

public abstract class Piece {

	protected final PieceType pieceType;
	protected final int piecePosition;
	protected final Alliance pieceAlliance;
	protected final boolean isFirstMove;
	private final int cachedHashCode;

	Piece(final PieceType pieceType, final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		this.pieceType = pieceType;
		this.piecePosition = piecePosition;
		this.pieceAlliance = pieceAlliance;
		this.isFirstMove = isFirstMove;
		this.cachedHashCode = computeHashCode();
	}

	public Alliance getPieceAlliance() {
		return this.pieceAlliance;
	}

	public boolean isFirstMove() {
		return this.isFirstMove;
	}

	public int getPiecePosition() {
		return this.piecePosition;
	}

	public PieceType getPieceType() {
		return this.pieceType;
	}
	
	public int getPieceValue() {
		return this.pieceType.getPieceValue();
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Piece)) {
			return false;
		}
		final Piece otherPiece = (Piece) other;
		return this.piecePosition == otherPiece.getPiecePosition() && this.pieceType == otherPiece.getPieceType()
				&& this.pieceAlliance == otherPiece.getPieceAlliance() && this.isFirstMove == otherPiece.isFirstMove();
	}

	@Override
	public int hashCode() {
		return this.cachedHashCode;
	}

	private int computeHashCode() {
		int result = this.pieceType.hashCode();
		result = 31 * result + this.pieceAlliance.hashCode();
		result = 31 * result + this.piecePosition;
		result = 31 * result + (this.isFirstMove ? 1 : 0);
		return result;
	}

	public abstract Collection<Move> calculateLegalMoves(final Board board);

	public abstract Piece movePiece(final Move move);
	
	public abstract int locationBonus();

	public enum PieceType {

		PAWN("P", 100),
		KNIGHT("N", 300),
		BISHOP("B", 300),
		ROOK("R", 500),
		QUEEN("Q", 900),
		KING("K", 10000);

		private String pieceName;
		private int pieceValue;

		PieceType(final String pieceName, final int pieceValue) {
			this.pieceName = pieceName;
			this.pieceValue = pieceValue;
		}
		
		@Override
		public String toString() {
			return this.pieceName;
		}
		
		public int getPieceValue() {
			return this.pieceValue;
		}

	}
}
