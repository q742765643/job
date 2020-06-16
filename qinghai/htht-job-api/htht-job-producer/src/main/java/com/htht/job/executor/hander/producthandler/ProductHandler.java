package com.htht.job.executor.hander.producthandler;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.producthandler.service.ProductProducerService;
import com.htht.job.executor.service.hander.GeneralHanderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author yuguoqing
 * @Date 2018年4月23日 下午2:35:22
 *
 *
 */
@JobHandler(value = "productHandler")
@Service
public class ProductHandler extends IJobHandler
{
	@Autowired
	private ProductProducerService productProducerService;

	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception
	{
		ResultUtil<String> result = new ResultUtil<String>();
		/** =======1.校验参数============== **/
//		ParsingUtil.argumentparsing(triggerParam, result);
		
//		if (!result.isSuccess())
//		{
//			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
//		}
		/** =======2.执行业务============================= **/
		result = productProducerService.execute(triggerParam, result);
		
		if (!result.isSuccess())
		{
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		
		return ReturnT.SUCCESS;
	}

}
