package com.ackermansoftware.targetpractice;

import com.ackermansoftware.targetpractice.gameengine.GameObject;

public class DifficultyManager extends GameObject {

	private int difficultyChangeThreshold = 50;
	private static final int difficultyIncrement = 50;

	private static final int winThreshold = 100;

	@Override
	public void think(GameState state) {
		if (state.score >= winThreshold) {
			state.gameListener.gameWon(state.score);
		}
		if (state.score >= difficultyChangeThreshold) {
			state.addTarget = true;
			difficultyChangeThreshold += difficultyIncrement;
		}
	}

}
