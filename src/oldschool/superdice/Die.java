package oldschool.superdice;

import android.content.Context;
import android.opengl.ETC1Util;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * The dice object. A simple textured box with additional properties like the result shown.
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

class Die
{
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
	private ByteBuffer mIndexBuffer;
	private int mNumber;
	private float mCubeRotationX, mCubeRotationY, mCubeRotationZ, mRotationX, mRotationY, mRotationZ;
	private boolean mIsRolling;
	private boolean mDoReset = false;
	private boolean mIsReady = true;
	private int[] mTextures = new int[6];

	private int[] mResourceIds = new int[]{
			R.raw.side1,
			R.raw.side2,
			R.raw.side3,
			R.raw.side4,
			R.raw.side5,
			R.raw.side6};

	private float[] mRotation = new float[]{
			(float) Math.floor(Math.random()*4)*90,
			(float) Math.floor(Math.random()*4)*90,
			(float) Math.floor(Math.random()*4)*90
	};

	/**
	 * The Cube constructor.
	 * Initiate the buffers.
	 *
	 * @param rotation Array of the x, y and z rotation angles in degrees
	 */
	public Die(float[] rotation)
	{
		/**
		 * The initial vertex definition
		 * 
		 * Note that each face is defined, even
		 * if indices are available, because
		 * of the texturing we want to achieve
		 */
		float vertices[] = {
				//Vertices according to faces
				-1.0f, -1.0f, 1.0f, //Vertex 0
				1.0f, -1.0f, 1.0f,  //v1
				-1.0f, 1.0f, 1.0f,  //v2
				1.0f, 1.0f, 1.0f,   //v3

				1.0f, -1.0f, 1.0f,  //...
				1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, -1.0f,

				1.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, -1.0f,

				-1.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, 1.0f,
				-1.0f, 1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f,

				-1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f,
				-1.0f, -1.0f, 1.0f,
				1.0f, -1.0f, 1.0f,

				-1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				-1.0f, 1.0f, -1.0f,
				1.0f, 1.0f, -1.0f,
		};

		/**
		 * The initial texture coordinates (u, v)
		 */
		float texture[] = {
				//Mapping coordinates for the vertices

				0.0f, 1.0f,
				1.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,

				0.0f, 1.0f,
				1.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,

				0.0f, 1.0f,
				1.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,

				0.0f, 1.0f,
				1.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,

				0.0f, 1.0f,
				1.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,

				0.0f, 1.0f,
				1.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 0.0f,
		};

		/**
		 * The initial indices definition
		 */
		byte indices[] = {
				//Faces definition
				0, 1, 3, 0, 3, 2,           //Face front
				4, 5, 7, 4, 7, 6,           //Face right
				8, 9, 11, 8, 11, 10,        //...
				12, 13, 15, 12, 15, 14,
				16, 17, 19, 16, 19, 18,
				20, 21, 23, 20, 23, 22,
		};

		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mVertexBuffer = byteBuf.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mTextureBuffer = byteBuf.asFloatBuffer();
		mTextureBuffer.put(texture);
		mTextureBuffer.position(0);
		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);

