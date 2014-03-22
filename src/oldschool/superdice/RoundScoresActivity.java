package oldschool.superdice;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
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

public class RoundScoresActivity extends Activity
{
	private TableLayout mTableLayout;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_round_scores);
		mTableLayout = (TableLayout) findViewById(R.id.roundScoreTable);
		populateUserTable();
	}

	private void populateUserTable()
	{
		ArrayList<User> users = (ArrayList<User>) getIntent().getSerializableExtra("users");
		for (User user : users)
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
			mTableLayout.addView(row);
		}
	}

	private void formatText(TextView textView)
	{
		textView.setTextSize(getResources().getDimension(R.dimen.default_font_size)/2);
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
