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
	private final DiceAnimationActivity mContext;
	private ArrayList<Die> mDice = new ArrayList<Die>();
	private boolean mIsFinished = true;
	private boolean mDoUpdateToast = false;
	private final float mInitialRotation = 2;

	/**
	 * Instantiates a dice renderer object with one die
	 *
	 * @param context The context
	 */
	public DiceRenderer(Context context, float[][] rotations)
	{

		mContext = (DiceAnimationActivity) context;
		setDice(1, rotations);
	}

	/**
	 * Instantiates a dice renderer object with multiple dice
	 *
	 * @param context The context
	 * @param diceCount Number of dice
	 */
	public DiceRenderer(Context context, int diceCount)
	{

		mContext = (DiceAnimationActivity) context;
		setDice(diceCount, new float[][]{{0, 0, 0}});
	}

	/**
	 * Initializes the dice ArrayList
	 * 
	 * @param count number of dice
	 */
	private void setDice(int count, float[][] rotations)
	{
		for (int i = 0; i < count; i++)
		{
			mDice.add(new Die(rotations[i]));
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		// Update the scene
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glLoadIdentity();
		gl.glEnable(GL10.GL_TEXTURE_2D);

		float dist = 3.3f;
		float posY = 0.0f;

		if (mDice.size() == 2)
		{
			posY = dist/-3.8f;
		}

		mIsFinished = true;

		gl.glTranslatef(0.0f, posY, -6.0f);

		for (int i = 0; i < mDice.size(); i++)
		{
			gl.glPushMatrix();
			Die die = mDice.get(i);
			die.rotate();
			gl.glTranslatef(0.0f, posY, 0.0f);
			die.draw(gl);

			if (die.isNotReady())
			{
				mIsFinished = false;
			}

			if (mDice.size() == 2)
			{

				posY += dist;
			}

			gl.glPopMatrix();
		}

		// If all the dice are ready, the result should be sent to the activity.
		if (mIsFinished && mDoUpdateToast)
		{
			mContext.finishRoll();
			mDoUpdateToast = false;
			for (Die die : mDice)
			{
				die.setReady(true);
			}
		}

		gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		// Initialize the scene
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
				GL10.GL_FASTEST);

		gl.glClearColor(0, 0, 0, 0);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		for (Die die : mDice)
		{
			die.loadGLTexture(gl, mContext);
		}
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
		if (mIsFinished)
		{
			mDoUpdateToast = true;
			for (int i = 0; i < mDice.size(); i++)
			{
				Die die = mDice.get(i);

				die.rotate(mInitialRotation + dirs[(i % 3)],
						mInitialRotation + dirs[(i + 1) % 3],
						mInitialRotation + dirs[(i + 2) % 3]);
				die.setRolling(true);
				die.setReady(false);
			}
		}
	}

	/**
	 * Sets the rotation of all dice. Expects the angles in the following structure:
	 * {
	 *  {dice0.x, dice0.y, dice0.z},
	 *  {dice1.x, dice1.y, dice1.z},
	 *  ...
	 * }
	 *
	 * @param rotations 2-dimensional Array with float values for the x, y and z rotation angles
	 */
	public void setDiceRotations(float[][] rotations)
	{
		for (int i = 0; i < mDice.size(); i++)
		{
			float[] rotation = rotations[i];
			Die die = mDice.get(i);
			die.setRotation(rotation);
		}
	}

	/**
	 * Returns the dice rotation angles in the following structure:
	 * {
	 *  {dice0.x, dice0.y, dice0.z},
	 *  {dice1.x, dice1.y, dice1.z},
	 *  ...
	 * }
	 *
	 * @return 2-dimensional Array with float values for the x, y and z rotation angles
	 */
	public float[][] getDiceRotations()
	{
		float[][] rotations = new float[mDice.size()][3];
		for (int i = 0; i < mDice.size(); i++)
		{
			Die die = mDice.get(i);
			rotations[i] = die.getRotations();
		}
		return rotations;
	}
}