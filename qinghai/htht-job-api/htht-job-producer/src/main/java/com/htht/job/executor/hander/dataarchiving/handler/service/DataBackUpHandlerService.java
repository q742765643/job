package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.UrlUtil;
import com.htht.job.executor.hander.dataarchiving.util.webservice.client.ClientUtil;
import com.htht.job.executor.model.dms.util.db.BaseDaoUtil;

@Transactional
@Service("dataBackUpHandlerService")
public class DataBackUpHandlerService {
	@Autowired
	private BaseDaoUtil baseDaoUtil;
	@Value("${xxl.job.admin.addresses}")
	private String addresses;
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			// 获取近线备份列表 
			List<Object[]> nearline = baseDaoUtil.getByJpql("select f_dataid,f_location from htht_dms_meta_info where (f_nearlinepath is null or f_nearlinepath='') and f_importdate > date_sub(SYSDATE(), interval (SELECT paramvalue FROM htht_dms_sys_param  where paramcode='scanday') day) and f_location is not null ",null);
			// 获取离线备份列表
			List<Object[]> offline = baseDaoUtil.getByJpql(" select f_dataid,f_location from htht_dms_meta_info where (f_offlinepath is null or f_offlinepath='') and f_importdate > date_sub(SYSDATE(), interval (SELECT paramvalue FROM htht_dms_sys_param  where paramcode='scanday') day) and f_location is not null ",null);
			// 获取近线备份流程ID
			List<Integer> nearline_jobid = baseDaoUtil.getByJpql("SELECT paramvalue FROM htht_dms_sys_param  where paramcode='nearline_jobid'",null);
			// 获取离线备份流程ID
			List<Integer> offline_jobid = baseDaoUtil.getByJpql("SELECT paramvalue FROM htht_dms_sys_param  where paramcode='offline_jobid'",null);
			
			
			if(offline_jobid.size()==0 || null == offline_jobid.get(0) || "".equals(offline_jobid.get(0))) {
				result.setErrorMessage("数据备份扫描-离线备份流程ID未填写!");
				return result;
			} else {
				for (int i = 0; i < offline.size(); i++) {
					HandlerParam handlerParam = new HandlerParam();
					handlerParam.setBackupDataid(offline.get(i)[0].toString());
					handlerParam.setBaseUrl(offline.get(i)[1].toString());
					String resultMessage = UrlUtil.getURLEncoderString(JSON.toJSONString(handlerParam));
					ClientUtil.interfaceUtil(addresses+"/api/achive/cp?jobId="+offline_jobid.get(0)+"&json="+resultMessage, "");
				}
			}
			
			if(nearline_jobid.size()==0 || null == nearline_jobid.get(0) || "".equals(nearline_jobid.get(0))) {
				result.setErrorMessage("数据备份扫描-近线备份流程ID未填写!");
				return result;
			} else {
				
				for (int i = 0; i < nearline.size(); i++) {
					HandlerParam handlerParam = new HandlerParam();
					handlerParam.setBackupDataid(nearline.get(i)[0].toString());
					handlerParam.setBaseUrl(nearline.get(i)[1].toString());
					String resultMessage = UrlUtil.getURLEncoderString(JSON.toJSONString(handlerParam));
					ClientUtil.interfaceUtil(addresses+"/api/achive/cp?jobId="+nearline_jobid.get(0)+"&json="+resultMessage, "");
				}
			}
		} catch (Exception e) {
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}
}
