package com.weatherstone.chess.engine.board;

public class MoveTransition {
	
	private final Board toBoard;
	private final Board fromBoard;
	private final Move move;
	private final MoveStatus moveStatus;
	
	public MoveTransition(final Board fromBoard,
						  final Board toBoard,
						  final Move move, 
						  final MoveStatus moveStatus) {
		this.fromBoard = fromBoard;
		this.toBoard = toBoard;
		this.move = move;
		this.moveStatus = moveStatus;
	}
	
	public MoveStatus getMoveStatus() {
		return this.moveStatus;
	}
	
	public Board getToBoard() {
		return this.toBoard;
	}
	
	public Board getFromBoard() {
		return this.fromBoard;
	}
	
	public Move getTransitionMove() {
		return this.move;
	}

}
