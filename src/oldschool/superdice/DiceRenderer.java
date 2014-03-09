package oldschool.superdice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

class DiceRenderer implements GLSurfaceView.Renderer
{

	private DiceAnimationActivity context;
	private ArrayList<Die> mDice = new ArrayList<Die>();
	private int[] number;
	final int dist = 4;

	public DiceRenderer(Context context)
	{

		this.context = (DiceAnimationActivity) context;
		this.setDice(1);
	}

	public DiceRenderer(Context context, int diceCount)
	{

		this.context = (DiceAnimationActivity) context;
		this.setDice(diceCount);
	}

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
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glEnable(GL10.GL_TEXTURE_2D);

		float posY = 0.0f;

		if (mDice.size() == 2)
		{

			posY = -1 * (dist / 4);
		}

		boolean isFinished = true;

		gl.glTranslatef(0.0f, posY, ((mDice.size() - 1) * -5) - 10.0f);

		for (int i = 0; i < mDice.size(); i++)
		{

			gl.glPushMatrix();
			Die die = mDice.get(i);
			float[] diceRotation = die.getRotation();
			die.rotate();
			gl.glTranslatef(0.0f, posY, 0.0f);
			die.draw(gl);

			if (!die.isReady())
			{
				isFinished = false;
			}

			if (mDice.size() == 2)
			{

				posY += dist;
			}

			gl.glPopMatrix();
		}

		if (isFinished)
		{

			context.toastNumber();
			for (int i = 0; i < mDice.size(); i++)
			{
				mDice.get(i).setReady(false);
			}
		}

		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
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

		for (int i = 0; i < mDice.size(); i++)
		{
			Die die = mDice.get(i);
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
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public int[] getNumber()
	{
		number = new int[mDice.size()];
		for (int i = 0; i < mDice.size(); i++)
		{
			number[i] = mDice.get(i).getNumber();
		}
		return number;
	}

	public void rollDice(float[] dirs)
	{
		for (int i = 0; i < mDice.size(); i++)
		{
			Die die = mDice.get(i);
			die.rotate(dirs[(i % 3)], dirs[(i + 1) % 3], dirs[(i + 2) % 3]);
			die.setRolling(true);
		}
	}
}