package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * The title activity called when the app is started up.
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class GameOverActivity extends Activity
{
	private User[] mUsers;
	private int mTargetScore;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		ArrayList<User> users = (ArrayList<User>) getIntent().getSerializableExtra("users");
		mUsers = new User[users.size()];
		int i = 0;
		for (User user : users)
		{
			mUsers[i] = user;
			i++;
		}
		mTargetScore = getIntent().getIntExtra("targetscore", 10);
		User winner = getWinner();
		winner.setGamesWon(winner.getGamesWon() + 1);
		setContentView(R.layout.activity_game_over);
		TextView textView = (TextView)findViewById(R.id.userNameText);
		textView.setText(winner.getName());
		ListView listView = (ListView)findViewById(R.id.roundScoreTable);
		ArrayAdapter<User> adapter = new UserScoresArrayAdapter(this, mUsers);
		listView.setAdapter(adapter);
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
		int i = 0;
		for (User user : mUsers)
		{
			i++;
			if (user.getTotalScore() > maxScore)
			{
				winner = user;
				maxScore = user.getTotalScore();
			}
		}
		return winner;
	}

	private void resetUserScores()
	{
		for (User user : mUsers)
		{
			user.setTotalScore(0);
			user.setRoundScore(0);
		}
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
		ArrayList<User> users = new ArrayList<User>();
		for (User user : mUsers)
		{
			users.add(user);
		}
		intent.putExtra("users", users);
		intent.putExtra("targetscore", mTargetScore);
		finish();
		startActivity(intent);
	}

	public void synchronize(View vie)
	{
		new Thread(new Runnable()
		{
			public void run()
			{

				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://hanjo.no-ip.biz/superdice/save.php");

				try
				{
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("users", getUsersJSON()));
					System.out.println(getUsersJSON());

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpclient.execute(httppost);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	private String getUsersJSON()
	{
		String json = "[";
		for (User user : mUsers)
		{
			if (json.length() > 1)
			{
				json += ",";
			}
			json += "{"
				+ "\"name\":\"" + user.getName() + "\","
				+ "\"games_won\":" + user.getGamesWon()
				+ "}";
		}
		return json + "]";
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
