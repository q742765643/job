package com.htht.job.core.api;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.dms.module.*;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfoDTO;
import com.htht.job.executor.model.downupload.CimissDataInfoDTO;
import com.htht.job.executor.model.fileinfo.FileInfoDTO;
import com.htht.job.executor.model.flowchart.FlowChartDTO;
import com.htht.job.executor.model.flowlog.FlowLogDTO;
import com.htht.job.executor.model.ftp.FtpDTO;
import com.htht.job.executor.model.mapping.Mapping;
import com.htht.job.executor.model.mapping.MatchRelation;
import com.htht.job.executor.model.parallellog.ParallelLogDTO;
import com.htht.job.executor.model.processsteps.ProcessStepsDTO;
import com.htht.job.executor.model.product.ProductDTO;
import com.htht.job.executor.model.productfileinfo.ProductFileInfoDTO;
import com.htht.job.executor.model.productinfo.ProductInfoDTO;
import com.htht.job.executor.model.registry.RegistryDTO;
import com.htht.job.executor.model.registryalgo.RegistryAlgoDTO;
import com.htht.job.executor.model.systemlog.SystemLog;
import com.htht.job.vo.NodeMonitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zzj on 2018/1/2.
 */
public interface DubboService {
    void execute(String params);

    Map<String, Object> pageListProduct(int start, int length, ProductDTO productDTO);

    ProductDTO saveProduct(ProductDTO productDTO);

    ReturnT<String> deleteProduct(java.lang.String id);

    ReturnT<String> deleteProducByTree(java.lang.String id);

    List<ProductDTO> findALlProduct();

    Map<String, Object> pageListProductFileInfo(int start, int length,
                                                ProductFileInfoDTO productFileInfoDTO);

    Map<String, Object> pageListProductFileInfos(int start, int length, String id);

    Map<String, Object> pageListProductInfo(int start, int length, String productId);

    ProductFileInfoDTO saveProductFileInfo(ProductFileInfoDTO productFileInfoDTO);

    void deleteProductFileInfo(String id);

    void deleteProductInfo(String id);

    String findSelectFtp();

    Page<FtpDTO> getFtpsByPage(Pageable pageable);

    boolean testConnectFtp(String ip, int port, String userName, String pwd);

    FtpDTO saveFtp(FtpDTO ftpDTO);

    FtpDTO getById(String id);

    int updeat(FtpDTO ftpDTO);

    int del(String id);

    List<NodeMonitor> findAllMonitor();

    List<FileInfoDTO> findFileInfoByWhere(String id);

    FlowChartDTO saveOrUpdateFlow(FlowChartDTO flowChartDTO);

    List<RegistryDTO> findAllRegistry();

    RegistryAlgoDTO saveRegistryAlgo(RegistryAlgoDTO registryAlgoDTO);

    List<RegistryAlgoDTO> getRegistListByAlgoId(String id);

    List<RegistryAlgoDTO> getRegistListByRegistryId(String id);

    void deleteRegistryAlgoByAlgoId(String id);
    
    void deleteRegistryAlgoByRegistryAlgo(String regId,String algoId);

    List<FlowChartDTO> findFlowList();

    List<CommonParameter> parseFlowXmlParameter(String id);

    FlowChartDTO getFlowById(String id);


    List<ProcessStepsDTO> findFlowCeaselesslyList(ProcessStepsDTO processStepsDTO);

    FlowLogDTO saveFlowLog(FlowLogDTO flowLogDTO);

    FlowLogDTO findByFlowLogId(String flowLogId);

    long getFlowLogCount(String nextId, int jobLogId);

    FlowLogDTO findByJobLogIdAndDataIdAndParentFlowlogId(int jobLogId, String dataId, String panrentFlowlogId);

    Map<String, Object> pageListFlow(int start, int length,
                                     FlowChartDTO flowChartDTO);

    List<FlowLogDTO> findFlowLogList(int jobLogId, String parentFlowlogId);

    RegistryDTO findRegistryById(String id);

    ParallelLogDTO saveParallelLog(ParallelLogDTO parallelLogDTO);

    List<ParallelLogDTO> findParallelLogList(ParallelLogDTO parallelLogDTO);

    ParallelLogDTO findParallelLogById(String id);

    Boolean existSimpleKey(String simpleKey);

    boolean setRedisSimple(String unsimpleKey, String jobId);

    String saveRegistry(RegistryDTO registryDTO);

    String updateRegistry(RegistryDTO registryDTO);

    String removeRegistry(String id);

    List<String> findAddressList(String id, int dealAmount);

    ReturnT<String> deleteFlow(String id);

    List<String> getNodeLineJobQueue(String id);

    List<String> getNodeOperateJobQueue(String id);

    void hmPut(String key, String hashKey, String value);

    List<Object> getBadNodeJobQueue(String ip);

    void delAll(String ip);

    void removeBadNodeJobQueue(String ip, String hashKey);

