package com.htht.job.executor.hander.dataarchiving.handler.service;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.util.ResultUtil;
import com.htht.job.core.util.ReturnCodeEnum;
import com.htht.job.executor.hander.dataarchiving.handler.module.HandlerParam;
import com.htht.job.executor.hander.dataarchiving.util.ComputerConnUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.hander.dataarchiving.util.UrlUtil;
import com.htht.job.executor.hander.dataarchiving.util.webservice.client.ClientUtil;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.redis.RedisService;
import com.htht.job.executor.service.dms.ArchiveRulesService;
import com.htht.job.executor.service.dms.DiskService;

@Transactional
@Service("dataScanHandlerService")
public class DataScanHandlerService {
	@Autowired
	private RedisService redisService;
	@Autowired
	private DiskService diskService;
	@Autowired
	private ArchiveRulesService archiveRulesService;
	@Autowired
	private FileUtil fileUtil;
	@Value("${xxl.job.admin.addresses}")
	private String addresses;
	public ResultUtil<String> execute(TriggerParam triggerParam, ResultUtil<String> result) {
		try {
			// 获取可归档磁盘
			List<Disk> archiveDisks = diskService.getArchiveDisk();
			// 获取可用工作磁盘
			List<Disk> workdisks = diskService.getWorkDisk();
			if (archiveDisks.size() == 0 && workdisks.size() == 0) {
				result.setErrorMessage("暂无可用归档磁盘！！");
				return result;
			}
			// 所有可用扫描磁盘
			List<Disk> disks = diskService.getScanDisk();
			if (disks.size() == 0) {
				result.setErrorMessage("暂无可用扫描磁盘！！");
				return result;
			}

			// 文件过滤条件
			String[] extensions = new String[] { "" };
			Disk disk = null;
			Disk archiveDisk = null;
			// 入库规则
			List<ArchiveRules> rules = archiveRulesService.findAll();
			ArchiveRules rule = null;
			boolean flag;
			for (int i = 0; i < disks.size(); i++) {
				disk = disks.get(i);
				// 共享磁盘登录验证
				flag = ComputerConnUtil.login(disk.getLoginurl(), disk.getLoginname(), disk.getLoginpwd());
				if (flag || new File(disk.getLoginurl()).exists()) {
					// 遍历文件夹取到所有文件
					Iterator<File> iter = fileUtil.listFilesAndDir(disk.getLoginurl(), extensions);
					while (iter.hasNext()) {
						// 如果存在，则调用next实现迭代
						File j = (File) iter.next();
						//判断文件正在入库
						if(null != redisService.get("archive_"+j.getName())) {
							continue;
						}
						// 判断文件占用
						if (!j.renameTo(j)) {
							continue;
						}
						System.out.println(j.getAbsolutePath());
						for (int j2 = 0; j2 < rules.size(); j2++) {
							rule = rules.get(j2);
							// 根据正则匹配入库流程
							boolean isMatch = Pattern.matches(rule.getRegexpstr(), j.getName());
							if (isMatch) {
								/*20180831-弃用文件夹形式start*/
								//如果入库产品是文件夹类型
//								if(rule.getFiletype()==0) {
//									if(!new File(j.getAbsolutePath()+j.getAbsolutePath().substring(j.getAbsolutePath().lastIndexOf("\\"))+rule.getFinalstr()).exists()) {
//										continue;
//									}
//								}
								/*20180831-弃用文件夹形式end*/
								
								// 判断入库规则中是否绑定归档路径,如果没有绑定则使用最早创建并且可用的归档磁盘
								if (rule.getArchivdisk().equals("")) {
									archiveDisk = archiveDisks.get(0);
								} else {
									archiveDisk = diskService.getById(rule.getArchivdisk());
								}
								HandlerParam handlerParam = new HandlerParam();
								handlerParam.setBaseUrl(j.getAbsolutePath());
								handlerParam.setArchiveRules(rule);
								handlerParam.setBaseDiskInfo(disk);
								handlerParam.setArchiveDiskInfo(archiveDisk);
								handlerParam.setWorkDiskInfo(workdisks.get(0));
								
								String resultMessage = UrlUtil.getURLEncoderString(JSON.toJSONString(handlerParam));
								// 触发指定入库流程
								ClientUtil.interfaceUtil(addresses+"/api/achive/cp?jobId="+rule.getFlowid()+"&json="+resultMessage, "");
								redisService.set("archive_"+j.getName(), j.getName());
							}
						}
					}

				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			result.setErrorMessage(ReturnCodeEnum.ReturnCodeEnum_110_ERROR.getValue());
			throw new RuntimeException();
		}
		return result;
	}

	public static void main(String args[]) {
//		String content = "GF4_WFV3_E109.5_N108.3_20191119_L1A000125236.tar.gz";
//
//		String pattern = "^(GF)(\\d+)(_)(WFV)(\\d+)(_)(E)([+-]?\\d*\\.\\d+)(?![-+0-9\\.])(_)(N)([+-]?\\d*\\.\\d+)(?![-+0-9\\.])(_)((?:(?:[1]{1}\\d{1}\\d{1}\\d{1})|(?:[2]{1}\\d{3}))(?:[0]?[1-9]|[1][012])(?:(?:[0-2]?\\d{1})|(?:[3][01]{1})))(?![\\d])(_)(L)(1)(A)(\\d+)(\\.)(t)(a)(r)(\\.)(g)(z)$";
//
//		boolean isMatch = Pattern.matches(pattern, content);
//		System.out.println("GF*" + isMatch);
		Iterator<File> iter = new FileUtil().listFilesAndDir("E:\\09-work", new String[] { "" });
		while (iter.hasNext()) {
			// 如果存在，则调用next实现迭代
			File j = (File) iter.next();
			System.out.println(j.getName());
		}
	}

}
