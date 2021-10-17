package com.weatherstone.chess.engine.player.ai;

import com.google.common.annotations.VisibleForTesting;
import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.pieces.Piece.PieceType;
import com.weatherstone.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {
	
	private static final int CHECK_BONUS = 45;
	private static final int CHECKMATE_BONUS = 10000;
	private static final int CASTLE_BONUS = 25;
	private static final int MOBILITY_MULTIPLIER = 5;
	private static final int ATTACK_MULTIPLIER = 1;
	private static final int TWO_BISHOP_BONUS = 25;
	
	private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();
	
	private StandardBoardEvaluator() {}
	
	public static StandardBoardEvaluator get() {
		return INSTANCE;
	}

	@Override
	public int evaluate(final Board board, final int depth) {
		return score(board.whitePlayer(), depth) - 
				score(board.blackPlayer(), depth);
	}
	
	public String evaluationDetails(final Board board, final int depth) {
		return
	               ("White Mobility : " + mobility(board.whitePlayer()) + "\n") +
	                "White kingThreats : " + kingThreats(board.whitePlayer(), depth) + "\n" +
	                "White attacks : " + attacks(board.whitePlayer()) + "\n" +
	                "White castle : " + castled(board.whitePlayer()) + "\n" +
	                "White pieceEval : " + pieceEvaluation(board.whitePlayer()) + "\n" +
	                "White pawnStructure : " + pawnStructure(board.whitePlayer()) + "\n" +
	                "---------------------\n" +
	                "Black Mobility : " + mobility(board.blackPlayer()) + "\n" +
	                "Black kingThreats : " + kingThreats(board.blackPlayer(), depth) + "\n" +
	                "Black attacks : " + attacks(board.blackPlayer()) + "\n" +
	                "Black castle : " + castled(board.blackPlayer()) + "\n" +
	                "Black pieceEval : " + pieceEvaluation(board.blackPlayer()) + "\n" +
	                "Black pawnStructure : " + pawnStructure(board.blackPlayer()) + "\n\n" +
	                "Final Score = " + evaluate(board, depth);
	    }
	
	@VisibleForTesting
	private static int score(final Player player,
							final int depth) {
		return mobility(player) +
				kingThreats(player, depth) + 
				attacks(player) +
				castled(player) +
				pieceEvaluation(player) +
				pawnStructure(player);
	}
	
	private static int attacks(final Player player) {
		int attackScore = 0;
		for (final Move move : player.getLegalMoves()) {
			if (move.isAttack()) {
				final Piece movedPiece = move.getMovedPiece();
				final Piece attackedPiece = move.getAttackedPiece();
				if (movedPiece.getPieceValue() <= attackedPiece.getPieceValue()) {
					attackScore ++;
				}
			}
		}
		return attackScore * ATTACK_MULTIPLIER;
	}
	
	private static int castled(final Player player) {
		return player.isCastled() ? CASTLE_BONUS : 0;
	}
	
	
	private static int pawnStructure(final Player player) {
		return PawnStructureAnalyzer.get().pawnStructureScore(player);
	}

	private static int check(final Player player) {
		return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
	}
	
	private static int depthBonus(final int depth) {
		return depth == 0 ? 1 : 100 * depth;
	}

	private static int mobility(final Player player) {
		return MOBILITY_MULTIPLIER * mobilityRaotio(player);
	}

	private static int mobilityRaotio(Player player) {
		return (int)((player.getLegalMoves().size() * 10.0f) / player.getOpponent().getLegalMoves().size());
	}
	
	private static int kingThreats(final Player player,
									final int depth) {
		return player.getOpponent().isInCheckMate() ? CHECKMATE_BONUS * depthBonus(depth) : check(player);
	}

	private static int pieceEvaluation(final Player player) {
		int pieceValuationScore = 0;
		int numBishops = 0;
		for (final Piece piece : player.getActivePieces()) {
			pieceValuationScore += piece.getPieceValue() + piece.locationBonus();
			if (piece.getPieceType() == PieceType.BISHOP) {
				numBishops++;
			}
		}
		return pieceValuationScore + (numBishops == 2 ? TWO_BISHOP_BONUS : 0);
	}

}
