package org.ixming.base.utils.debug;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 判断是否经过了时间间隔（多用于限制某些动作的重复执行）
 * 
 * @author Yin Yong
 *
 */
public class TimerTrigger {

	// interval set
	private final long mInterval;
	// 当前储存的时间
	private AtomicLong mDatetime;
	// 是否需要忽略第一次（因为初始时mDatetime为0，一般必然大于mInterval）
	private boolean mIgnoreFirst = false;
	public TimerTrigger(long interval) {
		this(interval, false);
	}
	
	public TimerTrigger(long interval, boolean ignoreFirst) {
		mInterval = interval;
		mIgnoreFirst = ignoreFirst;
		mDatetime = new AtomicLong(0);
	}
	
	/**
	 * 检测是否经过了时间间隔
	 */
	public boolean checkIfIntervalPast() {
		long cur = System.currentTimeMillis();
		long my = mDatetime.get();
		if (my == 0) {
			mDatetime.set(cur);
			if (mIgnoreFirst) {
				return false;
			}
			return true;
		} else {
			if (cur - my > mInterval) {
				mDatetime.set(cur);
				return true;
			}
			return false;
		}
	}
	
}
