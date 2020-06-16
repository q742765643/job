package com.htht.job.executor.hander.shardinghandler;

import com.htht.job.core.handler.SharingHandler;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zzj on 2018/1/16.
 */
@Service("historyJobHandlerShard")
public class HistoryJobHandlerShard implements SharingHandler {
    @Override
    public ResultUtil<List<String>> execute(String params, LinkedHashMap fixmap, LinkedHashMap dymap) throws Exception {
        ResultUtil<List<String>> result = new ResultUtil<List<String>>();
        String startDate = (String) fixmap.get("startDate");
        String endDate = (String) fixmap.get("endDate");
        List<Date> list = DateUtil.getHistoryDatelist(startDate, endDate);
        List<String> dateList = new ArrayList<String>();
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            String date = DateUtil.getIssue(list.get(i));
            set.add(date);
        }
        dateList.addAll(set);
        result.setResult(dateList);
        return result;
    }
}
