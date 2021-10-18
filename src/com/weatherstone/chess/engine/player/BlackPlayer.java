package com.weatherstone.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.Move.KingSideCastleMove;
import com.weatherstone.chess.engine.board.Move.QueenSideCastleMove;
import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.pieces.Rook;

import static com.weatherstone.chess.engine.pieces.Piece.PieceType.ROOK;

public class BlackPlayer extends Player {

	public BlackPlayer(final Board board, 
			           final Collection<Move> whiteStandardLegalMoves,
			           final Collection<Move> blackStandardLegalMoves) {
		super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
	}

	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
			final Collection<Move> opponentsLegals) {
		
		if (!hasCastleOpportunities()) {
			return Collections.emptyList();
		}
	
		final List<Move> kingCastles = new ArrayList<Move>();
		if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 4 && !this.isInCheck) {
			// King side castle
			if (this.board.getPiece(5) == null && this.board.getPiece(6) == null) {
				final Piece kingSideRook = this.board.getPiece(7);
				if (kingSideRook != null && kingSideRook.isFirstMove()) {
					if (Player.calculateAttacksOnTile(5, opponentsLegals).isEmpty()
							&& Player.calculateAttacksOnTile(6, opponentsLegals).isEmpty()
							&& kingSideRook.getPieceType() == ROOK) {
						if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
							kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 6,
									(Rook) kingSideRook, kingSideRook.getPiecePosition(), 5));
					}
				}
			}
		}
		// Queen side castle
		if (this.board.getPiece(1) == null && this.board.getPiece(2) == null
				&& this.board.getPiece(3) == null) {
			final Piece queenSideRook = this.board.getPiece(0);
			if (queenSideRook != null && queenSideRook.isFirstMove()) {
				if (Player.calculateAttacksOnTile(2, opponentsLegals).isEmpty()
						&& Player.calculateAttacksOnTile(3, opponentsLegals).isEmpty()
						&& queenSideRook.getPieceType() == ROOK) {
					if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12))
						kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 2,
								(Rook) queenSideRook, queenSideRook.getPiecePosition(), 3));
						
					}
				}
			}
		}
	
		return Collections.unmodifiableList(kingCastles);
	}

	@Override
	public WhitePlayer getOpponent() {
		return this.board.whitePlayer();
	}

	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getBlackPieces();
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.BLACK;
	}

	@Override
	public String toString() {
		return Alliance.BLACK.toString();
	}

}
