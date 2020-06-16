package com.htht.job.executor.hander.h8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.h8.service.H8ProHanderService;

/**
 * H8预处理Handler
 * @author Administrator
 *
 */
@JobHandler(value = "h8ProHandler")
@Service
public class H8ProHandler extends IJobHandler {
	@Autowired
	private H8ProHanderService h8ProHanderService;
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result=new 	ResultUtil<String>();
		/**=======1.校验参数==============**/
		ParsingUtil.argumentparsing(triggerParam, result);
		if(!result.isSuccess()){
			return new ReturnT<String>(ReturnT.FAIL_CODE,  result.toString());
		}		
		/**=======2.执行业务=============================**/
		result=h8ProHanderService.excute(triggerParam,result);
		if(!result.isSuccess()){
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}
