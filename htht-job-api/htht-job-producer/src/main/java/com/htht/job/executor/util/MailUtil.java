package com.htht.job.executor.util;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.htht.job.core.util.PropertiesUtil;

import javax.mail.internet.MimeMessage;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 邮件发送.Util
 *
 * @author xuxueli 2016-3-12 15:06:20
 */
public class MailUtil {
    static int total = 0;
    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
    private static String host;
    private static String port;
    private static String username;
    private static String password;
    private static String sendNick;

    /**
     <!-- spring mail sender -->
     <bean id="javaMailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl"  scope="singleton" >
     <property name="host" value="${mail.host}" />			<!-- SMTP发送邮件的服务器的IP和端口 -->
     <property name="port" value="${mail.port}" />
     <property name="username" value="${mail.username}" />	<!-- 登录SMTP邮件发送服务器的用户名和密码 -->
     <property name="password" value="${mail.password}" />
     <property name="javaMailProperties">					<!-- 获得邮件会话属性,验证登录邮件服务器是否成功 -->
     <props>
     <prop key="mail.smtp.auth">true</prop>
     <prop key="prop">true</prop>
     <!-- <prop key="mail.smtp.timeout">25000</prop> -->
     </props>
     </property>
     </bean>
     */

    static {
        host = PropertiesUtil.getString("master.job.mail.host");
        port = PropertiesUtil.getString("master.job.mail.port");
        username = PropertiesUtil.getString("master.job.mail.username");
        password = PropertiesUtil.getString("master.job.mail.password");
        sendNick = PropertiesUtil.getString("master.job.mail.sendNick");
    }



    /**
     * 发送邮件 (完整版) (纯JavaMail)
     *
     * @param toAddress       : 收件人邮箱
     * @param mailSubject     : 邮件主题
     * @param mailBody        : 邮件正文
     * @param mailBodyIsHtml: 邮件正文格式,true:HTML格式;false:文本格式
     *                        //@param inLineFile	: 内嵌文件
     * @param attachments     : 附件
     */
    public static boolean sendMail(String toAddress, String mailSubject, String mailBody,
                                   boolean mailBodyIsHtml, File[] attachments) {
        try {
            // 创建邮件发送类 JavaMailSender (用于发送多元化邮件，包括附件，图片，html 等)
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(host);            // 设置邮件服务主机
            mailSender.setUsername(username);    // 发送者邮箱的用户名
            mailSender.setPassword(password);    // 发送者邮箱的密码

            // 配置文件，用于实例化java.mail.session
            Properties pro = new Properties();
            pro.put("mail.transport.protocol", "smtp");
            pro.put("mail.smtp.auth", "true");        // 登录SMTP服务器,需要获得授权 (网易163邮箱新近注册的邮箱均不能授权,测试 sohu 的邮箱可以获得授权)
            pro.put("mail.smtp.socketFactory.port", port);
            pro.put("mail.smtp.socketFactory.fallback", "false");
            mailSender.setJavaMailProperties(pro);

            // 创建多元化邮件 (创建 mimeMessage 帮助类，用于封装信息至 mimeMessage)
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ArrayUtils.isNotEmpty(attachments), "UTF-8");

            helper.setFrom(username, sendNick);
            helper.setTo(toAddress);

            helper.setSubject(mailSubject);
            helper.setText(mailBody, mailBodyIsHtml);
            
            //添加附件
            if(ArrayUtils.isNotEmpty(attachments)){
            	for(File f:attachments){
            		if(f!=null && f.exists()){
//            			FileSystemResource file = new FileSystemResource(f);
//            			helper.addAttachment(file.getFilename(), file);
            			helper.addAttachment(f.getName(), f);
            		}
            	}
            }

            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public static boolean sendMail(String toAddress, String mailSubject, String mailBody, List<File> files) {
    	
    	File[] attachments = new File[files.size()];
    	for (int i = 0 ; i < files.size(); i++) {
//			if(files.get(i).exists()){
//			}
			attachments[i] = files.get(i);
		}
    	
    	return  sendMail(toAddress, mailSubject, mailBody, true, attachments);
    }

}
