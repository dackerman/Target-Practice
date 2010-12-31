package com.ackermansoftware.targetpractice;

import com.ackermansoftware.targetpractice.gameengine.DisableableAnimation;
import com.ackermansoftware.targetpractice.gameengine.GameListener;

import android.graphics.Rect;

public class GameState {
	private Rect screenBounds;
	public long score = 0;
	public DisableableAnimation missAnim;
	public DisableableAnimation hitAnim;
	public boolean addTarget = false;
	public GameListener gameListener;

	public GameState(Rect screenBounds) {
		this.screenBounds = screenBounds;
	}

	public Rect getScreenBounds() {
		return screenBounds;
	}
}