		if (rotation.length == 3)
		{
			mRotation = rotation;
		}
		setRotation(mRotation);
	}

	/**
	 * The object own drawing function.
	 * Called from the renderer to redraw this instance
	 * with possible changes in values.
	 *
	 * @param gl The GL Context
	 */
	public void draw(GL10 gl)
	{

		//Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		//Set the face rotation
		gl.glFrontFace(GL10.GL_CCW);

		gl.glRotatef(mCubeRotationX, 1.0f, 0, 0);
		gl.glRotatef(mCubeRotationY, 0, 1.0f, 0);
		gl.glRotatef(mCubeRotationZ, 0, 0, 1.0f);

 		//Enable the vertex and texture state
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);

		for (int i = 0; i < 6; i++)
		{
			//Bind our only previously generated texture in this case
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[i]);
			mIndexBuffer.position(6 * i);
			// gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			//Draw the vertices as triangles, based on the Index Buffer information
			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
		}

		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	/**
	 * Load the textures
	 *
	 * @param gl The GL Context
	 * @param context The Activity context
	 */
	public void loadGLTexture(GL10 gl, Context context)
	{
		//Generate a 6 texture pointer...
		gl.glGenTextures(6, mTextures, 0);

		for (int i = 0; i < 6; i++)
		{
			// Create a bitmap
			// Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mResourceIds[i]);
			InputStream input = context.getResources().openRawResource(mResourceIds[i]);


			//...and bind it to our array
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[i]);

			//Create Nearest Filtered Texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

			//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

			//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
			// GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

			//Load compressed texture
			try{
				ETC1Util.loadTexture(GL10.GL_TEXTURE_2D, 0, 0,GL10.GL_RGB, GL10.GL_UNSIGNED_SHORT_5_6_5, input);
			}
			catch(IOException e){
				System.out.println("DEBUG! IOException"+e.getMessage());
			}
			finally{
				try {
					input.close();
				} catch (IOException e) {
					// ignore exception thrown from close.
				}
			}
		}
	}

	/**
	 * Rotates the die around it's private rotation angles
	 */
	public void rotate()
	{
		rotate(mRotationX, mRotationY, mRotationZ);
	}

	/**
	 * Rotates the die around any rotation angle
	 * 
	 * @param x rotation angle around x axis
	 * @param y	rotation angle around y axis
	 * @param z	rotation angle around z axis
	 */
	public void rotate(float x, float y, float z)
	{
		/**
		 * Defines how slowly the speed of the animation decreases, the closer to 1, the slower the decrease.
		 */
		float friction = 0.98f;
		/**
		 * The minimal rotation speed, if this speed is reached, the die will try to snap to the next best side.
		 */
		float minRotationSpeed = 1.0f;
		mRotationX = x;
		mRotationY = y;
		mRotationZ = z;

		if (Math.abs(mRotationX) > 0)
		{
			if (Math.abs(mRotationX) > minRotationSpeed)
			{
				mRotationX *= friction;
			}
			mCubeRotationX += mRotationX;
		}
		if (Math.abs(mRotationY) > 0)
		{
			if (Math.abs(mRotationY) > minRotationSpeed)
			{
				mRotationY *= friction;
			}
			mCubeRotationY += mRotationY;
		}
		if (Math.abs(mRotationZ) > 0)
		{
			if (Math.abs(mRotationZ) > minRotationSpeed)
			{
				mRotationZ *= friction;
			}
			mCubeRotationZ += mRotationZ;
		}

		if (Math.abs(mRotationX) <= minRotationSpeed)
		{
			mCubeRotationX = findEdge(mCubeRotationX);
			if (mDoReset)
			{
				mRotationX = 0;
			}
		}
		if (Math.abs(mRotationY) <= minRotationSpeed)
		{
			mCubeRotationY = findEdge(mCubeRotationY);
			if (mDoReset)
			{
				mRotationY = 0;
			}
		}
		if (Math.abs(mRotationZ) <= minRotationSpeed)
		{
			mCubeRotationZ = findEdge(mCubeRotationZ);
			if (mDoReset)
			{
				mRotationZ = 0;
			}
		}

		evaluateNumber();

		if (mIsRolling && mRotationX == 0 && mRotationY == 0 && mRotationZ == 0)
		{
			mIsRolling = false;
			mIsReady = true;
			mRotation = new float[]{ mCubeRotationX, mCubeRotationY, mCubeRotationZ };
		}
	}

	/**
	 * Reads the currently thrown number according to the rotation angles.
	 */
	private void evaluateNumber()
	{
		int rotX = (int) (mCubeRotationX % 360) / 90;
		int rotY = (int) (mCubeRotationY % 360) / 90;
		int rotZ = (int) (mCubeRotationZ % 360) / 90;
		if (rotX < 0)
		{
			rotX += 4;
		}
		if (rotY < 0)
		{
			rotY += 4;
		}
		if (rotZ < 0)
		{
			rotZ += 4;
		}

		if ((rotX == 0 && rotY == 0) ||
			(rotX == 2 && rotY == 2))
		{
			mNumber = 1;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 2) ||
				(rotX == 0 && rotY == 3 && rotZ == 0) ||
				(rotX == 2 && rotY == 1 && rotZ == 0) ||
				(rotX == 2 && rotY == 3 && rotZ == 2) ||
				(rotX == 1 && rotZ == 1) ||
				(rotX == 3 && rotZ == 3))
		{

			mNumber = 2;
		}
		else if ((rotX == 0 && rotY == 2) ||
				(rotX == 2 && rotY == 0))
		{

			mNumber = 3;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 0) ||
				(rotX == 0 && rotY == 3 && rotZ == 2) ||
				(rotX == 2 && rotY == 1 && rotZ == 2) ||
				(rotX == 2 && rotY == 3 && rotZ == 0) ||
				(rotX == 1 && rotZ == 3) ||
				(rotX == 3 && rotZ == 1))
		{

			mNumber = 4;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 3) ||
				(rotX == 0 && rotY == 3 && rotZ == 1) ||
				(rotX == 2 && rotY == 1 && rotZ == 1) ||
				(rotX == 2 && rotY == 3 && rotZ == 3) ||
				(rotX == 1 && rotZ == 2) ||
				(rotX == 3 && rotZ == 0))
		{

			mNumber = 5;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 1) ||
				(rotX == 0 && rotY == 3 && rotZ == 3) ||
				(rotX == 2 && rotY == 1 && rotZ == 3) ||
				(rotX == 2 && rotY == 3 && rotZ == 1) ||
				(rotX == 1 && rotZ == 0) ||
				(rotX == 3 && rotZ == 2))
		{

			mNumber = 6;
		}
	}

	/**
	 * Finds if the die should fall on a side.
	 * 
	 * @param angle the angle to be checked
	 * @return new angle
	 */
	private float findEdge(float angle)
	{
		float snapPoint = 5;

		mDoReset = false;
		if (Math.abs(angle) % 90 <= snapPoint)
		{
			if (angle >= 0)
			{
				angle = (float) Math.floor(angle / 90) * 90;
			}
			else
			{
				angle = (float) Math.ceil(angle / 90) * 90;
			}
			mDoReset = true;
		}
		else if (Math.abs(angle) % 90 >= (90-snapPoint))
		{
			if (angle >= 0)
			{
				angle = (float) (Math.floor(angle / 90) + 1) * 90;
			}
			else
			{
				angle = (float) (Math.floor(angle / 90)) * 90;
			}
			mDoReset = true;
		}
		return angle;
	}

	/**
	 * Returns the thrown number of this die.
	 * 
	 * @return number
	 */
	public int getNumber()
	{
		return mNumber;
	}

	/**
	 * Define whether die is rolling or not.
	 * 
	 * @param rolling true for rolling, false for not.
	 */
	public void setRolling(boolean rolling)
	{
		mIsRolling = rolling;
	}

	/**
	 * Set the dice ready state.
	 * 
	 * @param ready true for ready, false for not.
	 */
	public void setReady(boolean ready)
	{
		mIsReady = ready;
	}

	/**
	 * Returns if the die is not ready.
	 *
	 * @return true if ball is not ready.
	 */
	public boolean isNotReady()
	{
		return !mIsReady;
	}

	/**
	 * Returns the rotation angles
	 *
	 * @return Array of the x, y and z rotation angles in degrees
	 */
	public float[] getRotations()
	{
		return mRotation;
	}

	/**
	 * Sets the rotation angles
	 *
	 * @param rotation Array of the x, y and z rotation angles in degrees
	 */
	public void setRotation(float[] rotation)
	{
		mCubeRotationX = rotation[0];
		mCubeRotationY = rotation[1];
		mCubeRotationZ = rotation[2];
		evaluateNumber();
	}
}
