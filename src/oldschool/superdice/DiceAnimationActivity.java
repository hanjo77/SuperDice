package oldschool.superdice;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The dice animation activity displaying and controlling the OpenGL animation
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class DiceAnimationActivity extends Activity implements SensorEventListener
{
	private SensorManager mSensorManager;
	private DiceRenderer mDiceRenderer;
	private TextView mScoreTextView;
	private int score = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Prepare view
		setContentView(R.layout.activity_dice_animation);
		TextView textView = (TextView) findViewById(R.id.titleText);
		textView.setText(getResources().getString(R.string.game_title));
		mScoreTextView = (TextView) findViewById(R.id.scoreText);

		// Set up the dice renderer and add to it's placeholder
		mDiceRenderer = new DiceRenderer(this);
		GLSurfaceView view = new GLSurfaceView(this);
		view.setRenderer(mDiceRenderer);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.renderContainer);
		layout.addView(view);

		// Set up gesture controls for non-shaky people
		view.setOnTouchListener(new DiceGestureListener(DiceAnimationActivity.this));

		// Init sensor
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
			if (accelerationSquareRoot >= 4) //
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

					Toast.makeText(getApplicationContext(), getResources().getText(R.string.you_thrown_a) + " " + number, Toast.LENGTH_SHORT).show();
				}
				score += total;
				mScoreTextView.setText(score+"");
			}
		});
	}

	public DiceRenderer getDiceRenderer()
	{
		return mDiceRenderer;
	}
}