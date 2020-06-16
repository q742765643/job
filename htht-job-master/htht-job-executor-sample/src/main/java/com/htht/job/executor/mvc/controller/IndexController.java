package com.htht.job.executor.mvc.controller;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
//@EnableAutoConfiguration
public class IndexController {
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

    @RequestMapping("/")
    @ResponseBody
    String index() {
        List<Map> list = atomicAlgorithmService.findTreeListBySql();
        System.out.println(list);

        return "xxl job executor running.";
    }

}