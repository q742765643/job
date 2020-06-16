package com.htht.job.admin.controller.ftp;

import com.alibaba.fastjson.JSONArray;
import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.ftp.FtpDTO;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzj on 2018/1/16.
 */
@Controller
@RequestMapping("/ftp")
public class FtpController {
    @Autowired
    private DubboService dubboService;


    @RequestMapping
    public String index(Model model) {

        return "/ftp/ftp";
    }


    @RequestMapping("/findSelectFtp")
    @ResponseBody
    public String findSelectFtp() {
        return dubboService.findSelectFtp();
    }

    @SystemControllerLog(description = "查看FTP", type = SystemLog.OPERATELOG)
    @RequestMapping("/getFtpsByPage")
    @ResponseBody
    public Map<String, Object> getFtpsByPage(@RequestParam(required = false, defaultValue = "0") int start,
                                             @RequestParam(required = false, defaultValue = "10") int length) {
        if (start != 0) {
            start = start / length;
        }
        Map<String, Object> maps = new HashMap<>();
        Pageable pageable = new PageRequest(start, length, new Sort(Sort.Direction.DESC, "updateTime"));
        Page<FtpDTO> page = dubboService.getFtpsByPage(pageable);

        maps.put("recordsTotal", page.getTotalElements());        // 总记录数
        maps.put("recordsFiltered", page.getTotalElements());    // 过滤后的总记录数
        maps.put("data", page.getContent());
        return maps;
    }

    @SystemControllerLog(description = "编辑了一条FTP记录", type = SystemLog.OPERATELOG)
    @RequestMapping("/getById")
    @ResponseBody
    public FtpDTO getById(String id) {
        return dubboService.getById(id);
    }

    @SystemControllerLog(description = "删除了一条FTP记录", type = SystemLog.OPERATELOG)
    @RequestMapping("/del")
    @ResponseBody
    public int del(String id) {
        return dubboService.del(id);
    }

    @SystemControllerLog(description = "更新了一条FTP记录", type = SystemLog.OPERATELOG)
    @RequestMapping("/updeat")
    @ResponseBody
    public int updeat(FtpDTO ftpDTO) {
        return dubboService.updeat(ftpDTO);
    }

    @SystemControllerLog(description = "添加了一条FTP记录", type = SystemLog.OPERATELOG)
    @RequestMapping("/addFtp")
    @ResponseBody
    public ReturnT<String> addFtp(FtpDTO ftpDTO) {
        ftpDTO.setUpdateTime(new Date());
        FtpDTO res = dubboService.saveFtp(ftpDTO);
        if (StringUtils.isEmpty(res.getId())) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @SystemControllerLog(description = "删除了多条FTP记录", type = SystemLog.OPERATELOG)
    @RequestMapping("/deleteFtps")
    @ResponseBody
    public ReturnT<String> deleteFtps(String data) {
        JSONArray json = JSONArray.parseArray(data);

        int res = 0;
        for (Object aJson : json) {
            res += dubboService.del(String.valueOf(aJson));
        }

        if (json.size() == res) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @SystemControllerLog(description = "进行了FTP测试连接操作", type = SystemLog.OPERATELOG)
    @RequestMapping("/testConnect")
    @ResponseBody
    public boolean testConnect(String ip, int port, String userName, String pwd) {
        try {
            return dubboService.testConnectFtp(ip, port, userName, pwd);
        } catch (Exception e) {
            return false;
        }
    }

}
