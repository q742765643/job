package com.htht.job.executor.service.builder;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.executor.service.builder.part.*;

import java.util.List;
import java.util.Map;

public class ProductHandler {
    private IssuePart issueHandler;
    private IsExistPart isExistPart;
    private CreateXmlPart createXmlPart;
    private DoExcutePart doExcutePart;
    private PraseXmlPart praseXmlPart;
    private Map<String, Object> jobDataMap;

    public ProductHandler(IssuePart issueHandler, IsExistPart isExistPart, CreateXmlPart createXmlPart,
                          DoExcutePart doExcutePart, PraseXmlPart praseXmlPart, Map<String, Object> jobDataMap) {
        super();
        this.issueHandler = issueHandler;
        this.isExistPart = isExistPart;
        this.createXmlPart = createXmlPart;
        this.doExcutePart = doExcutePart;
        this.praseXmlPart = praseXmlPart;
        this.jobDataMap = jobDataMap;
    }

    public ReturnT<String> excute(TriggerParam triggerParam, ReturnT<String> result) {
        /***============ 1.获取符合条件的期号列表 =================**/
        List<String> issueList = issueHandler.getIssueList(result, jobDataMap);
        if (result.getCode() == ReturnT.FAIL_CODE) {
            return result;
        }
        for (String issue : issueList) {
            /***============ 2.判断是否执行 =================**/
            jobDataMap.put("productIssue", issue);
            if (isExistPart.isExist(issue)) {
                /////////////////////////////////////////
                //                待补充               //
                /////////////////////////////////////////
                result.setCode(ReturnT.SUCCESS_CODE);
                result.setContent("产品已经存在");
                return result;
            }
            /***============ 3.创建输入xml =================**/
            XmlInfo xmlInfo = createXmlPart.createXml(issue, result, jobDataMap);
            if (result.getCode() == ReturnT.FAIL_CODE) {
                return result;
            }
            /***============ 4.执行算法调度 =================**/
            doExcutePart.doExcute(xmlInfo.getInputxml(), result, jobDataMap);
            if (result.getCode() == ReturnT.FAIL_CODE) {
                return result;
            }
            /***============ 5.解析xml入库 =================**/
            praseXmlPart.toDb(xmlInfo.getOutputxml(), result, jobDataMap);
//			praseXmlPart.toDb("C:\\Users\\Administrator\\Desktop\\product(1).xml", result, jobDataMap);
        }
        result.setCode(ReturnT.SUCCESS_CODE);
        result.setMsg("入库成功");
        return result;
    }
}
