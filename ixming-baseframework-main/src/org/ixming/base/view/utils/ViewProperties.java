package org.ixming.base.view.utils;


import android.view.View;

/**
 * Utility for getting some properties of the view in the framework
 * @author Yin Yong
 * @version 1.0
 */
public class ViewProperties {
	private View mView;
	private FloatRect mChildDrawingBounds;
	private FloatRect mChildDrawingBoundsoffsetPadding;
	public ViewProperties(View iv) {
		mView = iv;
	}
	
	/**
	 * @return getMeasuredWidth
	 */
	public float getWidth() {
		return mView.getMeasuredWidth();
	}

	/**
	 * @return getMeasuredHeight
	 */
	public float getHeight() {
		return mView.getMeasuredHeight();
	}
	
	public float getCenterX() {
		return getWidth() / 2F;
	}
	
	public float getCenterY() {
		return getHeight() / 2F;
	}

	public float getHorizontalPadding() {
		return mView.getPaddingLeft() + mView.getPaddingRight();
	}

	public float getVerticalPadding() {
		return mView.getPaddingTop() + mView.getPaddingBottom();
	}

	public float getTrueHorizontalSpace() {
		return getWidth() - getHorizontalPadding();
	}

	public float getTrueVerticalSpace() {
		return getHeight() - getVerticalPadding();
	}
	
	public FloatRect getChildDrawingBounds() {
		if (null == mChildDrawingBounds) {
			mChildDrawingBounds = new FloatRect();
		}
		mChildDrawingBounds.set(mView.getPaddingLeft(), mView.getPaddingTop(),
				getWidth() - mView.getPaddingRight(), getHeight() - mView.getPaddingBottom());
		return mChildDrawingBounds;
	}
	
	public FloatRect getChildDrawingBoundsoffsetPadding() {
		if (null == mChildDrawingBoundsoffsetPadding) {
			mChildDrawingBoundsoffsetPadding = new FloatRect();
		}
		mChildDrawingBoundsoffsetPadding.set(0, 0, getTrueHorizontalSpace(), getTrueVerticalSpace());
		return mChildDrawingBoundsoffsetPadding;
	}
	
	/**
	 * utility for getting value as {@code int}
	 * <p/>
	 * <ul>
	 * 	<li>getAsInt(0.0) = 0.0</li>
	 * 	<li>getAsInt(+infinity) = +(int) (Math.abs(val) + 0.5F)</li>
	 * 	<li>getAsInt(-infinity) = -(int) (Math.abs(val) + 0.5F)</li>
	 * 	<li>getAsInt(NaN) = NaN</li>
	 * </ul>
	 * @param val float value
	 * @return int value
	 */
	public static int getAsInt(float val) {
		int symbol = (int) Math.signum(val);
		int absVal = (int) (Math.abs(val) + 0.5F);
		return absVal * symbol;
	}
}
