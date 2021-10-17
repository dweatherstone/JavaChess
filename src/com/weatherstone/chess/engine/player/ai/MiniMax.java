package com.weatherstone.chess.engine.player.ai;

import java.util.concurrent.atomic.AtomicLong;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.BoardUtils;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.board.MoveTransition;
import com.weatherstone.chess.engine.board.Move.MoveFactory;

public class MiniMax implements MoveStrategy {
	
	private final BoardEvaluator evaluator;
	private final int searchDepth;
	private long boardsEvaluated;
	private long executionTime;
	private FreqTableRow[] freqTable;
	private int freqTableIndex;
	
	public MiniMax(final int searchDepth) {
		this.evaluator = StandardBoardEvaluator.get();
		this.searchDepth = searchDepth;
		this.boardsEvaluated = 0;
	}

	@Override
	public Move execute(Board board) {
		final long startTime = System.currentTimeMillis();
		Move bestMove = MoveFactory.getNullMove();
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenvalue = Integer.MAX_VALUE;
		int currentValue;
		System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
		this.freqTable = new FreqTableRow[board.currentPlayer().getLegalMoves().size()];
		this.freqTableIndex = 0;
		int moveCounter = 1;
		final int numMoves = board.currentPlayer().getLegalMoves().size();
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final FreqTableRow row = new FreqTableRow(move);
				this.freqTable[this.freqTableIndex] = row;
				currentValue = board.currentPlayer().getAlliance().isWhite() ?
					min(moveTransition.getToBoard(), this.searchDepth - 1) :
					max(moveTransition.getToBoard(), this.searchDepth - 1);
				System.out.println("\t" + toString() + " analyzing move (" + moveCounter + "/" + numMoves + ") " + move +
						" scores " + currentValue + " " + this.freqTable[this.freqTableIndex]);
				this.freqTableIndex++;
				if (board.currentPlayer().getAlliance().isWhite() && currentValue > highestSeenValue) {
					highestSeenValue = currentValue;
					bestMove = move;
				} else if (board.currentPlayer().getAlliance().isBlack() && currentValue < lowestSeenvalue) {
					lowestSeenvalue = currentValue;
					bestMove = move;
				}
			} else {
				System.out.println("\t" + toString() + " can't execute move (" + moveCounter + "/" + numMoves + ") " + move);
			}
			moveCounter++;
		}
		
		this.executionTime = System.currentTimeMillis() - startTime;
		System.out.printf("%s SELECTS %s [#boards = %d time taken = %d ms, rate = %.1f\n", board.currentPlayer(), bestMove, this.boardsEvaluated, this.executionTime, (1000 * ((double)this.boardsEvaluated/this.executionTime)));
		long total = 0;
		for (final FreqTableRow row : this.freqTable) {
			if (row != null) {
				total += row.getCount();
			}
		}
		if (this.boardsEvaluated != total) {
			System.out.println("Something went wrong with the # of boards evaluated");
		}
		
		return bestMove;
	}
	
	public int min(final Board board, final int depth) {
		if (depth == 0) {
			this.boardsEvaluated++;
			this.freqTable[this.freqTableIndex].increment();
			return this.evaluator.evaluate(board, depth);
		}
		if (isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int lowestSeenValue = Integer.MAX_VALUE;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final int currentValue = max(moveTransition.getToBoard(), depth - 1);
				if (currentValue < lowestSeenValue) {
					lowestSeenValue = currentValue;
				}
			}
		}
		return lowestSeenValue;
	}
	
	public int max(final Board board, final int depth) {
		if (depth == 0) {
			this.boardsEvaluated++;
			this.freqTable[this.freqTableIndex].increment();
			return this.evaluator.evaluate(board, depth);
		}
		if (isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int highestSeenValue = Integer.MIN_VALUE;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final int currentValue = min(moveTransition.getToBoard(), depth - 1);
				if (currentValue > highestSeenValue) {
					highestSeenValue = currentValue;
				}
			}
		}
		return highestSeenValue;
	}
	
	private static boolean isEndGameScenario(final Board board) {
		return board.currentPlayer().isInCheckMate() || 
			   board.currentPlayer().isInStaleMate();
	}
	
	@Override
	public long getNumBoardsEvaluated() {
		return this.boardsEvaluated;
	}

	@Override
	public String toString() {
		return "MiniMax";
	}
	
	private static class FreqTableRow {
		
		private final Move move;
		private final AtomicLong count;
		
		FreqTableRow(final Move move) {
			this.count = new AtomicLong();
			this.move = move;
		}
		
		long getCount() {
			return this.count.get();
		}
		
		void increment() {
			this.count.incrementAndGet();
		}
		
		@Override
		public String toString() {
			return BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getCurrentCoordinate()) + 
					BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getDestinationCoordinate()) + " : " + this.count; 
		}
	}

}
