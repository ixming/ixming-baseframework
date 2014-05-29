package com.vip.base.taskcenter.callback;

import com.vip.base.taskcenter.entity.ReqBean;

public interface OnLoadListener
{
    public void onSuccess(Object obj, ReqBean reqMode);

    public void onError(Object obj, ReqBean reqMode);
}