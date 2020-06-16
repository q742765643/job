package com.htht.job.executor.service.productinfo;

import com.htht.job.executor.dao.productinfo.ProductInfoDao;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: htht-job-api
 * @description:
 * @author: dingjiancheng
 * @create: 2018-09-20 16:10
 */
@Transactional
@Service("ProductInfoService")
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    ProductInfoDao productInfoDao;

    @Override
    public Map<String, Object> pageList(int start, int length, String productId) {
        Pageable pageable = new PageRequest(start, length, new Sort(Sort.Direction.DESC, "createTime"));
        Page<ProductInfoDTO> page = null;
        if (StringUtils.isEmpty(productId)) {
            page = productInfoDao.findAll(pageable);
        } else {
            page = productInfoDao.findByProductId(productId, pageable);
        }
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }

    @Override
    public void deleteProductInfo(String id) {
        //删除对应的文件
        productInfoDao.delete(id);
//        FileUtil.Deldir(productInfoDao.findOne(id).getProductPath());
    }

    @Override
    public ProductInfoDTO save(ProductInfoDTO productInfoDTO) {
        return productInfoDao.save(productInfoDTO);
    }

    @SuppressWarnings("unused")
    @Override
    public List<ProductInfoDTO> findProductExits(String productId, String issue, String cycle, String modelIdentify, String inputFileName, String regionId) {
        if (!StringUtils.isEmpty(inputFileName)) {
            return productInfoDao.findProductExits(productId, issue, cycle, modelIdentify, inputFileName, regionId);
        }
        return productInfoDao.findProductExits(productId, issue, cycle, modelIdentify, regionId);
    }

    @Override
    public List<ProductInfoDTO> findProductInfoListByIssueRange(String id, String startIssue, String endIssue) {
        return productInfoDao.findProductInfoListByIssueRange(id, startIssue, endIssue);
    }

    @Override
    public List<ProductInfoDTO> findProductExits(String issue, String cycle, String modelIdentify) {
    	return productInfoDao.findProductExits(issue, cycle, modelIdentify);
    }

}
