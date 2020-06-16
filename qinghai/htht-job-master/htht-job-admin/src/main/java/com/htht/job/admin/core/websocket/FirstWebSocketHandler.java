package com.htht.job.admin.core.websocket;

import com.htht.job.admin.service.PushService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/*
 * websocket处理器：功能实现的核心代码编写类
 */
@Component
public class FirstWebSocketHandler implements WebSocketHandler {

    @Resource
    private PushService pushService;
    public static final ArrayList<WebSocketSession> users;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //private final static Map<String, WebSocketSession> userMap;
    static {
        users = new ArrayList<WebSocketSession>();

        //userMap = new ConcurrentHashMap<String,WebSocketSession>(30);
    }

    //连接关闭后的操作
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
            users.remove(session);
            System.err.println("用户已成功关闭");

    }

    //建立连接后的操作
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            users.add(session);
    }

    //消息处理，在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String jsonStr = (String) message.getPayload();
        jsonStr = jsonStr.replace("{#start#}", "").replace("{#end#}", "");
        PushThread pushThread=new PushThread(jsonStr,session,pushService);
        pushThread.start();


    }


    //消息传输错误处理
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
    }

    public boolean supportsPartialMessages() {
        return false;
    }

    private String  getUserId(WebSocketSession session){
        try {
            String userName = (String) session.getAttributes().get("userName");
            return userName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}