package com.htht.job.admin.controller.appinterface;

/**
 * Created by zzj on 2018/3/13.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.util.FlowConstant;
import com.htht.job.core.util.MxImageExport;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.flowchart.FlowChartDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.redis.listener.Topic;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/api")
@Api(value = "用户controller", description = "用户相关操作")
public class demo {
    @Resource
    private AtomicAlgorithmService atomicAlgorithmService;
    @Resource
    private DubboService dubboService;

    @RequestMapping(value = "index", method = RequestMethod.POST)
    //方法上使用@ApiOperation
    @ApiOperation(value = "首页", notes = "跳转到首页")
    //参数使用@ApiParam
    public Object getIndex(@ApiParam(name = "topic实体", value = "json格式", required = true) @RequestBody Topic topic) {
        //业务内容，被我删除了，请忽略，主要看上面的注解
        Object obj = new Object();
        return obj;
    }

    @RequestMapping(value = "/index1", method = RequestMethod.GET)
    //方法上使用@ApiOperation
    @ApiOperation(value = "首页", notes = "跳转到首页")
    //参数使用@ApiParam
    public List<ZtreeView> getIndex1() {
        //业务内容，被我删除了，请忽略，主要看上面的注解
        List<Map> map = atomicAlgorithmService.findTreeListBySql();
        List<ZtreeView> resulTreeNodes = new ArrayList<ZtreeView>();
        ZtreeView node;
        for (int i = 0; i < map.size(); i++) {
            node = new ZtreeView();
            node.setId((String) map.get(i).get("id"));
            node.setpId((String) map.get(i).get("parent_id"));
            node.setName((String) map.get(i).get("text"));
            resulTreeNodes.add(node);

        }

        return resulTreeNodes;
    }

    @RequestMapping(value = "/index2", method = RequestMethod.GET)
    //方法上使用@ApiOperation
    @ApiOperation(value = "首页", notes = "跳转到首页")
    public AtomicAlgorithmDTO findParameterById(String id) {
        AtomicAlgorithmDTO atomicAlgorithmDTO = atomicAlgorithmService.findParameterById(id);
        if (null != atomicAlgorithmDTO) {
            List<CommonParameter> commonParameterList = JSON.parseArray(atomicAlgorithmDTO.getDynamicParameter(), CommonParameter.class);
            List<CommonParameter> outputParameter = new ArrayList<CommonParameter>();
            for (CommonParameter commonParameter : commonParameterList) {
                if (FlowConstant.OUTFILE.equals(commonParameter.getParameterType())
                        || FlowConstant.OUTSTRING.equals(commonParameter.getParameterType())
                        || FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())) {
                    outputParameter.add(commonParameter);
                }
            }
            atomicAlgorithmDTO.setDynamicParameter(JSON.toJSONString(commonParameterList));
            atomicAlgorithmDTO.setOutputParameter(JSON.toJSONString(outputParameter));
        } else {
            atomicAlgorithmDTO = new AtomicAlgorithmDTO();
            ProcessStepsDTO processStepsDTO = new ProcessStepsDTO();
            processStepsDTO.setFlowId(id);
            processStepsDTO.setDataId(FlowConstant.ENDFIGURE);
            List<ProcessStepsDTO> processStepsDTOList = dubboService.findStartOrEndFlowCeaselesslyList(processStepsDTO);
            List<CommonParameter> commonParameterList = dubboService.parseFlowXmlParameter(id);
            //List<CommonParameter> outputParameter = JSON.parseArray(processStepsList.get(0).getDynamicParameter(), CommonParameter.class);
            atomicAlgorithmDTO.setId(id);
            List<CommonParameter> inputParameterList = new ArrayList<CommonParameter>();
            for (CommonParameter commonParameter : commonParameterList) {
                if (!FlowConstant.OUTFILE.equals(commonParameter.getParameterType())
                        && !FlowConstant.OUTSTRING.equals(commonParameter.getParameterType())
                        && !FlowConstant.OUTFOLDER.equals(commonParameter.getParameterType())) {
                    inputParameterList.add(commonParameter);
                }
            }
            atomicAlgorithmDTO.setDynamicParameter(JSON.toJSONString(inputParameterList));
            commonParameterList.removeAll(inputParameterList);
            atomicAlgorithmDTO.setOutputParameter(JSON.toJSONString(commonParameterList));
            atomicAlgorithmDTO.setTypeId(2);
            atomicAlgorithmDTO.setProcessId(id);
        }
        return atomicAlgorithmDTO;
    }

    @RequestMapping(value = "/index3", method = RequestMethod.POST)
    //方法上使用@ApiOperation
    @ApiOperation(value = "首页", notes = "跳转到首页")
    public void findParameterById(HttpServletRequest request) {
        FlowChartDTO newflow = new FlowChartDTO();
        try {
            String requestBody = request.getParameter("requestBody");
            FlowChartDTO flowChartDTO = JSON.parseObject(requestBody, FlowChartDTO.class);
            String xml = URLDecoder.decode(flowChartDTO.getPicture(), "utf-8");
            byte[] pic = MxImageExport.getImageByte(xml, flowChartDTO.getPicWidth(), flowChartDTO.getPicHeight());
            flowChartDTO.setProcessPicture(pic);
            flowChartDTO.setProcessFigure(flowChartDTO.getFile());
            flowChartDTO.setId(flowChartDTO.getProcessId());
            newflow = dubboService.saveOrUpdateFlow(flowChartDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/processDesigner/saveLocal", method = RequestMethod.POST)
    public void saveLocal(HttpServletRequest request, HttpServletResponse response) {
        String xml = request.getParameter("json");
        String name = request.getParameter("name");
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "application/octet-stream");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((name + ".xml").getBytes(), "iso-8859-1"));
            response.getWriter().write(xml);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}