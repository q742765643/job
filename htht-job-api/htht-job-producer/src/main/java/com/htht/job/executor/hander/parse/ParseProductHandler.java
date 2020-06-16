package com.htht.job.executor.hander.parse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.parse.service.ParseProductHandlerService;

/**
 * 	解析入库功能
 * @author Administrator
 *
 */
@JobHandler(value = "parseProductHandler")
@Service
public class ParseProductHandler extends IJobHandler {
	@Autowired
	private ParseProductHandlerService parseProductHandlerService;
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result=new 	ResultUtil<>();
		/**=======1.校验参数==============**/
		ParsingUtil.argumentparsing(triggerParam, result);
		if(!result.isSuccess()){
			return new ReturnT<>(ReturnT.FAIL_CODE,  result.toString());
		}		
		/**=======2.执行业务=============================**/
		result=parseProductHandlerService.excute(triggerParam,result);
		if(!result.isSuccess()){
			return new ReturnT<>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}

