package com.htht.job.admin.controller;

import com.htht.job.admin.core.model.FileParam;
import com.htht.job.admin.service.FileResourceManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@RequestMapping("/folderscan")
public class FolderScanLogicController {


    @Autowired
    private FileResourceManager resourceManager;



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
    @PostMapping(value = "/getRootToJsonWithConfig", produces = {"application/json;charset=UTF-8"})
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
    @PostMapping(value = "/getChildrenFiles2",produces = {"application/json;charset=UTF-8"})
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
    @PostMapping(value = "/getChildrenFiles3",produces = {"application/json;charset=UTF-8"})
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
    @PostMapping(value = "/getChildrenFiles4",produces = {"application/json;charset=UTF-8"})
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

    @PostMapping(value = "/getChildrenFiles",produces = {"application/json;charset=UTF-8"})
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


