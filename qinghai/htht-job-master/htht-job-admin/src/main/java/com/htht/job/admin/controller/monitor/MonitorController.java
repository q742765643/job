package com.htht.job.admin.controller.monitor;

import com.htht.job.admin.core.jobbean.MonitorJobInfoBean;
import com.htht.job.admin.core.model.XxlJobInfo;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.dao.XxlJobInfoDao;
import com.htht.job.admin.service.CheckAliveService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.vo.NodeMonitor;
import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import org.apache.commons.lang.StringUtils;
import org.hyperic.sigar.Sigar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
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
    @Autowired
    private DubboService dubboService;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	private CheckAliveService checkAliveService;

	private  ExecutorService executor = Executors.newCachedThreadPool();


	@RequestMapping
    public String index(Model model) throws InterruptedException {
        List<NodeMonitor> list=dubboService.findAllMonitor();
        List<String> adressList=checkAliveService.checkAliveByMonitors(list);
        System.out.println(adressList);
		List<NodeMonitor> list1=Collections.synchronizedList(new ArrayList<NodeMonitor>());
		final  CountDownLatch latch = new CountDownLatch(list.size());
		for(NodeMonitor monitor:list){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String address=monitor.getIp();
						boolean flag=false;
						for(String adressUse:adressList){
							if(adressUse.trim().equals(address.trim())){
								flag=true;
								break;
							}
						}
						if(flag){
							ExecutorBiz executorBiz = RealReference.getExecutorBiz(address);
							ReturnT<NodeMonitor> returnT=executorBiz.getSystemMessage();
							NodeMonitor nodeMonitor=returnT.getContent();
							monitor.setCpuUsage(nodeMonitor.getCpuUsage());
							monitor.setMemoryUsage(nodeMonitor.getMemoryUsage());
							monitor.setHardDiskUsage(nodeMonitor.getHardDiskUsage());
							monitor.setIsRun(0);
						}else{
							monitor.setIsRun(1);
							monitor.setCpuUsage(0L);
							monitor.setMemoryUsage(0L);
							monitor.setHardDiskUsage(0L);


						}
					}catch (Exception e) {
						monitor.setIsRun(1);
						monitor.setCpuUsage(0L);
						monitor.setMemoryUsage(0L);
						monitor.setHardDiskUsage(0L);
					}finally {
						list1.add(monitor);
						latch.countDown();
					}

				}
			});
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		model.addAttribute("list",list1);
        return  "/monitor/monitor.index";
    }
    
	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(Registry registry){

		// valid
		if(null==registry.getRegistryKey() || StringUtils.isBlank(registry.getRegistryKey())) {
			return new ReturnT<String>(500, "请输入节点名称");
		}
		if(null==registry.getRegistryIp() || StringUtils.isBlank(registry.getRegistryIp())){
			return new ReturnT<String>(500, "请输入节点IP");
		}
		registry.setCreateTime(new Date());
		String ret = dubboService.saveRegistry(registry);
//		int ret = xxlJobGroupDao.save(xxlJobGroup);
	//	int ret = 0;
		return (ret.length()>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(Registry registry){
		// valid
		if (null == registry.getId()|| StringUtils.isBlank(registry.getId())) {
			return new ReturnT<String>(500, "更新失败");
		}
		if(null==registry.getRegistryKey() || StringUtils.isBlank(registry.getRegistryKey())) {
			return new ReturnT<String>(500, "请输入节点名称");
		}
		if(null==registry.getRegistryIp() || StringUtils.isBlank(registry.getRegistryIp())){
			return new ReturnT<String>(500, "请输入节点IP");
		}
		String ret = dubboService.updateRegistry(registry);
		return (ret.length()>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(String id){
		// valid
		if (null == id|| StringUtils.isBlank(id)) {
			return new ReturnT<String>(500, "删除失败");
		}
		String ret = dubboService.removeRegistry(id);
		//删除算法节点对应关系
		dubboService.delAlgoRegByRegId(id);
		return (ret.equals("success"))?ReturnT.SUCCESS:ReturnT.FAIL;
	}
	
	@RequestMapping("/info")
	@ResponseBody
	public List info(String id){
		//获取当前节点排队队列
		List<String> nodeLineQueue =dubboService.getNodeLineJobQueue(id);
		Map<XxlJobInfo,Integer> nodeLineQueueJob =new HashMap<>();
		for (String string : nodeLineQueue) {
			int count = Collections.frequency(nodeLineQueue, string);
			XxlJobInfo loadById = xxlJobInfoDao.loadById(Integer.parseInt(string));
			nodeLineQueueJob.put(loadById, count);
		}
		//获取当前节点执行队列
		Map<XxlJobInfo,Integer> nodeOperateQueueJob =new HashMap<>();
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
			if(nodeOperateQueueJob.containsKey(xxlJobInfo)){
				monitorJobInfoBean.setXxlJobInfo(xxlJobInfo);
				monitorJobInfoBean.setLineNumber(nodeLineQueueJob.get(xxlJobInfo));
				monitorJobInfoBean.setOperateNumber(nodeOperateQueueJob.get(xxlJobInfo));
				list.add(monitorJobInfoBean);
			}else{
				monitorJobInfoBean.setXxlJobInfo(xxlJobInfo);
				monitorJobInfoBean.setLineNumber(nodeLineQueueJob.get(xxlJobInfo));
				list.add(monitorJobInfoBean);
			}
		}
		Set<XxlJobInfo> operateSet = nodeOperateQueueJob.keySet();
		for (XxlJobInfo xxlJobInfo : operateSet) {
			MonitorJobInfoBean monitorJobInfoBean = new MonitorJobInfoBean();
			if(!nodeLineQueueJob.containsKey(xxlJobInfo)){
				monitorJobInfoBean.setXxlJobInfo(xxlJobInfo);
				monitorJobInfoBean.setOperateNumber(nodeOperateQueueJob.get(xxlJobInfo));
				list.add(monitorJobInfoBean);
			}
		}
		return list;
	}
	@ResponseBody
	@RequestMapping("/usage")
	public ReturnT<NodeMonitor>  usage(String ip) throws Exception {
		ExecutorBiz executorBiz = RealReference.getExecutorBiz(ip);
		ReturnT<NodeMonitor> returnT=executorBiz.getSystemMessage();
		return returnT;
	}
	@ResponseBody
	@RequestMapping("/findIpList")
	public List<Map>  findIpList(Model model) throws Exception {
		List<NodeMonitor> list=dubboService.findAllMonitor();
		List<Map> mapList=new ArrayList<>();
		list.forEach(nodeMonitor -> {
			Map map=new HashMap();
			map.put("id",nodeMonitor.getIp());
			map.put("text",nodeMonitor.getIp());
			mapList.add(map);
		});
		return mapList;
	}

}
