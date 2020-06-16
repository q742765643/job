package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.downupload.DownResult;
import com.htht.job.executor.service.downupload.DownResultService;
import org.htht.util.DateUtil;
import org.htht.util.MatchTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by atom on 2018/11/14.
 */

@Service("GRIBProductHandlerShard")
public class GRIBProductHandlerShard implements SharingHandler {




    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
        List<String> FileList = new ArrayList<String>();
        ResultUtil<List<String>> result = new ResultUtil<List<String>>();
        String startTime  = (String) fixmap.get("startTime");
        String endTime  = (String) fixmap.get("endTime");
        String inputFile=(String)dymap.get("inputFile");
        String cycle=(String)dymap.get("cycle");
        if (!inputFile.endsWith("\\")){
            inputFile=inputFile+"\\";
        }
        //SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat simple1 = new SimpleDateFormat("yyyy-MM-dd");


        if(startTime.contains("{")&&startTime.contains("yyyy") || !(startTime.length()>0) || endTime.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE,-1);
            String issue=MatchTime.matchIssue(calendar.getTime(),cycle);
            String date=DateUtil.formatDateTime(calendar.getTime(),"yyyyMMdd");
            String sf=inputFile+"h14_"+date+"_0000.grib";
            File f=new File(sf);
            if (f.exists()&&f.isFile()){
                FileList.add(sf+"@"+issue);
            }
        }else{
            Date startTimeC=DateUtil.strToDate(startTime,"yyyy-MM-dd");
            Date endTimeC =DateUtil.strToDate(endTime,"yyyy-MM-dd");
            Calendar ca = Calendar.getInstance();
            ca.setTime(endTimeC);
            endTimeC=ca.getTime();
            ca.setTime(startTimeC);
            while(ca.getTime().before(endTimeC)||ca.getTime().equals(endTimeC)){//判断是否到结束日期
                ca.add(Calendar.DATE, 1);
                String issue=MatchTime.matchIssue(ca.getTime(),cycle);
                String str=DateUtil.formatDateTime(ca.getTime(),"yyyyMMdd");
                String sf=inputFile+"h14_"+str+"_0000.grib";
                File f=new File(inputFile+str+".grib");
                if (f.exists()&&f.isFile()){
                    FileList.add(sf+"@"+issue);
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
