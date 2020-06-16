package com.htht.job.core.handler;

import com.htht.job.core.util.ResultUtil;

import java.util.LinkedHashMap;

public interface SharingHandler {
    @SuppressWarnings("rawtypes")
    public abstract <T> ResultUtil<T> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception;
}
