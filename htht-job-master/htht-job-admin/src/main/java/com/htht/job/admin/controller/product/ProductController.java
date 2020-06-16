package com.htht.job.admin.controller.product;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.htht.job.admin.controllerLog.SystemControllerLog;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.api.datacategory.DataCategoryService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.datacategory.DataCategoryDTO;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfoDTO;
import com.htht.job.executor.model.downupload.AlgorithmRelationParam;
import com.htht.job.executor.model.product.ProductDTO;
import com.htht.job.executor.model.systemlog.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class ProductController {
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

    @RequestMapping("/getTreeNode")
    @ResponseBody
    public String getTreeNode(String treeKey) {
        List<DataCategoryDTO> dataCategories;
        if (StringUtils.isNotEmpty(treeKey)) {
            dataCategories = dataCategoryService.getTreeNodeById(treeKey);
        } else {
            dataCategories = dataCategoryService.getTreeNodeById();
        }
        return JSON.toJSONString(dataCategories);
    }

    @RequestMapping("/getProductIcon")
    @ResponseBody
    public List<DictCodeDTO> getProductIcon() {
        return dubboService.findChildrenDictCode("图标管理");
    }

    @RequestMapping("/getProductIconName")
    @ResponseBody
    public List<DictCodeDTO> getProductIconName() {
        return dubboService.findChildrenDictCode("产品图标");
    }

    @SystemControllerLog(description = "更新了一个产品节点", type = SystemLog.OPERATELOG)
    @RequestMapping("/saveNewProduct")
    @ResponseBody
    public ReturnT<String> saveNewProduct(ProductDTO productDTO, DataCategoryDTO dataCategoryDTO) {
        DataCategoryDTO t = dataCategoryService.saveTreeNode(dataCategoryDTO);
        if (null == t.getId()) {
            return ReturnT.FAIL;
        }
        productDTO.setTreeId(t.getId());
        ProductDTO p = dubboService.saveProduct(productDTO);
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(DataCategoryDTO dataCategoryDTO) {
        DataCategoryDTO t = dataCategoryService.saveTreeNode(dataCategoryDTO);
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
        return dataCategoryService.deleteTreeNode(id);
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length, ProductDTO productDTO) {
        if (start != 0) {
            start = start / length;
        }
        return dubboService.pageListProduct(start, length, productDTO);
    }

    @SystemControllerLog(description = "新增了一个产品", type = SystemLog.OPERATELOG)
    @RequestMapping("/saveProduct")
    @ResponseBody
    public ReturnT<String> saveProduct(ProductDTO productDTO) {
        //更新ico路径，因为发布平台用这个字段
        DataCategoryDTO dc = dataCategoryService.getById(productDTO.getTreeId());
        dc.setIconPath(productDTO.getIconPath());
        dataCategoryService.saveTreeNode(dc);

        ProductDTO p = dubboService.saveProduct(productDTO);
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
            Map<String, Integer> map = new HashMap<>();
            map.put("id", i);
            map.put("text", i);

            list.add(map);
        }
        return JSON.toJSONString(list);
    }


    @RequestMapping("/queryAlgoByTreeId")
    @ResponseBody
    public Map<String, Object> connProduct(String treeId, int start, int length) {
        List<AlgorithmRelationInfoDTO> algoId = dubboService.queryAlgo(treeId);

        List<AtomicAlgorithmDTO> list = new ArrayList<>();
        for (AlgorithmRelationInfoDTO algo : algoId) {
            list.add(atomicAlgorithmService.queryAogoInfo(algo.getAlgoId()));
        }

        Pageable pageable = new PageRequest(start, length, new Sort(Sort.Direction.DESC, "createTime"));

        Page<AtomicAlgorithmDTO> pg = new PageImpl<>(list, pageable, list.size());

        Map<String, Object> maps = new HashMap<>();
        maps.put("recordsTotal", pg.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", pg.getTotalElements()); // 过滤后的总记录数
        maps.put("data", pg.getContent()); // 分页列表
        return maps;
    }

    @RequestMapping("/buildAlgoRelation")
    @ResponseBody
    public ReturnT<String> conProduct(AlgorithmRelationParam algori) {
        AlgorithmRelationInfoDTO algorithmRelationInfoDTO = new AlgorithmRelationInfoDTO();
        algorithmRelationInfoDTO.setTreeId(algori.getTreeId());
        for (String algo : algori.getAlgoId()) {
            algorithmRelationInfoDTO.setAlgoId(algo);
            AlgorithmRelationInfoDTO algoo = dubboService.saveRelation(algorithmRelationInfoDTO);
            if (null == algoo) {
                return ReturnT.FAIL;
            }
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/deleteRelation")
    @ResponseBody
    public ReturnT<String> coProduct(AlgorithmRelationInfoDTO algor) {
        return dubboService.deleteRelation(algor.getTreeId(), algor.getAlgoId());
    }
}
