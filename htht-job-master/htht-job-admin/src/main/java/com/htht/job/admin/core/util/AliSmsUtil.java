package com.htht.job.admin.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;

/**
 * 阿里云短信发送.Util
 *
 * @author zhangzhipeng 2018.12.26
 */
public class AliSmsUtil {
    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
	// 产品名称:云通信短信API产品,开发者无需替换
	private static final String product = "Dysmsapi";
	// 产品域名,开发者无需替换
	private static final String domain = "dysmsapi.aliyuncs.com";

	private static final String accessKeyId;
	private static final String accessKeySecret;

	static {
		accessKeyId = PropertiesUtil.getString("master.job.alisSms.accessKeyId");
		accessKeySecret = PropertiesUtil.getString("master.job.alisSms.accessKeySecret");
	}

	/**
	 * 发送短信
	 * @param phoneNumbers 短信接收号码,支持以逗号分隔的形式
	 * @param signName 短信签名
	 * @param templateCode 短信模板ID
	 * @param map 短信模板变量
	 * @return
	 */
	public static SendSmsResponse sendSms(String phoneNumbers,String signName,String templateCode,Map<String,String> map){
		Gson gson = new Gson();
		String sjson = gson.toJson(map);
		return sendSms(phoneNumbers, signName, templateCode, sjson);
	}
	/**
	 * 发送短信
	 * @param phoneNumbers 短信接收号码,支持以逗号分隔的形式
	 * @param signName 短信签名
	 * @param templateCode 短信模板ID
	 * @param json 短信模板变量替换
	 * @return
	 */
	public static SendSmsResponse sendSms(String phoneNumbers,String signName,String templateCode,String json){
		SendSmsResponse sendSmsResponse = new SendSmsResponse();
		try {
			// 可自助调整超时时间
			System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
			System.setProperty("sun.net.client.defaultReadTimeout", "10000");
	
			// 初始化acsClient,暂不支持region化
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
	
			// 组装请求对象-具体描述见控制台-文档部分内容
			SendSmsRequest request = new SendSmsRequest();
			// 必填:待发送手机号
			request.setPhoneNumbers(phoneNumbers);
			// 必填:短信签名-可在短信控制台中找到
			request.setSignName(signName);
			// 必填:短信模板-可在短信控制台中找到
			request.setTemplateCode(templateCode);
			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			request.setTemplateParam(json);
			//request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
	
			// 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
			// request.setSmsUpExtendCode("90997");
	
			// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
			//request.setOutId("yourOutId");
	
			// hint 此处可能会抛出异常，注意catch
			sendSmsResponse = acsClient.getAcsResponse(request);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return sendSmsResponse;
	}
	// 查明细
	public static QuerySendDetailsResponse querySendDetails(String bizId,String phoneNumber){
		return querySendDetails(bizId,phoneNumber,new Date());
	}
	/**
	 * 查明细
	 * @param bizId
	 * @param phoneNumber
	 * @param date
	 * @return
	 */
	public static QuerySendDetailsResponse querySendDetails(String bizId,String phoneNumber,Date date){
		QuerySendDetailsResponse querySendDetailsResponse = new QuerySendDetailsResponse();
		try {
			// 可自助调整超时时间
			System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
			System.setProperty("sun.net.client.defaultReadTimeout", "10000");
	
			// 初始化acsClient,暂不支持region化
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
					accessKeyId, accessKeySecret);
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,
					domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
	
			// 组装请求对象
			QuerySendDetailsRequest request = new QuerySendDetailsRequest();
			// 必填-号码
			request.setPhoneNumber(phoneNumber);
			// 可选-流水号
			request.setBizId(bizId);
			// 必填-发送日期 支持30天内记录查询，格式yyyyMMdd
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
			request.setSendDate(ft.format(date));
			// 必填-页大小
			request.setPageSize(10L);
			// 必填-当前页码从1开始计数
			request.setCurrentPage(1L);
	
			// hint 此处可能会抛出异常，注意catch
			querySendDetailsResponse = acsClient
					.getAcsResponse(request);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return querySendDetailsResponse;
	}

	public static void main(String[] args){
		Map map = new HashMap();
		map.put("time", "2019年1月1日9点");
		map.put("degree", "二级");
		map.put("ganNum", "2018号");
		map.put("distance", "1.3公里号");
		map.put("vegetation", "林地");
		Gson g = new Gson();
		String s = g.toJson(map);
		System.out.println(s);
		String phoneNumbers = "15311440457,15311440458";
		String signName = "航天宏图";
		String templateCode = "SMS_153790884";
		/*
		${time}监测到${degree}疑似火情,火点最近杆塔号${ganNum},距离约${distance},植被为${vegetation}
		*/
		String json = "{\"time\":\"2019年1月1日9点\",\"degree\":\"二级\",\"ganNum\":\"2018号\",\"distance\":\"1.3公里\","
				+ "\"vegetation\":\"林地\"}";
		System.out.println(json);
		// 发短信
		SendSmsResponse response = sendSms(phoneNumbers, signName, templateCode, json);
		System.out.println("短信接口返回的数据----------------");
		System.out.println("Code=" + response.getCode());
		System.out.println("Message=" + response.getMessage());
		System.out.println("RequestId=" + response.getRequestId());
		System.out.println("BizId=" + response.getBizId());

		//Thread.sleep(3000L);

		// 查明细
		if (response.getCode() != null && response.getCode().equals("OK")) {
			String[] phs = phoneNumbers.split(",");
			for (int j = 0; j < phs.length; j++) {
				QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(response.getBizId(),phs[j]);
				System.out.println("短信明细查询接口返回数据----------------");
				System.out.println("Code=" + querySendDetailsResponse.getCode());
				System.out.println("Message="
						+ querySendDetailsResponse.getMessage());
				int i = 0;
				for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse
						.getSmsSendDetailDTOs()) {
					System.out.println("SmsSendDetailDTO[" + i + "]:");
					System.out.println("Content=" + smsSendDetailDTO.getContent());
					System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
					System.out.println("OutId=" + smsSendDetailDTO.getOutId());
					System.out
					.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
					System.out.println("ReceiveDate="
							+ smsSendDetailDTO.getReceiveDate());
					System.out
					.println("SendDate=" + smsSendDetailDTO.getSendDate());
					System.out.println("SendStatus="
							+ smsSendDetailDTO.getSendStatus());
					System.out.println("Template="
							+ smsSendDetailDTO.getTemplateCode());
				}
				System.out.println("TotalCount="
						+ querySendDetailsResponse.getTotalCount());
				System.out.println("RequestId="
						+ querySendDetailsResponse.getRequestId());
			}
		}

	}
}
