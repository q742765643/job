package com.htht.job.executor.hander.cimiss3;/**
 * Created by zzj on 2018/12/6.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.executor.hander.cimiss2.module.DownloadInfo;
import com.htht.job.executor.model.downupload.DownResultDTO;
import com.htht.job.executor.service.downupload.DownResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;

/**
 * 5
 *
 * @program: htht-job
 * @description:
 * @author: zzj
 * @create: 2018-12-06 16:51
 **/
@JobHandler(value = "cimiss3Handler")
@Service
public class Cimiss3Handler extends IJobHandler {
    @Autowired
    private DownResultService downResultService;

    @Override
    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {

        DownloadInfo info = JSON.parseObject(triggerParam.getExecutorParams(), DownloadInfo.class);
        File file=new File(info.getFilePath()+'/'+info.getFileName());
        HttpURLConnection httpConnection= (HttpURLConnection) new URL(info.getFileURL()).openConnection();

        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        httpConnection.setUseCaches(false);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type","application/octet-stream");
        httpConnection.setRequestProperty("Connection","Keep-Alive");
        httpConnection.setRequestProperty("Charset","UTF-8");

        try (InputStream inputStream = httpConnection.getInputStream();
             OutputStream outputStream = new FileOutputStream(file);
        ) {
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }

        }
         catch (IOException e) {
            Files.delete(file.toPath());
            return ReturnT.FAIL;

        }
        DownResultDTO downResultDTO = new DownResultDTO();
        downResultDTO.setZt("1");
        downResultDTO.setRealFileName(info.getFileName());
        downResultDTO.setFileSize(Long.parseLong(info.getFileSize()));
        downResultDTO.setBz("cimiss2file");
        downResultDTO.setFormat(info.getFormat());
        downResultDTO.setFilePath(info.getFilePath());
        downResultService.saveDownResult(downResultDTO);


        return ReturnT.SUCCESS;
    }
}

