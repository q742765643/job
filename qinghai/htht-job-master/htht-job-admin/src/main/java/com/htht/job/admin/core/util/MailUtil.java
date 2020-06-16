package com.htht.job.admin.core.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.htht.job.admin.controller.JobLogController;
import com.htht.job.admin.core.model.XxlJobLog;
import com.htht.job.core.biz.model.LogResult;
import com.htht.job.core.biz.model.ReturnT;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import java.io.File;
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
    private static String mailSubjectByXxlJobLog = "《调度监控报警》(业务支撑系统)";
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
        logger.info("--MailUtil--host="+host);
        logger.info("--MailUtil--port="+port);
        logger.info("--MailUtil--username="+username);
        logger.info("--MailUtil--password="+password);
        logger.info("--MailUtil--sendNick="+sendNick);
        System.out.println("--MailUtil--host="+host);
        System.out.println("--MailUtil--port="+port);
        System.out.println("--MailUtil--username="+username);
        System.out.println("--MailUtil--password="+password);
        System.out.println("--MailUtil--sendNick="+sendNick);
    }

    /**
     * 发送邮件 (完整版)(结合Spring)
     * <p>
     * //@param javaMailSender: 发送Bean
     * //@param sendFrom		: 发送人邮箱
     * //@param sendNick		: 发送人昵称
     *
     * @param toAddress       : 收件人邮箱
     * @param mailSubject     : 邮件主题
     * @param mailBody        : 邮件正文
     * @param mailBodyIsHtml: 邮件正文格式,true:HTML格式;false:文本格式
     * @param attachments     : 附件
     */
    @SuppressWarnings("null")
    public static boolean sendMailSpring(String toAddress, String mailSubject, String mailBody, boolean mailBodyIsHtml, File[] attachments) {
        JavaMailSender javaMailSender = null;//ResourceBundle.getInstance().getJavaMailSender();
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, ArrayUtils.isNotEmpty(attachments), "UTF-8"); // 设置utf-8或GBK编码，否则邮件会有乱码;multipart,true表示文件上传


            helper.setFrom(username, sendNick);
            helper.setTo(toAddress);

            // 设置收件人抄送的名片和地址(相当于群发了)
            //helper.setCc(InternetAddress.parse(MimeUtility.encodeText("邮箱001") + " <@163.com>," + MimeUtility.encodeText("邮箱002") + " <@foxmail.com>"));

            helper.setSubject(mailSubject);
            helper.setText(mailBody, mailBodyIsHtml);

            // 添加附件
            if (ArrayUtils.isNotEmpty(attachments)) {
                for (File file : attachments) {
                    helper.addAttachment(MimeUtility.encodeText(file.getName()), file);
                }
            }

            // 群发
            //MimeMessage[] mailMessages = { mimeMessage };

            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
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
        	logger.info("进入sendMail-------host="+host+" username="+username + " mailBody="+mailBody + " toAddress=" + toAddress);
        	System.out.println("进入sendMail-------host="+host+" username="+username + " mailBody="+mailBody + " toAddress=" + toAddress);
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
            helper.setCc(username);
            // 设置收件人抄送的名片和地址(相当于群发)
            //helper.setCc(InternetAddress.parse(MimeUtility.encodeText("邮箱001") + " <@163.com>," + MimeUtility.encodeText("邮箱002") + " <@foxmail.com>"));

            // 内嵌文件，第1个参数为cid标识这个文件,第2个参数为资源
            //helper.addInline(MimeUtility.encodeText(inLineFile.getName()), inLineFile);

            // 添加附件
            /*if (ArrayUtils.isNotEmpty(attachments)) {
				for (File file : attachments) {
					helper.addAttachment(MimeUtility.encodeText(file.getName()), file);
				}
			}*/

            // 群发
            //MimeMessage[] mailMessages = { mimeMessage };

            mailSender.send(mimeMessage);
            logger.info("结束sendMail-------host="+host+" username="+username);
        	System.out.println("结束sendMail-------host="+host+" username="+username);
            return true;
        } catch (Exception e) {
        	System.out.println("sendMail---"+e.getMessage());
            logger.error("sendMail---"+e.getMessage(), e);
        }
        return false;
    }
    /**
     * 发送邮件 (完整版) (纯JavaMail)
     *
     * @param toAddress       : 收件人邮箱
     * @param XxlJobLog
     */
    public static boolean sendMailByXxlJobLog(String toAddress,XxlJobLog jobLog) {
    	try {
    		logger.info("进入sendMailByXxlJobLog-------host="+host+" username="+username + " toAddress=" + toAddress);
    		System.out.println("进入sendMailByXxlJobLog-------host="+host+" username="+username + " toAddress=" + toAddress);
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
    		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
    		
    		helper.setFrom(username, sendNick);
    		helper.setTo(toAddress);
    		String info[] = getRedisInfo(jobLog);
    		String proName = null==info?"":info[0];
    		String issue = null==info?"":info[1];
    		String subject = null==info?mailSubjectByXxlJobLog:proName+issue;
    		helper.setSubject(subject);
    		helper.setText(splitMailBody(proName,issue,jobLog), true);
    		helper.setCc(username);
    		// 设置收件人抄送的名片和地址(相当于群发)
    		//helper.setCc(InternetAddress.parse(MimeUtility.encodeText("邮箱001") + " <@163.com>," + MimeUtility.encodeText("邮箱002") + " <@foxmail.com>"));
    		
    		// 内嵌文件，第1个参数为cid标识这个文件,第2个参数为资源
    		//helper.addInline(MimeUtility.encodeText(inLineFile.getName()), inLineFile);
    		
    		
    		// 群发
    		//MimeMessage[] mailMessages = { mimeMessage };
    		
    		mailSender.send(mimeMessage);
    		logger.info("结束sendMailByXxlJobLog-------host="+host+" username="+username);
    		System.out.println("结束sendMailByXxlJobLog-------host="+host+" username="+username);
    		return true;
    	} catch (Exception e) {
    		System.out.println("sendMailByXxlJobLog---"+e.getMessage());
    		logger.error("sendMailByXxlJobLog---"+e.getMessage(), e);
    	}
    	return false;
    }

    private static String[] getRedisInfo(XxlJobLog jobLog) {
    	String logId = "XxlJobLog" + jobLog.getId();
    	if(RedisUtil.exists(logId)){
    		String redisMsg = RedisUtil.get(logId);
    		System.out.println("sendMailByXxlJobLog====sendMailByXxlJobLog========"+redisMsg);
    		String msgArr[] = redisMsg.replace("\"", "").split(",");
    		RedisUtil.remove(logId);
    		return msgArr;
    	}
		return null;
	}

	private static String splitMailBody(String proName,String issue,XxlJobLog jobLog) {
    	
    	JobLogController contro = new JobLogController();
    	ReturnT<LogResult> result = contro.parallelLogDetailCat1(1, jobLog.getLogFileName(), jobLog.getExecutorAddress());
    	
    	StringBuffer msg = new StringBuffer();
    	msg.append("技术人员，您好。");
    	msg.append("<br>");
    	msg.append("当前");
    	msg.append("<font color=\"red\">");
    	msg.append(issue);
    	msg.append("</font>");
    	msg.append("期次的 ");
    	msg.append("<font color=\"red\">");
    	msg.append(proName);
    	msg.append("</font>");
    	msg.append(" 产品生产失败，以下是调度日志打印的内容：");
    	msg.append("<br>");
    	msg.append(result.getContent().getLogContent());
    	msg.append("<br>");
    	msg.append("请技术人员针对该期次产品进行检查。");
    	
		return msg.toString();
	}

	public static void main(String[] args) {

        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 1; i++) {
            exec.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    while (total < 1) {
                        String mailBody = "<html><head><meta http-equiv="
                                + "Content-Type"
                                + " content="
                                + "text/html; charset=gb2312"
                                + "></head><body><h1>新书快递通知</h1>你的新书快递申请已推送新书，请到<a href=''>空间"
                                + "</a>中查看</body></html>";

                        sendMail("1376961804@qq.com", "新书邮件", mailBody, true, null);
                        System.out.println(total);
                        total++;
                    }
                }
            }));
        }
    }

}
