package com.htht.job.admin.service.impl;

import com.htht.job.admin.core.model.FileParam;
import com.htht.job.admin.service.FileResourceManager;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.utilbean.UploadAlgoEntity;
import com.htht.job.executor.model.dictionary.DictCode;
import com.mysql.jdbc.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * 本地读取文件 资源管理器实现类
 *
 * @author xlj
 */
@Component
public class LocalFileResourceManager implements FileResourceManager {
	
	@Resource
    public DubboService dubboService;
    //private static final String NEW_DIR_NAME = "新建文件夹";
    private static final String NEW_DIR_NAME = "new folder";

    public static void main(String[] args) {
        File file = new File("W:/");
        LocalFileResourceManager manager = new LocalFileResourceManager();
        manager.getChildrenFiles(file.getPath());

        System.out.println(file.isDirectory());
    }

    @Override
    public FileParam createNewDirectory(String parent, Integer dataSource) {
        File file = new File(parent);
        File newDir = null;
        String newDirName = null;
        if (file.exists() && file.isDirectory()) {
            int i = 0;
            while (true) {
                newDirName = i == 0 ? NEW_DIR_NAME : NEW_DIR_NAME + i;
                newDir = new File(parent, newDirName);
                if (!newDir.exists()) {
                    newDir.mkdirs();
                    break;
                }
                i++;
            }
            FileParam param = buildFileParam(newDir);
            return param;
        }
        return null;
    }

    @Override
    public FileParam renameDirectory(String oldPath, String newName, Integer dataSource) {
        File file = new File(oldPath);
        File newFile = null;
        if (file.exists() && file.isDirectory()) {
            newFile = new File(file.getParentFile(), newName);
            if (file.renameTo(newFile)) {
                return buildFileParam(newFile);
            }
        }
        return null;
    }

    @Override
    public boolean deleteFile(String path, Integer dataSource) {
        File file = new File(path);
        boolean flag = false;
        if (file.exists() && file.isDirectory()) {
            flag = file.delete();
        }
        return flag;
    }

    @Override
    public List<FileParam> getShareRootFiles() {
        List<FileParam> params = new ArrayList<FileParam>();
        //获取共享目录根路径
		DictCode sharePathDict= null;
		String os = System.getProperty("os.name");  
		String sharePath = dubboService.getMasterSharePath(os);
        String publicDirName = "共享目录";
        FileParam param = buildFileParam(sharePath);
        param.setName(publicDirName);
        param.setParent(true);
        params.add(param);
        return params;
    }

    public List<FileParam> getRootFiles() {
        List<FileParam> params = new ArrayList<FileParam>();
        //获取共享目录根路径
		DictCode sharePathDict= null;
		String os = System.getProperty("os.name");  
		String sharePath = dubboService.getMasterSharePath(os);
        String publicDirName = "共享目录";
        FileParam param = buildFileParam(sharePath);
        param.setName(publicDirName);
        param.setParent(true);
        params.add(param);
        return params;
    }

    public List<FileParam> getChildrenFiles(String pid) {
        File file = new File(pid);
        File[] roots = file.listFiles();
        List<FileParam> params = new ArrayList<FileParam>();
        if (roots != null) {
            for (int i = 0; i < roots.length; i++) {
                File child = roots[i];
                FileParam childItem = buildFileParam(child);
                params.add(childItem);
            }
        }
        return params;
    }
    
	private List<FileParam> getChildrenFiles2(String pid) {
		File file = new File(pid);
        File[] roots = file.listFiles();
        List<FileParam> params = new ArrayList<FileParam>();
        if (roots != null) {
            for (int i = 0; i < roots.length; i++) {
            	if(roots[i].isDirectory()) {
            		if(roots[i].listFiles().length>0) {
            			File child = roots[i];
                        FileParam childItem = buildFileParam(child);
                        params.add(childItem);
            		}
            	}else {
            		String name = roots[i].getName();
        			String upperCaseName = name.toUpperCase();
        			//upperCaseName.contains("PAN") &&
        			if (upperCaseName.endsWith(".TIFF")) {
        				File child = roots[i];
                        FileParam childItem = buildFileParam(child);
                        params.add(childItem);
        			}
            	}
            	
                
            }
        }
        return params;
	}
	
