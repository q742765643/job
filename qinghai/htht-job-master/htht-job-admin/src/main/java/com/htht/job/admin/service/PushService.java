package com.htht.job.admin.service;

import org.springframework.web.socket.WebSocketSession;

/**
 * Created by zzj on 2018/5/10.
 */
public interface PushService {
    public void pushWeb(String jsonStr,WebSocketSession session);
}
