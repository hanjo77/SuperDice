package oldschool.superdice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Die
{
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
	private boolean resetPoint = false;
	private boolean ready = false;
	final float friction = 0.99f;

	/**
	 * Our texture pointer
	 */
	private int[] textures = new int[6];

	/**
	 * Textures
	 */
	private int[] resourceIds = new int[]{
			R.drawable.side1,
			R.drawable.side2,
			R.drawable.side3,
			R.drawable.side4,
			R.drawable.side5,
			R.drawable.side6};

	/**
	 * The initial vertex definition
	 * <p/>
	 * Note that each face is defined, even
	 * if indices are available, because
	 * of the texturing we want to achieve
	 */
	private float vertices[] = {
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
	private float texture[] = {
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
	private byte indices[] = {
			//Faces definition
			0, 1, 3, 0, 3, 2,           //Face front
			4, 5, 7, 4, 7, 6,           //Face right
			8, 9, 11, 8, 11, 10,        //...
			12, 13, 15, 12, 15, 14,
			16, 17, 19, 16, 19, 18,
			20, 21, 23, 20, 23, 22,
	};

	/**
	 * The Cube constructor.
	 * <p/>
	 * Initiate the buffers.
	 */
	public Die()
	{
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
	}

	/**
	 * The object own drawing function.
	 * Called from the renderer to redraw this instance
	 * with possible changes in values.
	 *
	 * @param gl - The GL Context
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
	 * @param gl      - The GL Context
	 * @param context - The Activity context
	 */
	public void loadGLTexture(GL10 gl, Context context)
	{
		//Generate a 6 texture pointer...
		gl.glGenTextures(6, textures, 0);

		Bitmap bitmap = null;

		for (int i = 0; i < 6; i++)
		{
			// Create a bitmap
			bitmap = BitmapFactory.decodeResource(context.getResources(), resourceIds[i]);

			//...and bind it to our array
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);

			//Create Nearest Filtered Texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

			//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

			//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

			//Clean up
			bitmap = null;
		}
	}

	public void rotate()
	{
		rotate(rotationX, rotationY, rotationZ);
	}

	public void rotate(float x, float y, float z)
	{
		rotationX = x;
		rotationY = y;
		rotationZ = z;

		if (Math.abs(rotationX) > 0)
		{
			if (Math.abs(rotationX) > 0.2f)
			{
				rotationX *= friction;
			}
			mCubeRotationX += rotationX;
		}
		if (Math.abs(rotationY) > 0)
		{
			if (Math.abs(rotationY) > 0.2f)
			{
				rotationY *= friction;
			}
			mCubeRotationY += rotationY;
		}
		if (Math.abs(rotationZ) > 0)
		{
			if (Math.abs(rotationZ) > 0.2f)
			{
				rotationZ *= friction;
			}
			mCubeRotationZ += rotationZ;
		}

		if (Math.abs(rotationX) <= 0.2f)
		{
			mCubeRotationX = findEdge(mCubeRotationX);
			if (resetPoint)
			{
				rotationX = 0;
			}
		}
		if (Math.abs(rotationY) <= 0.2f)
		{
			mCubeRotationY = findEdge(mCubeRotationY);
			if (resetPoint)
			{
				rotationY = 0;
			}
		}
		if (Math.abs(rotationZ) <= 0.2f)
		{
			mCubeRotationZ = findEdge(mCubeRotationZ);
			if (resetPoint)
			{
				rotationZ = 0;
			}
		}

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

		if ((rotX == 0 && rotY == 0 && rotZ == 0) ||
				(rotX == 0 && rotY == 0 && rotZ == 1) ||
				(rotX == 0 && rotY == 0 && rotZ == 2) ||
				(rotX == 0 && rotY == 0 && rotZ == 3) ||
				(rotX == 2 && rotY == 2 && rotZ == 0) ||
				(rotX == 2 && rotY == 2 && rotZ == 1) ||
				(rotX == 2 && rotY == 2 && rotZ == 2) ||
				(rotX == 2 && rotY == 2 && rotZ == 3))
		{
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
				(rotX == 3 && rotY == 3 && rotZ == 3))
		{

			number = 2;
		}
		else if ((rotX == 0 && rotY == 2 && rotZ == 0) ||
				(rotX == 0 && rotY == 2 && rotZ == 1) ||
				(rotX == 0 && rotY == 2 && rotZ == 2) ||
				(rotX == 0 && rotY == 2 && rotZ == 3) ||
				(rotX == 2 && rotY == 0 && rotZ == 0) ||
				(rotX == 2 && rotY == 0 && rotZ == 1) ||
				(rotX == 2 && rotY == 0 && rotZ == 2) ||
				(rotX == 2 && rotY == 0 && rotZ == 3))
		{

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
				(rotX == 3 && rotY == 3 && rotZ == 1))
		{

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
				(rotX == 3 && rotY == 3 && rotZ == 0))
		{

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
				(rotX == 3 && rotY == 3 && rotZ == 2))
		{

			number = 6;
		}

		if (isRolling && rotationX == 0 && rotationY == 0 && rotationZ == 0)
		{

			isRolling = false;
			ready = true;
		}
	}

	private float findEdge(float point)
	{
		resetPoint = false;
		if (Math.abs(point) % 90 <= 10)
		{
			if (point >= 0)
			{
				point = (float) Math.floor(point / 90) * 90;
			}
			else
			{
				point = (float) Math.ceil(point / 90) * 90;
			}
			resetPoint = true;
		}
		else if (Math.abs(point) % 90 >= 80)
		{
			if (point >= 0)
			{
				point = (float) (Math.floor(point / 90) + 1) * 90;
			}
			else
			{
				point = (float) (Math.floor(point / 90)) * 90;
			}
			resetPoint = true;
		}
		return point;
	}

	public int getNumber()
	{
		return number;
	}

	public float[] getRotation()
	{
		return new float[]{mCubeRotationX, mCubeRotationY, mCubeRotationZ};
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public boolean isRolling()
	{
		return isRolling;
	}

	public void setRolling(boolean rolling)
	{
		isRolling = rolling;
	}

	public boolean isReady()
	{
		return ready;
	}

	public void setReady(boolean ready)
	{
		this.ready = ready;
	}
}
