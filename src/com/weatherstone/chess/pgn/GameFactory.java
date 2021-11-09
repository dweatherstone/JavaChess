package com.weatherstone.chess.pgn;

public class GameFactory {
	
	public static Game createGame(final PGNGameTags tags, final String gameText, final String outcome) {
		try {
			return new ValidGame(tags, PGNUtils.processMoveText(gameText), outcome);
		} catch(final ParsePGNException e) {
			return new InvalidGame(tags, gameText, outcome);
		}
	}

}
