package org.ixming.base.common.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/*package*/ class ActivityToken implements Parcelable {

	// 实例池
	private static final Object sPoolSync = new Object();
	private static int sPoolSize = 0;
	private static final int MAX_POOL_SIZE = 10;
	private static final SpecLinkedList<ActivityToken> sPool
	= new SpecLinkedList<ActivityToken>();
	
	/*package*/ static ActivityToken obtain() {
		synchronized (sPoolSync) {
			if (!sPool.isEmpty()) {
				sPoolSize--;
				return sPool.pop();
			}
		}
		return new ActivityToken();
	}

	private Intent mSourceIntent;
	private String mSourceActivityClass;
	private Activity mSourceActivityRef;
	private String mActivityClass;
	private Activity mActivityRef;

	/**
	 * 当前的综合状态值
	 */
	private int mState = START_STATE_UNDEFINED | LIFECYCLE_STATE_UNDEFINED;
	
	private boolean mIsFinishedBeforeCreated = false;
	
	// Activity的启动状态 >>>>> start
	private static final int START_STATE_MARK = 0xF;
	private static final int START_STATE_UNDEFINED = 0x0;
	// 调用了startActivity，但是目标Activity还没有真正create
	private static final int START_STATE_PENDING = 0x1;
	// 目标Activity onCreate调用（onNewIntent仍然）
	private static final int START_STATE_CREATED = 0x2;
	// 目标Activity finish调用
	private static final int START_STATE_PENDING_FINISHED = 0x3;
	// 目标Activity onDestroy调用
	private static final int START_STATE_FINISHED = 0x4;
	// Activity的启动状态 >>>>> end
	private void setStartState(int newState) {
		int oldState = mState;
		mState = getLifecycleState(oldState) | getStartState(newState);
	}
	private int getStartState(int state) {
		return state & START_STATE_MARK;
	}
	
	// Activity的显示状态 >>>>> start
	private static final int LIFECYCLE_STATE_MARK = 0xF0;
	private static final int LIFECYCLE_STATE_UNDEFINED = 0x00;
	// 目标Activity正在显示（onResume之后的状态）
	private static final int LIFECYCLE_STATE_SHOWING = 0x10;
	// 目标Activity被其他窗口覆盖（onPause之后的状态）
	private static final int LIFECYCLE_STATE_HIDING = 0x20;
	// Activity的显示状态 >>>>> end
	private void setLifecycleState(int newState) {
		int oldState = mState;
		mState = getStartState(oldState) | getLifecycleState(newState);
	}
	private int getLifecycleState(int state) {
		return state & LIFECYCLE_STATE_MARK;
	}
	
	private ActivityToken() { }
	
	/**
	 * 设置目标Activity启动的Intent
	 */
	/*package*/ void prepareStartActivity(Activity from,
			String targetClass, Intent intent) {
		setStartState(START_STATE_PENDING);
		mSourceActivityRef = from;
		mSourceActivityClass = from.getClass().getName();
		mSourceIntent = intent;
		
		mActivityClass = targetClass;
	}
	
	/**
	 * 设置目标Activity（需要Activity真实启动后的实例）
	 */
	/*package*/ void setInstanceOnCreated(Activity activity, Intent intent) {
		setStartState(START_STATE_CREATED);
		mActivityRef = activity;
		mActivityClass = activity.getClass().getName();
		
		mSourceIntent = intent;
	}
	
	/*package*/ void setInstanceOnNewIntent(Activity activity, Intent intent) {
		setStartState(START_STATE_CREATED);
		mActivityClass = activity.getClass().getName();
		mActivityRef = activity;
		
		mSourceIntent = intent;
	}
	
	/*package*/ void onActivityResume() {
		setLifecycleState(LIFECYCLE_STATE_SHOWING);
	}
	
	/*package*/ void onActivityPause() {
		setLifecycleState(LIFECYCLE_STATE_HIDING);
	}
	
	/**
	 * 目标Activity的finish方法调用
	 */
	/*package*/ void prepareFinishActivity() {
		setStartState(START_STATE_PENDING_FINISHED);
	}
	
	/**
	 * 目标Activity的被强制finish
	 */
	/*package*/ void setFinishedBeforeCreated() {
		mIsFinishedBeforeCreated = true;
	}
	
	/*package*/ void onActivityDestroyed() {
		setStartState(START_STATE_FINISHED);
	}
	
	/**
	 * 获取目标Activity启动的Intent
	 */
	public Intent getSourceIntent() {
		return mSourceIntent;
	}
	
	/*package*/ Activity getSourceActivity() {
		return mSourceActivityRef;
	}
	
	public String getSourceActivityClass() {
		return mSourceActivityClass;
	}
	
	/**
	 * 获取目标Activity（需要Activity真实启动后的实例）
	 */
	/*package*/ Activity getActivity() {
		return mActivityRef;
	}
	
	public String getActivityClass() {
		return mActivityClass;
	}
	
	/*package*/ boolean isFinishedBeforeCreated() {
		return mIsFinishedBeforeCreated;
	}
	
	/*package*/ boolean isActivityPendingStart() {
		return getStartState(mState) == START_STATE_PENDING;
	}
	
	/*package*/ boolean isActivityAlive() {
		return getStartState(mState) == START_STATE_CREATED;
	}
	
	/*package*/ boolean isActivityFinished() {
		int state = getStartState(mState);
		return state == START_STATE_PENDING_FINISHED
				|| state == START_STATE_FINISHED;
	}
	
	/*package*/ boolean isActivityShowingOnScreen() {
		return isActivityAlive()
				&& getLifecycleState(mState) == LIFECYCLE_STATE_SHOWING;
	}
	
	/*package*/ boolean isActivityHidingFromScreen() {
		return isActivityAlive()
				&& getLifecycleState(mState) == LIFECYCLE_STATE_HIDING;
	}
	
	/**
	 * 回收对象
	 */
	public void recycle() {
		if (null != mActivityRef) {
			mActivityRef = null;
		}
		mActivityClass = null;
		if (null != mSourceActivityRef) {
			mSourceActivityRef = null;
		}
		mSourceActivityClass = null;
		mSourceIntent = null;
		
		mState = START_STATE_UNDEFINED | LIFECYCLE_STATE_UNDEFINED;
		mIsFinishedBeforeCreated = false;
		
		synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
            	sPool.push(this);
                sPoolSize++;
            }
        }
	}
	
	@Override
	public String toString() {
		String activityInfo = (null == mActivityRef)
				? mActivityClass : mActivityRef.toString();
		String fromactivityInfo = (null == mSourceActivityRef)
				? mSourceActivityClass : mSourceActivityRef.toString();
		return "ActivityToken { " + activityInfo 
				+ ", From = " + fromactivityInfo
				+ ", State = 0x" + Integer.toHexString(mState)
				+ " }";
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mActivityClass);
		dest.writeString(mSourceActivityClass);
		dest.writeParcelable(mSourceIntent, flags);
		dest.writeInt(mState);
	}
	
	public static final Parcelable.Creator<ActivityToken> CREATOR 
		= new Parcelable.Creator<ActivityToken>() {
		public ActivityToken createFromParcel(Parcel in) {
			return ActivityToken.obtain().readFromParcel(in);
		}

		public ActivityToken[] newArray(int size) {
			return new ActivityToken[size];
		}
	};

	private ActivityToken readFromParcel(Parcel in) {
		return this;
	}
	
	private ActivityToken(Parcel in) {
		mActivityClass = in.readString();
		mSourceActivityClass = in.readString();
		mSourceIntent = in.readParcelable(ActivityToken.class.getClassLoader());
		mState = in.readInt();
	}

}
