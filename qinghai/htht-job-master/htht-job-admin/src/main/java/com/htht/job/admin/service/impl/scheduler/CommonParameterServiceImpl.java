package com.htht.job.admin.service.impl.scheduler;/**
 * Created by zzj on 2018/10/30.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.admin.service.CommonParameterService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.constant.BooleanConstant;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.parallellog.ParallelLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job-api
 * @description:
 * @author: zzj
 * @create: 2018-10-30 13:21
 **/
@Service
public class CommonParameterServiceImpl implements CommonParameterService {
    private static Logger logger = LoggerFactory.getLogger(CommonParameterServiceImpl.class);

    @Resource
    private DubboService dubboService;

    /** 
    * @Description: 根据uuid替换输出值
    * @Param: [commonParametersOld, commonParametersNew] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void repalceInListValueByUuid(List<CommonParameter> commonParametersOld, List<CommonParameter> commonParametersNew){
        for(int i=0;i<commonParametersOld.size();i++){
            for(CommonParameter commonParameter:commonParametersNew){
                if(commonParametersOld.get(i).getUuid().equals(commonParameter.getUuid())
                        &&!FlowConstant.OUTFILE.equals(commonParameter.getParameterType())
                        &&!FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())
                        &&!FlowConstant.OUTSTRING.equals(commonParameter.getParameterType())){
                    commonParametersOld.get(i).setValue(commonParameter.getValue());
                    commonParametersOld.get(i).setExpandedname(commonParameter.getExpandedname());
                    break;
                }
            }

        }
    }
    /** 
    * @Description: 根据dataId替换所有值 
    * @Param: [commonParametersOld, commonParametersNew] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void repalceListValueByDataId(List<CommonParameter> commonParametersOld,List<CommonParameter> commonParametersNew){
        for(int i=0;i<commonParametersOld.size();i++){
            for(CommonParameter commonParameter:commonParametersNew){
                if(commonParametersOld.get(i).getDataID().equals(commonParameter.getDataID())){
                    commonParametersOld.get(i).setValue(commonParameter.getValue());
                    commonParametersOld.get(i).setExpandedname(commonParameter.getExpandedname());
                    break;
                }
            }

        }
    }
    /** 
    * @Description: 根据dataId替换输入值
     * @Param: [commonParametersOld, commonParametersNew] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void repalceInListValueByDataId(List<CommonParameter> commonParametersOld,List<CommonParameter> commonParametersNew){
        for(int i=0;i<commonParametersOld.size();i++){
            for(CommonParameter commonParameter:commonParametersNew){
                if(commonParametersOld.get(i).getDataID().equals(commonParameter.getDataID())
                        &&!FlowConstant.OUTFILE.equals(commonParameter.getParameterType())
                        &&!FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())
                        &&!FlowConstant.OUTSTRING.equals(commonParameter.getParameterType()) ){
                    commonParametersOld.get(i).setValue(commonParameter.getValue());
                    commonParametersOld.get(i).setExpandedname(commonParameter.getExpandedname());
                    break;
                }
            }

        }
    }
    /** 
    * @Description: 去掉reply 根据dataid替换传递值 
    * @Param: [commonParametersOld, commonParametersNew] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void repalceListValueByDataIdReply(List<CommonParameter> commonParametersOld,List<CommonParameter> commonParametersNew){
        for(int i=0;i<commonParametersOld.size();i++){
            for(CommonParameter commonParameter:commonParametersNew){
                if(commonParametersOld.get(i).getDataID().equals(commonParameter.getDataID().replace("Reply_", ""))){
                    commonParametersOld.get(i).setValue(commonParameter.getValue());
                    commonParametersOld.get(i).setExpandedname(commonParameter.getExpandedname());
                    break;
                }
            }

        }
    }
    /** 
    * @Description: 根据uuid替换所有值
    * @Param: [commonParametersOld, commonParametersNew] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void repalceListValueByUuid(List<CommonParameter> commonParametersOld,List<CommonParameter> commonParametersNew){
        for(int i=0;i<commonParametersOld.size();i++){
            for(CommonParameter commonParameter:commonParametersNew){
                if(commonParametersOld.get(i).getUuid().equals(commonParameter.getUuid())){
                    commonParametersOld.get(i).setValue(commonParameter.getValue());
                    commonParametersOld.get(i).setExpandedname(commonParameter.getExpandedname());
                    break;
                }
            }

        }
    }
   
    /** 
    * @Description: 根据流程id寻找所有输出值 
    * @Param: [jobLogId, flowId] 
    * @return: java.util.List<com.htht.job.executor.model.algorithm.CommonParameter> 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public List<CommonParameter> findOutputParameter(int jobLogId,String flowId) {
        List<CommonParameter> commonParameterList = new ArrayList<>();
        List<FlowLog> flowLogList = dubboService.findFlowLogList(jobLogId,flowId);
        for (FlowLog flowLog : flowLogList) {
            List<CommonParameter> commonParameters = JSON.parseArray(flowLog.getDynamicParameter(), CommonParameter.class);
            for (CommonParameter commonParameter : commonParameters) {
                if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType()) ||
                        FlowConstant.OUTSTRING.equals(commonParameter.getParameterType()) ||
                        FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())) {
                    commonParameterList.add(commonParameter);
                }
            }

        }
        return commonParameterList;
    }
    /** 
    * @Description: 替换输出值 
    * @Param: [dynamicParameter, newflowLog, parallelLog, mapValue] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void replaceFlowDynamicParameter(List<CommonParameter> dynamicParameter, FlowLog newflowLog, ParallelLog parallelLog, Map<Integer, String> mapValue  ){

        List<CommonParameter> pdynamicParameter = JSON.parseArray(parallelLog.getDynamicParameter(), CommonParameter.class);
        for (int j = 0; j < pdynamicParameter.size(); j++) {
            this.replaceFlowDynamicParameterOut(pdynamicParameter.get(j),j,newflowLog,mapValue);
            if(FlowConstant.OUTFOLDER.equals(dynamicParameter.get(j).getParameterType())){
                String value = mapValue.get(j);
                if (StringUtils.isEmpty(value)) {
                    mapValue.put(j, pdynamicParameter.get(j).getValue());
                }
            }

        }

    }
    /** 
    * @Description: 替换输出值
    * @Param: [commonParameter, j, flowLog, mapValue] 
    * @return: void 
    * @Author: zzj
    * @Date: 2018/10/30 
    */ 
    public void replaceFlowDynamicParameterOut(CommonParameter commonParameter,int j,FlowLog flowLog,Map<Integer, String> mapValue){

        if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType()) ||
                FlowConstant.OUTSTRING.equals(commonParameter.getParameterType())) {
            if (BooleanConstant.TRUE.equals(flowLog.getIsPl())) {
                String value = mapValue.get(j);
                if (StringUtils.isEmpty(value)) {
                    mapValue.put(j, commonParameter.getValue());
                } else {
                    mapValue.put(j, mapValue.get(j) + "#HT#" + commonParameter.getValue());

                }
            } else {
                mapValue.put(j, commonParameter.getValue());
            }
        }
    }

}

