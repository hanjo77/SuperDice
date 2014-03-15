package oldschool.superdice;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * The title activity called when the app is started up.
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class MainActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
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
	public void startDiceAnimation(View view)
	{
		Intent intent = new Intent(this, DiceAnimationActivity.class);
		stopService(getIntent());
		startActivity(intent);
	}

}
