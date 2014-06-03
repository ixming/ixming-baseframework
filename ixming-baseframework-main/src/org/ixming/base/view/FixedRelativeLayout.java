package org.ixming.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 可以设置RelativeLayout的最大宽度和高度值。
 * <p>在xml配置文件中，直接设置android:maxWidth和android:maxHeight属性即可。</p>
 * @author Yin Yong
 * @version 1.0
 */
public class FixedRelativeLayout extends RelativeLayout {
	
	private final int MAX_UNDEFINED = Integer.MIN_VALUE;
	private int mMaxWidth = MAX_UNDEFINED;
	private int mMaxHeight = MAX_UNDEFINED;
	public FixedRelativeLayout(Context context) {
		super(context);
	}

	public FixedRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public FixedRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				new int[] { android.R.attr.maxWidth, android.R.attr.maxHeight });
		int maxWidth = a.getDimensionPixelOffset(0, -1);
		if (maxWidth > 0) {
			mMaxWidth = maxWidth;
		}
		int maxHeight = a.getDimensionPixelOffset(1, -1);
		if (maxHeight > 0) {
			mMaxHeight = maxHeight;
		}
		a.recycle();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 1.根据原先的测量
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 2.再根据测量结果对比，是否超过最大高度
		boolean measureChanged = false;
		int width = getMeasuredWidth();
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		if (mMaxWidth != MAX_UNDEFINED) {
			if (width > mMaxWidth) {
				width = mMaxWidth; 
				//TODO 一些情况下，原先的值不是EXACTLY，如果是AT_MOST，子View的显示就不会跟期望中一致
				modeWidth = MeasureSpec.EXACTLY;
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, modeWidth);
				measureChanged = true;
			}
		}
		
		int height = getMeasuredHeight();
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
		if (mMaxHeight != MAX_UNDEFINED) {
			if (height > mMaxHeight) {
				height = mMaxHeight; 
				//TODO 一些情况下，原先的值不是EXACTLY，如果是AT_MOST，子View的显示就不会跟期望中一致
				modeHeight = MeasureSpec.EXACTLY;
				heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, modeHeight);
				measureChanged = true;
			}
		}
		// 3.如果改变了，重新测量
		if (measureChanged) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 计算Rect bounds
		int left = getPaddingLeft();
		int top = getPaddingTop();
		int right = left;
		int bottom = top;

		//TODO ensure again
		int layoutWidth = r - l;
		if (mMaxWidth != MAX_UNDEFINED) {
			layoutWidth = Math.min(layoutWidth, mMaxWidth);
		}
		right = Math.max(left, layoutWidth - getPaddingRight());
		
		int layoutHeight = b - t;
		if (mMaxHeight != MAX_UNDEFINED) {
			layoutHeight = Math.min(layoutHeight, mMaxHeight);
		}
		bottom = Math.max(top, layoutHeight - getPaddingBottom());
		// layout children
		super.onLayout(changed, left, top, right, bottom);
	}
	
	/**
	 * @param height in pixels
	 * @added 1.0
	 */
	public void setMaxHeight(int height) {
		if (height <= 0) {
			mMaxHeight = MAX_UNDEFINED;
		} else {
			mMaxHeight = height;
		}
		requestLayout();
		invalidate();
	}
	
	/**
	 * @return height in pixels
	 * @added 1.0
	 */
	public int getMaxHeight() {
		return mMaxHeight;
	}
	
	/**
	 * @param width in pixels
	 * @added 1.0
	 */
	public void setMaxWidth(int width) {
		if (width <= 0) {
			mMaxWidth = MAX_UNDEFINED;
		} else {
			mMaxWidth = width;
		}
		requestLayout();
		invalidate();
	}
	
	/**
	 * @return width in pixels
	 * @added 1.0
	 */
	public int getMaxWidth() {
		return mMaxWidth;
	}
}
