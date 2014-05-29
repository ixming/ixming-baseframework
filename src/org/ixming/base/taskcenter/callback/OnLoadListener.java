package org.ixming.base.taskcenter.callback;

import org.ixming.base.taskcenter.entity.ReqBean;

public interface OnLoadListener
{
    public void onSuccess(Object obj, ReqBean reqMode);

    public void onError(Object obj, ReqBean reqMode);
}