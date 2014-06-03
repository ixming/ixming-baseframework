package org.ixming.base.taskcenter.task;

import java.io.File;

import org.ixming.base.file.FileCompositor;
import org.ixming.base.file.app.LocalFileUtility;
import org.ixming.base.network.HttpClientUtil;
import org.ixming.base.taskcenter.entity.ReqFileBean;
import org.ixming.base.utils.android.LogUtils;


public class DownloadFileTask extends BaseTask {
	private static final String TAG = DownloadFileTask.class.getSimpleName();
	
	public DownloadFileTask(ReqFileBean reqFileBean) {
		this.bean = reqFileBean;
	}

	private ReqFileBean bean;

	@Override
	public void run() {
		boolean shouldDownLoad = false;
		FileCompositor file = LocalFileUtility.getFileByUrl(
				bean.getParamPath(), bean.getUrl(), bean.getSuffix());
		if (bean.isCacheMode()) {
			shouldDownLoad = !file.exists();
		} else {
			shouldDownLoad = true;
		}
		
		if (shouldDownLoad) {
			try {
				file.createNewFile();
				download(file.getAbsoluteFile());
			} catch (Exception ignore) { }
		}
		
		if (null != bean.getListener()) {
			File targetFile = file.getAbsoluteFile();
			if (file.exists() && targetFile.isFile()) {
				bean.getListener().onSuccess(targetFile.getAbsolutePath());
			} else {
				LogUtils.e(TAG, "error file ==null");
				bean.getListener().onFailed();
			}
		}
	}

	private void download(File file) {
		/**
		 * 美妆1.0 特别处理 过滤html 该判断在非唯美妆应用可以删除
		 */
		if (".html".equals(bean.getSuffix())) {
			HttpClientUtil.downloadHtmlFile(bean.getUrl(), file);
		} else {
			HttpClientUtil.downloadFile(bean.getUrl(), file);
		}

	}
}
