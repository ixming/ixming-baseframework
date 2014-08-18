package org.ixming.base.network.simple;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.ixming.base.io.IOConstants;
import org.ixming.base.io.IOUtils;
import org.ixming.base.network.state.BlurryReponseCode;

public abstract class BaseHttpJsonResponseTask extends BaseHttpTask {

	private final String mEncoding;
	public BaseHttpJsonResponseTask(HttpUriRequest request) {
		this(request, IOConstants.DEF_CHARSET);
	}
	
	public BaseHttpJsonResponseTask(HttpUriRequest request, String encoding) {
		super(request);
		mEncoding = encoding;
	}

	@Override
	protected void onHttpResult(BlurryReponseCode responseCode, HttpRes httpRes) {
		try {
			HttpEntity entity = httpRes.getHttpEntity();
			InputStream ins = entity.getContent();
			
			Header header = entity.getContentEncoding();
			if (null != header && header.getValue().contains("gzip")) {
				ins = new GZIPInputStream(ins);
			}
			
			if (onJsonStreamLoaded(ins)) {
				return ;
			}
			
			String json = IOUtils.inputStreamToString(ins, mEncoding);
			if (onJsonStringLoaded(json)) {
				return ;
			}
		} catch (Exception e) {
		}  
	}

	/**
	 * @return 如果通过onJsonStreamLoaded处理了数据，返回TRUE，将不会再收到onJsonStringLoaded回调
	 */
	protected boolean onJsonStreamLoaded(InputStream ins) {
		return false;
	}
	
	/**
	 * @return 如果通过onJsonStringLoaded处理了数据，返回TRUE
	 */
	protected boolean onJsonStringLoaded(String json) {
		return false;
	}

}
