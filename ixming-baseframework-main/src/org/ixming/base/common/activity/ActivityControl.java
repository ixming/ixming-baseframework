package org.ixming.base.common.activity;

import java.util.Iterator;

import org.ixming.base.common.BaseApplication;
import org.ixming.base.common.LocalBroadcasts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 管理Activity，并方便用于整个APP中界面的Context的使用、调度
 * 
 * @author Yin Yong
 */
public class ActivityControl {

	private static final String TAG = ActivityControl.class.getSimpleName();

	// >>>>>>>>>>>>>>>>>>>>>
	// open API
	/**
	 * 对外的广播Action
	 */
	public static final String ACTION_ACTIVITY_EVENTS = "activitycontrol.ACTION_ACTIVITY_EVENTS";
	/**
	 * 事件类型
	 * 
	 * @see {@link #TYPE_START}
	 * @see {@link #TYPE_TOP_CHANGED}
	 * @see {@link #TYPE_FINISH}
	 */
	public static final String EXTRA_TYPE = "extra_type";
	/**
	 * 事件指定的Activity类 <br/>
	 * <br/>
	 * 事件{@link #TYPE_START} 、{@link #TYPE_FINISH} 时会传入
	 */
	public static final String EXTRA_ACTIVITY = "extra_activity";
	/**
	 * 启动当前Activity（{@code ActivityControl#EXTRA_ACTIVITY}）的类 <br/>
	 * <br/>
	 * 事件{@link #TYPE_START} 时会传入
	 */
	public static final String EXTRA_FROM_ACTIVITY = "extra_from_activity";

	/**
	 * 事件类型：启动Activity
	 */
	public static final int TYPE_START = 0x1;
	/**
	 * 事件类型：顶部显示的Activity被替换
	 */
	public static final int TYPE_TOP_CHANGED = 0x2;
	/**
	 * 事件类型：Activity被关闭
	 */
	public static final int TYPE_FINISH = 0x3;

	/**
	 * 当ActivityCreate时，认为可以触发该事件
	 */
	private static void onPrepareStartActivity(ActivityToken token) {
		Log.d(TAG, "onPrepareStartActivity");
		LocalBroadcasts.sendLocalBroadcast(
				new Intent(ACTION_ACTIVITY_EVENTS)
						.putExtra(EXTRA_TYPE, TYPE_START)
						.putExtra(EXTRA_ACTIVITY, token.getActivityClass())
						.putExtra(EXTRA_FROM_ACTIVITY,
								token.getSourceActivityClass()));
	}

	/**
	 * 当ActivityDestroy时，认为可以触发该事件
	 */
	private static void onPrepareFinishActivity(ActivityToken token) {
		Log.d(TAG, "onPrepareStartActivity");
		LocalBroadcasts.sendLocalBroadcast(
				new Intent(ACTION_ACTIVITY_EVENTS).putExtra(EXTRA_TYPE,
						TYPE_FINISH).putExtra(EXTRA_ACTIVITY,
						token.getActivityClass()));
	}

	/**
	 * 当ActivityResume时，认为可以触发该事件
	 */
	private static void onActivityTopChanged(ActivityToken token) {
		Log.d(TAG, "onActivityTopChanged");
		LocalBroadcasts.sendLocalBroadcast(
				new Intent(ACTION_ACTIVITY_EVENTS).putExtra(EXTRA_TYPE,
						TYPE_TOP_CHANGED).putExtra(EXTRA_ACTIVITY,
						token.getActivityClass()));
	}

	// <<<<<<<<<<<<<<<<<<<<<

	// >>>>>>>>>>>>>>>>>>>>>
	// singleTon
	private static ActivityControl sInstance;

	/**
	 * @return the singleton of the class
	 */
	public synchronized static ActivityControl getInstance() {
		if (null == sInstance) {
			sInstance = new ActivityControl();
		}
		return sInstance;
	}

	static interface IActivityTokener {
	}

