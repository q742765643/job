package com.htht.job.executor.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.htht.job.executor.util.DubboIpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.MonitorQueue;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.dms.module.*;
import com.htht.job.executor.model.downupload.AlgorithmRelationInfo;
import com.htht.job.executor.model.downupload.CimissDataInfo;
import com.htht.job.executor.model.exceldata.ExcelDataPageInfo;
import com.htht.job.executor.model.exceldata.ExcelDataTemp;
import com.htht.job.executor.model.exceldata.ExcelElementInfo;
import com.htht.job.executor.model.exceldata.ExcelElementInfoView;
import com.htht.job.executor.model.fileinfo.FileInfo;
import com.htht.job.executor.model.flowchart.FlowChartModel;
import com.htht.job.executor.model.flowlog.FlowLog;
import com.htht.job.executor.model.ftp.Ftp;
import com.htht.job.executor.model.mapping.Mapping;
import com.htht.job.executor.model.mapping.MatchRelation;
import com.htht.job.executor.model.parallellog.ParallelLog;
import com.htht.job.executor.model.processsteps.ProcessSteps;
import com.htht.job.executor.model.product.Product;
import com.htht.job.executor.model.productfileinfo.ProductFileInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.model.registryalgo.RegistryAlgo;
import com.htht.job.executor.model.systemlog.SystemLog;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.algorithm.ProductRelationService;
import com.htht.job.executor.service.dictionary.DictCodeService;
import com.htht.job.executor.service.dms.*;
import com.htht.job.executor.service.downupload.DownResultService;
import com.htht.job.executor.service.exceldata.DataManageService;
import com.htht.job.executor.service.fileinfo.FileInfoService;
import com.htht.job.executor.service.flowchart.FlowChartService;
import com.htht.job.executor.service.flowlog.FlowLogService;
import com.htht.job.executor.service.ftp.FtpService;
import com.htht.job.executor.service.mapping.MatchRelationService;
import com.htht.job.executor.service.monitor.MonitorService;
import com.htht.job.executor.service.parallellog.ParallelLogService;
import com.htht.job.executor.service.processsteps.ProcessStepsService;
import com.htht.job.executor.service.product.ProductService;
import com.htht.job.executor.service.productfileinfo.ProductFileInfoService;
import com.htht.job.executor.service.productinfo.ProductInfoService;
import com.htht.job.executor.service.registry.RegistryService;
import com.htht.job.executor.service.registryalgo.RegistryAlgoService;
import com.htht.job.executor.service.shard.ShardingService;
import com.htht.job.executor.service.systemlog.SystemLogService;
import com.htht.job.vo.NodeMonitor;

import net.sf.json.JSONArray;

/**
 * Created by zzj on 2018/1/2.
 */
@Transactional
@Service("dubboService")
public class DubboServiceImpl implements DubboService {

	@Autowired
	ProductInfoService productInfoService;
	
	@Autowired
	private DataManageService dataManageService;

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
	
	/**
	 * 保存算法和节点关系映射
	 * @param registryAlgo
	 * @return
	 */
	@Override
	public RegistryAlgo saveRegistryAlgo(RegistryAlgo registryAlgo){
		RegistryAlgo save = registryAlgoService.save(registryAlgo);
		return save;
	}
	/**
	 * 查找所有节点
	 */
	@Override
	public List<Registry> findAllRegistry(){
		List<Registry> registryList = registryService.findAll();
		return registryList;
	}
	/**
	 * ftp
	 * 
	 * 按照id查询数据
	 */
	@Override
	public Ftp getById(String id) {
		return ftpService.getById(id);
	}

	/**
	 * 修改FTP实体信息
	 * 
	 * @param ftp
	 *            实体对象
	 * @return 返回1 修改成功 0 修改失败
	 * @author miaowei 2018-01-24
	 */
	@Override
	public int updeat(Ftp ftp) {
		return this.ftpService.updeat(ftp);
	}

