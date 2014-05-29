package com.vip.base.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.vip.base.file.FileCompositor;
import com.vip.base.file.app.LocalFileUtility;
import com.vip.base.taskcenter.async.TaskHandler;
import com.vip.base.utils.LogUtils;
import com.vip.base.utils.Utils;

public class ImageUtil {
	private static final String TAG = ImageUtil.class.getSimpleName();

	private static LruCache<String, Bitmap> imageCache = null;
	private static ImageUtil instance;
	public static final int IMAGE_SRC = 1;
	public static final int IMAGE_BACKGROUND = 2;

	public static ImageUtil getInstance() {
		if (instance == null) {
			instance = new ImageUtil();
		}
		return instance;
	}

	public static BitmapFactory.Options defaultBitmapOptions() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		return options;
	}

	private ImageUtil() {
		if (imageCache == null) {
			LogUtils.i("imageCache", "init imageCache");
			int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			int cacheSize = maxMemory / 2;
			imageCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					int biSize = value.getRowBytes();
					int hei = value.getHeight();
					int bitSize = biSize * hei;
					int rSize = bitSize / 1024;
					return rSize;
				}

				@Override
				protected void entryRemoved(boolean evicted, String key,
						Bitmap oldValue, Bitmap newValue) {
					imageCache.remove(key);
					new PhantomReference<Bitmap>(oldValue,
							new ReferenceQueue<Bitmap>());
					oldValue = null;
					System.gc();
					LogUtils.i("imageCache", "entryRemoved old" + oldValue);
				}
			};
		}
	}

	private void addCacheUrl(String url, Bitmap bitmap) {
		if (Utils.isNotNull(url) && imageCache != null && bitmap != null) {
			synchronized (imageCache) {
				imageCache.put(url, bitmap);
			}
			LogUtils.i("imageCache", "imageCache size--->" + imageCache.size());
			LogUtils.i("imageCache",
					"imageCache maxsize--->" + imageCache.maxSize());
			LogUtils.i("imageCache",
					"imageCache putCount--->" + imageCache.putCount());
			LogUtils.i("imageCache", "imageCache evictionCount--->"
					+ imageCache.evictionCount());
		}
	}

	public Bitmap getImageCacheBitmap(String key) {
		if (Utils.isNotNull(key)) {
			return imageCache.get(key);
		}
		return null;
	}

	/**
	 * 本地assets下图片
	 * 
	 * @param path
	 * @return
	 */
	public Bitmap getBitmapFromAssets(Context context, String path) {
		Bitmap bitmap = null;
		try {
			if (imageCache.get(path) != null) {
				bitmap = imageCache.get(path);
			}
			if (bitmap == null) {
				bitmap = Utils.getBitmapFromAssets(context, path);
			}
			addCacheUrl(path, bitmap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}

	public void setBackground(View view, String url) {
		ImageDownloadListener listener = ImageDownloadListener
				.obtain(view, url).setType(IMAGE_BACKGROUND);
		setBackground(listener);
	}

	public void setImageSrc(ImageView imageView, String url) {
		ImageDownloadListener listener = ImageDownloadListener.obtain(
				imageView, url).setType(IMAGE_SRC);
		setImageSrc(listener);
	}

	public void setImage(ImageDownloadListener listener) {
		_setImage(listener);
	}

	/**
	 * @param context
	 * @param listener
	 */
	private void setBackground(ImageDownloadListener listener) {
		if (null == listener || TextUtils.isEmpty(listener.getUrl())) {
			return;
		}
		listener.setType(IMAGE_BACKGROUND);
		_setImage(listener);
	}

	/**
	 * @param context
	 * @param listener
	 */
	private void setImageSrc(ImageDownloadListener listener) {
		if (null == listener || TextUtils.isEmpty(listener.getUrl())) {
			return;
		}
		listener.setType(IMAGE_SRC);
		_setImage(listener);
	}

	private void _setImage(ImageDownloadListener listener) {
		try {
			// // 避免listView复用导致显示图片错乱 显示前对比url相等才显示
			// iv.setTag(url);
			// set default drawable
			listener.setDefDrawable();
			// 第一优先级 取内存
			Bitmap bm = getImageCacheBitmap(listener.getUrl());
			if (null != bm) {
				LogUtils.i(TAG, "setBackground get bitmap by memory !!!"
						+ Thread.currentThread().getId());
				listener.onSuccessLoadBitmap(bm);
				return;
			}
			_loadImageFromFile(listener);
		} catch (Exception e) {
			Log.i(TAG, "setImage0 Exception:" + e.getMessage());
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
			final Bitmap bmp = getBitmapFromFile(file.getAbsoluteFile());
			if (null != bmp) {
				addCacheUrl(listener.getUrl(), bmp);
				LogUtils.i(TAG, "_loadImageFromFile get bitmap "
						+ "by sdcard or storage !!!");
				listener.onSuccessLoadBitmap(bmp);
				return;
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

	// >>>>>>>>>>>>>>>>>>>>>>>>
	// static methods
	public static Bitmap getBitmapFromRes(Context context, int resId) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = defaultBitmapOptions();
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					resId, options);
		} catch (Exception e) {
			bitmap = null;
			LogUtils.e(TAG, "getBitmapFromRes Exception: " + e.getMessage());
		}
		return bitmap;
	}

	public static Bitmap getBitmapFromFileInputStream(FileInputStream is) {
		if (is == null)
			return null;
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = defaultBitmapOptions();
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null,
					options);
		} catch (Exception e) {
			bitmap = null;
			LogUtils.e(TAG,
					"getBitmapFromFileInputStream Exception: " + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return bitmap;
	}

	public static Bitmap getBitmapFromFile(String filePath) {
		return getBitmapFromFile(new File(filePath));
	}

	public static Bitmap getBitmapFromFile(File file) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			return getBitmapFromFileInputStream(fis);
		} catch (Exception e) {
			LogUtils.e(TAG, "getBitmapFromFile Exception: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 将Bitmap转化为字节数组
	 * 
	 * @param bmp
	 *            target bitmap to read
	 * @param needRecycle
	 *            是否需要回收bitmap
	 * @added 1.0
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		if (null == bmp) {
			return null;
		}
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.PNG, 100, output);
			if (needRecycle) {
				bmp.recycle();
			}
			byte[] result = output.toByteArray();
			return result;
		} catch (Exception e) {
			LogUtils.e(TAG, "bmpToByteArray Exception: " + e.getMessage());
			return null;
		} finally {
			if (null != output) {
				try {
					output.close();
				} catch (Exception ex) {
				}
			}
		}
	}

	public static byte[] scaleBitmapIfNeededToSize(Bitmap bitmap, long size) {
		byte[] data = null;
		try {
			float width = bitmap.getWidth();
			float height = bitmap.getHeight();
			data = bmpToByteArray(bitmap, false);
			float des = data.length;
			des = des / size;
			if (des <= 1) {
				return data;
			}
			final float scale;
			if (des <= 2.5F) {
				scale = 0.95F;
			} else if (des <= 5.0F) {
				scale = 0.9F;
			} else if (des <= 7.5F) {
				scale = 0.85F;
			} else {
				scale = 0.8F;
			}
			while (true) {
				if (null == data || data.length <= size) {
					break;
				}
				Bitmap bitmapCopy = bitmap;
				width *= scale;
				height *= scale;
				bitmap = Bitmap.createScaledBitmap(bitmapCopy, (int) width,
						(int) height, true);
				bitmapCopy.recycle();
				data = bmpToByteArray(bitmap, false);
			}
		} catch (Exception e) {
			data = null;
			LogUtils.e(TAG,
					"scaleBitmapIfNeededToSize Exception: " + e.getMessage());
		}
		return data;
	}
}
