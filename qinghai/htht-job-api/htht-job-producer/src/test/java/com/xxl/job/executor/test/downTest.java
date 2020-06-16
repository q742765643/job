package com.xxl.job.executor.test;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.Application;
import com.htht.job.executor.model.paramtemplate.DownParam;
import com.htht.job.executor.service.shard.ShardingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class downTest {
    @Autowired
    private ShardingService shardingService;
    @Test
    public void testDown() {
        LinkedHashMap map=new LinkedHashMap();
        DownParam downParam=new DownParam();
        downParam.setDownloadType("history");
        downParam.setDownloadDate("2018-01-02 - 2018-01-03");
        //downParam.setDirectoryType("ftp");
        //downParam.setTimeFormat("yyyy-MM-dd");
        //downParam.setSourceDirectory("/root/Desktop");
        //downParam.setFilterRules("");
        ResultUtil<List<String>> resultUtil= shardingService.execute(JSON.toJSONString(downParam),"downJobHandler",map,map);
        System.out.print(resultUtil);
    }
}
