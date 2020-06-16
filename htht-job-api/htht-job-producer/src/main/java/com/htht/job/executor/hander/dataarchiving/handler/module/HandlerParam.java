package com.htht.job.executor.hander.dataarchiving.handler.module;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.htht.job.core.util.DateUtil;
import com.htht.job.executor.hander.dataarchiving.util.FileUtil;
import com.htht.job.executor.model.dms.module.ArchiveRules;
import com.htht.job.executor.model.dms.module.Disk;
import com.htht.job.executor.model.dms.module.MetaInfo;

public class HandlerParam {
	FileUtil fileUtil = new FileUtil();
	private String baseUrl;// 数据原始路径
	private ArchiveRules archiveRules;//入库规则--正则表达试
	private Disk baseDiskInfo;// 源数据磁盘信息
	private Disk archiveDiskInfo;// 归档磁盘信息
	private Disk imgDiskInfo;// 图片服务磁盘信息
	private Disk workDiskInfo;// 数据处理磁盘信息
	/* 物理信息表信息 */
	private MetaInfo metaInfo;
	/* 影像信息表信息 */
	private Map<String, String> archiveMap;
	
	/*备份数据dataid*/
	private String backupDataid;
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	
	public ArchiveRules getArchiveRules() {
		return archiveRules;
	}

	public void setArchiveRules(ArchiveRules archiveRules) {
		this.archiveRules = archiveRules;
	}

	public MetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(MetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public Disk getBaseDiskInfo() {
		return baseDiskInfo;
	}

	public void setBaseDiskInfo(Disk baseDiskInfo) {
		this.baseDiskInfo = baseDiskInfo;
	}

	public Disk getArchiveDiskInfo() {
		return archiveDiskInfo;
	}

	public void setArchiveDiskInfo(Disk archiveDiskInfo) {
		this.archiveDiskInfo = archiveDiskInfo;
	}

	public Map<String, String> getArchiveMap() {
		return archiveMap;
	}

	public void setArchiveMap(Map<String, String> archiveMap) {
		this.archiveMap = archiveMap;
	}

	public Disk getImgDiskInfo() {
		return imgDiskInfo;
	}

	public void setImgDiskInfo(Disk imgDiskInfo) {
		this.imgDiskInfo = imgDiskInfo;
	}

	public Disk getWorkDiskInfo() {
		return workDiskInfo;
	}

	public void setWorkDiskInfo(Disk workDiskInfo) {
		this.workDiskInfo = workDiskInfo;
	}
	
	public String getBackupDataid() {
		return backupDataid;
	}

	public void setBackupDataid(String backupDataid) {
		this.backupDataid = backupDataid;
	}

	/**
	 * 图片去黑边后数据的保存地址
	 * 
	 * @return
	 */
	public String getDelBlackPath() {
		try {
			return (imgDiskInfo.getLoginurl() + "/" 
					+ archiveMap.get("F_SATELLITEID") + "/"
					+ archiveMap.get("F_SENSORID") + "/" 
					+ archiveRules.getDatalevel() + "/"
					+ getDataTime() + "/" 
					+ (fileUtil.getFileNameWithoutSuffix(new File(baseUrl)))+ ".jpg").replaceAll("'", "");
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * 图片去黑边后数据的保存地址
	 * 文件夹类产品
	 * @return
	 */
//	public String getDelBlackDirPath() {
//		try {
//			return (imgDiskInfo.getLoginurl() + "/" 
//					+ archiveMap.get("F_SATELLITEID") + "/"
//					+ archiveMap.get("F_SENSORID") + "/" 
//					+ archiveRules.getDatalevel() + "/"
//					+ getDataTime() + "/" 
//					+ new File(baseUrl).getName()+ ".jpg").replaceAll("'", "");
//		} catch (Exception e) {
//			return "";
//		}
//	}
	
	/**
	 * 图片去黑边后数据的保存地址--没有IP的路径
	 * 
	 * @return
	 */
	public String getDelBlackPathNoIP() {
		try {
			return  (archiveMap.get("F_SATELLITEID") + "/"
					+ archiveMap.get("F_SENSORID") + "/" 
					+ archiveRules.getDatalevel() + "/"
					+ getDataTime() + "/" 
					+ (fileUtil.getFileNameWithoutSuffix(new File(baseUrl)))+ ".jpg").replaceAll("'", "");
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 归档逻辑目录
	 * @return
	 */
	public String getArchivePath() {
		try {
			return  (archiveMap.get("F_SATELLITEID") + "/"
					+ archiveMap.get("F_SENSORID") + "/" 
					+ archiveRules.getDatalevel() + "/"
					+ getDataTime() + "/").replaceAll("'", "") ;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 获取数据解压路径
	 * 数据处理工作空间--解压、解析、处理
	 * @return
	 */
	public String getWorkSpacePath() {
		try {
//		return (fileUtil.getArchivePath() + "/" + (fileUtil.getFileNameWithoutSuffix(new File(baseUrl)))).replaceAll("'", "");
			return (workDiskInfo.getLoginurl() + "/" + (fileUtil.getFileNameWithoutSuffix(new File(baseUrl)))).replaceAll("'", "");
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * 获取数据路径--文件夹类型产品
	 * 数据处理工作空间--解压、解析、处理
	 * @return
	 */
//	public String getWorkSpaceDir() {
//		try {
////		return (fileUtil.getArchivePath() + "/" + (fileUtil.getFileNameWithoutSuffix(new File(baseUrl)))).replaceAll("'", "");
//			return (workDiskInfo.getLoginurl() + "/" + new File(baseUrl).getName()).replaceAll("'", "");
//		} catch (Exception e) {
//			return "";
//		}
//	}
	
	/**
	 * 清理工作空间数据
	 */
	public void deleteWorkSpaceFile() {
		String workSpacePath = getWorkSpacePath();
		String fileName = fileUtil.getFileNameWithoutSuffix(new File(baseUrl));
		File[] f = new File(workSpacePath).getParentFile().listFiles();
		if(null!=f){
			for (int i = 0; i < f.length; i++) {
				if(f[i].getName().equals(fileName)) {
					FileUtils.deleteQuietly(f[i]);
				}
			}
		}
	}
	
	/**
	 * 获取数据时间--用于文件夹创建
	 */
	private String getDataTime() {
		String dataTime = "null";
		if(null != archiveMap.get("F_PRODUCETIME")) {
			try {
				dataTime = DateUtil.formatDateTime(DateUtil.strToDate(archiveMap.get("F_PRODUCETIME"),"yyyyMMddhhmmss"), "yyyy/MM/dd");
//				dataTime = DateUtil.formatDateTime(DateUtil.strToDate(archiveMap.get("F_PRODUCETIME")), "yyyy/MM/dd");
			} catch (Exception e) {
				dataTime = "errorTime";
			}
		} 
		return dataTime;
	}
}
