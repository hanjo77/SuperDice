package oldschool.superdice;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class to capture the gesture controls for dice animation.
 *
 * @author Hansjürg Jaggi, Stephan Menzi & Satesh Paramasamy
 */
public class DiceGestureListener implements View.OnTouchListener
{
	private final GestureDetector mGestureDetector;
	private final DiceRenderer mDiceRenderer;

	public DiceGestureListener (Context context)
	{
		mGestureDetector = new GestureDetector(context, new GestureListener());
		mDiceRenderer = ((DiceAnimationActivity) context).getDiceRenderer();
	}

	private final class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				mDiceRenderer.rollDice(generateDirections(diffX, diffY));
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return false;
		}

		private float[] generateDirections(float diffX, float diffY) {

			float divisor = 20;
			return new float[]{ diffX/divisor, diffY/divisor, ((diffX+diffY)/2)/divisor };
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
}
