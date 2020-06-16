package com.htht.job.executor.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.handler.SharingHandler;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.dms.module.ArchiveCatalog;
import com.htht.job.executor.model.dms.module.ArchiveFiledManage;
import com.htht.job.executor.model.dms.module.ArchiveFiledMap;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.SystemParam;
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
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.algorithm.ProductRelationService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.dms.ArchiveCatalogService;
import com.htht.job.executor.service.dms.ArchiveFiledManageService;
import com.htht.job.executor.service.dms.ArchiveFiledMapService;
import com.htht.job.executor.service.dms.ArchiveRulesService;
import com.htht.job.executor.service.dms.DiskService;
import com.htht.job.executor.service.dms.MeataImgAndInfoService;
import com.htht.job.executor.service.dms.SystemParamService;
import com.htht.job.executor.service.downupload.DownResultService;
import com.htht.job.executor.service.fileinfo.FileInfoService;
import com.htht.job.executor.service.flowchart.FlowChartService;
import com.htht.job.executor.service.flowlog.FlowLogService;
import com.htht.job.executor.service.ftp.FtpService;
import com.htht.job.executor.service.mapping.MatchRelationService;
import com.htht.job.executor.service.monitor.MonitorService;
import com.htht.job.executor.service.parallellog.ParallelLogService;
import com.htht.job.executor.service.processsteps.ProcessStepsService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.product.RasterStaticService;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.service.registryalgo.RegistryAlgoService;
import com.htht.job.executor.service.shard.ShardingService;
import com.htht.job.executor.service.smart.SmartToDbService;
import com.htht.job.executor.service.systemlog.SystemLogService;
import com.htht.job.executor.util.DubboIpUtil;
import com.htht.job.executor.util.SpringContextUtil;
import com.htht.job.vo.NodeMonitor;

/**
 * Created by zzj on 2018/1/2.
 */
@Transactional
@Service("dubboService")
public class DubboServiceImpl implements DubboService {
    private Logger logger = LoggerFactory.getLogger(DubboServiceImpl.class);


    @Autowired
    ProductInfoService productInfoService;

    @Autowired
    private SystemLogService systemLogService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductFileInfoService productFileInfoService;
    @Autowired
    private ShardingService shardingService;
    @Autowired
    private FtpService ftpService;
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private FlowChartService flowChartService;
    @Autowired
    private RegistryService registryService;
    @Autowired
    private RegistryAlgoService registryAlgoService;
    @Autowired
    private ProcessStepsService processStepsService;
    @Autowired
    private FlowLogService flowLogService;
    @Autowired
    private ParallelLogService parallelLogService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private DictCodeService dictCodeService;
    @Autowired
    private MatchRelationService matchRelationService;
    @Autowired
    private DownResultService downResultService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private ArchiveCatalogService archiveCatalogService;
    @Autowired
    private ArchiveRulesService archiveRulesService;
    @Autowired
    private ArchiveFiledMapService archiveFiledMapService;
    @Autowired
    private ArchiveFiledManageService archiveFiledManageService;
    @Autowired
    private DiskService diskService;
    @Autowired
    private SystemParamService systemParamService;
    @Autowired
    private MeataImgAndInfoService meataImgAndInfoService;
    @Autowired
    private SmartToDbService smartToService;
    @Autowired
    private RasterStaticService rasterStaticService;

    /**
     * 保存算法和节点关系映射
     *
     * @param registryAlgoDTO
     * @return
     */
    @Override
    public RegistryAlgoDTO saveRegistryAlgo(RegistryAlgoDTO registryAlgoDTO) {
        return registryAlgoService.save(registryAlgoDTO);
    }

    /**
     * 查找所有节点
     */
    @Override
    public List<RegistryDTO> findAllRegistry() {
        return registryService.findAll();
    }

    /**
     * ftp
     * <p>
     * 按照id查询数据
     */
    @Override
    public FtpDTO getById(String id) {
        return ftpService.getById(id);
    }

    /**
     * 修改FTP实体信息
     *
     * @param ftpDTO 实体对象
     * @return 返回1 修改成功 0 修改失败
     * @author miaowei 2018-01-24
     */
    @Override
    public int updeat(FtpDTO ftpDTO) {
        return this.ftpService.updeat(ftpDTO);
    }