    void deleteByParameterId(String jobparameterId);

    void delAlgoRegByRegId(String id);

    List<ProcessStepsDTO> findStartOrEndFlowCeaselesslyList(ProcessStepsDTO processStepsDTO);

    ReturnT<String> deleteFlowLog(String id);


    Map findParallelLogPage(int logId, int start, int length, int logStatus);

    public void lPush(String k, String v);

    public List<Mapping> mapping(String id);

    /**
     * @return
     */
    List<ZtreeView> allTree();

    /**
     * @param start
     * @param length
     * @param searchText
     * @param id
     * @return
     */
    String dicCodeList(int start, int length, String searchText, String id);

    /**
     * @param dictCodeDTO
     */
    void saveOrUpdateDicCode(DictCodeDTO dictCodeDTO);

    /**
     * @param id
     */
    void deleteDicCode(String id);

    public List<MatchRelation> saveMatchRelation(List<MatchRelation> matchRelations);

    List<DictCodeDTO> findChildrenDictCode(String string);

    DictCodeDTO findOneselfDictCode(String string);

    public String getExePath(ReturnT<String> os);

    public String getNodeSharePath(ReturnT<String> os);

    public String getMasterSharePath(String os);

    void deleteParallelLogAndFlowLog(int jobLodId);

    MatchRelation findMatchRelationByJobIdAndDataId(int jobId, String dataId);

    List<CommonParameter> findEnd(int jobLogId);

    List<CimissDataInfoDTO> getCimissData(String dataType);

    SystemLog saveSystemLog(SystemLog systemLog);

    Page<SystemLog> getSystemLogsByPage(Pageable pageable);

    Page<SystemLog> getSystemLogsByCategory(String category, Pageable pageable);

    int deleteSystemLog(String id);

    List<AlgorithmRelationInfoDTO> queryAlgo(String treeid);

    AlgorithmRelationInfoDTO saveRelation(AlgorithmRelationInfoDTO algorithmRelationInfoDTO);

    ReturnT<String> deleteRelation(String treeId, String lgoid);

    ProductDTO findByTreeId(String treeid);

    /**
     * @return
     */
    List<ArchiveCatalog> findArchiveCatalogs();

    /**
     * @param archiveCatalog
     * @return
     */
    int saveArchiveCatalog(ArchiveCatalog archiveCatalog);

    /**
     * @param start
     * @param length
     * @param archiveRules
     * @return
     */
    Map<String, Object> pageListArchiveRules(int start, int length, ArchiveRules archiveRules);

    int saveArchiveRules(ArchiveRules archiveRules, List<ArchiveFiledMap> fileMapList);

    /**
     * @return
     */
    List<ArchiveFiledManage> findAllArchiveFiledManages();

    /**
     * @param archive_rule_id
     * @return
     */
    List<ArchiveFiledMap> findArchiveFiledMap(String archiveRuleId);

    /**
     * @return
     */
    List<Disk> findfileDisks();

    /**
     * @param id
     * @return
     */
    ReturnT<String> deleteTreeNodeArchiveCatalog(String id);

    /**
     * @param id
     * @return
     */
    ReturnT<String> deleteArchiveRulesAndFiledMap(String id);

    /**
     * @param start
     * @param length
     * @param searchText
     * @param id
     * @return
     */
    String systemParamList(int start, int length, String searchText, String id);

    /**
     * @param systemParam
     */
    void saveOrUpdateSystemParam(SystemParam systemParam);

    /**
     * @param id
     */
    void deleteSystemParam(String id);

    /**
     * @param catalogCode
     * @return
     */
    ReturnT<String> findArchiverules(String catalogCode);

    Map<String, Object> pageListMeataImgAndInfo(int start, int length, String catalogcode, Date fProducetimeStart, Date fProducetimeEnd, String fLevel, Integer fCloudamount, String fSatelliteid);
    
    String getDownloadPath(String dataid);
    
    String deleteData(String dataid);

    List<ProductInfoDTO> findProductInfoListByIssueRange(String id, String startIssue, String endIssue);

    ProductInfoDTO saveProductInfo(ProductInfoDTO productInfoDTO);

    void deleteProductFileInfoByIssue(String deleteIssue, String id);

    String getIp();

    String diskList(int start, int length, String searchText);

    void saveOrUpdateDisk(Disk disk);

    void delDisk(String id);

    int enableOrdisable(String id, String rulestatus);

    /**
     * @param valueOf
     * @param archivdisk
     * @return
     */
    int updateDisk(String valueOf, String archivdisk);

    void updateRecycleflag(String mark, String id);

    boolean checkBroadCast(String handler);

    public ReturnT<String> smartToDbByJson(String jsonObject, ReturnT<String> returnT);

    List<Object[]> rasterStaticList(String productId, String regionId, int parseInt, String cycle, Date startTime,
			Date endTime);

	List<Object> statisticTypeList();

    List<DictCodeDTO> findByParentId(String parentId);
}
