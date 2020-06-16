package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;

import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.hander.predatahandler.service.DataMataInfoService;
import com.htht.job.executor.model.downupload.DownResult;
import com.htht.job.executor.service.downupload.DownResultService;
import org.htht.util.MatchTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by atom on 2018/11/9.
 */


@Service("ARMProductHandlerShard")
public class ARMProductHandlerShard implements SharingHandler {

    @Autowired
    private DataMataInfoService dataMataInfoService;
    @Autowired
    private DownResultService downResultService;

    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
        List<String> FileList = new ArrayList<String>();
        ResultUtil<List<String>> result = new ResultUtil<List<String>>();
        String startTime  = (String) fixmap.get("startTime");
        String endTime  = (String) fixmap.get("endTime");
        String inputPath=(String) dymap.get("inputFile");
        if (!inputPath.endsWith("\\")){
            inputPath=inputPath+"\\";
        }
        String cycle = (String) dymap.get("cycle");

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if(startTime.contains("{")&&startTime.contains("yyyy") || !(startTime.length()>0) || endTime.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH,-1);
            String startDate = MatchTime.matchIssue(calendar.getTime(),cycle).substring(0,6);
            String issue=startDate+"000000";
            File f=new File(inputPath+startDate);
            if (f.exists()&&f.isDirectory()&&f.listFiles().length==7){
                FileList.add(issue);
            }
        }else{
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM");
            Date startTimeC=simple.parse(startTime);
            Date endTimeC =simple.parse(endTime);
            Calendar ca = Calendar.getInstance();
            ca.setTime(endTimeC);
            endTimeC=ca.getTime();
            ca.setTime(startTimeC);
            while(ca.getTime().before(endTimeC)||ca.getTime().equals(endTimeC)){//判断是否到结束日期
                String str=MatchTime.matchIssue(ca.getTime(),cycle).substring(0,6);
                ca.add(Calendar.MONTH, 1);
                File f=new File(inputPath+str);
                System.err.println(inputPath+str);
                if (f.exists()&&f.isDirectory()&&f.listFiles().length==7){
                    FileList.add(str+"000000");
                }
            }
        }






        if (FileList.isEmpty()) {
            return result;
        }
        if (!result.isSuccess()) {
            return result;
        }
        result.setResult(FileList);
        if(result.getResult().isEmpty()){
            System.out.println("EndShard");
        }
        return result;
    }
}
