package com.ackermansoftware.targetpractice.gameengine;

public interface GameListener {
	public void updateScore(long newScore);

	public void gameWon(long endScore);
}
