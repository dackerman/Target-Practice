package com.ackermansoftware.targetpractice.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.ackermansoftware.targetpractice.R;

public class TitleActivity extends Activity {

	@Override
	// Touch anywhere and Go to the game.
	public boolean onTouchEvent(MotionEvent event) {
		Intent gameIntent = new Intent();
		gameIntent.setClassName("com.ackermansoftware.targetpractice", "com.ackermansoftware.targetpractice.activities.TargetPractice");
		startActivity(gameIntent);
		finish();
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_screen);
	}

}
