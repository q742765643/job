package com.htht.job.admin.core.shiro.common;


/**
 * Created by admin on 2017/6/29.
 */
public class UpmsResult extends BaseResult {

    public UpmsResult(UpmsResultConstant upmsResultConstant, Object data) {
        super(upmsResultConstant.getCode(), upmsResultConstant.getMessage(), data);
    }
}
