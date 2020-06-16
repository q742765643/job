package com.htht.job.admin.controller.product;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.admin.core.enums.ExecutorFailStrategyEnum;
import com.htht.job.admin.core.model.XxlJobGroup;
import com.htht.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.htht.job.admin.dao.XxlJobGroupDao;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.datacategory.DataCategoryService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.glue.GlueTypeEnum;
import com.htht.job.executor.model.algorithm.AtomicAlgorithm;
import com.htht.job.executor.model.datacategory.DataCategory;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfo;
import com.htht.job.executor.model.downupload.AlgorithmRelationParam;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Controller
@RequestMapping("/product")
public class ProductController {
	
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    
    @Autowired
    private DataCategoryService dataCategoryService;

    @Autowired
    private DubboService dubboService;

    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

    @RequestMapping
    public String index() {
        return "/product/product.index";
    }

    @RequestMapping("/RSproduct")
    public String RSproduct(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());    // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());    // 阻塞处理策略-字典
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());        // 失败处理策略-字典

        /**============获取模型下拉框============**/
        List<AtomicAlgorithm> executorHandlerlist = atomicAlgorithmService.findListParameter();
        List<Product> productList = dubboService.findALlProduct();
        model.addAttribute("executorHandlerlist", executorHandlerlist);
        model.addAttribute("productList", productList);

        //模态下拉框执行策略
        List<DictCode> executionStrategyList= dubboService.findChildrenDictCode("执行策略");
        //  List<DictCode> executionStrategyList= dubboService.findALlExecutionStrategy();
        model.addAttribute("executionStrategyList", executionStrategyList);
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);
        model.addAttribute("tasktype", 11);
        return "product/weatherPreData.index";
    }
    
    
    
    @RequestMapping("/AMPproduct")
    public String AMPproduct(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());    // 路由策略-列表
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());                                // Glue类型-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());    // 阻塞处理策略-字典
        model.addAttribute("ExecutorFailStrategyEnum", ExecutorFailStrategyEnum.values());        // 失败处理策略-字典

        /**============获取模型下拉框============**/
        List<AtomicAlgorithm> executorHandlerlist = atomicAlgorithmService.findListParameter();
        List<Product> productList = dubboService.findALlProduct();
        model.addAttribute("executorHandlerlist", executorHandlerlist);
        model.addAttribute("productList", productList);

        //模态下拉框执行策略
        List<DictCode> executionStrategyList= dubboService.findChildrenDictCode("执行策略");
        //  List<DictCode> executionStrategyList= dubboService.findALlExecutionStrategy();
        model.addAttribute("executionStrategyList", executionStrategyList);
        // 任务组
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);
        model.addAttribute("tasktype", 10);
        return "product/weatherPreData.index";
    }
    
    

    @RequestMapping("/getTreeNode")
    @ResponseBody
    public String getTreeNode(String treeKey) {
    	List<DataCategory> dataCategories;
    	if(StringUtils.isNotEmpty(treeKey)){
    		dataCategories = dataCategoryService.getTreeNodeById(treeKey);
    	}else{
    		dataCategories = dataCategoryService.getTreeNodeById();
    	}
    	return JSON.toJSONString(dataCategories);
    }

    @RequestMapping("/getProductIcon")
    @ResponseBody
    public List<DictCode> getProductIcon() {
        return dubboService.findChildrenDictCode("图标管理");
    }
    
    @RequestMapping("/getProductIconName")
    @ResponseBody
    public List<DictCode> getProductIconName() {
        return dubboService.findChildrenDictCode("产品图标");
    }

	@SystemControllerLog(description = "更新了一个产品节点", type = SystemLog.OPERATELOG)
    @RequestMapping("/saveNewProduct")
    @ResponseBody
    public ReturnT<String> saveNewProduct(Product product,DataCategory dataCategory) {
        DataCategory t = dataCategoryService.saveTreeNode(dataCategory);
        if (null == t.getId()) {
            return ReturnT.FAIL;
        }
        product.setTreeId(t.getId());
        Product p = dubboService.saveProduct(product);
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(DataCategory dataCategory) {
        DataCategory t = dataCategoryService.saveTreeNode(dataCategory);
        if (null != t.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @SystemControllerLog(description = "删除了一个产品节点", type = SystemLog.OPERATELOG)
    @RequestMapping("/deleteTreeNode")
    @ResponseBody
    public ReturnT<String> deleteTreeNode(String id) {
        dubboService.deleteProducByTree(id);
//          dataCategoryService.deleteTreeNode(id);
        return dataCategoryService.deleteTreeNode(id);
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length, Product product) {
        //parameterModel.setModelName("1");
        if (start != 0) {
            start = start / length;
        }
        return dubboService.pageListProduct(start, length, product);
    }

    @SystemControllerLog(description = "新增了一个产品", type = SystemLog.OPERATELOG)
    @RequestMapping("/saveProduct")
    @ResponseBody
    public ReturnT<String> saveProduct(Product product) {
    	//更新ico路径，因为发布平台用这个字段
    	DataCategory dc = dataCategoryService.getById(product.getTreeId());
    	dc.setIconPath(product.getIconPath());
    	dataCategoryService.saveTreeNode(dc);
    	
        Product p = dubboService.saveProduct(product);
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @SystemControllerLog(description = "删除了一个产品", type = SystemLog.OPERATELOG)
    @RequestMapping("/deleteProduct")
    @ResponseBody
    public ReturnT<String> deleteProduct(String id) {
        return dubboService.deleteProduct(id);
    }

    @RequestMapping("/select")
    @ResponseBody
    public String select() {
        List<Map<String, Integer>> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("id", i);
            map.put("text", i);

            list.add(map);
        }
        return JSON.toJSONString(list);
    }


    @RequestMapping("/queryAlgoByTreeId")
    @ResponseBody
    public Map<String, Object> connProduct(String treeId,int start,int length) {
        List<AlgorithmRelationInfo> algoId = dubboService.queryAlgo(treeId);

        List<AtomicAlgorithm> list = new ArrayList<>();
        for(AlgorithmRelationInfo algo : algoId){
            list.add(atomicAlgorithmService.queryAogoInfo(algo.getAlgoId()));
        }

        Pageable pageable = new PageRequest(start,length,new Sort(Sort.Direction.DESC, "createTime"));

        Page<AtomicAlgorithm> pg = new PageImpl<AtomicAlgorithm>(list, pageable, list.size());

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", pg.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", pg.getTotalElements()); // 过滤后的总记录数
        maps.put("data", pg.getContent()); // 分页列表
        return maps;
    }

    @RequestMapping("/buildAlgoRelation")
    @ResponseBody
    public ReturnT<String> conProduct(AlgorithmRelationParam algori) {
        AlgorithmRelationInfo algorithmRelationInfo = new AlgorithmRelationInfo();
        algorithmRelationInfo.setTreeId(algori.getTreeId());
        for(String algo : algori.getAlgoId()){
            algorithmRelationInfo.setAlgoId(algo);
            AlgorithmRelationInfo algoo = dubboService.saveRelation(algorithmRelationInfo);
            if(null == algoo){
                return ReturnT.FAIL;
            }
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/deleteRelation")
    @ResponseBody
    public ReturnT<String> coProduct(AlgorithmRelationInfo algor) {
        return dubboService.deleteRelation(algor.getTreeId(),algor.getAlgoId());
    }
}