	// 正在运行的Activity
	private SpecLinkedList<ActivityToken> mActivityStack = new SpecLinkedList<ActivityToken>();
	// 调用了startActivity但是没有正在打开的Activity
	private SpecLinkedList<ActivityToken> mPendingTokenStack = new SpecLinkedList<ActivityToken>();

	private ActivityControl() {
	}

	// 对外的API--open API
	/**
	 * PS：如果栈顶的Activity没有显示在屏幕中——状态为Pause， 同样会返回null。
	 * 
	 * @return 显示在当前应用最上层的Activity
	 */
	public synchronized Activity getTopActivity() {
		// return getActivityFromTop(0);
		ActivityToken top = null;
		if (!mActivityStack.isEmpty()) {
			top = mActivityStack.peek();
		}
		if (null != top) {
			return top.getActivity();
		}
		return null;
	}

	/**
	 * 如果Activity栈为空，
	 * 
	 * @param index
	 *            如果 index < 0 返回null
	 */
	public synchronized Activity getActivityFromTop(int index) {
		if (index < 0) {
			return null;
		}
		// REMIND 应用全部的Activity已经退出，但是还没有被杀掉；
		// 这种情况能够调用，但是需要避免此种情况下调用时crash
		if (mActivityStack.isEmpty()) {
			return null;
		}
		index = Math.max(0, mActivityStack.size() - 1 - index);
		ActivityToken token = mActivityStack.get(index);
		return token.getActivity();
	}

	/**
	 * instance指定的Activity是否是由clz指定的Activity类启动
	 */
	public boolean isActivityFrom(Activity instance, String className) {
		Activity act = instance;
		ActivityToken token;
		while (true) {
			if (null == act) {
				break;
			}
			token = searchActFromStackTop1(act);
			if (null == token) {
				break;
			}
			String sourceClass = token.getSourceActivityClass();
			if (className.equals(sourceClass)) {
				return true;
			}
			act = token.getSourceActivity();
			// token = searchFirstFromTop(sourceClass, false);
		}
		return false;
	}

	/**
	 * 从Activity栈中获取第一个类型为clz指定类的Activity。 <br/>
	 * <br/>
	 * 如果没有，则返回null。
	 */
	public Activity getActivityByClass(String className) {
		ActivityToken token = searchFirstFromTop(className, false);
		if (null == token) {
			return null;
		}
		return token.getActivity();
	}

	/**
	 * 判断当前应用是否正在设备上打开着。
	 */
	public synchronized boolean isAppRunning() {
		if (mActivityStack.isEmpty()) {
			return false;
		}
		ActivityToken top = mActivityStack.peek();
		return null != top && null != top.getActivity()
				&& top.isActivityAlive();
	}

	/**
	 * 判断当前应用是否正在设备上打开着。 <br/>
	 * <br/>
	 * 判断机制有二：<br/>
	 * ·栈顶的Activity是否已经创建<br/>
	 * ·栈顶的Activity是否处在resume生命周期<br/>
	 */
	/* package */synchronized boolean isAppShowingOnScreen() {
		if (mActivityStack.isEmpty()) {
			return false;
		}
		ActivityToken top = mActivityStack.peek();
		return null != top && null != top.getActivity()
				&& top.isActivityShowingOnScreen();
	}

	/**
	 * instance指定的Activity是否处在Activity栈顶部
	 */
	/* package */boolean isActivityOnTop(Activity instance) {
		return getTopActivity() == instance;
	}

	// >>>>>>>>>>>>>>>>>>>
	// start new Activities, finish old Activities
	/**
	 * 启动一个新的Activity
	 * 
	 * @param clz
	 *            将要启动的Activity的Class
	 */
	/* package */<T extends Activity> void startActivity(Class<T> clz, int flags) {
		startActivityForResult(clz, flags, -1);
	}

