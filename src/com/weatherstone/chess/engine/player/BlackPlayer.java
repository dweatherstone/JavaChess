package com.weatherstone.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.Move.KingSideCastleMove;
import com.weatherstone.chess.engine.board.Move.QueenSideCastleMove;
import com.weatherstone.chess.engine.board.Tile;
import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.pieces.Rook;

public class BlackPlayer extends Player {

	public BlackPlayer(final Board board, final Collection<Move> whiteStandardLegalMoves,
			final Collection<Move> blackStandardLegalMoves) {
		super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
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
	public Player getOpponent() {
		return this.board.whitePlayer();
	}

	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
			final Collection<Move> opponentsLegals) {

		final List<Move> kingCastles = new ArrayList<Move>();
		if (this.playerKing.isFirstMove() && !this.isInCheck()) {
			// King side castle
			if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(7);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
					if (Player.calculateAttacksOnTile(5, opponentsLegals).isEmpty()
							&& Player.calculateAttacksOnTile(6, opponentsLegals).isEmpty()
							&& rookTile.getPiece().getPieceType().isRook()) {
						kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 6,
								(Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
					}
				}
			}
			// Queen side castle
			if (!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied()
					&& !this.board.getTile(3).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(0);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
					if (Player.calculateAttacksOnTile(2, opponentsLegals).isEmpty()
							&& Player.calculateAttacksOnTile(3, opponentsLegals).isEmpty()
							&& rookTile.getPiece().getPieceType().isRook()) {
						kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 2,
								(Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
					}
				}
			}
		}

		return ImmutableList.copyOf(kingCastles);
	}

}
