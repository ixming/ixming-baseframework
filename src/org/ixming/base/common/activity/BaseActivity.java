package org.ixming.base.common.activity;


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
		initView(mRootView);
		initListener();
		prepareInitData(mRootView, savedInstanceState);
		initData(mRootView, savedInstanceState);
	}

	void prepareInitView(View rootView) {
	};

	void prepareInitData(View rootView, Bundle savedInstanceState) {
	};
	

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