	/**
	 * 与 {@link #startActivity(Class)} 不同， 本方法是为需要使用
	 * {@link Activity#startActivity(Intent)} 的地方 准备的。
	 * 
	 * <p>
	 * XXBaseActivity中{@link Activity#startActivity(Intent)}一般已经实现了
	 * 该方法，所以它是为体制之外的Activity准备的。
	 * </p>
	 * 
	 * @param intent
	 *            The intent to start
	 */
	/* package */<T extends Activity> void beforeStartActivityForBase(
			Intent intent) {
		if (null == intent.getComponent()) {
			//REMIND 如果是通过Action打开Activity，不予处理
			return ;
		}
		String className = intent.getComponent().getClassName();
		Activity top = getTopActivity();
		if (null == top) {
			Log.w(TAG, "startActivity top == null!");
			return ;
		}
		// 如果同时start多个相同的Activity
		if (shouldStart(top, className)) {
			prepareStartActivity(top, className, intent);
		} else {
			Log.w(TAG, "startActivity clz is null or multi repeated start!");
		}	
	}

	/* package */<T extends Activity> void startActivityForResult(Class<T> clz,
			int flags, int requestCode) {
		Activity top = getTopActivity();
		if (null != top) {
			String className = clz.getName();
			// 如果同时start多个相同的Activity
			if (shouldStart(top, className)) {
				Intent intent = new Intent(top, clz);
				intent.addFlags(flags);
				prepareStartActivity(top, className, intent);
				top.startActivityForResult(intent, requestCode);
			} else {
				Log.w(TAG, "startActivityForResult multi repeated start!");
			}
		} else {

			Log.w(TAG, "startActivityForResult top == null!");
		}
	}

	/**
	 * 从栈顶查找，并结束目标Activity
	 * 
	 * @param activity
	 *            要查找的Activity的对象
	 */
	/* package */void finishActivity(Activity activity) {
		finishActivity0(activity);
		activity.finish();
	}

	/**
	 * 为了XXBaseActivity单独出来的方法。
	 * <p>
	 * 该种情况下不需要调用Activity.finish();
	 * </p>
	 */
	/* package */void finishActivity0(Activity activity) {
		ActivityToken token = searchFirstFromTop(activity, false);
		Log.d(TAG, "finishActivity<Activity> token = " + token);
		if (null == token) {
			return;
		}
		prepareFinishActivity(token);
	}

	/* package */boolean shouldStart(Activity from, String className) {
		ActivityToken top = peekTopToken();
		if (null == top) {
			return true;
		}
		// REMIND
		return from != top.getSourceActivity()
				|| (null != top.getActivityClass() && top.getActivityClass()
						.equals(className));
	}

	// <<<<<<<<<<<<<<<<< end

	
	public void clearAll() {
		Log.d(TAG, "clearAll mActivityStack = " + mActivityStack.size());
		if (!mActivityStack.isEmpty()) {
			for (ActivityToken token : mActivityStack) {
				Activity act = token.getActivity();
				if (null != act) {
					act.finish();
				} else {
					if (token.isActivityPendingStart())
						token.setFinishedBeforeCreated();
				}
			}
		}
		
		if (!mPendingTokenStack.isEmpty()) {
			for (ActivityToken token : mPendingTokenStack) {
				token.setFinishedBeforeCreated();
			}
		}
	}
	/**
	 * 从栈顶查找，并结束目标Activity以上的Activity
	 * 
	 * @param act
	 *            要查找的Activity的对象
	 * @param includeTarget
	 *            是否finish掉clz指定的Activity
	 */
	/* package */void popClear(Activity act, boolean includeTarget) {
		checkActivityInstance(act);
		int index = searchActFromStackTop2(act);
		if (index < 0) {
			Log.w(TAG, "searchFromTopAndClearAll index < 0");
			return;
		}
		SpecLinkedList<ActivityToken> sub = mActivityStack.subList(index);
		if (null == sub || sub.isEmpty()) {
			Log.w(TAG, "searchFromTopAndClearAll sub is empty");
			return;
		}
		if (!includeTarget) {
			// remove target from this sub list
			sub.pop();
		}
		for (ActivityToken token : sub) {
			if (null == token.getActivity()) {
				if (token.isActivityPendingStart()) {
					token.setFinishedBeforeCreated();
				}
				continue;
			}
			token.getActivity().finish();
		}
	}

