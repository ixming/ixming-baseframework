package org.ixming.base.taskcenter.entity;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class ReqImgBean implements Parcelable
{
    // 当前的上下文
    private Context mContext = null;
    // 下载地址
    private String url = "";
    // 请求类型，post put get delete
    private String reqType = "";
    // 请求标示
    private int reqMode = 0;
    // 地址附带信息
    private String json = "";
    //存储路径
    private String paramPath;
	private Object obj = null;
	private int index = -1;
    private String sign = "";
    private ImageView imageView;
    //设置图片类型
    private int type=0;
    public String getParamPath() {
		return paramPath;
	}
	public void setParamPath(String paramPath) {
		this.paramPath = paramPath;
	}
	public ImageView getImageView() {
		return imageView;
	}
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

    
	//handler 
    private Handler handler =null;
    
    private String sourceUrl = ""; 

    public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public Context getmContext() {
		return mContext;
	}
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

    public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}


	public Context getContext()
    {
        return mContext;
    }

    public void setContext(Context mContext)
    {
        this.mContext = mContext;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getReqType()
    {
        return reqType;
    }

    public void setReqType(String reqType)
    {
        this.reqType = reqType;
    }

    public int getReqMode()
    {
        return reqMode;
    }

    public void setReqMode(int reqMode)
    {
        this.reqMode = reqMode;
    }

    public String getJson()
    {
        return json;
    }

    public void setJson(String json)
    {
        this.json = json;
    }
    
    public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
    public String toString()
    {
        return " url : " + url + " reqType : " + reqType + " json: " + json;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

    }

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}
	 @Override
    public boolean equals(Object obj)
    {
        if (obj.getClass().getName().equals(this.getClass().getName()))
        {
            if (obj instanceof ReqImgBean)
            {
            	ReqImgBean temp = (ReqImgBean) obj;
            	if (    temp.getUrl().equalsIgnoreCase(this.getUrl()) &&
            			temp.getReqMode() == this.getReqMode() && temp.getJson().equals(this.getJson()))
                    {
                       return true;
                    }
            }
        }
        return false;
    }
}