	/**
	 * 删除FTP实体信息
	 * 
	 * @param id
	 *            需要删除的实体主键
	 * @return 1 删除成功 0删除失败
	 * @author miaowei 2018-01-24
	 */
	@Override
	public int del(String id) {
		return this.ftpService.del(id);
	}
	@Override
	public ResultUtil<List<String>> execute(String params, String handler,
											LinkedHashMap fixmap, LinkedHashMap dymap) {
		return shardingService.execute(params, handler, fixmap, dymap);
	}
	@Override
	public Map<String, Object> pageListProduct(int start, int length,
			Product product) {
		Map<String, Object> map = productService.pageList(start, length,
				product);
		return map;
	}
	@Override
	public Product saveProduct(Product product) {
		Product p = productService.saveProduct(product);
		return p;
	}
	@Override
	public ReturnT<String> deleteProduct(String id) {
		ReturnT<String> r = productService.deleteProduct(id);
		return r;
	}
	@Override
	public ReturnT<String> deleteProducByTree(String id) {
		ReturnT<String> r = productService.deleteProductByTreeId(id);
		return r;
	}
	@Override
	public List<Product> findALlProduct() {
		return productService.findALlProduct();
	}
	@Override
	public Map<String, Object> pageListProductFileInfo(int start, int length,
			ProductFileInfo productFileInfo) {
		Map<String, Object> map = productFileInfoService.pageList(start,
				length, productFileInfo);
		return map;
	}

	@Override
	public Map<String, Object> pageListProductFileInfos(int start, int length, String id) {
		Map<String, Object> map = productFileInfoService.pageLists(start,
				length, id);
		return map;
	}

	@Override
	public Map<String, Object> pageListProductInfo(int start, int length, String productId) {
		Map<String, Object> map = productInfoService.pageList(start, length, productId);
		return map;
	}

	@Override
	public ProductFileInfo saveProductFileInfo(ProductFileInfo productFileInfo) {
		ProductFileInfo p = productFileInfoService
				.save(productFileInfo);
		return p;
	}
	@Override
	public void deleteProductFileInfo(String id) {
		List<ProductFileInfo> p2 = productFileInfoService.findByproductInfoId(id);
		for(ProductFileInfo p : p2){
			File myfile = new File(p.getFilePath().toString());
			if(myfile.exists()){myfile.delete();}
		}
		productFileInfoService.deleteByproductInfoId(id);
	}

	@Override
	public void deleteProductInfo(String id) {
		productInfoService.deleteProductInfo(id);
	}
	
	@Override
	public ProductInfo findProductInfoById(String id) {
		
		return productInfoService.findProductInfoById(id);
	}

	@Override
	public String findSelectFtp() {
		List<Ftp> list = ftpService.findAll();
		List<Map> mapList = new ArrayList<Map>();
		for (int i = 0; i < list.size(); i++) {
			Map map = new HashMap();
			map.put("id", list.get(i).getId());
			map.put("text", list.get(i).getName());
			mapList.add(map);
		}
		return JSON.toJSONString(mapList);
	}

	@Override
	public Page<Ftp> getFtpsByPage(Pageable pageable) {
		return ftpService.getFtpsByPage(pageable);
	}

	@Override
	public boolean testConnectFtp(String ip, int port, String userName, String pwd) {
		return ftpService.testConnectFtp(ip, port, userName, pwd);
	}

	@Override
	public Ftp saveFtp(Ftp ftp) {
		return ftpService.save(ftp);
	}

	@Override
    public List<NodeMonitor> findAllMonitor(){
		return monitorService.findAll();
	}
	@Override
	public List<FileInfo> findFileInfoByWhere(String id){
		return fileInfoService.findByWhere(id);
	}
	
	@Override
	public FlowChartModel saveOrUpdateFlow(FlowChartModel flowChartModel){
		return flowChartService.saveOrUpdate(flowChartModel);
	}
	@Override
	public List<RegistryAlgo> getRegistListByAlgoId(String id) {
		List<RegistryAlgo> algoList = registryAlgoService.registryAlgoService(id);
		return algoList;
	}
	@Override
	public void deleteRegistryAlgoByAlgoId(String id) {
		registryAlgoService.deleteRegistryAlgoByAlgoId(id);
	}
	@Override
	public List<FlowChartModel>  findFlowList(){
		return  flowChartService.findFlowList();
	}
	public List<CommonParameter> parseFlowXmlParameter(String id){
		return flowChartService.parseFlowXmlParameter(id);
	}
	public FlowChartModel getFlowById(String id){
		return flowChartService.getById(id);
	}


