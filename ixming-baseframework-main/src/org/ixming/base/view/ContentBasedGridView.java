package org.ixming.base.view;

import org.ixming.base.view.utils.ViewUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 根据内容高度，完全显示该GridView
 * 
 * @author Yin Yong
 */
public class ContentBasedGridView extends GridView {

	public ContentBasedGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initContentBasedGridView();
	}

	public ContentBasedGridView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.style.Widget_GridView);
	}

	public ContentBasedGridView(Context context) {
		super(context);
		initContentBasedGridView();
	}
	
	private void initContentBasedGridView() {
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightSpec = MeasureSpec.makeMeasureSpec(ViewUtils.maxHeightOfView(), MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightSpec);
	}
	
	@Override
	public int computeVerticalScrollExtent() {
		int computeVerticalScrollExtent = super.computeVerticalScrollExtent();
		return computeVerticalScrollExtent;
	}
	
	@Override
	public int computeVerticalScrollOffset() {
		int computeVerticalScrollOffset = super.computeVerticalScrollOffset();
		return computeVerticalScrollOffset;
	}
	
	@Override
	public int computeVerticalScrollRange() {
		int computeVerticalScrollRange = super.computeVerticalScrollRange();
		return computeVerticalScrollRange;
	}
	
}
