package org.ixming.base.image;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Bitmap;

class UrlImageCacheToken extends WeakReference<Bitmap> {

	private String mUrl;
	private final AtomicInteger mCount = new AtomicInteger(0);
	private final int mByteCount;
	public UrlImageCacheToken(String url, Bitmap bm, ReferenceQueue<Bitmap> queue) {
		super(bm, queue);
		this.mUrl = url;
		int biSize = bm.getRowBytes();
		int hei = bm.getHeight();
		mByteCount = biSize * hei;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public void increment() {
		synchronized (mCount) {
			if (getRefCount() < 0) {
				mCount.set(0);
			}
			mCount.incrementAndGet();
		}
	}
	
	public void decrement() {
		synchronized (mCount) {
			if (getRefCount() >= 1) {
				mCount.decrementAndGet();
			}
		}
	}
	
	public void setRefCount(int count) {
		synchronized (mCount) {
			mCount.set(Math.max(0, count));
		}
	}
	
	public void addRefCount(int delta) {
		synchronized (mCount) {
			mCount.set(getRefCount() + Math.max(0, delta));
		}
	}
	
	public int getRefCount() {
		synchronized (mCount) {
			return Math.max(0, mCount.get());
		}
	}
	
	public int getByteCount() {
		return mByteCount;
	}
	
	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof UrlImageCacheToken)) {
			return false;
		}
		UrlImageCacheToken another = (UrlImageCacheToken) o;
		return mUrl.equals(another.mUrl);
	}
	
	@Override
	public int hashCode() {
		return mUrl.hashCode();
	}
	
	@Override
	public String toString() {
		return "{ url = " + getUrl() 
				+ ", byteCount = " + getByteCount()
				+ ", refCount =" + getRefCount()+ " }";
	}
}
