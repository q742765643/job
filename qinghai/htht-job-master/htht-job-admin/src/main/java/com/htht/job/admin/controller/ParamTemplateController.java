package com.htht.job.admin.controller;

/**
 * Created by zzj on 2018/1/10.
 */

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.htht.job.core.api.algorithm.TaskParametersService;
import com.htht.job.executor.model.algorithm.TaskParameters;
import com.htht.job.executor.model.paramtemplate.CimissDownParam;
import com.htht.job.executor.model.paramtemplate.DownParam;
import com.htht.job.executor.model.paramtemplate.PreDataParam;
import com.htht.job.executor.model.paramtemplate.ProductParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/template")
public class ParamTemplateController {
    @Autowired
    private TaskParametersService taskParametersService;

    @RequestMapping("/down")
    public String down(Model model, String jobId) {
        try {
            TaskParameters taskParameters = taskParametersService.findJobParameterById(jobId);
            DownParam downParam;
            if (taskParameters == null) {
                downParam = new DownParam();
            } else {
                downParam = JSON.parse(taskParameters.getModelParameters(), DownParam.class);

            }
            model.addAttribute("downParam", downParam);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "/paramtemplate/downparam";
    }

    @RequestMapping("/cimissdownload")
    public String cimissparam(Model model, String jobId) {
        try {
            TaskParameters taskParameters = taskParametersService.findJobParameterById(jobId);
            CimissDownParam downParam;
            if (taskParameters == null) {
                downParam = new CimissDownParam();
            } else {
                downParam = JSON.parse(taskParameters.getModelParameters(), CimissDownParam.class);
            }
            model.addAttribute("cimissParam", downParam);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "/paramtemplate/cimissparam";
    }

    @RequestMapping("/projection")
    public String projection(Model model,String jobId) {
        try {
            TaskParameters taskParameters = taskParametersService.findJobParameterById(jobId);
            PreDataParam projectionParam;
            if(taskParameters ==null){
                 projectionParam=new PreDataParam();
            }else{
                 projectionParam= JSON.parse(taskParameters.getModelParameters(),PreDataParam.class);

            }
            model.addAttribute("projectionParam", projectionParam);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "/paramtemplate/projectionparam";
    }
    
    @RequestMapping("/product")
    public String product(Model model,String jobId) {
        try {
            TaskParameters taskParameters = taskParametersService.findJobParameterById(jobId);
            ProductParam productParam;
            if(taskParameters ==null){
            	productParam=new ProductParam();
            }else{
            	productParam= JSON.parse(taskParameters.getModelParameters(),ProductParam.class);
            }
            model.addAttribute("productParam", productParam);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "/paramtemplate/productparam";
    }
    
    @RequestMapping("/product2")
    public String product2(Model model,String jobId) {
        try {
            TaskParameters taskParameters = taskParametersService.findJobParameterById(jobId);
            ProductParam productParam;
            if(taskParameters ==null){
            	productParam=new ProductParam();
            }else{
            	productParam= JSON.parse(taskParameters.getModelParameters(),ProductParam.class);
            }
            model.addAttribute("productParam", productParam);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "/paramtemplate/productparam2";
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
}