    /**
     * 删除FTP实体信息
     *
     * @param id 需要删除的实体主键
     * @return 1 删除成功 0删除失败
     * @author miaowei 2018-01-24
     */
    @Override
    public int del(String id) {
        return this.ftpService.del(id);
    }

    @Override
    public void execute(String params) {
        shardingService.execute(params);
    }

    @Override
    public Map<String, Object> pageListProduct(int start, int length,
                                               ProductDTO productDTO) {
        return productService.pageList(start, length, productDTO);
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        return productService.saveProduct(productDTO);
    }

    @Override
    public ReturnT<String> deleteProduct(String id) {
        return  productService.deleteProduct(id);
    }

    @Override
    public ReturnT<String> deleteProducByTree(String id) {
        return productService.deleteProductByTreeId(id);
    }

    @Override
    public List<ProductDTO> findALlProduct() {
        return productService.findALlProduct();
    }

    @Override
    public Map<String, Object> pageListProductFileInfo(int start, int length,
                                                       ProductFileInfoDTO productFileInfoDTO) {

        return productFileInfoService.pageList(start, length, productFileInfoDTO);
    }

    @Override
    public Map<String, Object> pageListProductFileInfos(int start, int length, String id) {
        return productFileInfoService.pageLists(start, length, id);
    }

    @Override
    public Map<String, Object> pageListProductInfo(int start, int length, String productId) {
        return productInfoService.pageList(start, length, productId);
    }

    @Override
    public ProductFileInfoDTO saveProductFileInfo(ProductFileInfoDTO productFileInfoDTO) {
        return productFileInfoService.save(productFileInfoDTO);
    }

    @Override
    public void deleteProductFileInfo(String id)  {
        List<ProductFileInfoDTO> p2 = productFileInfoService.findByproductInfoId(id);
        for (ProductFileInfoDTO p : p2) {
            File myfile = new File(p.getFilePath());
            try {
                Files.deleteIfExists(myfile.toPath());
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }

        }
        productFileInfoService.deleteByproductInfoId(id);
    }

    @Override
    public void deleteProductInfo(String id) {
        productInfoService.deleteProductInfo(id);
    }

