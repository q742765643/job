package com.htht.job.admin.controller.fieldManger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.service.XxlJobService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.dms.module.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/archiveCatalog")
public class ArchiveCatalogController {

    @Autowired
    private DubboService dubboService;
    @Resource
    private XxlJobService xxlJobService;

    @RequestMapping
    public String index(Model model) {
        //查找与数管相关流程
        List<XxlJobInfo> dataFlowList = xxlJobService.findDataFlow();
        model.addAttribute("dataFlowList", dataFlowList);
        List<ArchiveFiledManage> filedManages = dubboService.findAllArchiveFiledManages();
        model.addAttribute("filedManages", filedManages);
        List<Disk> fileDiskList = dubboService.findfileDisks();
        model.addAttribute("fileDiskList", fileDiskList);
        return "/fieldmanger/archiveCatalog.index";
    }

    //获取数据目录树
    @RequestMapping("/getArchiveCatalog")
    @ResponseBody
    public String ArchiveCatalog() {
        List<ArchiveCatalog> archiveCatalogs;
        archiveCatalogs = dubboService.findArchiveCatalogs();
        return JSON.toJSONString(archiveCatalogs);
    }

    //添加数据目录树
    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(ArchiveCatalog archiveCatalog) {
        int i = dubboService.saveArchiveCatalog(archiveCatalog);
        if (i > 0) {
            return ReturnT.SUCCESS;
        }
        return ReturnT.FAIL;
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length, ArchiveRules archiveRules) {
        //parameterModel.setModelName("1");
        if (start != 0) {
            start = start / length;
        }
        Map<String, Object> map = dubboService.pageListArchiveRules(start, length, archiveRules);
        List<ArchiveRules> data = (List<ArchiveRules>) map.get("data");
        List<XxlJobInfo> findDataFlow = xxlJobService.findDataFlow();
        for (ArchiveRules archiveRules2 : data) {
            for (XxlJobInfo xxlJobInfo : findDataFlow) {
                if (archiveRules2.getFlowid() == xxlJobInfo.getId()) {
                    archiveRules2.setFlowName(xxlJobInfo.getJobDesc());
                }
            }
        }
        map.put("data", data);

        return map;
    }

    @RequestMapping("/saveArchiveRules")
    @ResponseBody
    public ReturnT<String> saveArchiveRules(@RequestParam("archiveRules") String archiveRules, @RequestParam("fileMapList") String fileMapListStr) {
        List<ArchiveFiledMap> fileMapList = JSON.parseArray(fileMapListStr, ArchiveFiledMap.class);
        ArchiveRules archiveRules1 = JSON.parseObject(archiveRules, ArchiveRules.class);

        int a = dubboService.saveArchiveRules(archiveRules1, fileMapList);
        if (a > 0) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    //获取所有字段
    @RequestMapping("/findAllArchiveFiledManage")
    @ResponseBody
    public String findAllArchiveFiledManage() {
        List<ArchiveFiledManage> filedManages;
        filedManages = dubboService.findAllArchiveFiledManages();
        return JSON.toJSONString(filedManages);
    }

    //获取 FiledMap
    @RequestMapping("/getArchiveFiledMap")
    @ResponseBody
    public String getArchiveFiledMap(@RequestParam("archiveRuleId") String archiveRuleId) {
        List<ArchiveFiledMap> fileMapList = dubboService.findArchiveFiledMap(archiveRuleId);
        //System.out.println(JSON.toJSON(fileMapList).toString());
        return JSON.toJSON(fileMapList).toString();
    }

    //通过目录删除
    @RequestMapping("/deleteTreeNode")
    @ResponseBody
    public ReturnT<String> deleteTreeNode(String id) {
        List<ArchiveFiledMap> fileMapList = dubboService.findArchiveFiledMap(id);
        //System.out.println(JSON.toJSON(fileMapList).toString());
        return dubboService.deleteTreeNodeArchiveCatalog(id);
    }

    //删除
    @RequestMapping("/deleteArchiveRulesAndFiledMap")
    @ResponseBody
    public ReturnT<String> deleteArchiveRulesAndFiledMap(String id) {
        return dubboService.deleteArchiveRulesAndFiledMap(id);
    }

    //获取Archiverules
    @RequestMapping("/findArchiverules")
    @ResponseBody
    public ReturnT<String> findArchiverules(String catalogCode) {
        return dubboService.findArchiverules(catalogCode);
    }

    //启用或者禁用
    @RequestMapping("/enableOrdisable")
    @ResponseBody
    public ReturnT<String> enableOrdisable(String data, String mark) {
        JSONArray json = JSONArray.parseArray(data);

        int res = 0;
        for (Object aJson : json) {
            res += dubboService.enableOrdisable(String.valueOf(aJson), mark);
        }

        if (json.size() == res) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    //批量修改归档磁盘
    @RequestMapping("/updateDiskBatch")
    @ResponseBody
    public ReturnT<String> updateDiskBatch(String ids, String archivdisk) {
        JSONArray json = JSONArray.parseArray(ids);
        int res = 0;
        for (Object aJson : json) {
            res += dubboService.updateDisk(String.valueOf(aJson), archivdisk);
        }

        if (json.size() == res) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

}
