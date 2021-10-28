package com.weatherstone.chess.pgn;

import java.util.Collections;

public class InvalidGame extends Game {
	
	final String marlformedGameText;
	
	public InvalidGame(final PGNGameTags tags,
            		   final String malformedGameText,
            		   final String outcome) {
		super(tags, Collections.emptyList(), outcome);
		this.marlformedGameText = malformedGameText;
	}
	
	@Override
	public String toString() {
		return "Invalid Game " + this.tags;
	}

	@Override
	public boolean isValid() {
		return false;
	}

}
