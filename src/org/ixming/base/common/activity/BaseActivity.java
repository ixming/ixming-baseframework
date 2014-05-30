package org.ixming.base.common.activity;


import org.ixming.inject4android.InjectConfigure;
import org.ixming.inject4android.InjectorUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 基本的Activity，它规定了代码的一些格式，需要相应的遵循，使得结构相对清晰。
 */
public abstract class BaseActivity extends Activity implements ILocalActivity {

	private ActivityControl mActivityControl = ActivityControl.getInstance();
	private View mRootView;
	protected Context context;
	protected Context appContext;
	protected Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mActivityControl.onActivityCreate(this, getIntent());
		super.onCreate(savedInstanceState);
		context = this;
		appContext = getApplicationContext();
		handler = provideActivityHandler();
		if (getLayoutResId() != 0) {
			mRootView = LayoutInflater.from(this).inflate(getLayoutResId(),
					null);
			setContentView(mRootView);
			prepareInitView(mRootView);

		}
		
		if (useInjectBeforeInitView()) {
			injectSelf();
		}
		
		initView(mRootView);
		initListener();
		prepareInitData(mRootView, savedInstanceState);
		initData(mRootView, savedInstanceState);
	}

	void prepareInitView(View rootView) {
	};

	void prepareInitData(View rootView, Bundle savedInstanceState) {
	};
	
	// utilities for injecting
	/**
	 * used by {@link #injectSelf()};
	 * <p/>
	 * default return null.
	 */
	protected InjectConfigure provideActivityInjectConfigure() {
		return null;
	}
		
	/**
	 * 初始时，是否主动使用动态注入，默认是使用（true）
	 */
	protected boolean useInjectBeforeInitView() {
		return true;
	}
	
	/**
	 * 调用该方法，动态注入
	 */
	protected void injectSelf() {
		InjectConfigure provided = provideActivityInjectConfigure();
		if (null != provided) {
			injectSelf(provided);
		} else {
			InjectorUtils.defaultInstance().inject(this);
		}
	}

	/**
	 * 根据提供的配置，调用该方法，动态注入
	 */
	protected void injectSelf(InjectConfigure configure) {
		InjectorUtils.instanceBuildFrom(configure).inject(this);
	}
	
	/**
	 * @param target 需要注入的对象
	 * @param rootView 所在的View
	 */
	protected void injectTarget(Object target, View rootView) {
		InjectorUtils.defaultInstance().inject(target, rootView);
	}
	
	/**
	 * @param target 需要注入的对象
	 * @param rootView 所在的View
	 * @param configure 注入的配置
	 */
	protected void injectTarget(Object target, View rootView, InjectConfigure configure) {
		InjectorUtils.instanceBuildFrom(configure).inject(target, rootView);
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		mActivityControl.onActivityNewIntent(this, intent);
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onResume() {
		mActivityControl.onActivityResume(this);
		super.onResume();

	}

	@Override
	protected void onPause() {
		mActivityControl.onActivityPause(this);
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		mActivityControl.onActivityDestroy(this);
		super.onDestroy();
	}

	@Override
	public final View getRootView() {
		return mRootView;
	}

	@Override
	public BaseActivity bindClickListener(int resId) {
		return bindClickListener(findViewById(resId));
	}

	@Override
	public BaseActivity bindClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(this);
		}
		return this;
	}

	@Override
	public BaseActivity removeClickListener(int resId) {
		return removeClickListener(findViewById(resId));
	}

	@Override
	public BaseActivity removeClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(null);
		}
		return this;
	}

	@Override
	public void onClick(View v) {
	}
	
	/**
	 * <p>
	 * base基类中已经有了默认的实现，如果有特殊的需要，请重写此方法； 该默认实现不保证符合所有的特殊情况。
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public boolean customBack() {
		finish();
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (customBack()) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	/**
	 * [已修改] 没有任何效果，使用{@link #superStartActivity(Intent)} <br/>
	 * <br/>
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void startActivity(Intent intent) {
		// 在startActivityForResult已有
		// mActivityControl.beforeStartActivityForBase(intent);
		superStartActivity(intent);
	}

	/**
	 * [已修改] 没有任何效果，使用{@link #superStartActivityForResult(Intent, int)} <br/>
	 * <br/>
	 * 
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// 在startActivityForResult已有
		mActivityControl.beforeStartActivityForBase(intent);
		superStartActivityForResult(intent, requestCode);
	}

	/**
	 * [推荐使用] 启动新的Activity
	 */
	public void startActivity(Class<? extends Activity> clz) {
		mActivityControl.startActivity(clz, 0);
	}

	public void startActivity(Class<? extends Activity> clz, int flags) {
		mActivityControl.startActivity(clz, flags);
	}

	/**
	 * [推荐使用] 启动新的Activity，并等待结果回调
	 */
	public void startActivityForResult(Class<? extends Activity> clz,
			int requestCode) {
		mActivityControl.startActivityForResult(clz, 0, requestCode);
	}

	/**
	 * [已修改] 结束当前Activity <br/>
	 * <br/>
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		mActivityControl.finishActivity0(this);
		superFinish();
	}

	public final void superStartActivity(Intent intent) {
		super.startActivity(intent);
	}

	public final void superStartActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	public final void superFinish() {
		super.finish();
	}
}