	public List<ProcessSteps> findFlowCeaselesslyList(ProcessSteps processSteps){
		return processStepsService.findFlowCeaselesslyList(processSteps);
	}
	public FlowLog saveFlowLog(FlowLog flowLog){
        return flowLogService.saveFlowLog(flowLog);
	}
	public FlowLog findByFlowLogId(String flowLogId){
		return flowLogService.findByFlowLogId(flowLogId);
	}

	public long  getFlowLogCount(String nextId,int jobLogId){
		return flowLogService.getFlowLogCount(nextId,jobLogId);
	}
	public FlowLog findByJobLogIdAndDataIdAndParentFlowlogId(int jobLogId,String dataId,String parentFlowlogId){
		return flowLogService.findByJobLogIdAndDataIdAndParentFlowlogId(jobLogId,dataId,parentFlowlogId);
	}
	public Map<String, Object> pageListFlow(int start, int length,
											FlowChartModel flowChartModel){
		return flowChartService.pageListFlow(start,length, flowChartModel);
	}
	public List<FlowLog> findFlowLogList(int jobLogId,String parentFlowlogId){
		return flowLogService.findFlowLogList(jobLogId,parentFlowlogId);
	}
	@Override
	public Registry findRegistryById(String id) {
		Registry registry = registryService.getById(id);
		return registry;
	}

