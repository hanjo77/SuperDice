package oldschool.superdice;

import android.app.Activity;
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
import android.view.*;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * The dice animation activity displaying and controlling the OpenGL animation
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class DiceAnimationActivity extends Activity implements SensorEventListener
{
	private ArrayList<User> mUsers;
	private SensorManager mSensorManager;
	private DiceRenderer mDiceRenderer;
	private TextView mScoreTextView;
	private User mCurrentUser;
	private int mCurrentUserIndex = 0;
	private int mTargetScore;
	private boolean mCanRollDice = false;
	private boolean mUserSwitched = true;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mUsers = (ArrayList<User>) getIntent().getSerializableExtra("users");
		mTargetScore = getIntent().getIntExtra("targetscore", 10);
		float[][] diceRotations = new float[][]{{}};
		if (savedInstanceState != null) {

			mCurrentUserIndex = savedInstanceState.getInt("currentUser", 0);
			mCanRollDice = savedInstanceState.getBoolean("canRollDice");
			mUserSwitched = savedInstanceState.getBoolean("userSwitched");
			diceRotations = new float[][]{savedInstanceState.getFloatArray("diceRotations")};
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

		mCurrentUser = mUsers.get(mCurrentUserIndex);

		// Prepare view
		setContentView(R.layout.activity_dice_animation);
		TextView textView = (TextView) findViewById(R.id.titleText);
		textView.setText(getResources().getString(R.string.game_title).replace("[NAME]", mCurrentUser.getName()));
		mScoreTextView = (TextView) findViewById(R.id.scoreText);
		mScoreTextView.setText((mCurrentUser.getTotalScore()+mCurrentUser.getRoundScore())+"");

		// Set up the dice renderer and add to it's placeholder
		mDiceRenderer = new DiceRenderer(this, diceRotations);
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
		askForStart();
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
				intent.putExtra("users", mUsers);
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
			if (accelerationSquareRoot >= 2 && mCanRollDice) //
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

		SavedInstanceState.putInt("currentUser", mCurrentUserIndex);
		SavedInstanceState.putBoolean("canRollDice", mCanRollDice);
		SavedInstanceState.putBoolean("userSwitched", mUserSwitched);
		SavedInstanceState.putFloatArray("diceRotations", mDiceRenderer.getDiceRotations()[0]);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		mCurrentUserIndex = savedInstanceState.getInt("currentUser");
		mCanRollDice = savedInstanceState.getBoolean("canRollDice");
		mUserSwitched = savedInstanceState.getBoolean("userSwitched");
		mDiceRenderer.setDiceRotations(new float[][]{savedInstanceState.getFloatArray("diceRotations")});
		if (!mCanRollDice)
		{
			if (mUserSwitched)
			{
				askForStart();
			}
			else
			{
				finishRoll();
			}
		}
	}

	/**
	 * Handles the end of a dice roll.
	 */
	public void finishRoll()
	{
		mCanRollDice = false;
		mUserSwitched = false;
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				int number = mDiceRenderer.getNumber()[0];
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiceAnimationActivity.this);

				// set title
				alertDialogBuilder.setTitle(getResources().getText(R.string.you_rolled_a) + " " + number);
				User nextUser;
				if (mCurrentUserIndex >= mUsers.size()-1) {

					nextUser = mUsers.get(0);
				}
				else {

					nextUser = mUsers.get(mCurrentUserIndex+1);
				}

				if (number > 1) {
					mCurrentUser.setRoundScore(mCurrentUser.getRoundScore()+number);
					if (mCurrentUser.getTotalScore() + mCurrentUser.getRoundScore() >= mTargetScore)
					{
						mCurrentUser.setTotalScore(mCurrentUser.getTotalScore() + mCurrentUser.getRoundScore());
						Intent intent = new Intent(DiceAnimationActivity.this, GameOverActivity.class);
						intent.putExtra("users", mUsers);
						intent.putExtra("targetscore", mTargetScore);
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
								mCanRollDice = true;
							}
						});
						alertDialogBuilder.setNegativeButton(getResources().getText(R.string.button_pass_text), new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								mCurrentUser.setTotalScore(mCurrentUser.getTotalScore() + mCurrentUser.getRoundScore());
								mCurrentUser.setRoundScore(0);
								switchUser();
							}
						});
						mScoreTextView.setText((mCurrentUser.getTotalScore() + mCurrentUser.getRoundScore()) + "");
					}
				}
				else {
					mCurrentUser.setRoundScore(0);
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
				if (messageView != null)
				{
					messageView.setGravity(Gravity.CENTER);
				}
			}
		});
	}

	/**
	 * Opens a dialog to request the user to roll the die.
	 */
	private void askForStart()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DiceAnimationActivity.this);

				// set title
				alertDialogBuilder.setTitle(getResources().getText(R.string.ask_for_start_title).toString().replace("[NAME]", mCurrentUser.getName()));
				alertDialogBuilder.setMessage(getResources().getText(R.string.ask_for_start_text).toString());
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton(getResources().getText(R.string.button_confirm_text), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
						mCanRollDice = true;
					}
				});

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

	/**
	 * Switches to the nest user
	 */
	private void switchUser()
	{
		mCurrentUserIndex++;
		if (mCurrentUserIndex >= mUsers.size()) {
			mCurrentUserIndex = 0;
		}
		mCurrentUser = mUsers.get(mCurrentUserIndex);
		TextView textView = (TextView) findViewById(R.id.titleText);
		textView.setText(getResources().getString(R.string.game_title).replace("[NAME]", mCurrentUser.getName()));
		mScoreTextView.setText((mCurrentUser.getTotalScore()+mCurrentUser.getRoundScore())+"");
		mUserSwitched = true;
		askForStart();
	}

	/**
	 * Returns the DiceRenderer instance
	 *
	 * @return DiceRenderer instance
	 */
	public DiceRenderer getDiceRenderer()
	{
		return mDiceRenderer;
	}
}