package com.htht.job.executor.hander.h8;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.ParsingUtil;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.h8.service.H8ProcessCloudNdviService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@JobHandler(value = "H8Process_CLOUD_NDVI")
@Service
public class H8ProcessCloudNdvi extends IJobHandler {

    @Autowired
    private H8ProcessCloudNdviService h8ProcessCloudNdviService;
    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        ResultUtil<String> result=new 	ResultUtil<>();
        /**=======1.校验参数==============**/
//        ParsingUtil.argumentparsing(triggerParam, result);
//        if(!result.isSuccess()){
//            return new ReturnT<String>(ReturnT.FAIL_CODE,  result.toString());
//
//        }
        /**=======2.执行业务=============================**/
        result=h8ProcessCloudNdviService.excute(triggerParam,result);
        if(!result.isSuccess()){
            return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
        }

        return ReturnT.SUCCESS;
    }
}
