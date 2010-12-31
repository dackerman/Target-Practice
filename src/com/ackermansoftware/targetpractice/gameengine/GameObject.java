package com.ackermansoftware.targetpractice.gameengine;

import com.ackermansoftware.targetpractice.GameState;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class GameObject {
	private static GameTime time = new SystemTime();

	public boolean processInput(MotionEvent e) {
		return false;
	}

	public void think(GameState state) {}

	public void render(Canvas canvas, Resources resources) {}

	public void setTimer(GameTime timer) {
		GameObject.time = timer;
	}

	protected long getTime() {
		return time.getMilliTime();
	}

	public interface GameTime {
		public long getNanoTime();
		public long getMilliTime();
	}

	public static class SystemTime implements GameTime {

		@Override
		public long getMilliTime() {
			return getNanoTime() / 1000000;
		}

		@Override
		public long getNanoTime() {
			return System.nanoTime();
		}

	}

}
