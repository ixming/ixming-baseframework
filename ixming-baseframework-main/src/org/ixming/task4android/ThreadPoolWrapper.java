package org.ixming.task4android;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.ixming.base.utils.debug.DummyComparator;

class ThreadPoolWrapper extends AbstractQueue<Runnable>
implements BlockingQueue<Runnable> {

	private static final Comparator<? super Runnable> sDefComparator = new DummyComparator<Runnable>();
	
	private final CompWrapper mComparator = new CompWrapper();
	private final BlockingQueue<Runnable> mDefQueue;
	private final BlockingQueue<Runnable> mPriorityQueue;
	{
		mComparator.setComparator(sDefComparator);
		mDefQueue = new LinkedBlockingQueue<Runnable>();
		mPriorityQueue = new PriorityBlockingQueue<Runnable>(50, mComparator);
	}
	// default 
	private BlockingQueue<Runnable> mQueue = mDefQueue;
	
	public ThreadPoolWrapper() {
	}
	
	/**
	 * 默认是LinkedBlockingQueue
	 */
	public void setPriorityBlockingQueue(Comparator<? super Runnable> comparator) {
		mComparator.setComparator(comparator);
		BlockingQueue<Runnable> newQueue = mPriorityQueue;
		if (newQueue != mQueue) {
			mQueue.drainTo(newQueue);
		}
		mQueue = newQueue;
	}
	
	public void setDefBlockingQueue() {
		BlockingQueue<Runnable> newQueue = mDefQueue;
		if (newQueue != mQueue) {
			mQueue.drainTo(newQueue);
		}
		mQueue = newQueue;
	}
	
	@Override
	public boolean offer(Runnable e) {
		if (null != mQueue) {
			return mQueue.offer(e);
		}
		return false;
	}

	@Override
	public Runnable poll() {
		if (null != mQueue) {
			return mQueue.poll();
		}
		return null;
	}

	@Override
	public Runnable peek() {
		if (null != mQueue) {
			return mQueue.peek();
		}
		return null;
	}

	@Override
	public Iterator<Runnable> iterator() {
		if (null != mQueue) {
			return mQueue.iterator();
		}
		return null;
	}

	@Override
	public int size() {
		if (null != mQueue) {
			return mQueue.size();
		}
		return 0;
	}
	
	// >>>>>>>>>>>>>>>>>>>
	@Override
	public void put(Runnable e) throws InterruptedException {
		if (null != mQueue) {
			mQueue.put(e);
		}
	}

	@Override
	public boolean offer(Runnable e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (null != mQueue) {
			return mQueue.offer(e, timeout, unit);
		}
		return false;
	}

	@Override
	public Runnable take() throws InterruptedException {
		if (null != mQueue) {
			return mQueue.take();
		}
		return null;
	}

	@Override
	public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
		if (null != mQueue) {
			return mQueue.poll(timeout, unit);
		}
		return null;
	}

	@Override
	public int remainingCapacity() {
		if (null != mQueue) {
			return remainingCapacity();
		}
		return 0;
	}

	@Override
	public int drainTo(Collection<? super Runnable> c) {
		if (null != mQueue) {
			return mQueue.drainTo(c);
		}
		return 0;
	}

	@Override
	public int drainTo(Collection<? super Runnable> c, int maxElements) {
		if (null != mQueue) {
			return mQueue.drainTo(c, maxElements);
		}
		return 0;
	}
}
