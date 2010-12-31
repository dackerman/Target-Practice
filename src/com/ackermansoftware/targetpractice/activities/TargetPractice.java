package com.ackermansoftware.targetpractice.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.ackermansoftware.targetpractice.R;
import com.ackermansoftware.targetpractice.TargetGameView;
import com.ackermansoftware.targetpractice.gameengine.GameListener;

public class TargetPractice extends Activity implements OnTouchListener, GameListener {

	TargetGameView game;
	long initialTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		game = (TargetGameView) this.findViewById(R.id.game_window);
		game.setOnTouchListener(this);
		// Time the game - see how long it takes for them to win.
		initialTime = System.nanoTime();

	}

	// Start the game.
	@Override
	protected void onStart() {
		super.onStart();
		game.start(this);
	}

	// Stop simulating when the user leaves the app.
	@Override
	protected void onPause() {
		super.onPause();
		game.stop();
	}

	// Handle touch events, and have them processed by the game.
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// We only care about down events - otherwise we'd get too many events.
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return false;
		game.onTouch(event);
		return true;
	}

	// Update the score when some other object changes it.
	@Override
	public void updateScore(long newScore) {
		TextView status = (TextView) this.findViewById(R.id.status_bar);
		status.setText("Score: " + newScore);
	}

	@Override
	public void gameWon(long endScore) {
		long endTime = System.nanoTime();
		long nanoDiff = endTime - initialTime;
		game.stop();
		Intent winGame = new Intent();
		winGame.putExtra("time", nanoDiff);
		winGame.putExtra("score", endScore);
		winGame.setClassName("com.ackermansoftware.targetpractice", "com.ackermansoftware.targetpractice.activities.WonGameActivity");
		startActivity(winGame);
		finish();
	}

}