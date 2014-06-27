package org.ixming.base.common.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * define base operations fragments should do as 
 * it recommends here
 * @version 1.0
 */
interface ILocalFragment extends View.OnClickListener, IInjectableActivity {

	/**
	 * define the layout res of the activity
	 */
	int provideLayoutResId();
	
	/**
	 * called before {@link #initData(View, android.os.Bundle)} while
	 * {@link Activity#onCreated(android.os.Bundle)} is running
	 * 
	 * @param view root view of the activity
	 * 
	 * @see #findViewById(int)
	 */
	void initView(View view);
	
	/**
	 * called immediately after {@link #initView(View)} while
	 * {@link Activity#onCreated(android.os.Bundle)} is running
	 * 
	 * @param view root view of the activity
	 * @param savedInstanceState If the activity is being re-created from
     * a previous saved state, this is the state.
	 */
	void initData(View view, Bundle savedInstanceState) ;
	
	/**
	 * initView, initData之后被调用
	 * @added 1.0
	 */
	void initListener();
	
	/**
	 * this method returns the pure View.
	 */
	View getRootView();
	
	/**
	 * 通过ID找到指定的View，并为之添加监听器；<br/>
	 * 
	 * 该方法着重强调此View只需添加点击事件，而不会对之进行状态或者
	 * 显示的改变。
	 */
	ILocalFragment bindClickListener(int resId);
	
	/**
	 * 给指定的View添加监听器
	 */
	ILocalFragment bindClickListener(View view);
	
	/**
	 * 移除resId指定的View的单击事件监听器
	 */
	ILocalFragment removeClickListener(int resId);
	
	/**
	 * 移除View的单击事件监听器
	 */
	ILocalFragment removeClickListener(View view);
	
	/**
	 * 创建一个本Activity的Handler对象，此方法在onCreate()中调用，且
	 * 在initView及initData之前。
	 * @added 1.0
	 */
	Handler provideActivityHandler();
	
	/**
	 * just like findViewById in Activity or View subclasses
	 * @param id target id to find
	 * @return View if it exists or null
	 */
	<T extends View>T findViewById(int id);
	
	/**
	 * return the application context
	 */
	Context getApplicationContext();
	
}
