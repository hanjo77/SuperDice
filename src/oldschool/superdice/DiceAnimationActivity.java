package oldschool.superdice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * The dice animation activity displaying and controlling the OpenGL animation
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class DiceAnimationActivity extends BaseActivity implements SensorEventListener
{
	private ArrayList<User> users;
	private SensorManager mSensorManager;
	private DiceRenderer mDiceRenderer;
	private TextView mScoreTextView;
	private User currentUser;
	private int currentUserIndex = 0;
	private int targetScore;
	private boolean canRollDice = true;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		users = (ArrayList<User>) getIntent().getSerializableExtra("users");
		targetScore = getIntent().getIntExtra("targetscore", 10);
		if (savedInstanceState != null) {

			currentUserIndex = savedInstanceState.getInt("currentUser", 0);
			canRollDice = savedInstanceState.getBoolean("canRollDice", true);
		}

		if (android.os.Build.VERSION.SDK_INT <= 14 || ViewConfiguration.get(this).hasPermanentMenuKey())
		{
			// We hide the menu
			//set up notitle
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			//set up full screen
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		currentUser = users.get(currentUserIndex);

		// Prepare view
		setContentView(R.layout.activity_dice_animation);
		TextView textView = (TextView) findViewById(R.id.titleText);
		textView.setText(getResources().getString(R.string.game_title).replace("[NAME]", currentUser.getName()));
		mScoreTextView = (TextView) findViewById(R.id.scoreText);
		mScoreTextView.setText((currentUser.getTotalScore()+currentUser.getRoundScore())+"");

		// Set up the dice renderer and add to it's placeholder
		mDiceRenderer = new DiceRenderer(this);
		GLSurfaceView view = new GLSurfaceView(this);
		view.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
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

		menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				Intent intent = new Intent(DiceAnimationActivity.this, RoundScoresActivity.class);
				intent.putExtra("users", users);
				startActivity(intent);
				return false;
			}
		});
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
			if (accelerationSquareRoot >= 2 && canRollDice) //
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

	@Override
	protected void onSaveInstanceState(Bundle SavedInstanceState) {
		super.onSaveInstanceState(SavedInstanceState);

		SavedInstanceState.putInt("currentUser", currentUserIndex);
		SavedInstanceState.putBoolean("canRollDice", canRollDice);
		SavedInstanceState.putFloatArray("dicePositions", mDiceRenderer.getDiceRotations()[0]);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		currentUserIndex = savedInstanceState.getInt("currentUser");
		canRollDice = savedInstanceState.getBoolean("canRollDice");
		mDiceRenderer.setDiceRotations(new float[][]{ savedInstanceState.getFloatArray("dicePositions") });
		if (!canRollDice) {

			finishRoll();
		}
	}

	/**
	 * Handles the end of a dice roll.
	 */
	public void finishRoll()
	{
		canRollDice = false;
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				int number = mDiceRenderer.getNumber()[0];
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
					if (currentUser.getTotalScore() + currentUser.getRoundScore() >= targetScore)
					{
						currentUser.setTotalScore(currentUser.getTotalScore() + currentUser.getRoundScore());
						Intent intent = new Intent(DiceAnimationActivity.this, GameOverActivity.class);
						intent.putExtra("users", users);
						intent.putExtra("targetscore", targetScore);
						startActivity(intent);
						finish();
					}
					else
					{
						// set dialog message for successful round
						alertDialogBuilder.setMessage(getResources().getText(R.string.roll_again_text).toString().replace("[NAME]", nextUser.getName()));
						alertDialogBuilder.setCancelable(false);
						alertDialogBuilder.setPositiveButton(getResources().getText(R.string.button_roll_again_text), new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								dialog.cancel();
								canRollDice = true;
							}
						});
						alertDialogBuilder.setNegativeButton(getResources().getText(R.string.button_pass_text), new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								currentUser.setTotalScore(currentUser.getTotalScore() + currentUser.getRoundScore());
								currentUser.setRoundScore(0);
								switchUser();
								canRollDice = true;
							}
						});
						mScoreTextView.setText((currentUser.getTotalScore() + currentUser.getRoundScore()) + "");
					}
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
							canRollDice = true;
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
				if (messageView != null)
				{
					messageView.setGravity(Gravity.CENTER);
				}
			}
		});
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