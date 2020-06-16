package com.htht.job.admin.core.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.htht.job.admin.core.shiro.common.UpmsConstant;
import com.htht.job.admin.core.shiro.session.UpmsSessionDao;
import com.htht.job.admin.core.util.PropertiesFileUtil;
import com.htht.job.admin.core.util.RedisUtil;
import com.htht.job.admin.core.util.RequestParameterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//authc filter rewrite
public class UpmsAuthenticationFilter extends AuthenticationFilter {
    private static Logger _log = LoggerFactory.getLogger(UpmsAuthenticationFilter.class);
    @Autowired
    UpmsSessionDao upmsSessionDao;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        //判断当前应用类型
        String upmsType = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.upms.type");
        session.setAttribute(UpmsConstant.UPMS_TYPE, upmsType);
        if ("server".equals(upmsType)) {
            return subject.isAuthenticated();
        }
        if ("client".equals(upmsType)) {
            boolean flag=  validateClient(request, response);
            System.out.println(flag);
            return  flag;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        StringBuffer ssoServerUrl = new StringBuffer(PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.upms.sso.server.url"));

        String upmsType = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.upms.type");
        String appid = PropertiesFileUtil.getInstance(UpmsConstant.CONFIG).get("master.upms.addId");

        if ("server".equals(upmsType)) {
            PrintWriter out = servletResponse.getWriter();
            out.println("<html>");
            out.println("<script>");
            out.println("window.open ('"+ssoServerUrl.append("/sso/login").toString()+"','_parent')");
            out.println("</script>");
            out.println("</html>");
            //WebUtils.toHttp(servletResponse).sendRedirect(ssoServerUrl.append("/toLogin").toString());
            return false;
        }
        ssoServerUrl.append("/sso/index").append("?").append("appid").append("=").append(appid);

        //跳到服务端后的回跳地址
        HttpServletRequest httpServletRequest = WebUtils.toHttp(servletRequest);
        StringBuffer backUrl = httpServletRequest.getRequestURL();
        String queryString = httpServletRequest.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            backUrl.append("?").append(queryString);
        }
        ssoServerUrl.append("&").append("backurl").append("=").append(URLEncoder.encode(backUrl.toString(), "utf-8"));
        WebUtils.toHttp(servletResponse).sendRedirect(ssoServerUrl.toString());
        return false;
    }

    //认证中心登录成功带回code
    private boolean validateClient(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        String sessionId = session.getId().toString();
        int timeOut = (int) session.getTimeout() / 1000;
        // 判断局部会话是否登录
        String cacheClientSession = RedisUtil.get(UpmsConstant.MASTER_UPMS_CLIENT_SESSION_ID + "_" + sessionId);
        if (StringUtils.isNotBlank(cacheClientSession)) {
            //更新code有效期
            RedisUtil.set(UpmsConstant.MASTER_UPMS_CLIENT_SESSION_ID + "_" + sessionId, cacheClientSession, timeOut);
            Jedis jedis = RedisUtil.getJedis();
            jedis.expire(UpmsConstant.MASTER_UPMS_CLIENT_SESSION_IDS + "_" + cacheClientSession, timeOut);
            jedis.close();
            //移除url中的code参数
            if (null != request.getParameter("code")) {
                String backUrl = RequestParameterUtil.getParameterWithOutCode((HttpServletRequest) request);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                try {
                    httpServletResponse.sendRedirect(backUrl);
                } catch (IOException e) {
                    _log.error("局部会话已登录，移除code参数跳转出错: ", e);
                }
            } else {
                return true;
            }
        }
        //没有局部会话没有登录，判断是否有认证中心code
        String code = request.getParameter("upms_code");
        //客户端已拿到code
        if (StringUtils.isNotBlank(code)) {
            //HttpPost校验code
            try {
                StringBuffer ssoServerUrl = new StringBuffer(PropertiesFileUtil.getInstance("config").get("master.upms.sso.server.url"));
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(ssoServerUrl.toString() + "/sso/code");

                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("code", code));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
                if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = closeableHttpResponse.getEntity();
                    JSONObject result = JSONObject.parseObject(EntityUtils.toString(httpEntity));
                    if (1 == result.getIntValue("code") && result.getString("data").equals(code)) {
                        //code 校验通过
                        RedisUtil.set(UpmsConstant.MASTER_UPMS_CLIENT_SESSION_ID + "_" + sessionId, code, timeOut);
                        //保存code对应的局部会话sessionId，方便退出操作
                        RedisUtil.sadd(UpmsConstant.MASTER_UPMS_CLIENT_SESSION_IDS + "_" + code, sessionId, timeOut);
                        _log.debug("当前code={}，对应的注册系统个数：{}个", code, RedisUtil.getJedis().scard(UpmsConstant.MASTER_UPMS_CLIENT_SESSION_IDS + "_" + code));
                        //移除url中upms_code参数
                        String backurl = RequestParameterUtil.getParameterWithOutCode((HttpServletRequest) request);
                        try {
                            String username = request.getParameter("upms_username");
                            subject.login(new UsernamePasswordToken(username, ""));
                            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                            httpServletResponse.sendRedirect(backurl);
                            return true;
                        } catch (IOException e) {
                            _log.error("已拿到code，移除code参数跳转出错：", e);
                        }
                    } else {
                        _log.warn(result.getString("data"));
                    }
                }

            } catch (IOException e) {
                _log.error("验证code失败：", e);
            }
        }
        return false;
    }
}
