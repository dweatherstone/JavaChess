package com.weatherstone.chess.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.MoveStatus;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.engine.pieces.King;
import com.weatherstone.chess.engine.pieces.Piece;

import static com.weatherstone.chess.engine.pieces.Piece.PieceType.KING;

public abstract class Player {

	protected final Board board;
	protected final King playerKing;
	protected final Collection<Move> legalMoves;
	protected final boolean isInCheck;

	public Player(final Board board, 
			      final Collection<Move> playerLegals, 
			      final Collection<Move> opponentLegals) {
		this.board = board;
		this.playerKing = establishKing();
		this.isInCheck = !calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
		playerLegals.addAll(calculateKingCastles(playerLegals, opponentLegals));
		this.legalMoves = Collections.unmodifiableCollection(playerLegals);
		
	}

	public King getPlayerKing() {
		return this.playerKing;
	}

	public Collection<Move> getLegalMoves() {
		return this.legalMoves;
	}

	protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
		final List<Move> attackMoves = new ArrayList<Move>();
		for (final Move move : moves) {
			if (piecePosition == move.getDestinationCoordinate()) {
				attackMoves.add(move);
			}
		}

		return ImmutableList.copyOf(attackMoves);
	}

	private King establishKing() {
		for (final Piece piece : getActivePieces()) {
			if (piece.getPieceType() == KING) {
				return (King) piece;
			}
		}
		throw new RuntimeException("Should not reach here! No King so not a valid board");
	}

	public boolean isMoveLegal(final Move move) {
		return this.legalMoves.contains(move);
	}

	public boolean isInCheck() {
		return this.isInCheck;
	}

	public boolean isInCheckMate() {
		return this.isInCheck && !hasEscapeMoves();
	}

	public boolean isInStaleMate() {
		return !this.isInCheck && !hasEscapeMoves();
	}

	protected boolean hasEscapeMoves() {
		for (final Move move : this.legalMoves) {
			final MoveTransition transition = makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				return true;
			}
		}
		return false;
	}

	public boolean isCastled() {
		return this.playerKing.isCastled();
	}
	
	public boolean isKingSideCastleCapable() {
		return this.playerKing.isKingSideCastleCapable();
	}
	
	public boolean isQueenSideCastleCapable() {
		return this.playerKing.isQueenSideCastleCapable();
	}

	public MoveTransition makeMove(final Move move) {

		/*
		 * if (!isMoveLegal(move)) { return new MoveTransition(this.board, move,
		 * MoveStatus.ILLEGAL_MOVE); } final Board transitionBoard = move.execute();
		 * final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
		 * transitionBoard.currentPlayer().getOpponent().getPlayerKing().
		 * getPiecePosition(), transitionBoard.currentPlayer().getLegalMoves()); if
		 * (!kingAttacks.isEmpty()) { return new MoveTransition(this.board, move,
		 * MoveStatus.LEAVES_PLAYER_IN_CHECK); }
		 * 
		 * return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
		 */
		if (!this.legalMoves.contains(move)) {
			return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
		}
		final Board transitionedBoard = move.execute();
		return transitionedBoard.currentPlayer().getOpponent().isInCheck() ?
				new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK) :
				new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);	
		
	}
	
	public MoveTransition unMakeMove(final Move move) {
		return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
	}
	
	protected boolean hasCastleOpportunities() {
		return !this.isInCheck && !this.playerKing.isCastled() &&
				(this.playerKing.isKingSideCastleCapable() || this.playerKing.isQueenSideCastleCapable());
	}

	public abstract Collection<Piece> getActivePieces();

	public abstract Alliance getAlliance();

	public abstract Player getOpponent();

	protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
			Collection<Move> opponentsLegals);
}
