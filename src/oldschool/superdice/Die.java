package oldschool.superdice;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.GLES10;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * The dice object. A simple textured box with additional properties like the result shown.
 * 
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */

public class Die
{
	/**
	 * Current context, used for issues with default device orientation and textures
	 */
	private Context context;
	/**
	 * The buffer holding the vertices
	 */
	private FloatBuffer vertexBuffer;
	/**
	 * The buffer holding the texture coordinates
	 */
	private FloatBuffer textureBuffer;
	/**
	 * The buffer holding the indices
	 */
	private ByteBuffer indexBuffer;
	/**
	 * The thrown number
	 */
	private int number;
	/**
	 * Indicates if the dice is rolling *
	 */
	private boolean isRolling;
	/**
	 * Rotation values *
	 */
	private float mCubeRotationX, mCubeRotationY, mCubeRotationZ, rotationX, rotationY, rotationZ;
	/**
	 * Used to define whether a rotation shall be reset to zero.
	 */
	private boolean resetPoint = false;
	/**
	 * Defines whether the die is ready (not rolling)
	 */
	private boolean ready = true;
	/**
	 * Friction defines how slowly the speed of the animation decreases, the closer to 1, the slower the decrease.
	 */
	final float friction = 0.98f;
	/**
	 * The minimal rotation speed, if this speed is reached, the die will try to snap to the next best side.
	 */
	final float minRotationSpeed = 1.0f;
	/**
	 * the snapping limit angle - when below this angle the die will fall to the corresponding side.
	 */
	final float snapPoint = 5;
	final int textureWidth = 512;
	final int textureHeight = 512;

	/**
	 * Our texture pointer
	 */
	private int[] textures = new int[6];

	/**
	 * Textures
	 */
	private int[] resourceIds = new int[]{
			R.raw.side1,
			R.raw.side2,
			R.raw.side3,
			R.raw.side4,
			R.raw.side5,
			R.raw.side6};


	/**
	 * The Cube constructor.
	 * Initiate the buffers.
	 */
	public Die(Context context)
	{
		/**
		 * The initial vertex definition
		 * <p/>
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
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
		indexBuffer = ByteBuffer.allocateDirect(indices.length);
		indexBuffer.put(indices);
		indexBuffer.position(0);
		this.context = context;
	}

	public boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
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
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		for (int i = 0; i < 6; i++)
		{
			//Bind our only previously generated texture in this case
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
			indexBuffer.position(6 * i);
			// gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			//Draw the vertices as triangles, based on the Index Buffer information
			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		}

		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	/**
	 * Load the textures
	 *
	 * @param gl      The GL Context
	 * @param context The Activity context
	 */
	public void loadGLTexture(GL10 gl, Context context)
	{
		//Generate a 6 texture pointer...
		gl.glGenTextures(6, textures, 0);

		for (int i = 0; i < 6; i++)
		{
			// Create a bitmap
			// Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceIds[i]);
			InputStream input = context.getResources().openRawResource(resourceIds[i]);


			//...and bind it to our array
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);

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
		rotate(rotationX, rotationY, rotationZ);
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
		rotationX = x;
		rotationY = y;
		rotationZ = z;

		if (Math.abs(rotationX) > 0)
		{
			if (Math.abs(rotationX) > minRotationSpeed)
			{
				rotationX *= friction;
			}
			mCubeRotationX += rotationX;
		}
		if (Math.abs(rotationY) > 0)
		{
			if (Math.abs(rotationY) > minRotationSpeed)
			{
				rotationY *= friction;
			}
			mCubeRotationY += rotationY;
		}
		if (Math.abs(rotationZ) > 0)
		{
			if (Math.abs(rotationZ) > minRotationSpeed)
			{
				rotationZ *= friction;
			}
			mCubeRotationZ += rotationZ;
		}

		if (Math.abs(rotationX) <= minRotationSpeed)
		{
			mCubeRotationX = findEdge(mCubeRotationX);
			if (resetPoint)
			{
				rotationX = 0;
			}
		}
		if (Math.abs(rotationY) <= minRotationSpeed)
		{
			mCubeRotationY = findEdge(mCubeRotationY);
			if (resetPoint)
			{
				rotationY = 0;
			}
		}
		if (Math.abs(rotationZ) <= minRotationSpeed)
		{
			mCubeRotationZ = findEdge(mCubeRotationZ);
			if (resetPoint)
			{
				rotationZ = 0;
			}
		}

		evaluateNumber();

		if (isRolling && rotationX == 0 && rotationY == 0 && rotationZ == 0)
		{

			isRolling = false;
			ready = true;
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
			number = 1;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 2) ||
				(rotX == 0 && rotY == 3 && rotZ == 0) ||
				(rotX == 2 && rotY == 1 && rotZ == 0) ||
				(rotX == 2 && rotY == 3 && rotZ == 2) ||
				(rotX == 1 && rotZ == 1) ||
				(rotX == 3 && rotZ == 3))
		{

			number = 2;
		}
		else if ((rotX == 0 && rotY == 2) ||
				(rotX == 2 && rotY == 0))
		{

			number = 3;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 0) ||
				(rotX == 0 && rotY == 3 && rotZ == 2) ||
				(rotX == 2 && rotY == 1 && rotZ == 2) ||
				(rotX == 2 && rotY == 3 && rotZ == 0) ||
				(rotX == 1 && rotZ == 3) ||
				(rotX == 3 && rotZ == 1))
		{

			number = 4;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 3) ||
				(rotX == 0 && rotY == 3 && rotZ == 1) ||
				(rotX == 2 && rotY == 1 && rotZ == 1) ||
				(rotX == 2 && rotY == 3 && rotZ == 3) ||
				(rotX == 1 && rotZ == 2) ||
				(rotX == 3 && rotZ == 0))
		{

			number = 5;
		}
		else if ((rotX == 0 && rotY == 1 && rotZ == 1) ||
				(rotX == 0 && rotY == 3 && rotZ == 3) ||
				(rotX == 2 && rotY == 1 && rotZ == 3) ||
				(rotX == 2 && rotY == 3 && rotZ == 1) ||
				(rotX == 1 && rotZ == 0) ||
				(rotX == 3 && rotZ == 2))
		{

			number = 6;
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
		resetPoint = false;
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
			resetPoint = true;
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
			resetPoint = true;
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
		return number;
	}

	/**
	 * Gets the current rotation.
	 * 
	 * @return float Array of x, y and z axis rotation angles
	 */
	public float[] getRotation()
	{
		return new float[]{mCubeRotationX, mCubeRotationY, mCubeRotationZ};
	}

	/**
	 * Define whether die is rolling or not.
	 * 
	 * @param rolling true for rolling, false for not.
	 */
	public void setRolling(boolean rolling)
	{
		isRolling = rolling;
	}

	/**
	 * Set the dice ready state.
	 * 
	 * @param ready true for ready, false for not.
	 */
	public void setReady(boolean ready)
	{
		this.ready = ready;
	}

	/**
	 * Returns if the die is not ready.
	 *
	 * @return true if ball is not ready.
	 */
	public boolean isNotReady()
	{
		return !ready;
	}
}
