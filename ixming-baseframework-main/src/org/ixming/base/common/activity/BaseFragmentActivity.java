package org.ixming.base.common.activity;

import org.ixming.base.image.ImageUtil;
import org.ixming.inject4android.InjectConfigure;
import org.ixming.inject4android.InjectorUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseFragmentActivity extends FragmentActivity implements ILocalActivity {
	
	private ActivityControl mActivityControl = ActivityControl.getInstance();
	private View mRootView;
	protected Context context;
	protected Context appContext;
	protected Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mActivityControl.onActivityCreate(this, getIntent());
		ImageUtil.getInstance().onActivityCreate(this);
		
		super.onCreate(savedInstanceState);
		context = this;
		appContext = getApplicationContext();
		handler = provideActivityHandler();
		mRootView = LayoutInflater.from(this).inflate(provideLayoutResId(), null);
		setContentView(mRootView);
		
		if (useInjectBeforeInitView()) {
			injectSelf();
		}
		
		prepareInitView(mRootView);
		initView(mRootView);
		prepareInitData(mRootView, savedInstanceState);
		initData(mRootView, savedInstanceState);
		initListener();
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
	@Override
	public InjectConfigure provideInjectConfigure() {
		return null;
	}
		
	/**
	 * 初始时，是否主动使用动态注入，默认是使用（true）
	 */
	@Override
	public boolean useInjectBeforeInitView() {
		return true;
	}
	
	/**
	 * 调用该方法，动态注入
	 */
	@Override
	public void injectSelf() {
		InjectConfigure provided = provideInjectConfigure();
		if (null != provided) {
			injectSelf(provided);
		} else {
			InjectorUtils.defaultInstance().inject(this);
		}
	}

	/**
	 * 根据提供的配置，调用该方法，动态注入
	 */
	@Override
	public void injectSelf(InjectConfigure configure) {
		InjectorUtils.instanceBuildFrom(configure).inject(this);
	}
	
	/**
	 * @param target 需要注入的对象
	 * @param rootView 所在的View
	 */
	@Override
	public void injectTarget(Object target, View rootView) {
		InjectorUtils.defaultInstance().inject(target, rootView);
	}
	
	/**
	 * @param target 需要注入的对象
	 * @param rootView 所在的View
	 * @param configure 注入的配置
	 */
	@Override
	public void injectTarget(Object target, View rootView, InjectConfigure configure) {
		InjectorUtils.instanceBuildFrom(configure).inject(target, rootView);
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		mActivityControl.onActivityNewIntent(this, intent);
		ImageUtil.getInstance().onActivityCreate(this);
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onResume() {
		mActivityControl.onActivityResume(this);
		ImageUtil.getInstance().onActivityResumed(this);
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
		ImageUtil.getInstance().onActivityDestoryed(this);
		super.onDestroy();
	}

	@Override
	public final View getRootView() {
		return mRootView;
	}

	@Override
	public BaseFragmentActivity bindClickListener(int resId) {
		return bindClickListener(findViewById(resId));
	}

	@Override
	public BaseFragmentActivity bindClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(this);
		}
		return this;
	}

	@Override
	public BaseFragmentActivity removeClickListener(int resId) {
		return removeClickListener(findViewById(resId));
	}

	@Override
	public BaseFragmentActivity removeClickListener(View view) {
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
	@Override
	public void startActivity(Class<? extends Activity> clz) {
		mActivityControl.startActivity(clz, 0);
	}

	@Override
	public void startActivity(Class<? extends Activity> clz, int flags) {
		mActivityControl.startActivity(clz, flags);
	}

	/**
	 * [推荐使用] 启动新的Activity，并等待结果回调
	 */
	@Override
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

	@Override
	public final void superStartActivity(Intent intent) {
		super.startActivity(intent);
	}

	@Override
	public final void superStartActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public final void superFinish() {
		super.finish();
	}
}
