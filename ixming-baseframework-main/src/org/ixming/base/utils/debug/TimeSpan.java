package org.ixming.base.utils.debug;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一般用作测试/校验/获取某种操作/行为等的时间跨度
 * 
 * @author Yin Yong
 *
 */
public class TimeSpan {

	private TimeSpan() { }
	
	private static final Map<String, Long> TIME_TAGS;
	static {
		TIME_TAGS = new LinkedHashMap<String, Long>();
	}
	
	/**
	 * 标记初始时间
	 */
	public static void startTime(String tag) {
		synchronized (TIME_TAGS) {
			TIME_TAGS.put(tag, System.currentTimeMillis());
		}
	}
	
	/**
	 * 相同<code> tag </code>的<b>初始时间</b>（调用{@link #startTime(String)}时记录）和
	 * <b>结束时间</b>（调用{@link #endTime(String)}时记录）之间的时间差
	 */
	public static long endTime(String tag) {
		synchronized (TIME_TAGS) {
			long endTime = System.currentTimeMillis();
			long startPr = 0;
			Long start = TIME_TAGS.get(tag);
			if (null != start) {
				startPr = start.longValue();
				return endTime - startPr;
			} else {
				return 0L;
			}
		}
	}
}
