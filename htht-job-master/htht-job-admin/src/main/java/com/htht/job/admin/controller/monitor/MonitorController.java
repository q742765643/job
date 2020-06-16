package com.htht.job.admin.controller.monitor;

import com.htht.job.admin.core.jobbean.MonitorJobInfoBean;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.core.util.PropertiesFileUtil;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.utilbean.AlgoManageEntity;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.registry.RegistryDTO;
import com.htht.job.executor.model.registryalgo.RegistryAlgoDTO;
import com.htht.job.vo.NodeMonitor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zzj on 2018/1/31.
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {
    private static Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private DubboService dubboService;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private CheckAliveService checkAliveService;
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

    private ExecutorService executor = Executors.newCachedThreadPool();


    @RequestMapping
    public String index(Model model) throws InterruptedException {
        List<NodeMonitor> list = dubboService.findAllMonitor();
        List<String> adressList = checkAliveService.checkAliveByMonitors(list);
        List<NodeMonitor> list1 = Collections.synchronizedList(new ArrayList<NodeMonitor>());
        final CountDownLatch latch = new CountDownLatch(list.size());
        for (NodeMonitor monitor : list) {
            executor.execute(()->{
                boolean flag = false;
                try {
                        String address = monitor.getIp();
                        for (String adressUse : adressList) {
                            if (adressUse.trim().equals(address.trim())) {
                                flag = true;
                                break;
                            }
                        }
                        monitor.setIsRun(1);
                        monitor.setCpuUsage(0L);
                        monitor.setMemoryUsage(0L);
                        monitor.setHardDiskUsage(0L);
                        if (flag) {
                            ExecutorBiz executorBiz = RealReference.getExecutorBiz(address);
                            ReturnT<NodeMonitor> returnT = executorBiz.getSystemMessage();
                            NodeMonitor nodeMonitor = returnT.getContent();
                            monitor.setCpuUsage(nodeMonitor.getCpuUsage());
                            monitor.setMemoryUsage(nodeMonitor.getMemoryUsage());
                            monitor.setHardDiskUsage(nodeMonitor.getHardDiskUsage());
                            monitor.setIsRun(0);
                        }

                    } catch (Exception e) {
                        logger.error(e.getMessage(),e);
                    } finally {

                        list1.add(monitor);
                        latch.countDown();
                    }
            });
        }

        try {
            latch.await();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        model.addAttribute("list", list1);
        return "/monitor/monitor.index";
    }

    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(RegistryDTO registryDTO) {
    	

        // valid
        if (null == registryDTO.getRegistryKey() || StringUtils.isBlank(registryDTO.getRegistryKey())) {
            return new ReturnT<>(500, "请输入节点名称");
        }
        if (null == registryDTO.getRegistryIp() || StringUtils.isBlank(registryDTO.getRegistryIp())) {
            return new ReturnT<>(500, "请输入节点IP");
        }
        registryDTO.setCreateTime(new Date());
        String ret = dubboService.saveRegistry(registryDTO);
        return (ret.length() > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(RegistryDTO registryDTO) {
        // valid
        if (null == registryDTO.getId() || StringUtils.isBlank(registryDTO.getId())) {
            return new ReturnT<>(500, "更新失败");
        }
        if (null == registryDTO.getRegistryKey() || StringUtils.isBlank(registryDTO.getRegistryKey())) {
            return new ReturnT<>(500, "请输入节点名称");
        }
        if (null == registryDTO.getRegistryIp() || StringUtils.isBlank(registryDTO.getRegistryIp())) {
            return new ReturnT<>(500, "请输入节点IP");
        }
        String ret = dubboService.updateRegistry(registryDTO);
        return (ret.length() > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(String id) {
        // valid
        if (null == id || StringUtils.isBlank(id)) {
            return new ReturnT<>(500, "删除失败");
        }
        String ret = dubboService.removeRegistry(id);
        //删除算法节点对应关系
        dubboService.delAlgoRegByRegId(id);
        return (ret.equals("success")) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/info")
    @ResponseBody
    public List info(String id) {
        //获取当前节点排队队列
        List<String> nodeLineQueue = dubboService.getNodeLineJobQueue(id);
        Map<XxlJobInfo, Integer> nodeLineQueueJob = new HashMap<>();
        for (String string : nodeLineQueue) {
            int count = Collections.frequency(nodeLineQueue, string);
            XxlJobInfo loadById = xxlJobInfoDao.loadById(Integer.parseInt(string));
            nodeLineQueueJob.put(loadById, count);
        }
        //获取当前节点执行队列
        Map<XxlJobInfo, Integer> nodeOperateQueueJob = new HashMap<>();
        List<String> nodeOperateQueue = dubboService.getNodeOperateJobQueue(id);
        for (String string : nodeOperateQueue) {
            int count = Collections.frequency(nodeOperateQueue, string);
            XxlJobInfo loadById = xxlJobInfoDao.loadById(Integer.parseInt(string));
            nodeOperateQueueJob.put(loadById, count);
        }

        ArrayList list = new ArrayList<>();
        Set<XxlJobInfo> lineSet = nodeLineQueueJob.keySet();
        for (XxlJobInfo xxlJobInfo : lineSet) {
            MonitorJobInfoBean monitorJobInfoBean = new MonitorJobInfoBean();
            if (nodeOperateQueueJob.containsKey(xxlJobInfo)) {
                monitorJobInfoBean.setXxlJobInfo(xxlJobInfo);
                monitorJobInfoBean.setLineNumber(nodeLineQueueJob.get(xxlJobInfo));
                monitorJobInfoBean.setOperateNumber(nodeOperateQueueJob.get(xxlJobInfo));
                list.add(monitorJobInfoBean);
            } else {
                monitorJobInfoBean.setXxlJobInfo(xxlJobInfo);
                monitorJobInfoBean.setLineNumber(nodeLineQueueJob.get(xxlJobInfo));
                list.add(monitorJobInfoBean);
            }
        }
        Set<XxlJobInfo> operateSet = nodeOperateQueueJob.keySet();
        for (XxlJobInfo xxlJobInfo : operateSet) {
            MonitorJobInfoBean monitorJobInfoBean = new MonitorJobInfoBean();
            if (!nodeLineQueueJob.containsKey(xxlJobInfo)) {
                monitorJobInfoBean.setXxlJobInfo(xxlJobInfo);
                monitorJobInfoBean.setOperateNumber(nodeOperateQueueJob.get(xxlJobInfo));
                list.add(monitorJobInfoBean);
            }
        }
        return list;
    }

    @ResponseBody
    @RequestMapping("/usage")
    public ReturnT<NodeMonitor> usage(String ip) {
        ExecutorBiz executorBiz = RealReference.getExecutorBiz(ip);
        ReturnT<NodeMonitor> returnT;
        returnT=executorBiz.getSystemMessage();
        return returnT;
    }

    @ResponseBody
    @RequestMapping("/findIpList")
    public List<Map> findIpList(Model model){
        List<NodeMonitor> list = dubboService.findAllMonitor();
        List<String> adressList = checkAliveService.checkAliveByMonitors(list);
        List<Map> mapList = new ArrayList<>();
        list.forEach(nodeMonitor -> {
            Map map = new HashMap();
            map.put("id", nodeMonitor.getIp());
            map.put("text", nodeMonitor.getIp());
            map.put("isRun","0");
            adressList.forEach(adress ->{
                if(nodeMonitor.getIp().trim().equals(adress.trim())){
                    map.put("isRun","1");
                }
            });
            mapList.add(map);
        });
        return mapList;
    }

	
	@ResponseBody
	@RequestMapping("/findAlgoManageList")
	public Map<String, Object> findAlgoManageList(@RequestParam(required = false, defaultValue = "0") int start,
                                         @RequestParam(required = false, defaultValue = "10") int length,
                                         String ipId,String treeId) {
        if (start != 0) {
            start = start / length;
        }
		AtomicAlgorithmDTO atomicAlgorithmDTO = new AtomicAlgorithmDTO();
		if(StringUtils.isNotEmpty(treeId)) {
			atomicAlgorithmDTO.setTreeId(treeId);
		}
		
		List<RegistryAlgoDTO> registryAlgoDTOList = dubboService.getRegistListByRegistryId(ipId);
		
		Map<String, Object> pageList = atomicAlgorithmService.pageList(start, length, atomicAlgorithmDTO);
		List<AtomicAlgorithmDTO> algoList = (List<AtomicAlgorithmDTO>) pageList.get("data");
		
	    ArrayList<AlgoManageEntity> algoManageEntityList = new ArrayList<>();
		for (AtomicAlgorithmDTO algo : algoList) {
			AlgoManageEntity algoManageEntity = new AlgoManageEntity();
			algoManageEntity.setAlgoId(algo.getId());
			algoManageEntity.setModelName(algo.getModelName());
			String algoPath = algo.getAlgoPath();
			if(StringUtils.isNotEmpty(algoPath)) {
				algoPath = algoPath.replace("\\", "/");
				String algoZipName = algoPath.substring(algoPath.lastIndexOf('/')+1);
				algoManageEntity.setAlgoZipName(algoZipName);
			}
			
			for (RegistryAlgoDTO registryAlgoDTO : registryAlgoDTOList) {
				if(registryAlgoDTO.getAlgoId().equals(algo.getId())) {
					algoManageEntity.setIsMapping(true);
				}
			}
			
			algoManageEntityList.add(algoManageEntity);
		}
		pageList.put("data",algoManageEntityList);
		return pageList;
	}
		
	@ResponseBody
	@RequestMapping("/saveAlgoManageList")
	public ReturnT<String> saveAlgoManageList(@RequestBody  List<AlgoManageEntity> algoManageList) throws InterruptedException {
		//映射绑定
		for (AlgoManageEntity algoManageEntity : algoManageList) {
			if(algoManageEntity.getIsMapping()) {
				dubboService.deleteRegistryAlgoByRegistryAlgo(algoManageEntity.getRegistryId(), algoManageEntity.getAlgoId());
				RegistryAlgoDTO registryAlgo = new RegistryAlgoDTO();
				Date date = new Date();
				registryAlgo.setCreateTime(date);
				registryAlgo.setAlgoId(algoManageEntity.getAlgoId());
				registryAlgo.setRegistryId(algoManageEntity.getRegistryId());
				dubboService.saveRegistryAlgo(registryAlgo);
			}else {
				dubboService.deleteRegistryAlgoByRegistryAlgo(algoManageEntity.getRegistryId(), algoManageEntity.getAlgoId());
			}
			
		}
		//多线程下载
		ArrayList<ReturnT<String>> arrayList = new ArrayList<>();
        // 开始的倒数锁
        final CountDownLatch begin = new CountDownLatch(1);
        // 结束的倒数锁
        final CountDownLatch end = new CountDownLatch(algoManageList.size());
        // 线程池个数
        final ExecutorService exec = Executors.newFixedThreadPool(algoManageList.size());
        for (AlgoManageEntity algoManageEntity : algoManageList) {
         	Runnable run = ()->{
					try {
						begin.await();
						if(algoManageEntity.getIsDownload()) {
							AtomicAlgorithmDTO algo = atomicAlgorithmService.findParameterById(algoManageEntity.getAlgoId());
							String algoPath = algo.getAlgoPath();
							if(StringUtils.isNotEmpty(algoPath)) {
								RegistryDTO registry = dubboService.findRegistryById(algoManageEntity.getRegistryId());
							    String address = registry.getRegistryIp();
                                ExecutorBiz executorBiz = RealReference.getExecutorBiz(address);
                                ReturnT<String> os = executorBiz.getOs();
                                //获取算法上传根路径
                                String uploadPath = dubboService.getNodeSharePath(os);
                                String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
                                String uploadPathNow = uploadPath + uploadSuffix;
                                //上传文件名
                                File algoFile = new File(algoPath);
                                String filename = algoFile.getName();
                                String executePath = dubboService.getExePath(os);
                                ReturnT<String> runResult = executorBiz.deployAlgo(uploadPathNow, filename, executePath);
                                arrayList.add(runResult);
							}
						}
					} catch (Exception e) {
					    logger.error(e.getMessage(),e);
					}finally {
						end.countDown();
					}
			};
			exec.submit(run);
		}
        //开始
        begin.countDown();
        //等待线程结束
        end.await();
        //关闭线程池
        exec.shutdown();
		return new ReturnT<>("部署成功");
	}
}
