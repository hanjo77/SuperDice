package oldschool.superdice;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class DiceAnimationActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private DiceRenderer mDiceRenderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Go fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mDiceRenderer = new DiceRenderer(this, 2);
		GLSurfaceView view = new GLSurfaceView(this);
		view.setRenderer(mDiceRenderer);
		setContentView(view);

		// Init sensor
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}

	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;

		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		if (accelationSquareRoot >= 4) //
		{
			mDiceRenderer.rollDice(new float[]{x, y, z});
		}
	}

	public void toastNumber() {
		runOnUiThread(new Runnable() {
			public void run() {
				String number = "";
				int[] numbers = mDiceRenderer.getNumber();
				if (numbers.length == 2 && numbers[0] == numbers[1]) {
					number = "double " + numbers[0] + "!";
				}
				else {
					for (int i = 0; i < numbers.length; i++)
					{
						if (i > 0)
						{
							if (i < numbers.length-1)
							{
								number += ", a ";
							}
							else if (i == numbers.length-1)
							{
								number += " and a ";
							}
						}
						number += numbers[i];
					}
				}
				Toast.makeText(getApplicationContext(), "You've thrown a " + number, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
}