	/* package */ActivityToken searchActFromStackTop1(Activity act) {
		for (ActivityToken token : mActivityStack) {
			if (act == token.getActivity()) {
				return token;
			}
		}
		return null;
	}

	/* package */int searchActFromStackTop2(Activity act) {
		int index = mActivityStack.size() - 1;
		for (ActivityToken token : mActivityStack) {
			if (act == token.getActivity()) {
				return index;
			}
			index--;
		}
		return -1;
	}

	// internal API
	/**
	 * 机制中，该方法设定为Activity在startActivity时调用
	 */
	/* package */void prepareStartActivity(Activity from, String className,
			Intent intent) {
		Log.d(TAG, "prepareStartActivity");
		// Android的机制，连续startActivity，
		// 先显示栈顶（最后调用的startActivity对应的Activity）的，
		// 中间的Activity暂不执行，在按返回键的时候，依次以出栈的顺序执行onCreate

		// 所以，只需要相应地以栈的形式，获取Pending ActivityToken即可
		ActivityToken token = ActivityToken.obtain();
		token.prepareStartActivity(from, className, intent);
		mPendingTokenStack.push(token);
		onPrepareStartActivity(token);
		// mActivityStack.push(token);
	}

	/* package */void prepareFinishActivity(ActivityToken token) {
		if (null == token.getActivity()) {
			if (token.isActivityPendingStart()) {
				token.setFinishedBeforeCreated();
			}
		}
		token.prepareFinishActivity();
		onPrepareFinishActivity(token);
	}

	/* package */void onActivityCreate(Activity instance, Intent intent) {
		Log.d(TAG, "onActivityCreate");
		// checkActivityInstance(instance);
		if (mPendingTokenStack.isEmpty()) {
			Log.w(TAG, "onActivityCreate pending token is empty!");
		}
		if (isLauncherIntent(intent)) {
			Log.d(TAG, "onActivityCreate isLauncherIntent!");
			// clear, if any cache remained
			mPendingTokenStack.clear();
			mActivityStack.clear();
			Log.d(TAG, "onActivityCreate launcher!");
			ActivityToken token = ActivityToken.obtain();
			token.setInstanceOnCreated(instance, intent);
			mActivityStack.push(token);
			return;
		}
		ActivityToken token = null;
		if (!mPendingTokenStack.isEmpty()) {
			token = mPendingTokenStack.pop();
		} else {
			token = ActivityToken.obtain();
		}
		// already in mActivitieStack
		mActivityStack.push(token);

		if (token.isFinishedBeforeCreated()) {
			finishActivity(instance);
		} else {
			token.setInstanceOnCreated(instance, intent);
		}
	}

	/* package */void onActivityNewIntent(Activity instance, Intent intent) {
		Log.d(TAG, "onActivityNewIntent");
		// checkActivityInstance(instance);
		ActivityToken token = null;
		if (!mPendingTokenStack.isEmpty()) {
			// pop if needed
			token = mPendingTokenStack.pop();
		}
		// clear history tokens
		searchAndRemove(instance.getClass());
		if (null == token) {
			Log.w(TAG, "onActivityNewIntent Token of instance = " + instance
					+ " not found!");
			onActivityCreate(instance, intent);
		} else {
			// already in mActivitieStack
			mActivityStack.push(token);
			if (token.isFinishedBeforeCreated()) {
				finishActivity(instance);
			} else {
				token.setInstanceOnNewIntent(instance, intent);
			}
		}
	}

