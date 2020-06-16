package com.htht.job.executor.service.product;

import com.htht.job.core.util.WriteToHtml;
import com.htht.job.executor.model.product.ProductDTO;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.model.productinfo.ProductAnalysisTableInfo;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Transactional
@Service("ProductUtil")
public class ProductUtil {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private ProductFileInfoService productFileInfoService;
    @Autowired
    private EntityManagerFactory emf;

    /**
     * 保存values数据
     *
     * @param values
     * @return
     */
    public int saveProductDetail(ProductAnalysisTableInfo pATInfo) {
        int count = 0;
        if (pATInfo == null) {
            return count;
        }
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createNativeQuery(pATInfo.generateCreateTableSql());
        query.executeUpdate();
        query = em.createNativeQuery(pATInfo.generateReplaceDataSql());
        count += query.executeUpdate();
        em.getTransaction().commit();
        em.close();
        return count;
    }


    /**
     * 保存saveProductInfo
     *
     * @param productId
     * @param regionId
     * @param issue
     * @param cycle
     * @return
     */
    public ProductInfoDTO saveProductInfo(String productId, String regionId,
                                          String issue, String cycle, String mosaicFile,
                                          String modelIdentify, String inputFileName) {
        List<String> list = Arrays.asList(mosaicFile.split("\\."));
        String mosaicFiles = mosaicFile.replace("." + list.get(list.size() - 1), "");

        ProductDTO p = productService.findById(productId);
        ProductInfoDTO pi = new ProductInfoDTO();
        pi.setProductId(productId);
        pi.setName(p.getName());
        pi.setMark(p.getMark());
        pi.setCycle(cycle);
        pi.setMapUrl(p.getMapUrl());
        pi.setFeatureName(p.getFeatureName());
        pi.setProductPath(p.getProductPath());
        pi.setGdbPath(p.getGdbPath());
        pi.setIssue(issue);
        pi.setRegionId(regionId);
        pi.setIsRelease(p.getIsRelease());
        pi.setBz(p.getBz());
        pi.setMosaicFile(mosaicFiles);
        pi.setCreateTime(new Date());
        pi.setModelIdentify(modelIdentify);
        pi.setInputFileName(inputFileName);
        ProductInfoDTO productInfoDTO = productInfoService.save(pi);
        return productInfoDTO;
    }

    /**
     * 保存saveProductInfo
     *
     * @param productId
     * @param regionId
     * @param issue
     * @param cycle
     * @return
     */
    public ProductInfoDTO saveProductInfoNdvi(String productId, String regionId,
                                              String issue, String cycle, String mosaicFile,
                                              String modelIdentify, String inputFileName) {
        ProductDTO p = productService.findById(productId);
        ProductInfoDTO pi = new ProductInfoDTO();
        pi.setProductId(productId);
        pi.setName(p.getName());
        pi.setMark(p.getMark());
        pi.setCycle(cycle);
        pi.setMapUrl(p.getMapUrl());
        pi.setFeatureName(p.getFeatureName());
        pi.setProductPath(p.getProductPath());
        pi.setGdbPath(p.getGdbPath());
        pi.setIssue(issue);
        pi.setRegionId(regionId);
        pi.setIsRelease(p.getIsRelease());
        pi.setBz(p.getBz());
        pi.setMosaicFile(mosaicFile);
        pi.setCreateTime(new Date());
        pi.setModelIdentify(modelIdentify);
        pi.setInputFileName(inputFileName);
        ProductInfoDTO productInfoDTO = productInfoService.save(pi);
        return productInfoDTO;
    }

    /**
     * 保存ProductInfoFile
     *
     * @param productInfoId
     * @param filePath
     * @param outputPath
     */
    public void saveProductInfoFile(String productInfoId, String filePath,
                                    String outputPath, String regionid, String issue, String cycle) {
        String fileName = new File(filePath).getName();
        String productType = "file";
        if (fileName.indexOf("png") > -1 || fileName.indexOf("jpg") > -1) {
            productType = "pic";
        } else if (fileName.indexOf("doc") > -1
                || fileName.indexOf("docx") > -1) {
            productType = "doc";
        }else if(fileName.toLowerCase().endsWith(".html")){
        	 productType = "doc";
        	 //doc->html
        	 String htmlName = new File(filePath).getName();
        	 WriteToHtml.saveHtmlToWord(filePath,htmlName, htmlName.replace(".html",".doc"));
        	 
        }
        String relativePath = filePath.replace(outputPath, "");
        ProductFileInfoDTO pfi = new ProductFileInfoDTO();
        pfi.setProductInfoId(productInfoId);
        pfi.setFilePath(filePath);
        pfi.setFileName(fileName);
        pfi.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
        pfi.setProductType(productType);
        pfi.setFilePath(filePath);
        pfi.setRelativePath(relativePath);
        pfi.setIsDel("0");
        pfi.setCreateTime(new Date());
        pfi.setRegion(regionid);
        pfi.setIssue(issue);
        pfi.setCycle(cycle);
        productFileInfoService.save(pfi);
    }
}