	/**
	 * @param getpId
	 * @return
	 */
	private List<FileParam> getChildrenFiles3(String pid) {
		File file = new File(pid);
        File[] roots = file.listFiles();
        List<FileParam> params = new ArrayList<FileParam>();
        if (roots != null) {
            for (int i = 0; i < roots.length; i++) {
            	if(roots[i].isDirectory()) {
            		if(roots[i].listFiles().length>0) {
            			File child = roots[i];
                        FileParam childItem = buildFileParam(child);
                        params.add(childItem);
            		}
            	}else {
            		String name = roots[i].getName();
        			String upperCaseName = name.toUpperCase();
        			//upperCaseName.contains("MSS") &&
        			if (upperCaseName.endsWith(".TIFF")) {
        				File child = roots[i];
                        FileParam childItem = buildFileParam(child);
                        params.add(childItem);
        			}
            	}
            	
                
            }
        }
        return params;
	}
	
	/**
	 * @param getpId
	 * @return
	 */
	private List<FileParam> getChildrenFiles4(String pid) {
		File file = new File(pid);
		File[] roots = file.listFiles();
		List<FileParam> params = new ArrayList<FileParam>();
		if (roots != null) {
			for (int i = 0; i < roots.length; i++) {
				if(roots[i].isDirectory()) {
					if(roots[i].listFiles().length>0) {
						File child = roots[i];
						FileParam childItem = buildFileParam(child);
						params.add(childItem);
					}
				}else {
					String name = roots[i].getName();
					String upperCaseName = name.toUpperCase();
					if (upperCaseName.endsWith(".IMG")) {
						File child = roots[i];
						FileParam childItem = buildFileParam(child);
						params.add(childItem);
					}
				}
				
				
			}
		}
		return params;
	}
	

	


    private FileParam buildFileParam(File file) {
        FileParam param = new FileParam();
        param.setFile(file.isFile());
        param.setParent(file.isDirectory() && file.list().length > 0);
        param.setIdAbsolutePath(file.getAbsolutePath());
        param.setName(file.getName());
        param.setPath(file.getAbsolutePath());
        param.setpId(file.getParent());
        param.setDataSource(getFileDataSource(param.getIdAbsolutePath()));
        return param;
    }

    /**
     * 查看文件路径是，属于公共目录还是私有目录
     * * @return
     */
    public int getFileDataSource(String newPath) {
//		String publicDir = ConfigUtil.getProperty("resource","public_directory");
//		String privateDir = this.getPrivateDirectory();
        int fileType = 0;
//		if(newPath.indexOf(publicDir) != -1){
//			fileType = SystemConstants.RESOURCE_PUBLIC_DATASORUCE;
//		}else if(newPath.indexOf(privateDir) != -1){
//			fileType = SystemConstants.RESOURCE_PRIVATE_DATASORUCE;
//		}else{
//			fileType = SystemConstants.RESOURCE_OTHER_DATASORUCE;
//		}
        return fileType;
    }

    /**
     * 获取私人目录
     **/
//	private String getPrivateDirectory() {
//		SystemUtil util = new SystemUtil();
//		try {
//			return util.getCurrentLoginUserFolderPath();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
    private FileParam buildFileParam(String filePath) {
        return buildFileParam(new File(filePath));
    }

//	@Override
//	public List<FileParam> getRootFiles() {
//		return getRootFiles();
//	}

    @Override
    public FileParam createNewDirectory(String parent, String newDirName, Integer dataSource) {
        File file = new File(parent);
        File newDir = new File(file, newDirName);
        boolean flag = false;
        if (!newDir.exists()) {
            flag = newDir.mkdirs();
        }
        if (flag) {
            FileParam param = buildFileParam(newDir);
            return param;
        }
        return null;
    }

    @Override
    public List<FileParam> getChildrenFiles(FileParam param) {
        return getChildrenFiles(param.getpId());
    }

    @Override
	public List<FileParam> getChildrenFiles2(FileParam param) {
		return getChildrenFiles2(param.getpId());
	}

	@Override
	public List<FileParam> getChildrenFiles3(FileParam param) {
		// TODO Auto-generated method stub
		return getChildrenFiles3(param.getpId());
	}
	
	@Override
	public List<FileParam> getChildrenFiles4(FileParam param) {
		// TODO Auto-generated method stub
		return getChildrenFiles4(param.getpId());
	}



	

}
