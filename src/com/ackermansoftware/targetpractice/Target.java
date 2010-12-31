package com.ackermansoftware.targetpractice;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.ackermansoftware.targetpractice.gameengine.AnimationFrame;
import com.ackermansoftware.targetpractice.gameengine.AnimationSequence;
import com.ackermansoftware.targetpractice.gameengine.AnimationSequenceListener;
import com.ackermansoftware.targetpractice.gameengine.GameObject;

public class Target extends GameObject implements AnimationSequenceListener {
	private static final int HIT_BONUS = 10;

	private AnimationSequence breakAnimation;
	private double x = 20.0;
	private double y = 20.0;
	private double vx = 60.0;
	private double vy = 60.0;
	private double w = 100.0;
	private double h = 100.0;
	private boolean isHit = false;
	private boolean missed = false;
	private static final double MAX_SPEED = 150.0;
	private long lastThinkTimestamp = -1;
	private Rect gameBounds = null;

	public Target() {
		breakAnimation = new AnimationSequence(4);
		breakAnimation.addFrame(new AnimationFrame(R.drawable.target, 0));
		breakAnimation.addFrame(new AnimationFrame(R.drawable.target_hit1, 100));
		breakAnimation.addFrame(new AnimationFrame(R.drawable.target_hit2, 50));
		breakAnimation.addFrame(new AnimationFrame(R.drawable.target_hit3, 50));
		breakAnimation.returnToBeginning = true;
		breakAnimation.looping = false;
		breakAnimation.setAnimationListener(this);
		double scale = Math.random() * 1.8;
		w = w * scale + 20;
		h = h * scale + 20;
	}

	public Target(double ix, double iy, double ivx, double ivy, double iw,
			double ih) {
		this();
		x = ix;
		y = iy;
		vx = ivx;
		vy = ivy;
		w = iw;
		h = ih;
	}

	@Override
	public void think(GameState state) {
		gameBounds = state.getScreenBounds();
		boolean outsideXBoundary = gameBounds.left > x || gameBounds.right < x;
		boolean outsideYBoundary = gameBounds.top > y || gameBounds.bottom < y;

		// If the target escaped the screen, then reset it and decrement score.
		if (outsideXBoundary || outsideYBoundary) {
			state.score -= 20;
			updateScore(state);
			Log.w("outside boundary", String.format("x:%s y:%s left:%s right:%s top:%s bottom:%s", x, y, gameBounds.left, gameBounds.right, gameBounds.top,
					gameBounds.bottom));
			Log.w("score", "" + state.score);
			setNewRandomCoords(gameBounds.width(), gameBounds.height());
			breakAnimation.resetAnimation();
			return;
		}

		// If the user tried to hit us on the input pass, then adjust the score.
		if (isHit) {
			isHit = false;
			if (missed) {
				Log.e("hit miss", "Missed!");
				// state.score += MISS_PENALTY;
				// state.missAnim.enable();
			} else {
				Log.e("hit miss", "Hit!");
				state.score += HIT_BONUS;
				state.hitAnim.enable();
				breakAnimation.startAnimation(getTime());
			}
			updateScore(state);
			w(String.format("x:%s y:%s left:%s right:%s top:%s bottom:%s", x, y, gameBounds.left, gameBounds.right, gameBounds.top, gameBounds.bottom));
		}

		move();
		lastThinkTimestamp = getTime();
	}

	@Override
	public void animationEnd() {
		setNewRandomCoords(gameBounds.width(), gameBounds.height());
	}

	private void updateScore(GameState state) {
		long score = state.score;
		if (state.gameListener != null) {
			state.gameListener.updateScore(score);
		}
	}

	@Override
	public boolean processInput(MotionEvent e) {
		isHit = true;
		double tx = e.getX();
		double ty = e.getY();
		
		// If the touch is within all boundaries, it was a hit.
		if (tx > x && tx < x + w && ty > y && ty < y + h) {
			d(String.format("x:%s y:%s tx:%s ty:%s h:%s w:%s", x, y, tx, ty, h, w));
			missed = false;
			return true;
		} else {
			missed = true;
			return false;
		}

	}

	public void reset(double newx, double newy, double newvx, double newvy) {
		x = newx;
		y = newy;
		vx = newvx;
		vy = newvy;
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public void move() {
		if (lastThinkTimestamp < 0)
			lastThinkTimestamp = getTime();
		long millisElapsed = getTime() - lastThinkTimestamp;
		x += vx * millisElapsed / 1000.0;
		y += vy * millisElapsed / 1000.0;
	}
	
	public boolean intersects(double tx, double ty) {
		double xdist = Math.abs(x - tx + w / 2.0);
		double ydist = Math.abs(y - ty + h / 2.0);

		w("xdist: " + xdist);
		w("ydist: " + ydist);

		// Return whether the provided point is within this target's bounds.
		if (xdist < 0.5 * w && ydist < 0.5 * h) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hit() {
		return isHit;
	}


	@Override
	public void render(Canvas canvas, Resources resources) {
		AnimationFrame frame = breakAnimation.getFrame(getTime());
		Drawable bitmap = resources.getDrawable(frame.drawable);
		bitmap.setBounds((int) x, (int) y, (int) (x + w), (int) (y + h));
		bitmap.draw(canvas);
	}

	private void setNewRandomCoords(int width, int height) {
		double x = getRandLocation(width, 0.5);
		double y = getRandLocation(height, 0.5);

		double velocityx = getRandVelocity();
		double velocityy = getRandVelocity();

		// The idea here is to give the user a chance to hit the target. By default,
		// we will move down and to the left. If our position puts us past the
		// halfway mark on either axis, make the velocity the opposite so we don't
		// have a situation where the target spawns at the edge and immediately
		// drops off, making it nearly impossible to get a point.
		if (x > w / 2)
			velocityx *= -1;
		if (y > h / 2)
			velocityy *= -1;

		Log.w("Target", String.format("New coords are x:%f y:%f vx:%f vy:%f", x, y, velocityx, velocityy));
		reset(x, y, velocityx, velocityy);
	}

	private double getRandLocation(int bound, double percentageCovered) {
		double offset = (1 - percentageCovered) * 0.5;
		double x = Math.random() * bound * percentageCovered + (bound * offset);
		return x;
	}

	private double getRandVelocity() {
		double v = Math.random() * MAX_SPEED;
		return v;
	}

	@Override
	public void animationReset() {}

	@Override
	public void animationStart() {}

	private void d(String msg) {
	// Log.e("Target", msg);
	}

	private void w(String msg) {
	// Log.w("Target", msg);
	}

}
