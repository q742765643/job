package com.htht.job.admin.core.websocket;

import com.htht.job.admin.service.PushService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.ArrayList;

/*
 * websocket处理器：功能实现的核心代码编写类
 */
@Component
public class FirstWebSocketHandler implements WebSocketHandler {

    public static final ArrayList<WebSocketSession> users;

    static {
        users = new ArrayList<>();
    }

    @Resource
    private PushService pushService;

    //连接关闭后的操作
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        users.remove(session);
    }

    //建立连接后的操作
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        users.add(session);
    }

    //消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String jsonStr = (String) message.getPayload();
        jsonStr = jsonStr.replace("{#start#}", "").replace("{#end#}", "");
        PushThread pushThread = new PushThread(jsonStr, session, pushService);
        pushThread.start();


    }


    //消息传输错误处理
    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
    }
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }



}