package com.htht.job.admin.controller;

/**
 * Created by zzj on 2018/1/10.
 */

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.executor.model.algorithm.TaskParametersDTO;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.paramtemplate.CimissDownParam;
import com.htht.job.executor.model.paramtemplate.DownParam;
import com.htht.job.executor.model.paramtemplate.PreDataParam;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/template")
public class ParamTemplateController {
    private static Logger logger = LoggerFactory.getLogger(ParamTemplateController.class);
    @Autowired
    private TaskParametersService taskParametersService;
    @Autowired
    private DubboService dubboService;

    @RequestMapping("/down")
    public String down(Model model, String jobId) {
        try {
            TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobId);
            DownParam downParam;
            if (taskParametersDTO == null) {
                downParam = new DownParam();
            } else {
                downParam = JSON.parseObject(taskParametersDTO.getModelParameters(), DownParam.class);

            }
            model.addAttribute("downParam", downParam);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return "/paramtemplate/downparam";
    }

    @RequestMapping("/cimissdownload")
    public String cimissparam(Model model, String jobId) {
        try {
            TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobId);
            CimissDownParam downParam;
            if (taskParametersDTO == null) {
                downParam = new CimissDownParam();
            } else {
                downParam = JSON.parseObject(taskParametersDTO.getModelParameters(), CimissDownParam.class);
            }
            model.addAttribute("cimissParam", downParam);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return "/paramtemplate/cimissparam";
    }

    @RequestMapping("/projection")
    public String projection(Model model, String jobId) {
        try {
            TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobId);
            PreDataParam projectionParam;
            if (taskParametersDTO == null) {
                projectionParam = new PreDataParam();
            } else {
                projectionParam = JSON.parseObject(taskParametersDTO.getModelParameters(), PreDataParam.class);

            }
            model.addAttribute("projectionParam", projectionParam);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return "/paramtemplate/projectionparam";
    }

    @RequestMapping("/product")
    public String product(Model model, String jobId) {
        try {
            TaskParametersDTO taskParametersDTO = taskParametersService.findJobParameterById(jobId);
            ProductParam productParam;
            if (taskParametersDTO == null) {
                productParam = new ProductParam();
            } else {
                productParam = JSON.parseObject(taskParametersDTO.getModelParameters(), ProductParam.class);
            }
            model.addAttribute("productParam", productParam);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return "/paramtemplate/productparam";
    }

    @RequestMapping("/ztree")
    public String ztree() {
        return "/paramtemplate/ztree";
    }

    //PieOthero共享目录显示（用于高光谱文件的选择）
    @RequestMapping("/ztree2")
    public String ztree2() {
        return "/paramtemplate/ztree2";
    }

    //PieOthero共享目录显示（用于文件夹的选择）
    @RequestMapping("/ztree3")
    public String ztree3() {
        return "/paramtemplate/ztree3";
    }

    //PieOthero共享目录显示（用于高光谱文件的选择）
    @RequestMapping("/ztree4")
    public String ztree4() {
        return "/paramtemplate/ztree4";
    }

    //PieOthero共享目录显示（用于高光谱文件的选择）
    @RequestMapping("/ztree5")
    public String ztree5() {
        return "/paramtemplate/ztree5";
    }

    @RequestMapping("/outwktztree")
    public String outWktZtree() {
        return "/paramtemplate/outwktztree";
    }

    @ResponseBody
    @RequestMapping("/findChildren")
    public List<Map> findChildren(String dictName){
        List<DictCodeDTO> list = dubboService.findChildrenDictCode(dictName);
        List<Map> mapList = new ArrayList<>();
        list.forEach(dictCodeDTO -> {
            Map map = new HashMap();
            map.put("id", dictCodeDTO.getId());
            map.put("text", dictCodeDTO.getDictCode()+"("+dictCodeDTO.getDictName()+")");
            mapList.add(map);
        });
        return mapList;
    }
    @ResponseBody
    @RequestMapping("/findByParentId")
    public List<Map> findByParentId(String parentId){
        List<DictCodeDTO> list = dubboService.findByParentId(parentId);
        List<Map> mapList = new ArrayList<>();
        list.forEach(dictCodeDTO -> {
            Map map = new HashMap();
            map.put("id", dictCodeDTO.getId());
            map.put("text", dictCodeDTO.getDictCode()+"("+dictCodeDTO.getDictName()+")");
            mapList.add(map);
        });
        return mapList;
    }
}
