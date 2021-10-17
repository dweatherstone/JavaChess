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

public class WhitePlayer extends Player {

	public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegalMoves,
			final Collection<Move> blackStandardLegalMoves) {
		super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
	}

	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getWhitePieces();
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.WHITE;
	}

	@Override
	public Player getOpponent() {
		return this.board.blackPlayer();
	}

	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
			final Collection<Move> opponentsLegals) {
		
		if (!hasCastleOpportunities()) {
			return Collections.emptyList();
		}

		final List<Move> kingCastles = new ArrayList<Move>();
		if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 60 && !this.isInCheck()) {
			// King side castle
			if (this.board.getPiece(61) == null && this.board.getPiece(62) == null) {
				final Piece kingSideRook = this.board.getPiece(63);
				if (kingSideRook != null && kingSideRook.isFirstMove()) {
					if (Player.calculateAttacksOnTile(61, opponentsLegals).isEmpty()
							&& Player.calculateAttacksOnTile(62, opponentsLegals).isEmpty()
							&& kingSideRook.getPieceType() == ROOK) {
						if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {
							kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62,
									(Rook) kingSideRook, kingSideRook.getPiecePosition(), 61));
						}
					}
				}
			}
			// Queen side castle
			if (this.board.getPiece(59) == null && this.board.getPiece(58) == null
					&& this.board.getPiece(57) == null) {
				final Piece queenSideRook = this.board.getPiece(56);
				if (queenSideRook != null && queenSideRook.isFirstMove()) {
					if (Player.calculateAttacksOnTile(59, opponentsLegals).isEmpty()
							&& Player.calculateAttacksOnTile(58, opponentsLegals).isEmpty()
							&& queenSideRook.getPieceType() == ROOK) {
						if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {
							kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58,
									(Rook) queenSideRook, queenSideRook.getPiecePosition(), 59));
						}
					}
				}
			}
		}

		return Collections.unmodifiableList(kingCastles);
	}
	
	@Override
	public String toString() {
		return Alliance.WHITE.toString();
	}

}
