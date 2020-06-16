package com.htht.job.executor.hander.dataarchiving.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.dataarchiving.handler.service.XMLParsingHandlerService;
import com.htht.job.executor.hander.dataarchiving.util.CleanUtil;

/**
 * 入库插件-XML解析插件
 * 
 * @author LY 2018-04-08
 *
 */
@JobHandler(value = "xmlParsingHandler")
@Service
public class XMLParsingHandler extends IJobHandler {
	@Autowired
	private XMLParsingHandlerService xmlParsingHandlerService;
	@Autowired
	private CleanUtil cleanUtil;
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil<String>();
		result = xmlParsingHandlerService.execute(triggerParam, result);
		if (!result.isSuccess()) {
			// 清理缓存
			cleanUtil.clearArchiveData(triggerParam);
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}
}
