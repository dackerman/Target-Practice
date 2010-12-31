package com.ackermansoftware.targetpractice.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ackermansoftware.targetpractice.R;

public class WonGameActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.won_game);
		long theScore = getIntent().getExtras().getLong("score");
		double secondsElapsed = getIntent().getExtras().getLong("time") / 1000000000.0;

		TextView score_result = (TextView) this.findViewById(R.id.score_result);
		score_result.setText("Your score was " + theScore + "! :D");
		
		TextView time_result = (TextView) this.findViewById(R.id.time_result);
		time_result.setText("And it took you " + secondsElapsed + " seconds to do it!");
		
		Button finished = (Button) this.findViewById(R.id.finished_button);
		finished.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		finish();
	}

}