	/* package */void onActivityResume(Activity target) {
		Log.d(TAG, "onActivityResume");
		// checkActivityInstance(target);
		ActivityToken token = searchFirstFromTop(target, false);
		if (null == token) {
			Log.w(TAG, "onActivityResume Token of target =" + target
					+ " not found!");
			return;
		}
		token.onActivityResume();
		// REMIND 当ActivityResume时，认为可以触发该事件
		onActivityTopChanged(token);
	}

	/* package */void onActivityPause(Activity target) {
		Log.d(TAG, "onActivityPause");
		// checkActivityInstance(target);
		ActivityToken token = searchFirstFromTop(target, false);
		if (null == token) {
			Log.w(TAG, "onActivityPause Token of target =" + target
					+ " not found!");
			return;
		}
		token.onActivityPause();
	}

	/* package */void onActivityDestroy(Activity target) {
		Log.d(TAG, "onActivityDestroy");
		// checkActivityInstance(target);
		ActivityToken token = searchFirstFromTop(target, true);
		if (null == token) {
			Log.w(TAG, "onActivityDestroy Token of target =" + target
					+ " not found!");
			return;
		}
		token.onActivityDestroyed();
		token.recycle();
	}

	/* package */ActivityToken peekTopToken() {
		return mActivityStack.isEmpty() ? null : mActivityStack.peek();
	}

	/* package */ActivityToken searchFirstFromTop(Activity target,
			boolean remove) {
		Iterator<ActivityToken> ite = mActivityStack.iterator();
		while (ite.hasNext()) {
			ActivityToken token = ite.next();
			if (target == token.getActivity()) {
				if (remove) {
					ite.remove();
				}
				return token;
			}
		}
		return null;
	}

	/* package */ActivityToken searchFirstFromTop(String className,
			boolean remove) {
		Iterator<ActivityToken> ite = mActivityStack.iterator();
		while (ite.hasNext()) {
			ActivityToken token = ite.next();
			if (className.equals(token.getActivityClass())) {
				if (remove) {
					ite.remove();
				}
				return token;
			}
		}
		return null;
	}

	/* package */boolean searchAndRemove(Class<? extends Activity> targetClz) {
		boolean flag = false;
		String className = targetClz.getName();
		Iterator<ActivityToken> ite = mActivityStack.iterator();
		while (ite.hasNext()) {
			ActivityToken token = ite.next();
			if (className.equals(token.getActivityClass())) {
				if (!flag) {
					flag = true;
				}
				ite.remove();
				token.recycle();
			}
		}
		return flag;
	}

	/* package */boolean searchAndRemove(Activity target) {
		boolean flag = false;
		Iterator<ActivityToken> ite = mActivityStack.iterator();
		while (ite.hasNext()) {
			ActivityToken token = ite.next();
			if (target == token.getActivity()) {
				if (!flag) {
					flag = true;
				}
				ite.remove();
				token.recycle();
			}
		}
		return flag;
	}

	/* package */void checkActivityInstance(Activity target) {
		if (target instanceof IActivityTokener) {
			return;
		}
		throw new IllegalArgumentException("Activity = " + target
				+ " is not an instance of IActivityTokener, "
				+ "Please use BaseXXXActivity as superClass");
	}

	/* package */void checkActivityClass(Class<? extends Activity> clz) {
		if (IActivityTokener.class.isAssignableFrom(clz)) {
			return;
		}
		throw new IllegalArgumentException("Class = " + clz
				+ " is not an subclass of IActivityTokener, "
				+ "Please use BaseXXXActivity as superClass");
	}

	/**
	 * 判断是否是启动应用的Intent
	 */
	public static boolean isLauncherIntent(Intent intent) {
		return null != intent && Intent.ACTION_MAIN.equals(intent.getAction())
				&& null != intent.getCategories()
				&& intent.getCategories().contains(Intent.CATEGORY_LAUNCHER);
	}
	
	public static void startNewTaskActivity(Class<? extends Activity> clz, int flags) {
		Context context = BaseApplication.getAppContext();
		context.startActivity(new Intent(context, clz)
			.addFlags(flags)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

}