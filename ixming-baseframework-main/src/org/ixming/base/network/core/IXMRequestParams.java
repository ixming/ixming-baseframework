package org.ixming.base.network.core;

import java.util.Map;

public interface IXMRequestParams {

	IXMRequestParams param(String name, String value);
	IXMRequestParams params(Map<String, String> params);
	
	Map<String, String> getParams();
}
