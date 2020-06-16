package com.htht.job.admin.controller;

import com.htht.job.admin.core.model.FileParam;
import com.htht.job.admin.service.FileResourceManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/folderscan")
public class FolderScanLogicController {

    private static final String REMOTE_DIR = "remote";
    private static final String LOCAL_DIR = "local";

    @Autowired
    private FileResourceManager resourceManager;

    public static void main(String[] args) {
        FolderScanLogicController logic = new FolderScanLogicController();
//		logic.setResourceManager(new RemoteFileResourceManager());
        String newName = "";
        String json = logic.createNewDirectory("101", 1);
        for (int i = 0; i < 10; i++) {
            newName = i == 0 ? "新建文件夹" : "新建文件夹" + i;
            json = logic.createNewDirectory("101", 1);
            System.out.println(json);
        }

        json = logic.renameDirectory("101", "我是一头卒", 2);
        System.out.println(logic.getRootToJson());

//		System.out.println(logic.getChildrenFiles("101", "remote"));
    }

    public void setResourceManager(FileResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    // 返回根目录（包含共享目录和私有目录）
    @RequestMapping("/getRootToJson")
    @ResponseBody
    public String getRootToJson() {
        List<FileParam> items = resourceManager.getRootFiles();
        JSONArray array = new JSONArray();
        for (int i = 0; i < items.size(); i++) {
            JSONObject object = createFileObject(items.get(i));
            object.put("pid", "1");
            array.add(object);
        }
        return array.toString();
    }

    //系统参数根目录(返回共享盘)
    @RequestMapping(value = "/getRootToJsonWithConfig", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String getRootToJsonWithConfig() {
        List<FileParam> items = resourceManager.getShareRootFiles();
        JSONArray array = new JSONArray();
        for (int i = 0; i < items.size(); i++) {
            JSONObject object = createFileObject(items.get(i));
            object.put("pId", "1");
            array.add(object);
        }
        return array.toString();
    }
    
 // pie创建工程文件过滤
 	@RequestMapping(value = "/getChildrenFiles2", method = RequestMethod.POST, produces = {
 			"application/json;charset=UTF-8" })
 	@ResponseBody
 	public String getChildrenFiles2(String pid, int dataSource) {
 		FileParam param = new FileParam();
 		param.setpId(pid);
 		param.setDataSource(dataSource);
 		List<FileParam> items = resourceManager.getChildrenFiles2(param);

 		JSONArray array = new JSONArray();
 		if (items != null) {
 			for (int i = 0; i < items.size(); i++) {
 				JSONObject object = createFileObject(items.get(i));
 				array.add(object);
 			}
 		}
 		return array.toString();

 	}

 	// pie创建工程文件过滤
 	@RequestMapping(value = "/getChildrenFiles3", method = RequestMethod.POST, produces = {
 			"application/json;charset=UTF-8" })
 	@ResponseBody
 	public String getChildrenFiles3(String pid, int dataSource) {
 		FileParam param = new FileParam();
 		param.setpId(pid);
 		param.setDataSource(dataSource);
 		List<FileParam> items = resourceManager.getChildrenFiles3(param);

 		JSONArray array = new JSONArray();
 		if (items != null) {
 			for (int i = 0; i < items.size(); i++) {
 				JSONObject object = createFileObject(items.get(i));
 				array.add(object);
 			}
 		}
 		return array.toString();

 	}
 	
 	// pie创建工程文件过滤
 	@RequestMapping(value = "/getChildrenFiles4", method = RequestMethod.POST, produces = {
 	"application/json;charset=UTF-8" })
 	@ResponseBody
 	public String getChildrenFiles4(String pid, int dataSource) {
 		FileParam param = new FileParam();
 		param.setpId(pid);
 		param.setDataSource(dataSource);
 		List<FileParam> items = resourceManager.getChildrenFiles4(param);
 		
 		JSONArray array = new JSONArray();
 		if (items != null) {
 			for (int i = 0; i < items.size(); i++) {
 				JSONObject object = createFileObject(items.get(i));
 				array.add(object);
 			}
 		}
 		return array.toString();
 		
 	}

    @RequestMapping(value = "/getChildrenFiles", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String getChildrenFiles(String pid, int dataSource) {
        FileParam param = new FileParam();
        param.setpId(pid);
        param.setDataSource(dataSource);
        List<FileParam> items = resourceManager.getChildrenFiles(param);

        JSONArray array = new JSONArray();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JSONObject object = createFileObject(items.get(i));
                array.add(object);
            }
        }
        return array.toString();


    }

    public boolean deleteDirectory(String idPath, int dataSource) {
        return resourceManager.deleteFile(idPath, dataSource);
    }

    /**
     * 给定上级目录，创建一个新目录
     *
     * @param parentDir
     * @return
     */

    public String createNewDirectory(String parentDir, int dataSource) {
        FileParam param = resourceManager.createNewDirectory(parentDir, dataSource);
        if (param != null) {
            JSONObject obj = createFileObject(param);
            return obj.toString();
        }
        return null;
    }

    /**
     * 重命名目录名称
     *
     * @param parentDir
     * @return
     */
    public String renameDirectory(String oldpath, String newName, int dataSource) {
        FileParam param = resourceManager.renameDirectory(oldpath, newName, dataSource);
        if (param != null) {
            JSONObject obj = createFileObject(param);
            return obj.toString();
        }
        return null;
    }

    private JSONObject createFileObject(String path) {
        JSONObject obj = new JSONObject();
        File child = new File(path);
        obj.put("id", child.getAbsolutePath());// id属性 ，数据传递
        obj.put("name", child.getName()); // name属性，显示节点名称
        obj.put("pId", child.getParentFile().getAbsolutePath());
        obj.put("isFile", child.isFile());
        obj.put("isParent", false);
        return obj;
    }

    private JSONObject createFileObject(FileParam param) {
        JSONObject obj = new JSONObject();
        obj.put("id", param.getIdAbsolutePath());// id属性 ，数据传递
        obj.put("name", param.getName()); // name属性，显示节点名称
        obj.put("pId", param.getpId());
        obj.put("path", param.getPath());
        obj.put("dataSource", param.getDataSource());
        obj.put("isFile", param.isFile());
        obj.put("isParent", param.isParent());
        return obj;
    }

}