	public  ParallelLog saveParallelLog(ParallelLog parallelLog){
		if(!StringUtils.isEmpty(parallelLog.getId())){
			parallelLog.setUpdateTime(new Date());
		}
		try {
			parallelLog = parallelLogService.saveParallelLog(parallelLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parallelLog;
	}
	public List<ParallelLog> findParallelLogList(ParallelLog parallelLog){
		return parallelLogService.findParallelLogList(parallelLog);
	}
	public ParallelLog findParallelLogById(String id) {
		return parallelLogService.findParallelLogById(id);
	}
	public Boolean existSimpleKey(String simpleKey) {
		boolean exists = redisService.exists(simpleKey);
		return exists;
	}
	public boolean setRedisSimple(String unsimpleKey, String jobId) {
		boolean success = redisService.set(unsimpleKey, jobId,36000000L);
		return success;
	}
	/**
	 * 保存添加的节点
	 */
	@Override
	public String saveRegistry(Registry registry) {
		Registry save = registryService.save(registry);
		return save.getId();
	}
	
	/**
	 * 更新节点
	 */
	@Override
	public String updateRegistry(Registry registry) {
		Registry save = registryService.update(registry);
		return save.getId();
	}
	@Override
	public String removeRegistry(String id) {
        String result = registryService.remove(id);
		return result;
	}
	public ArrayList<String> findAddressList(String id,int dealAmount){
		return registryService.findAddressList(id,dealAmount);
	}
	public ReturnT<String> deleteFlow(String id){
		return flowChartService.deleteFlow(id);
	}
	@Override
	public List<String> getNodeLineJobQueue(String id) {
		Registry registry = registryService.getById(id);
		String fuzzyKey = MonitorQueue.NODE_LINE_QUEUE+registry.getRegistryKey()+registry.getRegistryIp();
		Set<String> fuzzyQuery = redisService.fuzzyQuery(fuzzyKey);
		ArrayList<String> keys = new ArrayList<>();
		for (String string : fuzzyQuery) {
			Integer value = (Integer) redisService.get(string);
			keys.add(value+"");
		}
		return keys;
	}
	@Override
	public List<String> getNodeOperateJobQueue(String id) {
		Registry registry = registryService.getById(id);
		String fuzzyKey = MonitorQueue.NODE_OPERATION_QUEUE+registry.getRegistryKey()+registry.getRegistryIp();
		Set<String> fuzzyQuery = redisService.fuzzyQuery(fuzzyKey);
		ArrayList<String> keys = new ArrayList<>();
		for (String string : fuzzyQuery) {
			Integer value = (Integer) redisService.get(string);
			keys.add(value+"");
		}
		return keys;
	}
	//删除所有
	public void delAll(String ip) {
		redisService.fuzzyRemove(ip);
	}
	//任务清单汇总
	public void hmPut(String key, String hashKey, String value) {
		redisService.hmSet(key, hashKey, value);
	}
	
	//获取坏节点任务清单
	@Override
	public List<Object> getBadNodeJobQueue(String ip) {
		String key = ip+MonitorQueue.BAD_NODE_QUEUE;
		return redisService.hmValues(key);
	}
	//移除已执行坏节点任务
	@Override
	public void removeBadNodeJobQueue(String ip,String hashKey) {
		redisService.hmDel(ip + MonitorQueue.BAD_NODE_QUEUE, hashKey);
	}
	public  void  lPush(String k,String v){
		redisService.lPush(k,v);
	}
	public void deleteByParameterId(String jobparameterId){
		processStepsService.deleteByParameterId(jobparameterId);
	}
	@Override
	public void delAlgoRegByRegId(String id) {
		registryAlgoService.delAlgoRegByRegId(id);
	}

	public List<ProcessSteps> findStartOrEndFlowCeaselesslyList(ProcessSteps processSteps){
		return  processStepsService.findStartOrEndFlowCeaselesslyList(processSteps);
	}
	public ReturnT<String> deleteFlowLog(String id){
		return flowLogService.deleteFlowLog(id);
	}
	@Override
	public Map findParallelLogPage(int logId,int start,int length,int logStatus) {
		return parallelLogService.findParallelLogPage(logId,start,length,logStatus);
	}
	public  List<Mapping>  mapping(String id) {
		return processStepsService.mapping(id);
	}
	/**
	 * 字典项查询
	/***-------字典管理-----------**/
	/* 
	 * 以树状结构展示字典
	 */
	@Override
	public List<ZtreeView> allTree() {
		// TODO Auto-generated method stub
		return dictCodeService.allTree();
	}
	/* 
	 * 字典列表
	 */
	@Override
	public String dicCodeList(int start, int length, String searchText,String id) {
		// TODO Auto-generated method stub
		return dictCodeService.list(start,length,searchText,id);
	}
	@Override
	public void saveOrUpdateDicCode(DictCode dictCode) {
		dictCodeService.saveOrUpdateDicCode(dictCode);
	}
	@Override
	public void deleteDicCode(String id) {
		// TODO Auto-generated method stub
		dictCodeService.delete(id);
	}
    /** 
    * @Description:  保存匹配关系
    * @Param: [matchRelations] 
    * @return: java.util.List<com.htht.job.executor.model.mapping.MatchRelation> 
    * @Author: zzj
    * @Date: 2018/7/5 
    */ 
	public List<MatchRelation> saveMatchRelation(List<MatchRelation>  matchRelations){
		return matchRelationService.saveMatchRelation(matchRelations);
	}
//	@Override
//	public List<DictCode> findALlExecutionStrategy() {
//		// TODO Auto-generated method stub
//		return dictCodeService.findALlExecutionStrategy();
//	}
//	@Override
//	public List<DictCode> findALlModelIdentification() {
//		// TODO Auto-generated method stub
//		return dictCodeService.findALlModelIdentification();
//	}

	public void deleteParallelLogAndFlowLog(int jobLodId){
		 flowLogService.deleteParallelLogAndFlowLog(jobLodId);
	}

	public MatchRelation findMatchRelationByJobIdAndDataId(int jobId,String dataId){
		return matchRelationService.findMatchRelationByJobIdAndDataId(jobId,dataId);
	}
	@Override
	public List<CimissDataInfo> getCimissData(String dataType) {
		return downResultService.getCimissData(dataType);
	}
	@Override	public SystemLog saveSystemLog(SystemLog systemLog) {
		return systemLogService.save(systemLog);
	}

	@Override
	public Page<SystemLog> getSystemLogsByPage(Pageable pageable) {
		return systemLogService.getSystemLogsByPage(pageable);
	}

	@Override
	public Page<SystemLog> getSystemLogsByCategory(String category ,Pageable pageable) {
		return systemLogService.findSystemLogsByCategory(category, pageable);
	}

	@Override
	public int deleteSystemLog(String id) {
		return systemLogService.deleteSystemLog(id);
	}

	@Override
	public List<AlgorithmRelationInfo> queryAlgo(String treeid) {
		return productRelationService.queryAogo(treeid);
	}

	@Override
	public AlgorithmRelationInfo saveRelation(AlgorithmRelationInfo algorithmRelationInfo) {
		return productRelationService.saveRelation(algorithmRelationInfo);
	}

	@Override
	public ReturnT<String> deleteRelation(String treeId, String lgoid) {
		return productRelationService.deleteRelation(treeId,lgoid);
	}

	@Override
	public Product findByTreeId(String treeId) {
		return productService.getProductIdByTreeId(treeId);
	}

	@Override
	public List<DictCode> findChildrenDictCode(String string) {
		return dictCodeService.findChildren(string);
	}

	@Override
	public DictCode findOneselfDictCode(String string) {
		return dictCodeService.findOneself(string);
	}

	@Override
	public String getExePath(ReturnT<String> os) {
		//获取算法执行根路径
		DictCode executePathDict = null;
		if("windows".equals(os.getContent())){
			 executePathDict = findOneselfDictCode("windows算法执行根路径");
		}else{
			 executePathDict = findOneselfDictCode("linux算法执行根路径");
		}
		String executePath = executePathDict.getDictCode();
		return executePath;
	}
	@Override
	public String getNodeSharePath(ReturnT<String> os) {
		DictCode sharePathDict = null;
		if("windows".equals(os.getContent())){
			sharePathDict = findOneselfDictCode("windows共享目录根路径");
		}else{
			sharePathDict = findOneselfDictCode("linux共享目录根路径");
		}
		String uploadPath = sharePathDict.getDictCode();
		return uploadPath;
	}
	@Override
	public String getMasterSharePath(String os) {
		DictCode sharePathDict = null;
		if(os.toLowerCase().startsWith("win")){  
			sharePathDict = findOneselfDictCode("windows共享目录根路径");
		}else{
			sharePathDict = findOneselfDictCode("linux共享目录根路径");
		}
		String uploadPath = sharePathDict.getDictCode();
		return uploadPath;
	}
	public List<CommonParameter> findEnd(int jobLogId) {
		return flowLogService.findEnd(jobLogId);
	}


	public List<ArchiveCatalog> findArchiveCatalogs(){
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
		int i=-1;
		i = archiveRulesService.saveArchiveRules(archiveRules);
		
		if(i>0) {
			String id= archiveRulesService.findArchiveRules(archiveRules);
			
			if(StringUtils.isNotBlank(id)) {
				i= archiveFiledMapService.saves(fileMapList,id,archiveRules.getCatalogcode());
			}
			
		}
		return i;
	}

	public List<ArchiveFiledManage> findAllArchiveFiledManages() {
		return archiveFiledManageService.findAll();
	}

	public List<ArchiveFiledMap> findArchiveFiledMap(String archiveRuleId) {
		return archiveFiledMapService.getByArchiveRuleId(archiveRuleId);
	}
	/* 
	 * 删除目录
	 */
	@Override
	public ReturnT<String> deleteTreeNodeArchiveCatalog(String id) {
		try {
			if(StringUtils.isNotBlank(id)) {
				ArchiveCatalog ac = archiveCatalogService.getById(id);
				if(null!=ac&&StringUtils.isNotBlank(ac.getCatalogCode())) {
					//archiveRulesService.getBySpecification(ac)
					List<ArchiveCatalog> allArchiveRules= archiveCatalogService.getByCatalogCode(ac.getCatalogCode());
					archiveCatalogService.deleteTreeNodes(allArchiveRules);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
			ArchiveRules archiveRules = archiveRulesService.getById(id);
			ReturnT<String> result= archiveFiledMapService.deltearchiveFiledMaps(id);
			if(result.getCode()==200) {
			archiveRulesService.delete(id);
			}else {
				return ReturnT.FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnT.FAIL;
		}
		return ReturnT.SUCCESS;
	}
	@Override
	public String systemParamList(int start, int length, String searchText, String id) {
		// TODO Auto-generated method stub
		return systemParamService.list(start,length,searchText,id);
	}
	
	@Override
	public void saveOrUpdateSystemParam(SystemParam systemParam) {
		// TODO Auto-generated method stub
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
	public Map<String, Object> pageListMeataImgAndInfo(int start, int length,  String catalogcode , Date fProducetimeStart, Date fProducetimeEnd ,String fLevel, Integer fCloudamount,String fSatelliteid) {
		// TODO Auto-generated method stub
		return meataImgAndInfoService.meataImgAndInfoService(start, length, catalogcode,  fProducetimeStart, fProducetimeEnd,fLevel,fCloudamount,fSatelliteid);
	}
	@Override
	public List<ProductInfo> findProductInfoListByIssueRange(String id, String startIssue, String endIssue) {
		return productInfoService.findProductInfoListByIssueRange(id, startIssue, endIssue);
	}
	@Override
	public ProductInfo saveProductInfo(ProductInfo productInfo) {
		return productInfoService.save(productInfo);
	}
	@Override
	public void deleteProductFileInfoByIssue(String deleteIssue, String id) {
		productFileInfoService.deleteByproductInfoId(id);
	}
	@Override
	public String diskList(int start, int length, String searchText) {
		// TODO Auto-generated method stub
		return diskService.diskList(start,length,searchText);
	}
	@Override
	public void saveOrUpdateDisk(Disk disk) {
		diskService.saveOrUpdateDisk(disk);
	}
	@Override
	public void delDisk(String id) {
		// TODO Auto-generated method stub
		diskService.del(id);
	}
	@Override
	public  String getIp(){
        return DubboIpUtil.getIp();
	}
	@Override
	public int enableOrdisable(String id, String rulestatus) {
		return archiveRulesService.enableOrdisable(id,rulestatus);
	}
	@Override
	public int updateDisk(String id, String archivdisk) {
		return archiveRulesService.updateDisk(id,archivdisk);
	}
	@Override
	public void updateRecycleflag(String mark,String id) {
		// TODO Auto-generated method stub
		meataImgAndInfoService.updateRecycleflag(mark,id);
	}
	@Override
	public ExcelDataPageInfo findDataByexcelName(String excelName, int pageNum, int pageSize,String[] params) {

		return dataManageService.findData(excelName,pageNum,pageSize,params);
	}

	@Override
	public List<ExcelElementInfoView> findAllElement() {
		
		List<ExcelElementInfo> elements = dataManageService.findAllElement();
		List<ExcelElementInfoView> elementList = new ArrayList<>();
		for (ExcelElementInfo excelElementInfo : elements) {
			ExcelElementInfoView element = new ExcelElementInfoView();
			
			String excelHeader = excelElementInfo.getExcelHeader();
			String[] excelHeaderArr = excelHeader.split(",");
			List<Map<String, String>> excelHeaderList = new ArrayList<>();
			
			for (String string : excelHeaderArr) {
				Map<String, String> excelHeaders = new HashMap<>();
				String[] split = string.split(":");
				excelHeaders.put("key",split[0].replace(" ", "").trim());
				excelHeaders.put("value", split[1].replace(" ", "").trim());
				excelHeaderList.add(excelHeaders);
			}
			
			element.setId(excelElementInfo.getId());
			element.setName(excelElementInfo.getName());
			element.setTableName(excelElementInfo.getTableName());
			element.setNickName(excelElementInfo.getTableChineseName());
			element.setExcelHeaders(excelHeaderList);
			elementList.add(element);
		}
		return elementList;
	}
	
	@Override
	public List<String> findStationByName(String name) {

		List<String> stations = dataManageService.findStationByName(name);
		return stations;
	}
}
