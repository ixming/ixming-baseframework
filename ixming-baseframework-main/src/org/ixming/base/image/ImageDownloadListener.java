package org.ixming.base.image;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

import org.ixming.base.common.BaseApplication;
import org.ixming.base.taskcenter.callback.OnDownloadListener;
import org.ixming.base.utils.android.AndroidUtils;
import org.ixming.baseframework.R;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;


public class ImageDownloadListener implements OnDownloadListener {

	private static final int POOL_SIZE = 5;
	private static final LinkedList<ImageDownloadListener> mPool = new LinkedList<ImageDownloadListener>();

	static ImageDownloadListener obtain() {
		synchronized (mPool) {
			if (!mPool.isEmpty()) {
				return mPool.remove().empty();
			}
		}
		return new ImageDownloadListener();
	}

	public static ImageDownloadListener obtain(View view, String url) {
		return obtain().setViewAndUrl(view, url);
	}

	public static ImageDownloadListener obtain(View view, String url,
			Drawable drawable) {
		return obtain(view, url).setDefDrawable(drawable);
	}

	private int mType;
	private String mUrl;
	private WeakReference<View> mViewRef;
	private WeakReference<Drawable> mDefDrawableRef;

	/* package */ImageDownloadListener() {
		empty();
	}

	protected ImageDownloadListener empty() {
		mType = -1;
		mUrl = null;
		if (null != mViewRef) {
			mViewRef.clear();
			mViewRef = null;
		}
		if (null != mDefDrawableRef) {
			mDefDrawableRef.clear();
			mDefDrawableRef = null;
		}
		return this;
	}

	public void recycle() {
		empty();
		synchronized (mPool) {
			if (mPool.size() < POOL_SIZE) {
				mPool.add(this);
			}
		}
	}

	public ImageDownloadListener setType(int type) {
		mType = type;
		return this;
	}

	/* package */ImageDownloadListener setViewAndUrl(View view, String url) {
		mViewRef = new WeakReference<View>(view);
		mUrl = url;
		synchronized (view) {
			view.setTag(R.id.image_key, mUrl);
		}
		return this;
	}

	public ImageDownloadListener setDefDrawable(Drawable def) {
		mDefDrawableRef = new WeakReference<Drawable>(def);
		return this;
	}

	public View getView() {
		return null != mViewRef ? mViewRef.get() : null;
	}

	public Drawable getDefDrawable() {
		return null != mDefDrawableRef ? mDefDrawableRef.get() : null;
	}

	public String getUrl() {
		return mUrl;
	}

	@Override
	public final void onSuccess(String path) {
		View view = getView();
		if (null == view) {
			recycle();
			return;
		}
		String url = mUrl;
		if (TextUtils.isEmpty(url)) {
			recycle();
			return;
		}
		synchronized (view) {
			String tag = (String) view.getTag(R.id.image_key);
			if (mUrl.equals(tag)) {
				Bitmap bm = BitmapUtils.getBitmapFromFile(path);
				onSuccessLoadBitmap(bm);
			}
		}
	}

	@Override
	public boolean onDownload(long totalSize, long downloadSize) {
		return false;
	}

	/**
	 * 默认的实现setImageToView(View, Bitmap, type);
	 * <p>
	 * 已经保证在主线程中
	 * </p>
	 */
	public void onSuccess(View view, Bitmap bm) {
		setImageToView(view, bm, mType);
	}

	@Override
	public void onFailed() {

	}

	/* package */void setDefDrawable() {
		if (AndroidUtils.isMainThread()) {
			_setDefDrawable();
		} else {
			BaseApplication.getHandler().post(new Runnable() {
				@Override
				public void run() {
					_setDefDrawable();
				}
			});
		}
	}

	private void _setDefDrawable() {
		View view = getView();
		if (null == view) {
			return;
		}
		Drawable defDrawable = getDefDrawable();
		String url = mUrl;
		if (TextUtils.isEmpty(url)) {
			return;
		}
		synchronized (view) {
			String tag = (String) view.getTag(R.id.image_key);
			if (url.equals(tag)) {
				setImageToView(view, defDrawable, mType);
			}
		}
	}

	/* package */void onSuccessLoadBitmap(final Bitmap bm) {
		if (AndroidUtils.isMainThread()) {
			_syncSetBitmapOnSuccess(bm);
		} else {
			BaseApplication.getHandler().post(new Runnable() {
				@Override
				public void run() {
					_syncSetBitmapOnSuccess(bm);
				}
			});
		}
	}

	private void _syncSetBitmapOnSuccess(Bitmap bm) {
		View view = getView();
		if (null == view) {
			recycle();
			return;
		}
		String url = mUrl;
		if (TextUtils.isEmpty(url)) {
			recycle();
			return;
		}
		synchronized (view) {
			String tag = (String) view.getTag(R.id.image_key);
			if (url.equals(tag)) {
				onSuccess(view, bm);
				recycle();
			}
		}
	}

	public static void setImageToView(View view, Bitmap bm, int type) {
		setImageToView(view, new BitmapDrawable(view.getResources(), bm), type);
	}

	public static void setImageToView(final View view, final Drawable drawable,
			final int type) {
		_setImageToView(view, drawable, type);
	}

	private static void _setImageToView(View view, Drawable drawable, int type) {
		synchronized (view) {
			if (type == ImageUtil.IMAGE_SRC && view instanceof ImageView) {
				ImageView iv = (ImageView) view;
				iv.setImageDrawable(drawable);
			} else {
				view.setBackgroundDrawable(drawable);
			}
		}
	}

}
