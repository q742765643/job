package com.htht.job.admin.core.websocket;/**
 * Created by zzj on 2018/5/10.
 */

import com.htht.job.admin.service.PushService;
import org.springframework.web.socket.WebSocketSession;

/**
 * @program: htht-job
 * @description: 推送线程
 * @author: zzj
 * @create: 2018-05-10 16:30
 **/
public class PushThread extends Thread {
    private PushService pushService;
    private String jsonStr;
    private WebSocketSession session;

    public PushThread(String jsonStr, WebSocketSession session, PushService pushService) {
        this.jsonStr = jsonStr;
        this.pushService = pushService;
        this.session = session;
    }

    @Override
    public void run() {
        pushService.pushWeb(jsonStr, session);
    }

}

