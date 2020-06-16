package com.htht.job.executor.hander;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.*;
import com.htht.job.executor.hander.resolvehandler.ResolvePieprjService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by zzj on 2018/1/31.
 */
@JobHandler(value = "testHandler")
@Service
public class TestHandler extends IJobHandler {
    private static final String RESULT_START = "<result>";
    private static final String RESULT_END = "</result>";
    private static final String windows = "Z:";
    private static final String linux = "/RSData6";
    @Autowired
    private ResolvePieprjService resolvePieprjService;


    @Value("${cluster.job.executor.logpath}")
    private String logpath;

    public static void main(String[] args) throws IOException {
        ExecProcess processor = new ExecProcess();
        //CmdMessage cmdMsg = processor.execCmd("ping 127.0.0.1",true,"/zzj/data/logs/111.log");
        CmdMessage cmdMsg = processor.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111", true, "/zzj/data/logs/111.log");
        //ScriptUtil.execCmd("java -jar /zzj/data/logs/Demo.jar 111 1111","/zzj/data/logs/111.log");

    }

    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
        Map map=triggerParam.getDynamicParameter();
        String infile=(String) map.get("infile");
        String outfile=(String) map.get("outfile");

        System.out.println(infile);
        System.out.println(outfile);
        FileUtils.copyFile(new File(infile),new File(outfile));
        Thread.sleep(100);
        return ReturnT.SUCCESS;
    }

}
