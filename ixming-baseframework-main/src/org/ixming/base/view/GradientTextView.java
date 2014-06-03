package org.ixming.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.TextView;

public class GradientTextView extends TextView {

	private int[] mTextShaderColors = null;
	private float[] mTextShaderPositions = null;
	private boolean mIsTextShaderSet = false;
	
	private boolean mForegroundShaderChanged = false;
	private int[] mForegroundShaderColors = null;
	private float[] mForegroundShaderPositions = null;
	
	private Paint mForegroundPaint = new Paint();
	private boolean mIsForegroundShaderSet = false;
	public GradientTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initGradientTextView();
	}

	public GradientTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initGradientTextView();
	}

	public GradientTextView(Context context) {
		super(context);
		initGradientTextView();
	}

	private void initGradientTextView() {
		
	}
	
	private void setTextShader0(Shader shader) {
		mIsTextShaderSet = (null != shader);
		getPaint().setShader(shader);
	}
	
	/**
	 * 
	 * @param colors The colors to be distributed along the gradient line
	 * @param positions May be null. The relative positions [0..1] of each corresponding color in the colors array. 
	 * If this is null, the the colors are distributed evenly along the gradient line.
	 */
	public void setLinearGradientTextShader(int[] colors, float[] positions) {
		setLinearGradientTextShader0(colors, positions);
		postInvalidate();
	}
	
	private void setLinearGradientTextShader0(int[] colors, float[] positions) {
		mTextShaderColors = colors;
		mTextShaderPositions = positions;
		if (null != mTextShaderColors && null != mTextShaderPositions) {
			mIsTextShaderSet = true;
		} else {
			mIsTextShaderSet = false;
		}
	}
	
	public void setForegroundShader(Shader shader) {
		setForegroundShader0(shader);
		postInvalidate();
	}
	
	private void setForegroundShader0(Shader shader) {
		mIsForegroundShaderSet = (null != shader);
		mForegroundPaint.setShader(shader);
	}
	
	public void setLinearGradientForegroundShader(int[] colors, float[] positions) {
		setLinearGradientForegroundShader0(colors, positions);
		postInvalidate();
	}
	
	private void setLinearGradientForegroundShader0(int[] colors, float[] positions) {
		mForegroundShaderColors = colors;
		mForegroundShaderPositions = positions;
		if (null != mForegroundShaderColors && null != mForegroundShaderColors) {
			mIsForegroundShaderSet = true;
		} else {
			mIsForegroundShaderSet = false;
		}
		mForegroundShaderChanged = true;
	}
	
	/**
	 * 因为渐变的缘故，会出现边缘处，颜色太纯，导致显示不满足需求；
	 * <p/>
	 * 此处预留设置，延伸相对于该View高度一定的比率，使得显示更符合要求。
	 * @return
	 */
	protected float getLinearGradientOffsetRatio() {
		return 0.0F;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (mIsTextShaderSet) {
			setTextShader0(new LinearGradient(getPaddingLeft() + getScrollX(),
					getTotalPaddingTop() + getScrollY(), 
					getPaddingLeft() + getScrollX(),
					getTotalPaddingTop() + getScrollY() + getHeight() * (1.0F + getLinearGradientOffsetRatio()),
					mTextShaderColors, mTextShaderPositions, TileMode.CLAMP));
		}
		if (mForegroundShaderChanged) {
			setForegroundShader0(new LinearGradient(getPaddingLeft(), getPaddingTop(), 
					getPaddingLeft(), getHeight() - getPaddingBottom(),
					mForegroundShaderColors, mForegroundShaderPositions, TileMode.CLAMP));
			mForegroundShaderChanged = false;
		}
		
		super.onDraw(canvas);
		
//		Rect rect = new Rect(getPaddingLeft(), getPaddingTop(), 
//				getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
//		canvas.drawRect(rect, mForegroundPaint);
		
		if (mIsForegroundShaderSet) {
			canvas.drawPaint(mForegroundPaint);
		}
		
//		GradientDrawable d = new GradientDrawable(Orientation.TOP_BOTTOM,
//				new int[] { Color.TRANSPARENT, 0x88FFFFFF });
//		d.setShape(GradientDrawable.RECTANGLE);
//		d.setBounds(getPaddingLeft(), getPaddingTop(), getWidth()
//				- getPaddingRight(), getHeight() - getPaddingBottom());
//		d.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//		d.draw(canvas);
		
	}
}
