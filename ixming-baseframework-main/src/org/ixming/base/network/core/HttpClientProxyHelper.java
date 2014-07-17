package org.ixming.base.network.core;

import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.ixming.base.network.state.BlurryNetType;
import org.ixming.base.network.utils.NetWorkUtils;
import org.ixming.base.network.utils.NetworkManager;

/**
 * 
 * 遗留代码，几乎已经无效
 * 
 * @author Yin Yong
 *
 */
public class HttpClientProxyHelper {

	private HttpClientProxyHelper() { }
	
	/**
	 * 遗留代码，几乎已经无效
	 */
	public static void proxy(HttpClient client) throws Exception {
		BlurryNetType netType = NetworkManager.getBlurryNetWorkType();
		if (netType == BlurryNetType.WiFi) {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		} else {
			if (!NetWorkUtils.isOPhone()) {
				Map<String, Object> map = NetworkManager.getDefaultProxy();
				if (map != null && !map.isEmpty()) {
					if (android.os.Build.VERSION.SDK_INT <= 7) {
						String proxyHost = (String) map
								.get(NetworkManager.PROXY_HOST);
						int proxyPort = (Integer) map
								.get(NetworkManager.PROXY_PORT);
						HttpHost proxy = new HttpHost(proxyHost, proxyPort);
						client.getParams().setParameter(
								ConnRoutePNames.DEFAULT_PROXY, proxy);
					}
				} else {
					// cmnet set proxy
					client.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, null);
				}
			}
		}
	}
	
}
