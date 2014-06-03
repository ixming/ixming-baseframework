package org.ixming.base.taskcenter.async;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.ixming.base.network.HttpClientUtil;
import org.ixming.base.taskcenter.callback.OnDownloadListener;
import org.ixming.base.taskcenter.callback.OnLoadListener;
import org.ixming.base.taskcenter.entity.ReqBean;
import org.ixming.base.taskcenter.entity.ReqFileBean;
import org.ixming.base.taskcenter.task.BaseTask;
import org.ixming.base.taskcenter.task.DownloadFileTask;
import org.ixming.base.taskcenter.task.HttpRequestTask;
import org.ixming.base.taskcenter.task.TestTask;


public class TaskHandler {
	private static AtomicInteger priority = new AtomicInteger(0);
	private static String tag = "";
	private static Vector<BaseTask> queue = new Vector<BaseTask>();

	public static void sendRequest(String url, int reqMode,
			Map<String, String> data, OnLoadListener listener) {
		execHttpReqTask(url, reqMode, data, listener);
	}

	public static void sort() {
		for (int i = 0; i < queue.size(); i++) {
			for (int j = 0; j < (queue.size() - i - 1); j++) {
				if (queue.get(j + 1).getPriority() > queue.get(j).getPriority()) {
					BaseTask bt = queue.get(j);
					queue.set(j, queue.get(j + 1));
					queue.set(j + 1, bt);
				}
			}
		}
	}

	public static String getTag() {
		return tag;
	}

	public static void setTag(String tag) {
		TaskHandler.tag = tag;
	}

	public static int getPriority() {
		return priority.get();
	}

	/**
	 * 
	 */
	public static synchronized void getAndIncrement() {
		priority.set(priority.get() + 10);
	}

	public static synchronized void getAndDecrement() {
		priority.getAndDecrement();
	}

	public static synchronized void updatePriority(String tag) {
		for (BaseTask bt : queue) {
			if (tag.equals(bt.getTag())) {
				bt.setPriority(priority.get());
			}
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {

		for (int i = 1; i <= 10; i++) {
			TestTask task = new TestTask();
			TaskHandler.setTag(i + "");
			TaskHandler.getAndIncrement();
			TaskHandler.addTask(task);
		}
		for (int i = 0; i < queue.size(); i++) {
			System.out.println("第" + i + "个:" + queue.get(i).getTag());
		}
		System.out.println("执行优先级排序!");
		sort();
		System.out.println("排序后结果：");
		for (int i = 0; i < queue.size(); i++) {
			System.out.println("第" + i + "个:" + queue.get(i).getTag());
		}
	}

	public static void execHttpReqTask(String url, int reqMode,
			Map<String, String> data, OnLoadListener listener) {
		execHttpReqTask(url, reqMode, data, listener, HttpClientUtil.GET);
	}

	public static void execHttpReqTask(String url, int reqMode,
			Map<String, String> data, OnLoadListener listener, int reqType) {
		ReqBean reqBean = new ReqBean();
		reqBean.setUrl(url);
		reqBean.setReqMode(reqMode);
		reqBean.setData(data);
		reqBean.setListener(listener);
		reqBean.setReqType(reqType);
		HttpRequestTask task = new HttpRequestTask(reqBean);
		addTask(task);
	}

	/**
	 * 文件处理
	 */
	public static void downloadHtml(String url, OnDownloadListener listener) {
		execDownloadFile(url, "", ".html", true, listener);
	}

	/**
	 * 默认覆盖
	 * 
	 * @param url
	 * @param suffix
	 * @param listener
	 */
	public static void downloadResource(String url, String suffix,
			OnDownloadListener listener) {
		execDownloadFile(url, "", suffix, false, listener);
	}

	public static void execDownloadFile(String url, String parentDir,
			String suffix, boolean cacheMode, OnDownloadListener listener) {
		ReqFileBean fileBean = new ReqFileBean();
		fileBean.setUrl(url);
		fileBean.setListener(listener);
		fileBean.setParamPath(parentDir);
		fileBean.setSuffix(suffix);
		fileBean.setCacheMode(cacheMode);
		DownloadFileTask download = new DownloadFileTask(fileBean);
		addTask(download);
	}

	public static void execDownloadImg() {

	}

	public static BaseTask nextTask() {
		synchronized (queue) {
			System.out.println("queue.size:" + queue.size());
			if (queue.size() > 0) {
				BaseTask baseTask = queue.get(0);
				if (baseTask != null) {
					queue.remove(0);
				}
				return baseTask;
			} else {
				return null;
			}
		}
	}

	public static synchronized void addTask(BaseTask baseTask) {
		synchronized (queue) {
			System.out.println("空闲线程:" + ThreadPool.getrFreeCount() + "个！");
			if (ThreadPool.getrFreeCount() > 0) {
				System.out.println("任务" + baseTask.getTag() + "添加至空闲线程直接执行");
				ThreadPool.addTask(baseTask);
			} else {
				// 如果当前线程无空闲则放入缓冲区
				System.out.println("添加至可排序缓冲区");
				queue.add(baseTask);
			}
		}
	}
}
