/**
 * 
 */
package com.vip.base.network;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class NetworkManager
{
    private final static String LOG_TAG = NetworkManager.class.getSimpleName();
    public final static String PROXY_HOST = "proxy_host";
    public final static String PROXY_PORT = "proxy_port";
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
    public static String getNetWorkType(ConnectivityManager con)
    {
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        return getNetworkType(networkInfo);

    }

    private static String getNetworkType(NetworkInfo networkInfo)
    {
        String networkType = "UNKNOWN";
        try
        {
            if (networkInfo != null)
            {
                switch (networkInfo.getType())
                {
                    case ConnectivityManager.TYPE_WIFI:
                    {
                        networkType = "WIFI";
                        break;
                    }
                    default:
                    {
                        if (!TextUtils.isEmpty(networkInfo.getExtraInfo()))
                        {
                            networkType = networkInfo.getExtraInfo().toUpperCase();
                        }
                        else
                        {
                            Log.w(LOG_TAG, "networkInfo.getExtraInfo() is empty!");
                            networkType = networkInfo.getTypeName();
                        }
                        break;
                    }
                }
            }

            networkType = TextUtils.isEmpty(networkType) ? "UNKNOWN" : networkType;

            Log.w(LOG_TAG, "getNetworkType = " + networkType);

        } catch (Exception e)
        {
            networkType = "";
            e.printStackTrace();
        }
        return networkType;

    }

    @SuppressWarnings("unused")
	private static void showNetworkInfo(ConnectivityManager con)
    {
        try
        {
            NetworkInfo aNetworkInfo[] = con.getAllNetworkInfo();

            for (NetworkInfo networkInfo : aNetworkInfo)
            {
                if (networkInfo != null)
                {
                    Log.v(LOG_TAG, "NetworkInfo: " + networkInfo.toString());
                }
            }

            NetworkInfo networkInfo = con.getActiveNetworkInfo();
            if (networkInfo != null)
            {
                Log.i(LOG_TAG, "ActiveNetworkInfo: " + networkInfo.toString());
            }
            else
            {
                Log.i(LOG_TAG, "Failed to get active network info");
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }

    }

    @SuppressWarnings("finally")
    public static Map<String, Object> getProxy()
    {
        Map<String, Object> res = new HashMap<String, Object>();
        try
        {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int proxyPort = android.net.Proxy.getDefaultPort();

            Log.w(LOG_TAG, "proxyHost--->" + proxyHost);
            Log.w(LOG_TAG, "proxyPort--->" + proxyPort);

            if (!TextUtils.isEmpty(proxyHost) && (proxyPort > 0))
            {
                res.put(PROXY_HOST, proxyHost);
                res.put(PROXY_PORT, proxyPort);
            }
        }
        catch (Exception e)
        {
            res = null;
            e.printStackTrace();
        } 
        finally
        {
            return res;
        }

    }
    
	// Samsung GT-I9008L has a strange bug, apps which use WebView cannot connect to network in default mode
    public static void initNetworkForI9008L()
    {
		String model = Build.MODEL;    	
		if (!TextUtils.isEmpty(model) && model.equalsIgnoreCase("GT-I9008L"))
		{
			try 
			{
				// Samsung GT-I9008L use a special method to set current apn for webkit core
			    String ret = System.setProperty("android.com.browser.apn", "internet");
			    Log.w(LOG_TAG, "GT-I9008L found, result of 'System.setProperty' = " + ret);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
    }
	//
}
