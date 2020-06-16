package com.htht.job.admin.controller.dictionary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.datacategory.ZtreeView;


/**
 * @date:2018年6月27日下午1:54:07
 * @author:yss
 */
@Controller
@RequestMapping("/dicCode")
public class DicCodeController {
	
	@Autowired
    private DubboService dubboService;
	
	 	@RequestMapping
	    public String index(Model model) {

	        return "/dicCode/dictonary.index";
	    }


	    @RequestMapping("/allTree")
	    @ResponseBody
	    public List<ZtreeView> allTree() {
	        List<ZtreeView> list = dubboService.allTree();
	        return list;
	    }

	    @RequestMapping("/pageList")
	    @ResponseBody
	    public String pageList(@RequestParam(required = false, defaultValue = "0") int start,
	                           @RequestParam(required = false, defaultValue = "10") int length,
	                           String searchText,@RequestParam(required = false,defaultValue="")String id) {
	        if (start != 0) {
	            start = start / length;
	        }
	        return dubboService.dicCodeList(start, length, searchText,id);
	    }

	    @RequestMapping("/saveDicCode")
	    @ResponseBody
	    public ReturnT<String> saveDicCode(DictCode dictCode) {
	        try {
	            dubboService.saveOrUpdateDicCode(dictCode);
	            return ReturnT.SUCCESS;
	        } catch (Exception e) {
	            return ReturnT.FAIL;

	        }
	    }

	    @RequestMapping("/delDicCode/{id}")
	    @ResponseBody
	    public ReturnT<String> delResource(@PathVariable String id) {
	        try {
	            dubboService.deleteDicCode(id);
	            return ReturnT.SUCCESS;
	        } catch (Exception e) {
	            return ReturnT.FAIL;
	        }
	    }
	
	
	
}
