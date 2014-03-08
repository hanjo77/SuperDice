package oldschool.superdice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hanjo on 08.03.14.
 */
class DiceRenderer implements GLSurfaceView.Renderer{

	private DiceAnimationActivity context;
	private Die mDie = new Die();
	private float mCubeRotation;

	public DiceRenderer(Context context) {

		this.context = (DiceAnimationActivity)context;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glTranslatef(0.0f, 0.0f, -10.0f);
		gl.glRotatef(mCubeRotation, 1.0f, 1.0f, 0.5f);

		mDie.draw(gl);

		gl.glLoadIdentity();

		mCubeRotation -= 3f;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		mDie.loadGLTexture(gl, context);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);


		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
				GL10.GL_NICEST);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}