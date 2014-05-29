package org.ixming.base.taskcenter.callback;

public interface OnDownloadListener {
	/**
	 * 处理下载过程中的进度显示等，返回值表示是否还要继续下载
	 * @param totalSize
	 * @param downloadSize
	 * @return 是否还要继续下载
	 */
	public boolean onDownload(long totalSize, long downloadSize);

	public void onSuccess(String path);
	public void onFailed();
}
