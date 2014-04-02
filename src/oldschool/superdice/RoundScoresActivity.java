package oldschool.superdice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * The title activity called when the app is started up.
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class RoundScoresActivity extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_round_scores);
		ListView listView = (ListView) findViewById(R.id.roundScoreTable);

		ArrayList users = (ArrayList) getIntent().getSerializableExtra("users");

		assert users != null;
		User[] userArray = new User[users.size()];
		int i = 0;
		for (Object user : users)
		{
			userArray[i] = (User) user;
			i++;
		}
		ArrayAdapter<User> adapter = new UserScoresArrayAdapter(this, userArray, false);
		listView.setAdapter(adapter);
	}

	/**
	 * Starts the dice animation activity, called by the "play" button.
	 *
	 * @param view The view
	 */
	public void closeView(View view)
	{
		finish();
	}

}