    @Override
    public String findSelectFtp() {
        List<FtpDTO> list = ftpService.findAll();
        List<Map> mapList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map map = new HashMap();
            map.put("id", list.get(i).getId());
            map.put("text", list.get(i).getName());
            mapList.add(map);
        }
        return JSON.toJSONString(mapList);
    }

    @Override
    public Page<FtpDTO> getFtpsByPage(Pageable pageable) {
        return ftpService.getFtpsByPage(pageable);
    }

    @Override
    public boolean testConnectFtp(String ip, int port, String userName, String pwd) {
        return ftpService.testConnectFtp(ip, port, userName, pwd);
    }

    @Override
    public FtpDTO saveFtp(FtpDTO ftpDTO) {
        return ftpService.save(ftpDTO);
    }

    @Override
    public List<NodeMonitor> findAllMonitor() {
        return monitorService.findAll();
    }

    @Override
    public List<FileInfoDTO> findFileInfoByWhere(String id) {
        return fileInfoService.findByWhere(id);
    }

    @Override
    public FlowChartDTO saveOrUpdateFlow(FlowChartDTO flowChartDTO) {
        return flowChartService.saveOrUpdate(flowChartDTO);
    }

    @Override
    public List<RegistryAlgoDTO> getRegistListByAlgoId(String id) {
        return registryAlgoService.registryAlgoService(id);
    }

    @Override
    public void deleteRegistryAlgoByAlgoId(String id) {
        registryAlgoService.deleteRegistryAlgoByAlgoId(id);
    }
    
	@Override
	public void deleteRegistryAlgoByRegistryAlgo(String regId, String algoId) {
		registryAlgoService.deleteRegistryAlgoByRegistryAlgo(regId,algoId);
	}

    @Override
    public List<FlowChartDTO> findFlowList() {
        return flowChartService.findFlowList();
    }
    @Override
    public List<CommonParameter> parseFlowXmlParameter(String id) {
        return flowChartService.parseFlowXmlParameter(id);
    }
    @Override
    public FlowChartDTO getFlowById(String id) {
        return flowChartService.getById(id);
    }

    @Override
    public List<ProcessStepsDTO> findFlowCeaselesslyList(ProcessStepsDTO processStepsDTO) {
        return processStepsService.findFlowCeaselesslyList(processStepsDTO);
    }
    @Override
    public FlowLogDTO saveFlowLog(FlowLogDTO flowLogDTO) {
        return flowLogService.saveFlowLog(flowLogDTO);
    }
    @Override
    public FlowLogDTO findByFlowLogId(String flowLogId) {
        return flowLogService.findByFlowLogId(flowLogId);
    }
    @Override
    public long getFlowLogCount(String nextId, int jobLogId) {
        return flowLogService.getFlowLogCount(nextId, jobLogId);
    }
    @Override
    public FlowLogDTO findByJobLogIdAndDataIdAndParentFlowlogId(int jobLogId, String dataId, String parentFlowlogId) {
        return flowLogService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId, dataId, parentFlowlogId);
    }
    @Override
    public Map<String, Object> pageListFlow(int start, int length,
                                            FlowChartDTO flowChartDTO) {
        return flowChartService.pageListFlow(start, length, flowChartDTO);
    }
    @Override
    public List<FlowLogDTO> findFlowLogList(int jobLogId, String parentFlowlogId) {
        return flowLogService.findFlowLogList(jobLogId, parentFlowlogId);
    }

    @Override
    public RegistryDTO findRegistryById(String id) {
        return registryService.getById(id);
    }
    @Override
    public ParallelLogDTO saveParallelLog(ParallelLogDTO parallelLogDTO) {
        if (!StringUtils.isEmpty(parallelLogDTO.getId())) {
            parallelLogDTO.setUpdateTime(new Date());
        }
        try {
            parallelLogDTO = parallelLogService.saveParallelLog(parallelLogDTO);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return parallelLogDTO;
    }
    @Override
    public List<ParallelLogDTO> findParallelLogList(ParallelLogDTO parallelLogDTO) {
        return parallelLogService.findParallelLogList(parallelLogDTO);
    }
    @Override
    public ParallelLogDTO findParallelLogById(String id) {
        return parallelLogService.findParallelLogById(id);
    }
    @Override
    public Boolean existSimpleKey(String simpleKey) {
        return  redisService.exists(simpleKey);
    }
    @Override
    public boolean setRedisSimple(String unsimpleKey, String jobId) {
        return redisService.set(unsimpleKey, jobId, 36000000L);
    }

    /**
     * 保存添加的节点
     */
    @Override
    public String saveRegistry(RegistryDTO registryDTO) {
        RegistryDTO save = registryService.save(registryDTO);
        return save.getId();
    }

    /**
     * 更新节点
     */
    @Override
    public String updateRegistry(RegistryDTO registryDTO) {
        RegistryDTO save = registryService.update(registryDTO);
        return save.getId();
    }

    @Override
    public String removeRegistry(String id) {
        return registryService.remove(id);
    }
    @Override
    public List<String> findAddressList(String id, int dealAmount) {
        return registryService.findAddressList(id, dealAmount);
    }
    @Override
    public ReturnT<String> deleteFlow(String id) {
        return flowChartService.deleteFlow(id);
    }

    @Override
    public List<String> getNodeLineJobQueue(String id) {
        RegistryDTO registryDTO = registryService.getById(id);
        String fuzzyKey = MonitorQueue.NODE_LINE_QUEUE + registryDTO.getRegistryKey() + registryDTO.getRegistryIp();
        Set<String> fuzzyQuery = redisService.fuzzyQuery(fuzzyKey);
        ArrayList<String> keys = new ArrayList<>();
        for (String string : fuzzyQuery) {
            Integer value = (Integer) redisService.get(string);
            keys.add(value + "");
        }
        return keys;
    }

    @Override
    public List<String> getNodeOperateJobQueue(String id) {
        RegistryDTO registryDTO = registryService.getById(id);
        String fuzzyKey = MonitorQueue.NODE_OPERATION_QUEUE + registryDTO.getRegistryKey() + registryDTO.getRegistryIp();
        Set<String> fuzzyQuery = redisService.fuzzyQuery(fuzzyKey);
        ArrayList<String> keys = new ArrayList<>();
        for (String string : fuzzyQuery) {
            Integer value = (Integer) redisService.get(string);
            keys.add(value + "");
        }
        return keys;
    }

    //删除所有
    @Override
    public void delAll(String ip) {
        redisService.fuzzyRemove(ip);
    }

    //任务清单汇总
    @Override
    public void hmPut(String key, String hashKey, String value) {
        redisService.hmSet(key, hashKey, value);
    }

    //获取坏节点任务清单
    @Override
    public List<Object> getBadNodeJobQueue(String ip) {
        String key = ip + MonitorQueue.BAD_NODE_QUEUE;
        return redisService.hmValues(key);
    }

    //移除已执行坏节点任务
    @Override
    public void removeBadNodeJobQueue(String ip, String hashKey) {
        redisService.hmDel(ip + MonitorQueue.BAD_NODE_QUEUE, hashKey);
    }
    @Override
    public void lPush(String k, String v) {
        redisService.lPush(k, v);
    }
    @Override
    public void deleteByParameterId(String jobparameterId) {
        processStepsService.deleteByParameterId(jobparameterId);
    }

    @Override
    public void delAlgoRegByRegId(String id) {
        registryAlgoService.delAlgoRegByRegId(id);
    }
    @Override
    public List<ProcessStepsDTO> findStartOrEndFlowCeaselesslyList(ProcessStepsDTO processStepsDTO) {
        return processStepsService.findStartOrEndFlowCeaselesslyList(processStepsDTO);
    }
    @Override
    public ReturnT<String> deleteFlowLog(String id) {
        return flowLogService.deleteFlowLog(id);
    }

    @Override
    public Map findParallelLogPage(int logId, int start, int length, int logStatus) {
        return parallelLogService.findParallelLogPage(logId, start, length, logStatus);
    }
    @Override
    public List<Mapping> mapping(String id) {
        return processStepsService.mapping(id);
    }

    /**
     * 字典项查询
     * /***-------字典管理-----------
     **/
    /*
	 * 以树状结构展示字典
	 */
    @Override
    public List<ZtreeView> allTree() {
        return dictCodeService.allTree();
    }

    /*
     * 字典列表
     */
    @Override
    public String dicCodeList(int start, int length, String searchText, String id) {
        return dictCodeService.list(start, length, searchText, id);
    }

    @Override
    public void saveOrUpdateDicCode(DictCodeDTO dictCodeDTO) {
        dictCodeService.saveOrUpdateDicCode(dictCodeDTO);
    }

    @Override
    public void deleteDicCode(String id) {
        dictCodeService.delete(id);
    }

    /**
     * @Description: 保存匹配关系
     * @Param: [matchRelations]
     * @return: java.util.List<com.htht.job.executor.model.mapping.MatchRelation>
     * @Author: zzj
     * @Date: 2018/7/5
     */
    @Override
    public List<MatchRelation> saveMatchRelation(List<MatchRelation> matchRelations) {
        return matchRelationService.saveMatchRelation(matchRelations);
    }

    @Override
    public void deleteParallelLogAndFlowLog(int jobLodId) {
        flowLogService.deleteParallelLogAndFlowLog(jobLodId);
    }
    @Override
    public MatchRelation findMatchRelationByJobIdAndDataId(int jobId, String dataId) {
        return matchRelationService.findMatchRelationByJobIdAndDataId(jobId, dataId);
    }

    @Override
    public List<CimissDataInfoDTO> getCimissData(String dataType) {
        return downResultService.getCimissData(dataType);
    }

    @Override
    public SystemLog saveSystemLog(SystemLog systemLog) {
        return systemLogService.save(systemLog);
    }

    @Override
    public Page<SystemLog> getSystemLogsByPage(Pageable pageable) {
        return systemLogService.getSystemLogsByPage(pageable);
    }

    @Override
    public Page<SystemLog> getSystemLogsByCategory(String category, Pageable pageable) {
        return systemLogService.findSystemLogsByCategory(category, pageable);
    }

    @Override
    public int deleteSystemLog(String id) {
        return systemLogService.deleteSystemLog(id);
    }

    @Override
    public List<AlgorithmRelationInfoDTO> queryAlgo(String treeid) {
        return productRelationService.queryAogo(treeid);
    }

    @Override
    public AlgorithmRelationInfoDTO saveRelation(AlgorithmRelationInfoDTO algorithmRelationInfoDTO) {
        return productRelationService.saveRelation(algorithmRelationInfoDTO);
    }

    @Override
    public ReturnT<String> deleteRelation(String treeId, String lgoid) {
        return productRelationService.deleteRelation(treeId, lgoid);
    }

    @Override
    public ProductDTO findByTreeId(String treeId) {
        return productService.getProductIdByTreeId(treeId);
    }

    @Override
    public List<DictCodeDTO> findChildrenDictCode(String string) {
        return dictCodeService.findChildren(string);
    }

    @Override
    public DictCodeDTO findOneselfDictCode(String string) {
        return dictCodeService.findOneself(string);
    }

    @Override
    public String getExePath(ReturnT<String> os) {
        //获取算法执行根路径
        DictCodeDTO executePathDict = null;
        if ("windows".equals(os.getContent())) {
            executePathDict = findOneselfDictCode("windows算法执行根路径");
        } else {
            executePathDict = findOneselfDictCode("linux算法执行根路径");
        }
        return executePathDict.getDictCode();
    }

    @Override
    public String getNodeSharePath(ReturnT<String> os) {
        DictCodeDTO sharePathDict = null;
        if ("windows".equals(os.getContent())) {
            sharePathDict = findOneselfDictCode("windows共享目录根路径");
        } else {
            sharePathDict = findOneselfDictCode("linux共享目录根路径");
        }
        return sharePathDict.getDictCode();
    }

    @Override
    public String getMasterSharePath(String os) {
        DictCodeDTO sharePathDict = null;
        if (os.toLowerCase().startsWith("win")) {
            sharePathDict = findOneselfDictCode("windows共享目录根路径");
        } else {
            sharePathDict = findOneselfDictCode("linux共享目录根路径");
        }
        return sharePathDict.getDictCode();
    }
    @Override
    public List<CommonParameter> findEnd(int jobLogId) {
        return flowLogService.findEnd(jobLogId);
    }

    @Override
    public List<ArchiveCatalog> findArchiveCatalogs() {
        return archiveCatalogService.findArchiveCatalogs();
    }

    @Override
    public int saveArchiveCatalog(ArchiveCatalog archiveCatalog) {
        return archiveCatalogService.saveArchiveCatalog(archiveCatalog);
    }

    @Override
    public Map<String, Object> pageListArchiveRules(int start, int length, ArchiveRules archiveRules) {
        return archiveRulesService.pageListArchiveRules(start, length, archiveRules);
    }

    @Override
    public int saveArchiveRules(ArchiveRules archiveRules, List<ArchiveFiledMap> fileMapList) {
        int i = -1;
        i = archiveRulesService.saveArchiveRules(archiveRules);

        if (i > 0) {
            String id = archiveRulesService.findArchiveRules(archiveRules);

            if (StringUtils.isNotBlank(id)) {
                i = archiveFiledMapService.saves(fileMapList, id, archiveRules.getCatalogcode());
            }

        }
        return i;
    }
    @Override
    public List<ArchiveFiledManage> findAllArchiveFiledManages() {
        return archiveFiledManageService.findAll();
    }
    @Override
    public List<ArchiveFiledMap> findArchiveFiledMap(String archiveRuleId) {
        return archiveFiledMapService.getByArchiveRuleId(archiveRuleId);
    }

    /*
     * 删除目录
     */
    @Override
    public ReturnT<String> deleteTreeNodeArchiveCatalog(String id) {
        try {
            if (StringUtils.isNotBlank(id)) {
                ArchiveCatalog ac = archiveCatalogService.getById(id);
                if (null != ac && StringUtils.isNotBlank(ac.getCatalogCode())) {
                    //archiveRulesService.getBySpecification(ac)
                    List<ArchiveCatalog> allArchiveRules = archiveCatalogService.getByCatalogCode(ac.getCatalogCode());
                    archiveCatalogService.deleteTreeNodes(allArchiveRules);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return ReturnT.FAIL;
        }

        return ReturnT.SUCCESS;
    }

    @Override
    public List<Disk> findfileDisks() {
        return diskService.findfileDisks();
    }

    @Override
    public ReturnT<String> deleteArchiveRulesAndFiledMap(String id) {
        try {
            ReturnT<String> result = archiveFiledMapService.deltearchiveFiledMaps(id);
            if (result.getCode() == 200) {
                archiveRulesService.delete(id);
            } else {
                return ReturnT.FAIL;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public String systemParamList(int start, int length, String searchText, String id) {
        return systemParamService.list(start, length, searchText, id);
    }

    @Override
    public void saveOrUpdateSystemParam(SystemParam systemParam) {
        systemParamService.saveOrUpdateSystemParam(systemParam);
    }

    @Override
    public void deleteSystemParam(String id) {
        systemParamService.delete(id);

    }

    @Override
    public ReturnT<String> findArchiverules(String catalogCode) {

        return archiveRulesService.findArchiveRulesList(catalogCode);
    }

    @Override
    public Map<String, Object> pageListMeataImgAndInfo(int start, int length, String catalogcode, Date fProducetimeStart, Date fProducetimeEnd, String fLevel, Integer fCloudamount, String fSatelliteid) {
        return meataImgAndInfoService.meataImgAndInfoService(start, length, catalogcode, fProducetimeStart, fProducetimeEnd, fLevel, fCloudamount, fSatelliteid);
    }
    @Override
    public String getDownloadPath(String dataid) {
    	return meataImgAndInfoService.getDownloadPath(dataid);
    }
    @Override
    public String deleteData(String dataid) {
    	return meataImgAndInfoService.deleteData(dataid);
    }

    @Override
    public List<ProductInfoDTO> findProductInfoListByIssueRange(String id, String startIssue, String endIssue) {
        return productInfoService.findProductInfoListByIssueRange(id, startIssue, endIssue);
    }

    @Override
    public ProductInfoDTO saveProductInfo(ProductInfoDTO productInfoDTO) {
        return productInfoService.save(productInfoDTO);
    }

    @Override
    public void deleteProductFileInfoByIssue(String deleteIssue, String id) {
        productFileInfoService.deleteByproductInfoId(id);
    }

    @Override
    public String diskList(int start, int length, String searchText) {
        return diskService.diskList(start, length, searchText);
    }

    @Override
    public void saveOrUpdateDisk(Disk disk) {
        diskService.saveOrUpdateDisk(disk);
    }

    @Override
    public void delDisk(String id) {
        diskService.del(id);
    }

    @Override
    public String getIp() {
        return DubboIpUtil.getIp();
    }

    @Override
    public int enableOrdisable(String id, String rulestatus) {
        return archiveRulesService.enableOrdisable(id, rulestatus);
    }

    @Override
    public int updateDisk(String id, String archivdisk) {
        return archiveRulesService.updateDisk(id, archivdisk);
    }

    @Override
    public void updateRecycleflag(String mark, String id) {
        meataImgAndInfoService.updateRecycleflag(mark, id);
    }

    @Override
    public List<RegistryAlgoDTO> getRegistListByRegistryId(String id) {
        return registryAlgoService.getRegistListByRegistryId(id);
    }
    @Override
    public boolean checkBroadCast(String handler) {
        boolean flag = false;
        try {
            SharingHandler sharingHandler = (SharingHandler) SpringContextUtil.getBean(handler + "Shard");
            if (null != sharingHandler) {
                flag = true;
            }
        } catch (Exception e) {
            return false;
        }
        return flag;
    }
    @Override
    public ReturnT<String> smartToDbByJson(String jsonObject, ReturnT<String> returnT) {
        return smartToService.toDbByJson(jsonObject, returnT);

    }

	@Override
	public List<Object[]> rasterStaticList(String productId, String regionId, int level, String cycle,
			Date startTime, Date endTime) {
		return rasterStaticService.rasterStaticList(productId, regionId, level, cycle, startTime, endTime);
	}

	@Override
	public List<Object> statisticTypeList() {
		return  rasterStaticService.statisticTypeList();
	}
    @Override
    public List<DictCodeDTO> findByParentId(String parentId){
        return dictCodeService.findByParentId(parentId);
    }
}
