package oldschool.superdice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

/**
 * The dice renderer that controls the OpenGL animation logic.
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

class DiceRenderer implements GLSurfaceView.Renderer
{
	/**
	 * The parent activity.
	 */
	private DiceAnimationActivity context;
	/**
	 * An ArrayList of all the dice used.
	 */
	private ArrayList<Die> mDice = new ArrayList<Die>();
	/**
	 * The distance between the dice.
	 */
	final float dist = 3.3f;
	/**
	 * Defines if a dice roll is finished.
	 */
	private boolean isFinished = true;
	/**
	 * Defines whether the toast message on the parent activity shall be updated.
	 */
	private boolean doUpdateToast = false;

	/**
	 * Instantiates a dice renderer object with one die
	 * 
	 * @param context The context
	 */
	public DiceRenderer(Context context)
	{

		this.context = (DiceAnimationActivity) context;
		this.setDice(1);
	}

	/**
	 * Instantiates a dice renderer object with multiple dice
	 *
	 * @param context The context
	 * @param diceCount Number of dice
	 */
	public DiceRenderer(Context context, int diceCount)
	{

		this.context = (DiceAnimationActivity) context;
		this.setDice(diceCount);
	}

	/**
	 * Initializes the dice ArrayList
	 * 
	 * @param count number of dice
	 */
	private void setDice(int count)
	{
		for (int i = 0; i < count; i++)
		{
			mDice.add(new Die());
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		// Update the scene
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glEnable(GL10.GL_TEXTURE_2D);

		float posY = 0.0f;

		if (mDice.size() == 2)
		{
			posY = dist/-3.8f;
		}

		isFinished = true;

		gl.glTranslatef(0.0f, posY, -8.5f);

		for (int i = 0; i < mDice.size(); i++)
		{
			gl.glPushMatrix();
			Die die = mDice.get(i);
			die.rotate();
			gl.glTranslatef(0.0f, posY, 0.0f);
			die.draw(gl);

			if (die.isNotReady())
			{
				isFinished = false;
			}

			if (mDice.size() == 2)
			{

				posY += dist;
			}

			gl.glPopMatrix();
		}

		// If all the dice are ready, the result should be sent to the activity.
		if (isFinished && doUpdateToast)
		{
			context.toastNumber();
			doUpdateToast = false;
			for (Die die : mDice)
			{
				die.setReady(true);
			}
		}

		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// Initialize the scene
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		gl.glMatrixMode(GL10.GL_PROJECTION);

		float[] ambient = {0.1f, 1, 1, 1};
		float[] position = {0, 0, 0, -10};
		float[] direction = {0, 1, 0};

		gl.glEnable(GL10.GL_LIGHT1);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, position, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION, direction, 0);
		gl.glLightf(GL10.GL_LIGHT1, GL10.GL_SPOT_CUTOFF, 30.0f);

		for (Die die : mDice)
		{
			die.loadGLTexture(gl, context);
		}

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);


		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
				GL10.GL_NICEST);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		// Handles device rotation, possibly used again later...
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	/**
	 * Gets the array of results thrown
	 * 
	 * @return int Array of results thrown.
	 */
	public int[] getNumber()
	{
		int[] number = new int[mDice.size()];
		for (int i = 0; i < mDice.size(); i++)
		{
			number[i] = mDice.get(i).getNumber();
		}
		return number;
	}

	/**
	 * Triggers the actual dice rolling animation.
	 * 
	 * @param dirs Array of float values for x, y and z axis rotation angles
	 */
	public void rollDice(float[] dirs)
	{
		if (isFinished)
		{
			doUpdateToast = true;
			for (int i = 0; i < mDice.size(); i++)
			{
				Die die = mDice.get(i);
				die.rotate(dirs[(i % 3)], dirs[(i + 1) % 3], dirs[(i + 2) % 3]);
				die.setRolling(true);
				die.setReady(false);
			}
		}
	}
}