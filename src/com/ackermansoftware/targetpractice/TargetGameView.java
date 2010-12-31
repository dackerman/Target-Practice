package com.ackermansoftware.targetpractice;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ackermansoftware.targetpractice.activities.TargetPractice;
import com.ackermansoftware.targetpractice.gameengine.DisableableAnimation;
import com.ackermansoftware.targetpractice.gameengine.GameObject;

public class TargetGameView extends View {
	private static final int MISS_PENALTY = -4;

	static final int FRAME_RATE = 60;

	boolean isStopped = false;

	ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

	private GameState gameState;

	final long millisBetweenFrames = (long) (1000.0 / FRAME_RATE);

	DisableableAnimation hitText;
	DisableableAnimation missText;

	private TargetPractice mainGame;

	// Main function to start the draw loop
	public void start(TargetPractice mainGame) {
		Target t = new Target();
		this.mainGame = mainGame;
		gameObjects.add(t);
		missText = new DisableableAnimation(R.drawable.miss);
		missText.x = 20.0;
		missText.y = 20.0;
		gameObjects.add(missText);
		hitText = new DisableableAnimation(R.drawable.hit);
		hitText.x = 20.0;
		hitText.y = 20.0;
		gameObjects.add(hitText);
		gameObjects.add(new DifficultyManager());
		update();
	}

	// Method for drawing in a while loop without busy waiting.
	// This class will continually call the draw class at the specifed
	// framerate.
	RedrawHandler redrawHandler = new RedrawHandler();

	class RedrawHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			TargetGameView.this.update();
			TargetGameView.this.invalidate();
		}

		public void sleepUntilNextFrame() {
			this.removeMessages(0);
			if (!isStopped)
				sendMessageDelayed(obtainMessage(0), millisBetweenFrames);
		}
	};


	// Do the actual drawing of the targets.
	@Override
	public void onDraw(Canvas canvas) {
		if (gameState == null) {
			initializeGameState();
		}
		Resources res = getResources();
		synchronized (gameObjects) {
			for (GameObject obj : gameObjects) {
				obj.render(canvas, res);
			}
		}
	}

	private void initializeGameState() {
		gameState = new GameState(new Rect(0, 0, this.getWidth(), this.getHeight()));
		gameState.missAnim = missText;
		gameState.hitAnim = hitText;
		gameState.gameListener = mainGame;
	}

	// Update the position of everything and keep score.
	public void update() {
		synchronized (gameObjects) {
			// If we hit the last target, then make a new random one.
			if (gameState != null) {
				if (gameState.addTarget) {
					gameState.addTarget = false;
					gameObjects.add(new Target());
				}
				for (GameObject obj : gameObjects) {
					obj.think(gameState);
				}
			}
		}
		redrawHandler.sleepUntilNextFrame();
	}

	public void onTouch(MotionEvent e) {
		synchronized (gameObjects) {
			boolean wasHit = false;
			for (GameObject obj : gameObjects) {
				wasHit = wasHit || obj.processInput(e);
			}
			// If no targets registered a hit, penalize the player.
			if (!wasHit) {
				gameState.score += MISS_PENALTY;
				gameState.missAnim.enable();
			}
		}
	}

	// Basic Constructors.
	public TargetGameView(Context context) {
		super(context);
		init();
	}

	public TargetGameView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public void init() {
		
	}

	public long calculateScore() {
		return gameState.score;
	}

	// Convenience methods for debugging
	static final void D(String s) {
		Log.d("TargetGameView", s);
	}

	static final void W(String s) {
		Log.w("TargetGameView", s);
	}

	static final void E(String s) {
		Log.e("TargetGameView", s);
	}

	public void stop() {
		isStopped = true;
	}

}
