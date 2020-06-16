package com.htht.job.executor.service.productfileinfo;

import com.htht.job.executor.dao.productfileinfo.ProductFileInfoDao;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.service.fileinfo.FileInfoService;
import com.htht.job.executor.util.specification.SimpleSpecificationBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jeesys.common.jpa.dao.string.BaseDao;
import org.jeesys.common.jpa.service.string.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/2.
 */
@Transactional
@Service
public class ProductFileInfoServiceImpl extends BaseService<ProductFileInfoDTO> implements ProductFileInfoService {

    @Autowired
    private ProductFileInfoDao productFileInfoDao;
    @Autowired
    private FileInfoService fileInfoService;


    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> pageList(int start, int length,
                                        ProductFileInfoDTO productFileInfoDTO) {
        Page<ProductFileInfoDTO> page = this.findPageProduct(start, length,
                productFileInfoDTO);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }

    public Map<String, Object> pageLists(int start, int length,
                                         String id) {
        Pageable pageable = new PageRequest(start, length, new Sort(Sort.Direction.DESC, "createTime"));
        Page<ProductFileInfoDTO> page = productFileInfoDao.findByProductInfoId(id, pageable);
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", page.getTotalElements()); // 总记录数
        maps.put("recordsFiltered", page.getTotalElements()); // 过滤后的总记录数
        maps.put("data", page.getContent()); // 分页列表
        return maps;
    }

    public Page<ProductFileInfoDTO> findPageProduct(int start, int length, ProductFileInfoDTO productFileInfoDTO) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest d = new PageRequest(start, length, sort);
        Page<ProductFileInfoDTO> page = this.getPage(
                getWhereClause(productFileInfoDTO), d);
        return page;
    }

    private Specification<ProductFileInfoDTO> getWhereClause(
            ProductFileInfoDTO productFileInfoDTO) {
        SimpleSpecificationBuilder<ProductFileInfoDTO> specification = new SimpleSpecificationBuilder();

        specification.add("zt", "eq", "0");
//        if(!StringUtils.isEmpty(productFileInfo.getMenuId())){
//            specification.add("menuId","likeAll",productFileInfo.getMenuId());
//        }
        if (!StringUtils.isEmpty(productFileInfoDTO.getProductType())) {
            specification.add("productType", "likeAll", productFileInfoDTO.getProductType());
        }

        return specification.generateSpecification();

    }

    public List<ProductFileInfoDTO> findByWhere(String prdocutId, String issue) {
        SimpleSpecificationBuilder<ProductFileInfoDTO> specification = new SimpleSpecificationBuilder();
        specification.add("zt", "eq", "0");
        specification.add("productId", "eq", prdocutId);
        specification.add("issue", "eq", issue);
        List<ProductFileInfoDTO> list = this.getAll(specification.generateSpecification());
        return list;

    }

    public ProductFileInfoDTO save(ProductFileInfoDTO productFileInfoDTO) {

        ProductFileInfoDTO newProductFileInfoDTO = new ProductFileInfoDTO();
        if (!StringUtils.isEmpty(productFileInfoDTO.getId())) {
            ProductFileInfoDTO yProductFileInfoDTO = this.getById(productFileInfoDTO.getId());
            yProductFileInfoDTO.setUpdateTime(new Date());
            yProductFileInfoDTO.setFilePath(productFileInfoDTO.getFilePath());
            yProductFileInfoDTO.setFileType(productFileInfoDTO.getFileType());
            yProductFileInfoDTO.setProductType(productFileInfoDTO.getProductType());

            newProductFileInfoDTO = productFileInfoDao.save(yProductFileInfoDTO);
        } else {
            productFileInfoDTO.setCreateTime(new Date());
            newProductFileInfoDTO = productFileInfoDao.save(productFileInfoDTO);

        }
        return newProductFileInfoDTO;
    }

    public ProductFileInfoDTO deleteById(String id) {
        ProductFileInfoDTO productFileInfoDTO = this.getById(id);
        productFileInfoDao.delete(productFileInfoDTO);
        return productFileInfoDTO;
    }

    public void batchSaveProductFileInfo(List<ProductFileInfoDTO> list) {
        for (int i = 0; i < list.size(); i++) {
            entityManager.merge(list.get(i));
            if (i % 100 == 0) {
                entityManager.flush();
                entityManager.clear();
            }

        }

    }

    public List<ProductFileInfoDTO> findByWhereAndRegion(String prdocuttype, String issue, String[] regionandfiletype) {
        SimpleSpecificationBuilder<ProductFileInfoDTO> specification = new SimpleSpecificationBuilder();
        specification.add("zt", "eq", "0");
        specification.add("productType", "eq", prdocuttype);
        specification.add("issue", "eq", issue);
        specification.add("region", "eq", regionandfiletype[0]);
        specification.add("fileType", "eq", regionandfiletype[1]);
        List<ProductFileInfoDTO> list = this.getAll(specification.generateSpecification());
        return list;

    }

    @Override
    public List<ProductFileInfoDTO> findByproductInfoId(String id) {
        return productFileInfoDao.findByproductInfoId(id);
    }

    @Override
    public void deleteByproductInfoId(String id) {
        productFileInfoDao.deleteByproductInfoId(id);
    }

    @Override
    public BaseDao<ProductFileInfoDTO> getBaseDao() {
        return productFileInfoDao;
    }
}
