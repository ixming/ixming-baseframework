package org.ixming.base.network.simple;

import org.apache.http.HttpResponse;
import org.ixming.base.file.FileCompositor;
import org.ixming.base.file.app.LocalFileUtility;
import org.ixming.base.network.HttpHelper;
import org.ixming.base.network.state.BlurryReponseCode;
import org.ixming.base.utils.android.FrameworkLog;

public abstract class BaseDownloadTask extends BaseHttpTask {

	private static final String TAG = BaseDownloadTask.class.getSimpleName();
	/**
	 * TOKEN 是 {@link HttpRes} 或者 null
	 */
	public static final int DT_CODE_HTTP = -0x1;
	/**
	 * TOKEN 是 null
	 */
	public static final int DT_CODE_CREATE_FAILED = -0x2;
	/**
	 * TOKEN 是 null
	 */
	public static final int DT_CODE_SAVE_FAILED = -0x3;
	/**
	 * TOKEN 是 Exception或者null
	 */
	public static final int DT_CODE_SAVE_FAILED = -0x3;
	
	private FileCompositor mFile;
	private boolean mForceReplace;
	public BaseDownloadTask(String url) {
		super(HttpHelper.get(url, null));
		// default
		mFile = LocalFileUtility.getCommonFileByUrl(url, "");
	}
	
	/**
	 * default : url的MD5值，无后缀
	 */
	public void setFileName(FileCompositor file) {
		if (null != file) {
			mFile = file;
		}
	}
	
	public void setForceReplace(boolean forceReplace) {
		mForceReplace = forceReplace;
	}

	@Override
	protected void onHttpResult(BlurryReponseCode responseCode, HttpRes httpRes) {
		switch (responseCode) {
		case Success:
			onHttpSuccess(responseCode, httpRes);
			break;
		default:
			if (!isCanceled()) onFileDownloadFailed(mFile, httpRes);
			break;
		}
	}
	
	private void onHttpSuccess(BlurryReponseCode responseCode, HttpRes httpRes) {
		try {
			boolean fileCreatedHere = true;
			if (mFile.exists()) {
				if (mForceReplace) {
					mFile.deleteFile(true);
				} else {
					fileCreatedHere = false;
				}
			}
			
			boolean fileCreated = mFile.createNewFile();
			if (fileCreated) {
				try {
					if (mFile.save(httpRes.getContent())){
						if (!isCanceled()) onFileDownloaded(mFile);
					} else {
						if (!isCanceled()) onFileDownloadFailed(mFile, DT_CODE_SAVE_FAILED);
					}
				} catch (Exception e) {
					if (fileCreatedHere) {
						mFile.deleteFile(true);
					}
					throw e;
				}
			} else {
				if (!isCanceled()) onFileDownloadFailed(mFile, DT_CODE_SAVE_FAILED);
			}
		} catch (Exception e) {
			FrameworkLog.e(TAG, "onSuccess Exception: " + e.getMessage());
			e.printStackTrace();
			if (!isCanceled()) onFileDownloadFailed(mFile, DT_CODE_EXCEPTION, e);
		}  
	}
	
	/**
	 * 文件下载成功
	 * 
	 * <br/>
	 * 
	 * In Thread
	 */
	protected abstract void onFileDownloaded(FileCompositor file) ;
	
	/**
	 * @param code 本类中的DT_CODE_*常量
	 */
	protected abstract void onFileDownloadFailed(FileCompositor file, int dtCode, Object...token) ;

}
