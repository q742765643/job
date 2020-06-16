package com.htht.job.admin.controller.product;

import com.htht.job.admin.core.util.PropertiesFileUtil;
import com.htht.job.admin.service.ProcessService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.ZipFileUtil;
import com.htht.job.core.utilbean.UploadAlgoEntity;
import com.htht.job.executor.model.algorithm.*;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.registry.RegistryDTO;
import com.htht.job.executor.model.registryalgo.RegistryAlgoDTO;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;


@Controller
@RequestMapping("/processmodel")
public class ProcessController {
    private static Logger logger = LoggerFactory.getLogger(ProcessController.class);
    @Autowired
    public DubboService dubboService;
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;
    @Autowired
    private ProcessService processService;

    @RequestMapping
    public String index(Model model) {
        // job group (executor)
        List<RegistryDTO> list = dubboService.findAllRegistry();

        model.addAttribute("nodeList", list);
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());

        //获取模型标识下拉框

        List<DictCodeDTO> modelIdentificationyList = dubboService.findChildrenDictCode("模型标识");
        model.addAttribute("modelIdentificationyList", modelIdentificationyList);

        return "/processmeta/processmeta.index";
    }

    @RequestMapping(path = "/uploadalgo", method = RequestMethod.POST)
    @ResponseBody
    public UploadAlgoEntity uploadAlgo(@RequestParam MultipartFile file) {
        UploadAlgoEntity result = null;
        if (!file.isEmpty()) {
            //获取算法上传根路径
            String os = System.getProperty(JobConstant.OS_NAME);
            if (StringUtils.isNullOrEmpty(os)) {
                result = new UploadAlgoEntity(500, "获取主控所属系统失败");
                return result;
            }
            String uploadPath = dubboService.getMasterSharePath(os);
            String uploadSuffix = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get(JobConstant.HTHT_JOB_ALGO_UPLOADPATH);
            uploadPath = uploadPath + uploadSuffix;
            //上传文件名
            String filename = file.getOriginalFilename();
            File filepath = new File(uploadPath, filename);
            //判断路径是否存在，如果不存在就创建一个
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            try {
                File algoPathFile = new File(uploadPath + '/' + filename);
                file.transferTo(algoPathFile);
                result = new UploadAlgoEntity();
                result.setCode(UploadAlgoEntity.SUCCESS_CODE);
                result.setAlgoZipName(file.getOriginalFilename());
                result.setAlgoZipPath(algoPathFile.getAbsolutePath());
            }catch (IOException e) {
            	logger.error(e.toString());
            }
        }
        return result;
    }

    @RequestMapping(path = "/parseLoading", method = RequestMethod.GET)
    @ResponseBody
    public AtomicAlgorithmDTO parseLoading(String algoUploadPath, String algoZipName) {
        //创建parameterModel
        AtomicAlgorithmDTO atomicAlgorithmDTO = new AtomicAlgorithmDTO();
        atomicAlgorithmDTO.setAlgoPath(algoUploadPath);
        //获取算法上传根路径
        String os = System.getProperty(JobConstant.OS_NAME);
        String uploadPath = dubboService.getMasterSharePath(os);
        String uploadSuffix = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get(JobConstant.HTHT_JOB_ALGO_UPLOADPATH);
        uploadPath = uploadPath + uploadSuffix;
        //获取算法执行路径
        DictCodeDTO executePathDict = dubboService.findOneselfDictCode("windows算法执行根路径");
        String executePath = executePathDict.getDictCode();
        //上传文件名
        String filename = algoZipName;
        //解析zip包里面的xml文件,入库
        String noExtenName = ZipFileUtil.getFileNameNoEx(filename);
        String prefixPath = uploadPath + JobConstant.TEMP_FOLDER + '/' + noExtenName + '/' + noExtenName;
        //得到xml文件
        String xmlFilePath = uploadPath + JobConstant.TEMP_FOLDER + '/' + noExtenName + '/' + noExtenName + ".xml";
        String filecode = null;
        try {
            filecode = processService.getFileEncode(xmlFilePath);
            processService.convert(xmlFilePath, filecode, xmlFilePath, "UTF-8");
        } catch (Exception e) {
            logger.error(e.toString());
        }
        File xmlFile = new File(xmlFilePath);
        //解析xml,上传算法动态参数
        processService.storageXml(atomicAlgorithmDTO, xmlFile);
        //删除临时解压文件夹
        ZipFileUtil.deleteDir(new File(uploadPath + JobConstant.TEMP_FOLDER + '/' + filename));
        //判断算法类型
        String algoType = processService.getAlgoType(prefixPath);
        atomicAlgorithmDTO.setAlgoType(algoType);
        String localExePath = executePath + '/' + noExtenName + '/' + noExtenName;
        //添加上传算法调用固定参数
        String jsonString = processService.setFixedParameter(algoType, localExePath,noExtenName);
        atomicAlgorithmDTO.setFixedParameter(jsonString);
        //解析为bootstrap模态框显示参数格式
        processService.setBootstrapParameter(atomicAlgorithmDTO);
        return atomicAlgorithmDTO;
    }

    @RequestMapping(path = "/uploadAlgoCheck", method = RequestMethod.GET)
    @ResponseBody
    public ReturnT<String> uploadAlgoCheck(@RequestParam String algoUploadPath, @RequestParam String algoZipName) {
        //获取算法上传根路径
        String os = System.getProperty(JobConstant.OS_NAME);
        String uploadPath = dubboService.getMasterSharePath(os);
        String uploadSuffix = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get(JobConstant.HTHT_JOB_ALGO_UPLOADPATH);
        uploadPath = uploadPath + uploadSuffix;
        String noExtenName = ZipFileUtil.getFileNameNoEx(algoZipName);
        File temp = new File(uploadPath + JobConstant.TEMP_FOLDER);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        //先删除temp算法文件夹
        File file = new File(uploadPath + JobConstant.TEMP_FOLDER + '/' + noExtenName);
        if (file.exists()) {
            FileUtil.delDir(file.getAbsolutePath());
        }
        //解压zip包到TEMP目录中 
        ZipFileUtil.decompressZipFiles(uploadPath + JobConstant.TEMP_FOLDER, algoUploadPath);
        if (!new File(uploadPath + JobConstant.TEMP_FOLDER + '/' + noExtenName).exists() || !new File(uploadPath + JobConstant.TEMP_FOLDER + '/' + noExtenName).getName().equals(noExtenName)) {
            return new ReturnT<>(402, "上传失败,zip包中应有与zip包同名的文件夹");
        } else if (!new File(uploadPath + JobConstant.TEMP_FOLDER + '/' + noExtenName + '/' + noExtenName + ".xml").exists() || !new File(uploadPath + JobConstant.TEMP_FOLDER + '/' + noExtenName + '/' + noExtenName + ".xml").getName().equals(noExtenName + ".xml")) {
            return new ReturnT<>(403, "上传失败,zip包文件夹中应有与zip包同名的xml");
        }
        return new ReturnT<>("校验成功无异常");
    }

    @ResponseBody
    @RequestMapping("/save")
    public ReturnT<String> save(@RequestBody AtomicAlgorithmDTO atomicAlgorithmDTO) {
        //校验是否有不同系统的节点
        ArrayList<String> systemList = new ArrayList();
        if (!atomicAlgorithmDTO.getNodeList().isEmpty()) {
            for (String registryId : atomicAlgorithmDTO.getNodeList()) {
                RegistryDTO registryDTO = dubboService.findRegistryById(registryId);
                systemList.add(registryDTO.getDeploySystem());
            }
        }
        for (String string : systemList) {
            int frequency = Collections.frequency(systemList, string);
            if (frequency == systemList.size()) {
                break;
            } else {
                return new ReturnT<>(406, "请选择部署系统相同的节点");
            }
        }

       return processService.saveAtomicAlgorithmDTO(atomicAlgorithmDTO);
    }

    @RequestMapping(value = "/updateForTree", method = RequestMethod.POST)
    @ResponseBody
    public ReturnT<String> updateForTree(String id, String treeId) {
        boolean b = atomicAlgorithmService.updateForTree(id, treeId);
        if (!b) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
    
    @ResponseBody
    @RequestMapping("/update")
    public ReturnT<String> update(@RequestBody AtomicAlgorithmDTO atomicAlgorithmDTO) {
        //校验是否部署到相同系统的节点
        ArrayList<String> systemList = new ArrayList<>();
        if (!atomicAlgorithmDTO.getNodeList().isEmpty()) {
            for (String registryId : atomicAlgorithmDTO.getNodeList()) {
                RegistryDTO registryDTO = dubboService.findRegistryById(registryId);
                systemList.add(registryDTO.getDeploySystem());
            }
        }
        for (String string : systemList) {
            int frequency = Collections.frequency(systemList, string);
            if (frequency == systemList.size()) {
                break;
            } else {
                return new ReturnT<>(406, "请选择部署系统相同的节点");
            }
        }
        
        return processService.updateAtomicAlgorithmDTO(atomicAlgorithmDTO);
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(Model model, @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length, AtomicAlgorithmDTO atomicAlgorithmDTO) {

        if (start != 0) 
            start = start / length;
        return atomicAlgorithmService.pageList(start, length, atomicAlgorithmDTO);
    }

    @RequestMapping("/deleteParameter")
    @ResponseBody
    public ReturnT<String> deleteParameter(String id) {
        return atomicAlgorithmService.deleteParameter(id);
    }

    @RequestMapping("/getRegistListByAlgoId")
    @ResponseBody
    public List<RegistryAlgoDTO> getRegistListByAlgoId(@RequestParam(value = "algoId") String id) {
    	  return dubboService.getRegistListByAlgoId(id);
    }


}
