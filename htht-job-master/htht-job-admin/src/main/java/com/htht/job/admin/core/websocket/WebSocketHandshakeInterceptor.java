package com.htht.job.admin.core.websocket;

import org.apache.shiro.SecurityUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * websocket拦截器：一般情况下不做处理
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {


        //使用userName区分WebSocketHandler，以便定向发送消息(使用shiro获取session,或是使用上面的方式)
        String userName = (String) SecurityUtils.getSubject().getSession().getAttribute("userName");
        if (userName == null) {
            userName = "default-system";
        }
        attributes.put("userName", userName);

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }



}

