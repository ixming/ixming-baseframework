package org.ixming.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 去除一个BUG——当该ScrollView附着于一个Fragment，
 * 而外部的Fragment不停切换，当有attach事件触发时，ScrollView中
 * 的某一项会被认为focused，此时，该ScrollView就会滚动到该focused child的相应位置。
 * 
 * @author Yin Yong
 */
public class CustomScrollView extends ScrollView {

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomScrollView(Context context) {
		super(context);
	}
	
	@Override
	public void requestChildFocus(View child, View focused) {
		// 下面是父类的实现，实际情况下该方法在Fragment attach和detach时有可能调用，
		// 此时某个View会无缘无故地获得焦点
		// 导致Fragment重新attach时，滚动到了该View
//		if (!mIsLayoutDirty) {
//            scrollToChild(focused);
//        } else {
//            // The child may not be laid out yet, we can't compute the scroll yet
//            mChildToScrollTo = focused;
//        }
//        super.requestChildFocus(child, focused);
	}

}
