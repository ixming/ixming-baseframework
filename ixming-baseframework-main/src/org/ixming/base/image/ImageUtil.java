package org.ixming.base.image;

import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Iterator;

import org.ixming.base.file.FileCompositor;
import org.ixming.base.file.app.LocalFileUtility;
import org.ixming.base.taskcenter.async.TaskHandler;
import org.ixming.base.utils.android.LogUtils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ImageUtil {
	
	private static final String TAG = ImageUtil.class.getSimpleName();
	
	private static ImageUtil sInstance;
	private static final Object mSyncToken = new Object();

	public static ImageUtil getInstance() {
		synchronized (ImageUtil.class) {
			if (sInstance == null) {
				sInstance = new ImageUtil();
			}
			return sInstance;
		}
	}
	
	// >>>>>>>>>>>>>>>>>>>>>
	// 基于界面
	static final ReferenceQueue<Bitmap> sRefQueue = new ReferenceQueue<Bitmap>();
	
	static final WeakHashMapImpl<Object, HashSet<UrlImageCacheToken>> sUICache
		= new WeakHashMapImpl<Object, HashSet<UrlImageCacheToken>>() {
		protected void onEntryRemoved(Object key, java.util.HashSet<UrlImageCacheToken> value) {
			LogUtils.w(TAG, "sUICache onEntryRemoved");
			ImageUtil.getInstance().clearFromSet(value);
		}
	};
	private Object mCurrentUIToken;
	public void onActivityCreate(Object uiObj) {
		synchronized (mSyncToken) {
			LogUtils.i(TAG, "onActivityCreate !!!");
			mCurrentUIToken = uiObj;
			checkUICacheAndGetUrlList(uiObj);
		}
	}
	
	public void onActivityResumed(Object uiObj) {
		synchronized (mSyncToken) {
			LogUtils.i(TAG, "onActivityResumed !!!");
			poll();
			mCurrentUIToken = uiObj;
		}
	}
	
	public void onActivityDestoryed(Object uiObj) {
		synchronized (mSyncToken) {
			LogUtils.i(TAG, "onActivityDestoryed !!!");
			poll();
			clearFromSet(sUICache.remove(uiObj));
		}
	}
	
	private void clearFromSet(HashSet<UrlImageCacheToken> tokens) {
		if (null == tokens) {
			return ;
		}
		for (UrlImageCacheToken token : tokens) {
			token.decrement();
			if (token.getRefCount() < 1) {
				removeCacheUrl(token.getUrl());
			}
		}
	}
	
	void checkShouldIncrement(UrlImageCacheToken token) {
		synchronized (mSyncToken) {
			if (null == mCurrentUIToken) {
				return ;
			}
			HashSet<UrlImageCacheToken> tokenList = checkUICacheAndGetUrlList(mCurrentUIToken);
			boolean repeated = false;
			Iterator<UrlImageCacheToken> ite = tokenList.iterator();
			while (ite.hasNext()) {
				UrlImageCacheToken t = ite.next();
				if (t.equals(token)) {
					if (t != token) {
						// 如果已经存在了
						token.setRefCount(t.getRefCount());
					}
					repeated = true;
					break ;
				}
			}
			// 如果没有重复
			if (!repeated) {
				token.increment();
			}
			tokenList.add(token);
		}
	}
	
	private HashSet<UrlImageCacheToken> checkUICacheAndGetUrlList(Object uiToken) {
		HashSet<UrlImageCacheToken> tokenList = sUICache.get(mCurrentUIToken);
		if (null == tokenList) {
			tokenList = new HashSet<UrlImageCacheToken>();
			sUICache.put(mCurrentUIToken, tokenList);
		}
		return tokenList;
	}
	
	static void removeUrlImageCacheToken(UrlImageCacheToken token) {
		synchronized (mSyncToken) {
			if (sUICache.isEmpty()) {
				return ;
			}
			Iterator<HashSet<UrlImageCacheToken>> iteSet = sUICache.values().iterator();
			while (iteSet.hasNext()) {
				HashSet<UrlImageCacheToken> set = iteSet.next();
				if (null == set || set.isEmpty()) {
					continue ;
				}
				Iterator<UrlImageCacheToken> ite = set.iterator();
				while (ite.hasNext()) {
					UrlImageCacheToken t = ite.next();
					if (t.equals(token)) {
						ite.remove();
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 如果Bitmap引用已经被回收，则从所有持有该引用的Activity的UrlImageCacheToken列表中移除
	 */
    static void poll() {
    	// 耗时操作，会导致图片刷新到界面时卡顿
    	// System.gc(); 
    	synchronized (mSyncToken) {
    		UrlImageCacheToken token;
    		while ((token = (UrlImageCacheToken) sRefQueue.poll()) != null) {
    			LogUtils.d(TAG, "poll token = " + token);
    			removeUrlImageCacheToken(token);
    			removeCacheUrl(token.getUrl());
    		}
    	}
	}
	
	private static LruCache<String, UrlImageCacheToken> sImageCache = null;
	
	static {
		synchronized (mSyncToken) {
			// 以KB为单位
			int maxMemory = (int) (Runtime.getRuntime().maxMemory());
			int cacheSize = maxMemory / 2;
			
			sImageCache = new LruCache<String, UrlImageCacheToken>(cacheSize) {
				
				@Override
				protected int sizeOf(String key, UrlImageCacheToken value) {
					return value.getByteCount();
				}
				
				@Override
				protected void entryRemoved(boolean evicted, String key,
						UrlImageCacheToken oldValue, UrlImageCacheToken newValue) {
					// sImageCache.remove(key);
					LogUtils.i(TAG, "entryRemoved old = " + oldValue);
					oldValue = null;
					poll();
				}
				
			};
		}
	}
	
	public static final int IMAGE_SRC = 1;
	public static final int IMAGE_BACKGROUND = 2;
	private ImageUtil() { }

	UrlImageCacheToken addCacheUrl(String url, Bitmap bitmap) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		if (null == bitmap) {
			return null;
		}
		
		LogUtils.i("imageCache", "imageCache size--->" + sImageCache.size());
		LogUtils.i("imageCache",
				"imageCache maxsize--->" + sImageCache.maxSize());
		LogUtils.i("imageCache",
				"imageCache putCount--->" + sImageCache.putCount());
		LogUtils.i("imageCache", "imageCache evictionCount--->"
				+ sImageCache.evictionCount());
		
		synchronized (mSyncToken) {
			poll();
			UrlImageCacheToken token = new UrlImageCacheToken(url, bitmap, sRefQueue);
			sImageCache.put(url, token);
			return token;
		}
		
	}

	private static void removeCacheUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return ;
		}
		synchronized (mSyncToken) {
			sImageCache.remove(url);
		}
	}
	
	public Bitmap getImageCacheBitmap(String key) {
		UrlImageCacheToken token = getImageCacheToken(key);
		if (null == token) {
			return null;
		}
		return token.get();
	}
	
	UrlImageCacheToken getImageCacheToken(String key) {
		synchronized (mSyncToken) {
			if (TextUtils.isEmpty(key)) {
				return null;
			}
			UrlImageCacheToken token = sImageCache.get(key);
			return token;
		}
	}

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// local path
	public void setLocalImageSrc(View view, String path) {
		ImageDownloadListener listener = ImageDownloadListener.obtain(view, path);
		listener.setType(IMAGE_SRC);
		setLocalImage(listener);
	}
	
	public void setLocalImageBackground(View view, String path) {
		ImageDownloadListener listener = ImageDownloadListener.obtain(view, path);
		listener.setType(IMAGE_BACKGROUND);
		setLocalImage(listener);
	}
	
	public void setLocalImage(ImageDownloadListener listener) {
		try {
			// 避免listView复用导致显示图片错乱 显示前对比url相等才显示
			// iv.setTag(url);
			// set default drawable
			listener.setDefDrawable();
			// 第一优先级 取内存
			UrlImageCacheToken token = getImageCacheToken("file://" + listener.getUrl());
			if (checkTokenHasBitmap(token, listener)) {
				return ;
			}
			_loadImageFromFile2(listener);
		} catch (Exception e) {
			Log.i(TAG, "setLocalImage Exception:" + e.getMessage());
			e.printStackTrace();
			if (null != listener) {
				listener.onFailed();
				listener.recycle();
			}
		}
	}
	
	private void _loadImageFromFile2(ImageDownloadListener listener) throws Exception {
		// 第二优先级 尝试取本地
		final Bitmap bmp = BitmapUtils.getBitmapFromFile(listener.getUrl());
		if (null != bmp) {
			LogUtils.i(TAG, "_loadImageFromFile2 get bitmap "
					+ "by sdcard or storage !!!");
			UrlImageCacheToken token = addCacheUrl("file://" + listener.getUrl(), bmp);
			if (checkTokenHasBitmap(token, listener)) {
				return ;
			}
		}
		throw new Exception("cannot load '" + listener.getUrl() + "'");
	}
	
	private boolean checkTokenHasBitmap(UrlImageCacheToken token,
			ImageDownloadListener listener) {
		if (null == token) {
			return false;
		}
		if (null == token.get()) {
			// do nothing
			return false;
		} else {
			listener.onSuccessLoadBitmap(token.get());
			checkShouldIncrement(token);
			LogUtils.d(TAG, "setImage get bitmap ref count !!!"
					+ token.getRefCount());
			return true;
		}
	}
	
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// URL
	public void setBackground(View view, String url) {
		ImageDownloadListener listener = ImageDownloadListener
				.obtain(view, url).setType(IMAGE_BACKGROUND);
		listener.setType(IMAGE_BACKGROUND);
		setImage(listener);
	}

	public void setImageSrc(ImageView imageView, String url) {
		ImageDownloadListener listener = ImageDownloadListener.obtain(
				imageView, url).setType(IMAGE_SRC);
		listener.setType(IMAGE_SRC);
		setImage(listener);
	}

	public void setImage(ImageDownloadListener listener) {
		try {
			// // 避免listView复用导致显示图片错乱 显示前对比url相等才显示
			// iv.setTag(url);
			// set default drawable
			listener.setDefDrawable();
			// 第一优先级 取内存
			UrlImageCacheToken token = getImageCacheToken(listener.getUrl());
			if (checkTokenHasBitmap(token, listener)) {
				return;
			}
			// 第一优先级
			_loadImageFromFile(listener);
		} catch (Exception e) {
			Log.i(TAG, "setImage Exception:" + e.getMessage());
			e.printStackTrace();
			if (null != listener) {
				listener.onFailed();
				listener.recycle();
			}
		}
	}

	private void _loadImageFromFile(ImageDownloadListener listener)
			throws Exception {
		FileCompositor file = LocalFileUtility.getImageFileByUrl(
				listener.getUrl(), LocalFileUtility.IMAGE_FILE_SUFFIX);
		if (null != file && file.exists()) {
			if (file.size() <= 0) {
				LogUtils.i(TAG, "f.delete");
				file.deleteFile(true);
			}
			// 第二优先级 尝试取本地
			final Bitmap bmp = BitmapUtils.getBitmapFromFile(file.getAbsoluteFile());
			if (null != bmp) {
				LogUtils.i(TAG, "_loadImageFromFile get bitmap "
						+ "by sdcard or storage !!!");
				UrlImageCacheToken token = addCacheUrl(listener.getUrl(), bmp);
				if (checkTokenHasBitmap(token, listener)) {
					return;
				}
			}
		}
		// 第三优先级
		// 取网络图片网络图片取出后要存储到本地
		_loadImageFromNetwork(listener);
	}

	private void _loadImageFromNetwork(ImageDownloadListener listener) {
		TaskHandler.execDownloadFile(listener.getUrl(),
				LocalFileUtility.FILE_IMG_PATH,
				LocalFileUtility.IMAGE_FILE_SUFFIX, true, listener);
	}

}
