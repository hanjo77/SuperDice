package oldschool.superdice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * The dice animation activity displaying and controlling the OpenGL animation
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class DiceAnimationActivity extends BaseActivity implements SensorEventListener
{
	private ArrayList<User> users = new ArrayList<User>();
	private SensorManager mSensorManager;
	private DiceRenderer mDiceRenderer;
	private TextView mScoreTextView;
	private User currentUser;
	private int currentUserIndex = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		users = new ArrayList<User>();
		users.add(new User("Hanjo", 0, 0, 0));
		users.add(new User("Steff", 0, 0, 0));
		users.add(new User("Sädu", 0, 0, 0));
		currentUser = users.get(currentUserIndex);

		// Prepare view
		setContentView(R.layout.activity_dice_animation);
		TextView textView = (TextView) findViewById(R.id.titleText);
		textView.setText(getResources().getString(R.string.game_title).replace("[NAME]", currentUser.getName()));
		mScoreTextView = (TextView) findViewById(R.id.scoreText);

		// Set up the dice renderer and add to it's placeholder
		mDiceRenderer = new DiceRenderer(this);
		GLSurfaceView view = new GLSurfaceView(this);
		view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		view.setZOrderOnTop(true);
		view.setRenderer(mDiceRenderer);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.renderContainer);
		layout.addView(view);

		// Set up gesture controls for non-shaky people
		view.setOnTouchListener(new DiceGestureListener(DiceAnimationActivity.this));

		// Init sensor
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		// Check if the device got shaken and if yes, ask the renderer to roll the dice!
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			float[] values = event.values;

			float x = values[0];
			float y = values[1];
			float z = values[2];

			float accelerationSquareRoot = (x * x + y * y + z * z)
					/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
			if (accelerationSquareRoot >= 2) //
			{
				mDiceRenderer.rollDice(new float[]{x, y, z});
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	/**
	 * Display the toast message with the current result thrown.
	 */
	public void toastNumber()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				/*

				int total = 0;
				String number = "";
				int[] numbers = mDiceRenderer.getNumber();
				if (numbers.length == 2 && numbers[0] == numbers[1])
				{
					total += 2*numbers[0];
					number = "double " + numbers[0] + "!";
				}
				else
				{
					for (int i = 0; i < numbers.length; i++)
					{
						total += numbers[i];
						if (i > 0)
						{
							if (i < numbers.length - 1)
							{
								number += ", a ";
							} else if (i == numbers.length - 1)
							{
								number += " and a ";
							}
						}
						number += numbers[i];
					}
				}
				if (getApplicationContext() != null) {

					Toast.makeText(getApplicationContext(), getResources().getText(R.string.you_rolled_a) + " " + number, Toast.LENGTH_SHORT).show();
				}
				*/
				int number = mDiceRenderer.getNumber()[0];
				askForNextThrow(number);
			}
		});
	}

	public void askForNextThrow(int number) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiceAnimationActivity.this);

		// set title
		alertDialogBuilder.setTitle(getResources().getText(R.string.you_rolled_a) + " " + number);
		User nextUser;
		if (currentUserIndex >= users.size()-1) {

			nextUser = users.get(0);
		}
		else {

			nextUser = users.get(currentUserIndex+1);
		}

		if (number > 1) {
			currentUser.setRoundScore(currentUser.getRoundScore()+number);
			// set dialog message for successful round
			alertDialogBuilder.setMessage(getResources().getText(R.string.roll_again_text).toString().replace("[NAME]", nextUser.getName()));
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton(getResources().getText(R.string.button_roll_again_text), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			alertDialogBuilder.setNegativeButton(getResources().getText(R.string.button_pass_text), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{

					currentUser.setTotalScore(currentUser.getTotalScore() + currentUser.getRoundScore());
					currentUser.setRoundScore(0);
					switchUser();
				}
			});
			mScoreTextView.setText((currentUser.getTotalScore() + currentUser.getRoundScore()) + "");
		}
		else {
			currentUser.setRoundScore(0);
			// set dialog message for successful round
			alertDialogBuilder.setMessage(getResources().getText(R.string.round_finished_text).toString().replace("[NAME]", nextUser.getName()));
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton(getResources().getText(R.string.button_confirm_text), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			switchUser();
		}


		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

		// Must call show() prior to fetching text view
		TextView titleView = (TextView)alertDialog.findViewById(getResources().getIdentifier("alertTitle", "id", "android"));
		if (titleView != null) {
			titleView.setGravity(Gravity.CENTER);
		}
		TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
	}
	
	private void switchUser() {

		currentUserIndex++;
		if (currentUserIndex >= users.size()) {
			currentUserIndex = 0;
		}
		currentUser = users.get(currentUserIndex);
		TextView textView = (TextView) findViewById(R.id.titleText);
		textView.setText(getResources().getString(R.string.game_title).replace("[NAME]", currentUser.getName()));
		mScoreTextView.setText((currentUser.getTotalScore()+currentUser.getRoundScore())+"");
	}


	public DiceRenderer getDiceRenderer()
	{
		return mDiceRenderer;
	}
}