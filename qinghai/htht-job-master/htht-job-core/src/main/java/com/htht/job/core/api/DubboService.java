package com.htht.job.core.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.datacategory.ZtreeView;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.dms.module.ArchiveCatalog;
import com.htht.job.executor.model.dms.module.ArchiveFiledManage;
import com.htht.job.executor.model.dms.module.ArchiveFiledMap;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.SystemParam;
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
import com.htht.job.vo.NodeMonitor;

/**
 * Created by zzj on 2018/1/2.
 */
public interface DubboService {
	ResultUtil<List<String>> execute(String params, String handler, LinkedHashMap fixmap,
									 LinkedHashMap dymap);

	Map<String, Object> pageListProduct(int start, int length, Product product);

	Product saveProduct(Product product);

	ReturnT<String> deleteProduct(java.lang.String id);

	ReturnT<String> deleteProducByTree(java.lang.String id);

	List<Product> findALlProduct();

	Map<String, Object> pageListProductFileInfo(int start, int length,
			ProductFileInfo productFileInfo);

	Map<String, Object> pageListProductFileInfos(int start, int length, String id);

    Map<String, Object> pageListProductInfo(int start, int length, String productId);

	ProductFileInfo saveProductFileInfo(ProductFileInfo productFileInfo);

	void deleteProductFileInfo(String id);

	void deleteProductInfo(String id);
	
	ProductInfo findProductInfoById(String id);

	String findSelectFtp();

	Page<Ftp> getFtpsByPage(Pageable pageable);

	boolean testConnectFtp(String ip, int port, String userName, String pwd);

	Ftp saveFtp(Ftp ftp);

	Ftp getById(String id);

	int updeat(Ftp ftp);

	int del(String id);

	List<NodeMonitor> findAllMonitor();

	List<FileInfo> findFileInfoByWhere(String id);

	FlowChartModel saveOrUpdateFlow(FlowChartModel flowChartModel);
	
	List<Registry> findAllRegistry();
	
	RegistryAlgo saveRegistryAlgo(RegistryAlgo registryAlgo);

	List<RegistryAlgo> getRegistListByAlgoId(String id);

	void deleteRegistryAlgoByAlgoId(String id);

	List<FlowChartModel>  findFlowList();

	List<CommonParameter> parseFlowXmlParameter(String id);

	FlowChartModel getFlowById(String id);


	List<ProcessSteps> findFlowCeaselesslyList(ProcessSteps processSteps);

	FlowLog saveFlowLog(FlowLog flowLog);

	FlowLog findByFlowLogId(String flowLogId);

	long  getFlowLogCount(String nextId,int jobLogId);

	FlowLog findByJobLogIdAndDataIdAndParentFlowlogId(int jobLogId,String dataId,String panrentFlowlogId);

	Map<String, Object> pageListFlow(int start, int length,
											FlowChartModel flowChartModel);
	List<FlowLog> findFlowLogList(int jobLogId,String parentFlowlogId);

	Registry findRegistryById(String id);

	ParallelLog saveParallelLog(ParallelLog parallelLog);

	List<ParallelLog> findParallelLogList(ParallelLog parallelLog);

	ParallelLog findParallelLogById(String id);
	Boolean existSimpleKey(String simpleKey);

	boolean setRedisSimple(String unsimpleKey, String jobId);

	String saveRegistry(Registry registry);

	String updateRegistry(Registry registry);

	String removeRegistry(String id);

	ArrayList<String> findAddressList(String id,int dealAmount);

	ReturnT<String> deleteFlow(String id);

	List<String> getNodeLineJobQueue(String id);

	List<String> getNodeOperateJobQueue(String id);
	
	void hmPut(String key, String hashKey, String value);
	
	List<Object> getBadNodeJobQueue(String ip);
	
	void delAll(String ip);
	
	void removeBadNodeJobQueue(String ip,String hashKey);

	void deleteByParameterId(String jobparameterId);

	void delAlgoRegByRegId(String id);

	List<ProcessSteps> findStartOrEndFlowCeaselesslyList(ProcessSteps processSteps);

	ReturnT<String> deleteFlowLog(String id);


	Map findParallelLogPage(int logId, int start, int length,int logStatus);

	public  void  lPush(String k,String v);

	public  List<Mapping>  mapping(String id);

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
	 * @param dictCode
	 */
	void saveOrUpdateDicCode(DictCode dictCode);

	/**
	 * @param id
	 */
	void deleteDicCode(String id);

	public List<MatchRelation> saveMatchRelation(List<MatchRelation>  matchRelations);
	
	List<DictCode> findChildrenDictCode(String string);
	
	DictCode findOneselfDictCode(String string);
	
	public String getExePath(ReturnT<String> os);
	public String getNodeSharePath(ReturnT<String> os);
	public String getMasterSharePath(String os);
	/**
	 * @return
	 */
	//List<DictCode> findALlExecutionStrategy();

	/**
	 * @return
	 */
	//List<DictCode> findALlModelIdentification();

	void deleteParallelLogAndFlowLog(int jobLodId);

	MatchRelation findMatchRelationByJobIdAndDataId(int jobId,String dataId);

	List<CommonParameter> findEnd(int jobLogId);

	List<CimissDataInfo> getCimissData(String dataType);
	SystemLog saveSystemLog(SystemLog systemLog);

	Page<SystemLog> getSystemLogsByPage(Pageable pageable);
	Page<SystemLog> getSystemLogsByCategory(String category ,Pageable pageable);

	int deleteSystemLog(String id);

	List<AlgorithmRelationInfo>  queryAlgo(String treeid);

	AlgorithmRelationInfo  saveRelation(AlgorithmRelationInfo algorithmRelationInfo);

	ReturnT<String> deleteRelation(String treeId,String lgoid);

	Product findByTreeId(String treeid);
	
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

	int saveArchiveRules(ArchiveRules archiveRules,List<ArchiveFiledMap>fileMapList);

	/**
	 * @return
	 */
	List<ArchiveFiledManage> findAllArchiveFiledManages();

	/**
	 * @param archive_rule_id
	 * @return
	 */
	List<ArchiveFiledMap> findArchiveFiledMap(String archive_rule_id);

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
	
	List<ProductInfo> findProductInfoListByIssueRange(String id,
			String startIssue, String endIssue);

	ProductInfo saveProductInfo(ProductInfo productInfo);

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
	
	/**
	 *  查询表格数据
	 * @param excelName
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	ExcelDataPageInfo findDataByexcelName(String excelName, int pageNum, int pageSize,String[] params);
	
	/**
	 * 查找所有元素
	 * @return List<ExcelElementInfoView>
	 */
	List<ExcelElementInfoView> findAllElement();
	
	/**
	 * 根据元素名查找站点信息
	 * @param name 元素名简称
	 * @return
	 */
	List<String> findStationByName(String name);
	
//	/**
//	 * 导出数据
//	 * @param tableName
//	 * @return
//	 */
//	public ExcelDataTemp getExcelDataListByXml(String tableName);
}
