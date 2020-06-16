package com.htht.job.admin.controller.flow;/**
 * Created by zzj on 2018/4/3.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.admin.service.FileService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.MxImageExport;
import com.htht.job.core.utilbean.FileParam;
import com.htht.job.core.utilbean.RequestMessage;
import com.htht.job.executor.model.flowchart.FlowChartDTO;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job
 * @description: 流程管理控制层
 * @author: zzj
 * @create: 2018-04-03 15:51
 **/
@Controller
@RequestMapping("/flow")
public class FlowController {
    private static Logger logger = LoggerFactory.getLogger(FlowController.class);

    @Resource
    private DubboService dubboService;

    @Autowired
    private FileService fileService;

    @RequestMapping()
    public String index(Model model) {

        return "flow/flow.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length, FlowChartDTO flowChartDTO) {

        if (start != 0) {
            start = start / length;
        }
        return dubboService.pageListFlow(start, length, flowChartDTO);

    }

    @SystemControllerLog(description = "查看了流程任务", type = SystemLog.OPERATELOG)
    @RequestMapping("preview/{id}")
    public void getIcon(@PathVariable("id") String id,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        FlowChartDTO flowChartDTO = dubboService.getFlowById(id);
        byte[] data = flowChartDTO.getProcessPicture();
        response.setContentType("image/png");
        OutputStream out = response.getOutputStream();
        out.write(data);
        out.flush();
        out.close();
    }

    @ResponseBody
    @RequestMapping("/getProcessXmlById/{id}")
    public String getProcessXmlById(@PathVariable("id") String id) {
        try {
            FlowChartDTO flowChartDTO = dubboService.getFlowById(id);
            Map map = new HashMap();
            map.put("xml", flowChartDTO.getProcessFigure());
            map.put("id", flowChartDTO.getId());
            map.put("processDescribe", flowChartDTO.getProcessDescribe());


            return JSON.toJSONString(map);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    @SystemControllerLog(description = "删除了一个流程", type = SystemLog.OPERATELOG)
    @RequestMapping("/deleteFlow")
    @ResponseBody
    public ReturnT<String> deleteFlow(String id) {
        return dubboService.deleteFlow(id);
    }

    @SystemControllerLog(description = "保存了一个流程", type = SystemLog.OPERATELOG)
    @RequestMapping(value = "/saveProcessFromDesigner", method = RequestMethod.POST)
    @ResponseBody
    public ReturnT<String> saveProcessFromDesigner(HttpServletRequest request) {
        try {
            String requestBody = request.getParameter("requestBody");
            FlowChartDTO flowChartDTO = JSON.parseObject(requestBody, FlowChartDTO.class);
            String xml = URLDecoder.decode(flowChartDTO.getPicture(), "utf-8");
            byte[] pic = MxImageExport.getImageByte(xml, flowChartDTO.getPicWidth(), flowChartDTO.getPicHeight());
            flowChartDTO.setProcessPicture(pic);
            flowChartDTO.setProcessFigure(flowChartDTO.getFile());
            flowChartDTO.setId(flowChartDTO.getProcessId());
            dubboService.saveOrUpdateFlow(flowChartDTO);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ReturnT.FAIL;
    }

    @ResponseBody
    @RequestMapping("/getRootFolderList")
    public void getRootFolderList(HttpServletRequest request, HttpServletResponse response) {
        String callback = request.getParameter("callback");
        try {
            List<FileParam> list = fileService.getRootFolderList();
            JSONObject json = new JSONObject();
            json.put("listFiles", list);
            String callbackf = callback + "(" + json.toString() + ")";
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(callbackf);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @ResponseBody
    @RequestMapping("/listFilesByFolder")
    public void listFilesByFolder(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e1) {
            logger.error(e1.getMessage(), e1);
        }
        String callback = request.getParameter("callback");
        String encodePathList = request.getParameter("pathList");
        String decodePathList = "";
        try {
            decodePathList = URLDecoder.decode(encodePathList, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            logger.error(e1.getMessage(), e1);
        }
        RequestMessage rm = new RequestMessage();
        List<String> list = Arrays.asList(decodePathList.split(","));
        rm.setPathList(list);
        List<FileParam> resultList;
        try {
            resultList = fileService.listFilesByFolder(rm);
            JSONObject json = new JSONObject();
            json.put("listFiles", resultList);
            String callbackf = callback + "(" + json.toString() + ")";
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(callbackf);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}


