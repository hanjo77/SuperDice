package oldschool.superdice;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class DiceRenderer implements GLSurfaceView.Renderer{

	private DiceAnimationActivity context;
	private Die mDie = new Die();
	private boolean hasStarted = false;
	private int number = 0;
	private boolean resetPoint = false;
	private float mCubeRotationX, mCubeRotationY, mCubeRotationZ, rotationX, rotationY, rotationZ;
	final float friction = 0.99f;

	public DiceRenderer(Context context) {

		this.context = (DiceAnimationActivity)context;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glTranslatef(0.0f, 0.0f, -10.0f);
		gl.glRotatef(mCubeRotationX, 1.0f, 0, 0);
		gl.glRotatef(mCubeRotationY, 0, 1.0f, 0);
		gl.glRotatef(mCubeRotationZ, 0, 0, 1.0f);

		mDie.draw(gl);

		if (Math.abs(rotationX) > 0) {
			rotationX *= friction;
			mCubeRotationX += rotationX;
		}
		if (Math.abs(rotationY) > 0) {
			rotationY *= friction;
			mCubeRotationY += rotationY;
		}
		if (Math.abs(rotationZ) > 0) {
			rotationZ *= friction;
			mCubeRotationZ += rotationZ;
		}

		if (Math.abs(rotationX) < 0.3f) {

			mCubeRotationX = findEdge(mCubeRotationX);
			if (resetPoint) {
				rotationX = 0;
			}
		}
		if (Math.abs(rotationY) < 0.3f) {

			mCubeRotationY = findEdge(mCubeRotationY);
			if (resetPoint) {
				rotationY = 0;
			}
		}
		if (Math.abs(rotationZ) < 0.3f) {
			mCubeRotationZ = findEdge(mCubeRotationZ);
			if (resetPoint) {
				rotationZ = 0;
			}
		}

		int rotX = (int)(mCubeRotationX%360)/90;
		int rotY = (int)(mCubeRotationY%360)/90;
		int rotZ = (int)(mCubeRotationZ%360)/90;
		if (rotX < 0) {
			rotX += 4;
		}
		if (rotY < 0) {
			rotY += 4;
		}
		if (rotZ < 0) {
			rotZ += 4;
		}

		if ((rotX == 0 && rotY == 0 && rotZ == 0) ||
				(rotX == 0 && rotY == 0 && rotZ == 1) ||
				(rotX == 0 && rotY == 0 && rotZ == 2) ||
				(rotX == 0 && rotY == 0 && rotZ == 3) ||
				(rotX == 2 && rotY == 2 && rotZ == 0) ||
				(rotX == 2 && rotY == 2 && rotZ == 1) ||
				(rotX == 2 && rotY == 2 && rotZ == 2) ||
				(rotX == 2 && rotY == 2 && rotZ == 3)) {

			number = 1;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 2) ||
				(rotX == 0 && rotY == 3 && rotZ == 0) ||
				(rotX == 1 && rotY == 0 && rotZ == 1) ||
				(rotX == 1 && rotY == 1 && rotZ == 1) ||
				(rotX == 1 && rotY == 2 && rotZ == 1) ||
				(rotX == 1 && rotY == 3 && rotZ == 1) ||
				(rotX == 2 && rotY == 1 && rotZ == 0) ||
				(rotX == 2 && rotY == 3 && rotZ == 2) ||
				(rotX == 3 && rotY == 0 && rotZ == 3) ||
				(rotX == 3 && rotY == 1 && rotZ == 3) ||
				(rotX == 3 && rotY == 2 && rotZ == 3) ||
				(rotX == 3 && rotY == 3 && rotZ == 3)) {

			number = 2;
		}
		else if ((rotX == 0 && rotY == 2 && rotZ == 0) ||
				(rotX == 0 && rotY == 2 && rotZ == 1) ||
				(rotX == 0 && rotY == 2 && rotZ == 2) ||
				(rotX == 0 && rotY == 2 && rotZ == 3) ||
				(rotX == 2 && rotY == 0 && rotZ == 0) ||
				(rotX == 2 && rotY == 0 && rotZ == 1) ||
				(rotX == 2 && rotY == 0 && rotZ == 2) ||
				(rotX == 2 && rotY == 0 && rotZ == 3)) {

			number = 3;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 0) ||
				(rotX == 0 && rotY == 3 && rotZ == 2) ||
				(rotX == 1 && rotY == 0 && rotZ == 3) ||
				(rotX == 1 && rotY == 1 && rotZ == 3) ||
				(rotX == 1 && rotY == 2 && rotZ == 3) ||
				(rotX == 1 && rotY == 3 && rotZ == 3) ||
				(rotX == 2 && rotY == 1 && rotZ == 2) ||
				(rotX == 2 && rotY == 3 && rotZ == 0) ||
				(rotX == 3 && rotY == 0 && rotZ == 1) ||
				(rotX == 3 && rotY == 1 && rotZ == 1) ||
				(rotX == 3 && rotY == 2 && rotZ == 1) ||
				(rotX == 3 && rotY == 3 && rotZ == 1)) {

			number = 4;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 3) ||
				(rotX == 0 && rotY == 3 && rotZ == 1) ||
				(rotX == 1 && rotY == 0 && rotZ == 2) ||
				(rotX == 1 && rotY == 1 && rotZ == 2) ||
				(rotX == 1 && rotY == 2 && rotZ == 2) ||
				(rotX == 1 && rotY == 3 && rotZ == 2) ||
				(rotX == 2 && rotY == 1 && rotZ == 1) ||
				(rotX == 2 && rotY == 3 && rotZ == 3) ||
				(rotX == 3 && rotY == 0 && rotZ == 0) ||
				(rotX == 3 && rotY == 1 && rotZ == 0) ||
				(rotX == 3 && rotY == 2 && rotZ == 0) ||
				(rotX == 3 && rotY == 3 && rotZ == 0)) {

			number = 5;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 1) ||
				(rotX == 0 && rotY == 3 && rotZ == 3) ||
				(rotX == 1 && rotY == 0 && rotZ == 0) ||
				(rotX == 1 && rotY == 1 && rotZ == 0) ||
				(rotX == 1 && rotY == 2 && rotZ == 0) ||
				(rotX == 1 && rotY == 3 && rotZ == 0) ||
				(rotX == 2 && rotY == 1 && rotZ == 3) ||
				(rotX == 2 && rotY == 3 && rotZ == 1) ||
				(rotX == 3 && rotY == 0 && rotZ == 2) ||
				(rotX == 3 && rotY == 1 && rotZ == 2) ||
				(rotX == 3 && rotY == 2 && rotZ == 2) ||
				(rotX == 3 && rotY == 3 && rotZ == 2)) {

			number = 6;
		}

		if (hasStarted && rotationX == 0 && rotationY == 0 && rotationZ == 0) {

			context.toastNumber();
			hasStarted = false;
		}

		gl.glLoadIdentity();
	}

	private float findEdge(float point) {

		resetPoint = false;
		if (Math.abs(point)%90 < 30) {

			if (point > 0) {

				point = (float)Math.floor(point/90)*90;
			}
			else {

				point = (float)Math.floor(point/90)*90;
			}
			resetPoint = true;
		}
		else if (Math.abs(point)%90 > 60) {

			if (point > 0) {

				point = (float)Math.floor(point/90)*90 + 90;
			}
			else {

				point = (float)Math.floor(point/90)*90 - 90;
			}
			resetPoint = true;
		}
		return point;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

		gl.glMatrixMode(GL10.GL_PROJECTION);

		float[] ambient = {0.1f, 1, 1, 1};
		float[] position = {45, 20, 0, 1};
		float[] direction = {0, -1, 0};

		gl.glEnable(GL10.GL_LIGHT1);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambient, 0 );
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, position, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPOT_DIRECTION, direction, 0);
		gl.glLightf(GL10.GL_LIGHT1, GL10.GL_SPOT_CUTOFF, 30.0f);

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

	public int getNumber() {
		return number;
	}

	public void setRotation(float x, float y, float z) {

		rotationX = x;
		rotationY = y;
		rotationZ = z;
		hasStarted = true;
	}
}