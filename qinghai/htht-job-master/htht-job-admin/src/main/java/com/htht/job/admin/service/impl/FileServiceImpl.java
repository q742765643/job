package com.htht.job.admin.service.impl;

import com.htht.job.admin.core.util.PropertiesUtil;
import com.htht.job.admin.service.FileService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.utilbean.DateUtil;
import com.htht.job.core.utilbean.FileParam;
import com.htht.job.core.utilbean.RequestMessage;
import com.htht.job.core.utilbean.SystemConstants;
import com.htht.job.core.utilbean.UploadAlgoEntity;
import com.htht.job.executor.model.dictionary.DictCode;
import com.mysql.jdbc.StringUtils;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Service
public class FileServiceImpl implements FileService {
    @Resource
    private DubboService dubboService;
	
	@Override
	public List<FileParam> getRootFolderList() throws Exception {
		List<FileParam> rootFile = new ArrayList<FileParam>();
		// 获取共享目录和私有目录
		ArrayList<String> rootFileDir = new ArrayList<String>();
//		String string = PropertiesUtil.getString("htht.job.sharePath");
//		System.out.println(string);
        //获取共享目录根路径
		DictCode sharePathDict= null;
		String os = System.getProperty("os.name");  
		String sharePath = dubboService.getMasterSharePath(os);
		rootFileDir.add(sharePath);
	//	rootFileDir.add(ConfigProperties.PRIVATE_DIRECTORY);
	//	rootFileDir.add(ConfigProperties.PROJECT_PATH);
		FileParam fileDir = null;
		for (int i = 0; i < rootFileDir.size(); i++) {
			// 如果此目录不存在就创建
			File f = new File(rootFileDir.get(i));
			if (!new File(rootFileDir.get(i)).exists()) {
				f.mkdirs();
			}
			fileDir = getFileParam(f);
			rootFile.add(fileDir);
		}
		return rootFile;
	}

	
	/**
	 * 设置文件属性
	 * 
	 * @param file
	 * @return
	 */
	private FileParam getFileParam(File file) {
		FileParam param = new FileParam();
		param.setFile(file.isFile());
		String showPath = file.getPath();
		param.setPath(showPath);
		String lastUpdateDate = DateUtil.getDateTimeString(file.lastModified());
		param.setLastUpdateDate(lastUpdateDate);
        //获取共享目录根路径
		DictCode sharePathDict= null;
		String os = System.getProperty("os.name");  
		String PUBLIC_DIRECTORY = dubboService.getMasterSharePath(os);
		if (file.getPath().startsWith(PUBLIC_DIRECTORY)) {
			param.setDataSource(SystemConstants.FileTreeType.PUB.getKey());
			if (file.getPath().equals(PUBLIC_DIRECTORY)) {
				param.setName("共享目录");
			} else {
				param.setName(file.getName());
			}
			String publicDirPath = PUBLIC_DIRECTORY;
			if(publicDirPath .endsWith("\\") || publicDirPath.endsWith("/") ){
				publicDirPath = PUBLIC_DIRECTORY.substring(0,PUBLIC_DIRECTORY.length()-1);
			}
			
			showPath = showPath.replace(publicDirPath,"共享目录");
			param.setShowPath(showPath);
			param.setRootPath(PUBLIC_DIRECTORY);
		}else {
			param.setDataSource(SystemConstants.FileTreeType.DATA.getKey());
			param.setName(file.getName());
			/*********** 可能变动 **************/
			param.setShowPath(showPath);
			param.setRootPath(file.getPath());
			/*********** 可能变动 **************/
		}
		param.setSize(file.length());
		return param;
	}

	@Override
	public List<FileParam> listFilesByFolder(RequestMessage request) {
		List<FileParam> files = new ArrayList<FileParam>();
		if (request.getPathList() != null && request.getPathList().size() != 0) {
			File folderFile = new File(request.getPathList().get(0));
			File[] filesArray = folderFile.listFiles();

			FileParam fParam = null;
			for (File file : filesArray) {
				fParam = getFileParam(file);
				files.add(fParam);
			}
		}
		return files;
	}
	
	public static void main(String[]args){
		FileServiceImpl aa = new FileServiceImpl();
		List<FileParam> files = new ArrayList<FileParam>();
		File folderFile = new File(PropertiesUtil.getString("htht.job.sharePath"));
		File[] filesArray = folderFile.listFiles();
		FileParam fParam = null;
		for (File file : filesArray) {
			fParam = aa.getFileParam(file);
			files.add(fParam);
		}
	}
}
