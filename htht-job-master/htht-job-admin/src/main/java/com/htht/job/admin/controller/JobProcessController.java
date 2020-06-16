package com.htht.job.admin.controller;

import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.datacategory.DataCategoryService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/jobprocess")
public class JobProcessController {
    @Autowired
    private DataCategoryService dataCategoryService;
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

    @RequestMapping
    public String index(Model model) {
        return "/jobprocess/jobprocess.index";
    }


    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(AtomicAlgorithmDTO atomicAlgorithmDTO) {
        AtomicAlgorithmDTO p = atomicAlgorithmService.saveParameter(atomicAlgorithmDTO);
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length, AtomicAlgorithmDTO atomicAlgorithmDTO) {

        if (start != 0) {
            start = start / length;
        }
        return atomicAlgorithmService.pageList(start, length, atomicAlgorithmDTO);
    }

    @RequestMapping("/deleteParameter")
    @ResponseBody
    public ReturnT<String> deleteParameter(String id) {
        return atomicAlgorithmService.deleteParameter(id);
    }

}
