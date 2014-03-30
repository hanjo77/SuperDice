package oldschool.superdice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * The title activity called when the app is started up.
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class MainActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		TextView textView = (TextView)findViewById(R.id.titleText);
		textView.setText("Super Dice!");
		return true;
	}

	/**
	 * Starts the dice animation activity, called by the "play" button.
	 *
     * @param view The view
     */
	public void startSelectTargetScore(View view)
	{
		Intent intent = new Intent(this, SelectUsersActivity.class);
    	stopService(getIntent());
		startActivity(intent);
	}

}
