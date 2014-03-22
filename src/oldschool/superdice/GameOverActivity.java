package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * The title activity called when the app is started up.
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class GameOverActivity extends Activity
{
	private ArrayList<User> mUsers;
	private int mTargetScore;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mUsers = (ArrayList<User>) getIntent().getSerializableExtra("users");
		mTargetScore = getIntent().getIntExtra("targetscore", 10);
		User winner = getWinner();
		winner.setGamesWon(winner.getGamesWon() + 1);
		setContentView(R.layout.activity_game_over);
		TextView textView = (TextView)findViewById(R.id.userNameText);
		textView.setText(winner.getName());
		TableLayout tableLayout = (TableLayout)findViewById(R.id.roundScoreTable);
		populateUserTable(tableLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private User getWinner()
	{
		User winner = null;
		int maxScore = 0;
		for (User user : mUsers)
		{
			if (user.getTotalScore() > maxScore)
			{
				winner = user;
				maxScore = user.getTotalScore();
			}
		}
		return winner;
	}

	private void populateUserTable(TableLayout tableLayout)
	{
		try
		{
			for (User user : mUsers)
			{
				TableRow row= new TableRow(this);
				TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
				row.setLayoutParams(lp);
				int padding = (int)getResources().getDimension(R.dimen.activity_vertical_margin);
				row.setPadding(padding, padding, padding, padding);
				TextView textName = new TextView(this);
				textName.setText(user.getName());
				formatText(textName);
				TextView textScore = new TextView(this);
				textScore.setPadding(padding, 0, 0, 0);
				textScore.setGravity(Gravity.RIGHT);
				textScore.setText("" + user.getTotalScore());
				formatText(textScore);
				row.addView(textName);
				row.addView(textScore);
				tableLayout.addView(row);
			}
		}
		catch (NullPointerException e)
		{
			Log.e("USERS", "No users passed to GameOverActivity");
		}
	}

	private void resetUserScores()
	{
		for (User user : mUsers)
		{
			user.setTotalScore(0);
			user.setRoundScore(0);
		}
	}

	private void formatText(TextView textView)
	{
		textView.setTextSize(getResources().getDimension(R.dimen.default_font_size)/4);
	}

	/**
	 * Finishes this activity and Starts the dice animation activity, called by the "again" button.
	 *
	 * @param view The view
	 */
	public void startDiceAnimation(View view)
	{
		resetUserScores();
		Intent intent = new Intent(this, DiceAnimationActivity.class);
		intent.putExtra("users", mUsers);
		intent.putExtra("targetscore", mTargetScore);
		finish();
		startActivity(intent);
	}


	/**
	 * Finishes the activity and returns to home-screen.
	 *
	 * @param view The view
	 */
	public void exit(View view)
	{
		finish();
	}

}
