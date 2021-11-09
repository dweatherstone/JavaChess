package com.weatherstone.chess.engine.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.weatherstone.chess.engine.Alliance;
import com.weatherstone.chess.engine.board.Move.MoveFactory;
import com.weatherstone.chess.engine.pieces.Bishop;
import com.weatherstone.chess.engine.pieces.King;
import com.weatherstone.chess.engine.pieces.Knight;
import com.weatherstone.chess.engine.pieces.Pawn;
import com.weatherstone.chess.engine.pieces.Piece;
import com.weatherstone.chess.engine.pieces.Queen;
import com.weatherstone.chess.engine.pieces.Rook;
import com.weatherstone.chess.engine.player.BlackPlayer;
import com.weatherstone.chess.engine.player.Player;
import com.weatherstone.chess.engine.player.WhitePlayer;

public class Board {
	
	private final Map<Integer, Piece> boardConfig;
	private final Collection<Piece> whitePieces;
	private final Collection<Piece> blackPieces;
	
	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;
	private final Player currentPlayer;
	private final Pawn enPassantPawn;
	private final Move transitionMove;
	
	private static final Board STANDARD_BOARD = createStandardBoardImpl();
	
	private Board(final Builder builder) {
		this.boardConfig = Collections.unmodifiableMap(builder.boardConfig);
		this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
		this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);
		this.enPassantPawn = builder.enPassantPawn;
		
		final Collection<Move> whiteStandardMoves = calculateLegalMoves(this.whitePieces);
		final Collection<Move> blackStandardMoves = calculateLegalMoves(this.blackPieces);
		
		this.whitePlayer = new WhitePlayer(this, whiteStandardMoves, blackStandardMoves);
		this.blackPlayer = new BlackPlayer(this, whiteStandardMoves, blackStandardMoves);
		this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);
		this.transitionMove = builder.transitionMove != null ? builder.transitionMove : MoveFactory.getNullMove();
	}
	
	public Collection<Piece> getWhitePieces() {
		return this.whitePieces;
	}
	
	public Collection<Piece> getBlackPieces() {
		return this.blackPieces;
	}
	
	public Collection<Piece> getAllPieces() {
		return Stream.concat(this.whitePieces.stream(), this.blackPieces.stream()).collect(Collectors.toList());
	}
	
	public Pawn getEnPassantPawn() {
		return this.enPassantPawn;
	}
	
	public Move getTransitionMove() {
		return this.transitionMove;
	}
	
	public Collection<Move> getAllLegalMoves() {
		return Stream.concat(this.whitePlayer.getLegalMoves().stream(), this.blackPlayer.getLegalMoves().stream()).collect(Collectors.toList());
	}

	private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
		return pieces.stream().flatMap(piece -> piece.calculateLegalMoves(this).stream())
				.collect(Collectors.toList());
	}

	private static Collection<Piece> calculateActivePieces(final Builder builder, final Alliance alliance) {
		return builder.boardConfig.values().stream()
				.filter(piece -> piece.getPieceAlliance() == alliance)
				.collect(Collectors.toList());
	}
	
	public static Board createStandardBoard() {
		return STANDARD_BOARD;
	}
	
	private static Board createStandardBoardImpl() {
		final Builder builder = new Builder();
		builder.setPiece(new Rook(Alliance.BLACK, 0));
		builder.setPiece(new Knight(Alliance.BLACK, 1));
		builder.setPiece(new Bishop(Alliance.BLACK, 2));
		builder.setPiece(new Queen(Alliance.BLACK, 3));
		builder.setPiece(new King(Alliance.BLACK, 4, true, true));
		builder.setPiece(new Bishop(Alliance.BLACK, 5));
		builder.setPiece(new Knight(Alliance.BLACK, 6));
		builder.setPiece(new Rook(Alliance.BLACK, 7));
		for (int i = 8; i < 16; i++) {
			builder.setPiece(new Pawn(Alliance.BLACK, i));
		}
		for (int i = 48; i < 56; i++) {
			builder.setPiece(new Pawn(Alliance.WHITE, i));
		}
		builder.setPiece(new Rook(Alliance.WHITE, 56));
		builder.setPiece(new Knight(Alliance.WHITE, 57));
		builder.setPiece(new Bishop(Alliance.WHITE, 58));
		builder.setPiece(new Queen(Alliance.WHITE, 59));
		builder.setPiece(new King(Alliance.WHITE, 60, true, true));
		builder.setPiece(new Bishop(Alliance.WHITE, 61));
		builder.setPiece(new Knight(Alliance.WHITE, 62));
		builder.setPiece(new Rook(Alliance.WHITE, 63));
		// white to move
		builder.setMoveMaker(Alliance.WHITE);
		
		return builder.build();
	}
	
	public WhitePlayer whitePlayer() {
		return this.whitePlayer;
	}
	
	public BlackPlayer blackPlayer() {
		return this.blackPlayer;
	}
	
	public Player currentPlayer() {
		return this.currentPlayer;
	}
	
	public Piece getPiece(final int coordinate) {
		return this.boardConfig.get(coordinate);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			final String tileText = prettyPrint(this.boardConfig.get(i));
			sb.append(String.format("%3s", tileText));
			if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	private static String prettyPrint(final Piece piece) {
		if (piece != null) {
			return piece.getPieceAlliance().isBlack() ?
					piece.toString().toLowerCase() : piece.toString();
		}
		return "-";
	}

	public static class Builder {
		
		Map<Integer, Piece> boardConfig;
		Alliance nextMoveMaker;
		Pawn enPassantPawn;
		Move transitionMove;
		
		public Builder() {
			this.boardConfig = new HashMap<Integer, Piece>(32, 1.0f);
		}
		
		public Builder setPiece(final Piece piece) {
			this.boardConfig.put(piece.getPiecePosition(), piece);
			return this;
		}
		
		public Builder setMoveMaker(final Alliance nextMoveMaker) {
			this.nextMoveMaker = nextMoveMaker;
			return this;
		}
		
		public Board build() {
			return new Board(this);
		}

		public Builder setEnPassantPawn(Pawn enPassantPawn) {
			this.enPassantPawn = enPassantPawn;
			return this;
			
		}
		
		public Builder setMoveTransition(final Move transitionMove) {
			this.transitionMove = transitionMove;
			return this;
		}
		
	}

}
