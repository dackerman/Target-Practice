package com.ackermansoftware.targetpractice.gameengine;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class DisableableAnimation extends GameObject implements AnimationSequenceListener {
	public double x = 0;
	public double y = 0;
	private AnimationSequence anim = new AnimationSequence(1);
	private boolean enabled = false;

	public DisableableAnimation(int drawable) {
		anim.addFrame(new AnimationFrame(drawable, 200));
		anim.looping = false;
		anim.returnToBeginning = true;
		anim.setAnimationListener(this);
	}

	public void enable() {
		enabled = true;
		anim.startAnimation(getTime());
	}

	public void disable() {
		enabled = false;
		anim.resetAnimation();
	}

	@Override
	public void render(Canvas canvas, Resources resources) {
		if (enabled) {
			AnimationFrame frame = anim.getFrame(getTime());
			Drawable bitmap = resources.getDrawable(frame.drawable);
			bitmap.setBounds((int) x, (int) y, (int) (x + bitmap.getIntrinsicWidth()), (int) (y + bitmap.getIntrinsicHeight()));
			bitmap.draw(canvas);
		}
	}

	@Override
	public void animationEnd() {
		disable();
	}

	@Override
	public void animationReset() {}

	@Override
	public void animationStart() {}

}
