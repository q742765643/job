package com.htht.job.admin.controller.product;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.core.util.PropertiesFileUtil;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.enums.ExecutorBlockStrategyEnum;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.ZipFileUtil;
import com.htht.job.core.utilbean.UploadAlgoEntity;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.algorithm.*;
import com.htht.job.executor.model.registry.Registry;
import com.htht.job.executor.model.registryalgo.RegistryAlgo;
import com.mysql.jdbc.StringUtils;
import info.monitorenter.cpdetector.io.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Controller
@RequestMapping("/processmodel")
public class ProcessController {
    private static Logger logger = LoggerFactory.getLogger(ProcessController.class);
    @Autowired
    public DubboService dubboService;
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

    @RequestMapping
    public String index(Model model) {
        // job group (executor)
        List<Registry> list = dubboService.findAllRegistry();

        model.addAttribute("nodeList", list);
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values()); 
        
        //获取模型标识下拉框
       
        List<DictCode> modelIdentificationyList= dubboService.findChildrenDictCode("模型标识");
        model.addAttribute("modelIdentificationyList", modelIdentificationyList);
        
        return "/processmeta/processmeta.index";
    }
    
    @RequestMapping(path = "/uploadalgo", method = RequestMethod.POST)
    @ResponseBody
    public UploadAlgoEntity uploadAlgo(@RequestParam MultipartFile file){
    	UploadAlgoEntity result = null;
    	if(!file.isEmpty()){
            //获取算法上传根路径
    		String os = System.getProperty("os.name");  
    		if(StringUtils.isNullOrEmpty(os)){
    			result = new UploadAlgoEntity(500,"获取主控所属系统失败");
    			return result;
    		}
    		String uploadPath = dubboService.getMasterSharePath(os);
    		String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
    		uploadPath = uploadPath + uploadSuffix;
            //上传文件名
            String filename = file.getOriginalFilename();
            File filepath = new File(uploadPath, filename);
            //判断路径是否存在，如果不存在就创建一个
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            try {
            	File algoPathFile = new File(uploadPath + "/" + filename);
				file.transferTo(algoPathFile);
				result = new UploadAlgoEntity();
				result.setCode(UploadAlgoEntity.SUCCESS_CODE);
				result.setAlgoZipName(file.getOriginalFilename());
				result.setAlgoZipPath(algoPathFile.getAbsolutePath());
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return result;
    }
    
    @RequestMapping(path="/parseLoading",method = RequestMethod.GET)
    @ResponseBody
    public AtomicAlgorithm parseLoading(String algoUploadPath, String algoZipName){
		//创建parameterModel
		AtomicAlgorithm atomicAlgorithm = new AtomicAlgorithm();
		atomicAlgorithm.setAlgoPath(algoUploadPath);
        //获取算法上传根路径
		String os = System.getProperty("os.name");  
		String uploadPath = dubboService.getMasterSharePath(os);
		String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
		uploadPath = uploadPath + uploadSuffix;
    	//获取算法执行路径
		DictCode executePathDict = dubboService.findOneselfDictCode("windows算法执行根路径");
		String executePath = executePathDict.getDictCode();
        //上传文件名
        String filename = algoZipName;
        //解析zip包里面的xml文件,入库
        String noExtenName = ZipFileUtil.getFileNameNoEx(filename);
        String prefixPath = uploadPath + "/TEMP" + "/" + noExtenName + "/" + noExtenName;
        //判断算法类型
        String algoType = getAlgoType(prefixPath);
        atomicAlgorithm.setAlgoType(algoType);
        //得到xml文件
        String xmlFile_path=uploadPath + "/TEMP" + "/" + noExtenName + "/" + noExtenName + ".xml";
        String filecode = null;
        try {
            filecode = getFileEncode(xmlFile_path);
            convert(xmlFile_path, filecode, xmlFile_path, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        File xmlFile = new File(xmlFile_path);
        //解析xml,入库
        storageXml(atomicAlgorithm, xmlFile);
        //删除临时解压文件夹
        ZipFileUtil.deleteDir(new File(uploadPath + "/TEMP" + "/" + filename));
        //添加上传算法调用固定参数
        String localExePath = executePath + "/" + noExtenName + "/" + noExtenName;
        List<CommonParameter> jobparameterlist = new ArrayList<CommonParameter>();
        CommonParameter commonParameter = new CommonParameter();
        commonParameter.setId("1");
        commonParameter.setParameterDesc("执行路径");
        commonParameter.setParameterType("string");
        commonParameter.setParameterName("执行路径");
        if (AlgoTypeConstants.JAR.equals(algoType)) {
            localExePath += ".jar";
            commonParameter.setValue(localExePath);
        } else if (AlgoTypeConstants.EXE.equals(algoType)) {
            localExePath += ".exe";
            commonParameter.setValue(localExePath);
        } else if (AlgoTypeConstants.CPP.equals(algoType)) {
        	//获取遥感算法执行路径
    		DictCode AlgoLoaderPathDict = dubboService.findOneselfDictCode("windows遥感算法执行器路径");
    		String AlgoLoaderPath = AlgoLoaderPathDict.getDictCode();
            //String AlgoLoaderPath = PropertiesUtil.getString("htht.job.AlgoLoaderPath");
            localExePath = AlgoLoaderPath + " " + noExtenName + ".dll";
            commonParameter.setValue(localExePath);
        }else if(AlgoTypeConstants.SH.equals(algoType)){
        	localExePath += ".sh";
        	commonParameter.setValue(localExePath);
        }else if(AlgoTypeConstants.PY.equals(algoType)){
        	localExePath += ".py";
        	commonParameter.setValue("python "+localExePath);
        }else if(AlgoTypeConstants.LN.equals(algoType)){
            commonParameter.setValue(localExePath);
        }
        jobparameterlist.add(commonParameter);
        String jsonString = JSON.toJSONString(jobparameterlist);
        atomicAlgorithm.setFixedParameter(jsonString);
        
        //解析为bootstrap模态框显示参数格式
        if (null != atomicAlgorithm) {
            if (!StringUtils.isNullOrEmpty(atomicAlgorithm.getFixedParameter())) {
            	Map map = new HashMap(20);
            	List<CommonParameter> parameterlist = new ArrayList<CommonParameter>();
            	map.put("parameterType", "0");
                parameterlist = JSON.parseArray(atomicAlgorithm.getFixedParameter(),
                        CommonParameter.class);
                List<CommonParameter> list = new ArrayList<CommonParameter>();
                list.addAll(parameterlist);
                map.put("list", list);
                String jsonString2 = JSON.toJSONString(map);
                atomicAlgorithm.setFixedParameter(jsonString2);
                System.out.println(atomicAlgorithm.getFixedParameter());
            }
            if (!StringUtils.isNullOrEmpty(atomicAlgorithm.getDynamicParameter())){
            	Map map = new HashMap(20);
            	List<CommonParameter> parameterlist = new ArrayList<CommonParameter>();
            	map.put("parameterType", "0");
                parameterlist = JSON.parseArray(atomicAlgorithm.getDynamicParameter(),
                        CommonParameter.class);
                List<CommonParameter> list = new ArrayList<CommonParameter>();
                list.addAll(parameterlist);
                map.put("list", list);
                String jsonString2 = JSON.toJSONString(map);
                atomicAlgorithm.setDynamicParameter(jsonString2);
                System.out.println(atomicAlgorithm.getDynamicParameter());
            }
            
        }
    	return atomicAlgorithm;
    }
    
    @RequestMapping(path= "/uploadAlgoCheck", method = RequestMethod.GET)
    @ResponseBody
    public ReturnT<String> uploadAlgoCheck(@RequestParam String algoUploadPath,@RequestParam String algoZipName){
        //获取算法上传根路径
		String os = System.getProperty("os.name");  
		String uploadPath = dubboService.getMasterSharePath(os);
		String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
		uploadPath = uploadPath + uploadSuffix;
        String noExtenName = ZipFileUtil.getFileNameNoEx(algoZipName);
        File temp = new File(uploadPath + "/TEMP");
        if (!temp.exists()) {
            temp.mkdirs();
        }
        //先删除temp算法文件夹
        File file = new File(uploadPath + "/TEMP" + "/" + noExtenName);
        if(file.exists()){
        	FileUtil.Deldir(file.getAbsolutePath());
        }
        //解压zip包到TEMP目录中
        ZipFileUtil.decompressZipFiles(uploadPath + "/TEMP", algoUploadPath); 
        if(!new File(uploadPath + "/TEMP" + "/" + noExtenName).exists() || !new File(uploadPath + "/TEMP" + "/" + noExtenName).getName().equals(noExtenName)){
        	return new ReturnT<>(402, "上传失败,zip包中应有与zip包同名的文件夹");
        }else if(!new File(uploadPath + "/TEMP" + "/" + noExtenName+ "/" + noExtenName+".xml").exists() || !new File(uploadPath + "/TEMP" + "/" + noExtenName+ "/" + noExtenName+".xml").getName().equals(noExtenName+".xml")){
        	return new ReturnT<>(403, "上传失败,zip包文件夹中应有与zip包同名的xml");
        }
    	return new ReturnT<String>("校验成功无异常");
    }

    public String getAlgoType(String prefixName) {
        File jar = new File(prefixName + "." + AlgoTypeConstants.JAR);
        if (jar.exists()) {
            return AlgoTypeConstants.JAR;
        }
        File exe = new File(prefixName + "." + AlgoTypeConstants.EXE);
        if (exe.exists()) {
            return AlgoTypeConstants.EXE;
        }
        File sh = new File(prefixName + "." + AlgoTypeConstants.SH);
        if(sh.exists()){
        	return AlgoTypeConstants.SH;
        }
        File py = new File(prefixName + "." + AlgoTypeConstants.PY);
        if(py.exists()){
        	return AlgoTypeConstants.PY;
        }
        File ln = new File(prefixName);
        if(ln.exists()){
            return AlgoTypeConstants.LN;
        }
        return AlgoTypeConstants.CPP;
    }

    public void storageXml(AtomicAlgorithm atomicAlgorithm, File xmlFile) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element root = document.getRootElement();
            List<Element> childElements = root.elements();
            ArrayList<CommonParameter> arrayList = new ArrayList<>();
            for (Element child : childElements) {
                String name = child.getName();
                if ("select".equals(name)) {
                    CommonParameter parameter = new CommonParameter();
                    List<Attribute> attributes = child.attributes();
                    String defaultValue = child.getStringValue();
                    //设置参数默认值
                    parameter.setValue(defaultValue);
                    for (Attribute attribute : attributes) {
                        parameter = setParameter(parameter, attribute);
                    }
                    parameter.setParameterType(name);
                    arrayList.add(parameter);
                }
                if ("input".equals(name)) {
                    CommonParameter parameter = new CommonParameter();
                    String defaultValue = child.getStringValue();
                    parameter.setValue(defaultValue);
                    List<Attribute> attributes = child.attributes();
                    for (Attribute attribute : attributes) {
                        parameter = setParameter(parameter, attribute);
                        //设置inputinfolder
                        // System.out.println(attribute.getName()+"    "+attribute.getValue());
                        if (ParameterConstants.TYPE.equals(attribute.getName())) {
                            if ("infolder".equals(attribute.getValue())) {
                                parameter.setParameterType(name + attribute.getValue());
                            }
                        }
                        //设置inputparamfile
                        if (ParameterConstants.TYPE.equals(attribute.getName())) {
                            if ("paramfile".equals(attribute.getValue())) {
                                parameter.setParameterType(name + attribute.getValue());
                            }
                        }
                    }
                    arrayList.add(parameter);
                }
                if ("output".equals(name)) {
                    CommonParameter parameter = new CommonParameter();
                    String defaultValue = child.getStringValue();
                    parameter.setValue(defaultValue);
                    List<Attribute> attributes = child.attributes();
                    for (Attribute attribute : attributes) {
                        parameter = setParameter(parameter, attribute);
                        //设置outputstring类型
                        if (ParameterConstants.TYPE.equals(attribute.getName())) {
                            if ("string".equals(attribute.getValue())) {
                                parameter.setParameterType(name + attribute.getValue());
                            }
                        }
                    }
                    arrayList.add(parameter);
                }
                String jsonString = JSON.toJSONString(arrayList, SerializerFeature.DisableCircularReferenceDetect);
                atomicAlgorithm.setDynamicParameter(jsonString);
                
                if("ModelIdentification".equals(name)) {
                	CommonParameter parameter = new CommonParameter();
                	String defaultValue = child.getStringValue();
                	atomicAlgorithm.setModelIdentify(defaultValue);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    //读取子节点元素属性赋值给CommonParameter
    private CommonParameter setParameter(CommonParameter parameter, Attribute attr) {
        if (ParameterConstants.ID.equals(attr.getName())) {
            parameter.setId(attr.getValue());
        }
        if (ParameterConstants.PARAMETERDESC.equals(attr.getName())) {
            parameter.setParameterDesc(attr.getValue());
        }
        if (ParameterConstants.PARAMETERTYPE.equals(attr.getName())) {
            parameter.setParameterType(attr.getValue());
        }
        if (ParameterConstants.URL.equals(attr.getName())) {
            parameter.setUrl(attr.getValue());
        }
        if (ParameterConstants.VALUE.equals(attr.getName())) {
            String options = attr.getValue();
            JSONObject parseObject = JSON.parseObject(options);
            Set<String> keySet = parseObject.keySet();
            ArrayList<OptionBean> arrayList = new ArrayList<>();
            for (String key : keySet) {
                OptionBean optionBean = new OptionBean();
                String value = (String) parseObject.get(key);
                optionBean.setId(value);
                optionBean.setText(key);
                arrayList.add(optionBean);
                //System.out.println(key+"       "+value);
            }
            String jsonString = JSON.toJSONString(arrayList);
            jsonString = jsonString.replace("\"", "'");
           // System.out.println(jsonString);
            parameter.setUrl(jsonString);
        }
        if (ParameterConstants.OPERATE.equals(attr.getName())) {
            parameter.setOperate(attr.getValue());
        }
        if (ParameterConstants.CELLID.equals(attr.getName())) {
            parameter.setCellId(attr.getValue());
        }
        if (ParameterConstants.DATAID.equals(attr.getName())) {
            parameter.setDataID(attr.getValue());
        }
        if (ParameterConstants.GROUP.equals(attr.getName())) {
            parameter.setGroup(attr.getValue());
        }
        if (ParameterConstants.DES.equals(attr.getName())) {
            parameter.setParameterDesc(attr.getValue());
            parameter.setParameterName(attr.getValue());
        }
        if (ParameterConstants.ISNULL.equals(attr.getName())) {
            parameter.setIsNull(attr.getValue());
        }
        if (ParameterConstants.TYPE.equals(attr.getName())) {
            parameter.setParameterType(attr.getValue());
            //设置dataType
            if("int".equals(attr.getValue())){
            	parameter.setDataType("int");
            }else if("float".equals(attr.getValue())){
            	parameter.setDataType("float");
            }else if("double".equals(attr.getValue())){
            	parameter.setDataType("double");
            }else if("array".equals(attr.getValue())){
            	parameter.setDataType("array");
            }else if("long".equals(attr.getValue())){
            	parameter.setDataType("long");
            }else{
            	parameter.setDataType("string");
            }
        }
        if (ParameterConstants.EXPANDEDNAME.equals(attr.getName())) {
            parameter.setExpandedname(attr.getValue());
        }
        if(ParameterConstants.DIALOGTYPE.equals(attr.getName())){
        	parameter.setDialogType(attr.getValue());
        }
        if(ParameterConstants.DISPLAY.equals(attr.getName())) {
        	parameter.setDisplay(attr.getValue());
        }
        if (ParameterConstants.IDENTIFY.equals(attr.getName())) {
            parameter.setParameterName(attr.getValue());
        }
        return parameter;
    }
    
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ReturnT<String> save(AtomicAlgorithm atomicAlgorithm) {
    	 ArrayList<ReturnT<String>> arrayList = new ArrayList<>();
    	//校验是否有不同系统的节点
    	ArrayList<String> systemList = new ArrayList();
    	if(atomicAlgorithm.getNodeList().size() > 0){
    		for (String registryId : atomicAlgorithm.getNodeList()) {
    			Registry registry = dubboService.findRegistryById(registryId);
    			systemList.add(registry.getDeploySystem());
    		}
    	}
    	for (String string : systemList) {
    		int frequency = Collections.frequency(systemList,string);
    		if(frequency == systemList.size()){
    			break;
    		}else{
    			return new ReturnT<>(406, "请选择部署系统相同的节点");
    		}
    	}
    	
        AtomicAlgorithm p = atomicAlgorithmService.saveParameter(atomicAlgorithm);
        //建立算法和节点映射关系
        if (atomicAlgorithm.getNodeList().size() > 0) {
            if (!StringUtils.isNullOrEmpty(atomicAlgorithm.getId())) {
                //为修改算法调用,删除之前节点对应关系
                dubboService.deleteRegistryAlgoByAlgoId(atomicAlgorithm.getId());
            }
            //保存节点算法绑定关系
            for (String registryid : atomicAlgorithm.getNodeList()) {
            	RegistryAlgo registryAlgo = new RegistryAlgo();
            	Date date = new Date();
            	registryAlgo.setAlgoId(p.getId());
            	registryAlgo.setRegistryId(registryid);
            	registryAlgo.setCreateTime(date);
                dubboService.saveRegistryAlgo(registryAlgo);
			}
            //根据节点所属系统重新赋值算法固定参数
            String registryId = atomicAlgorithm.getNodeList().get(0);
            Registry registry = dubboService.findRegistryById(registryId);
			ExecutorBiz executorBiz = RealReference.getExecutorBiz(registry.getRegistryIp());
			ReturnT<String> os = executorBiz.getOs();
			//获取算法执行根路径
			String executePath = dubboService.getExePath(os);
	        //上传文件名
	        String filename = atomicAlgorithm.getAlgoZipName();
	        String noExtenName = ZipFileUtil.getFileNameNoEx(filename);
	        //获取算法上传根路径
			String Os = System.getProperty("os.name");  
			String uploadPath = dubboService.getMasterSharePath(Os);
			String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
			uploadPath = uploadPath + uploadSuffix;
	        String prefixPath = uploadPath + "/TEMP" + "/" + noExtenName + "/" + noExtenName;
	        //判断算法类型
	        String algoType = getAlgoType(prefixPath);
	        String localExePath = executePath + "/" + noExtenName + "/" + noExtenName;
	        List<CommonParameter> jobparameterlist = new ArrayList<CommonParameter>();
	        CommonParameter commonParameter = new CommonParameter();
	        commonParameter.setId("1");
	        commonParameter.setParameterDesc("执行路径");
	        commonParameter.setParameterType("string");
	        commonParameter.setParameterName("执行路径");
	        if (AlgoTypeConstants.JAR.equals(algoType)) {
	            localExePath += ".jar";
	            commonParameter.setValue(localExePath);
	        } else if (AlgoTypeConstants.EXE.equals(algoType)) {
	            localExePath += ".exe";
	            commonParameter.setValue(localExePath);
	        } else if (AlgoTypeConstants.CPP.equals(algoType)) {
	        	//获取遥感算法执行路径
	    		DictCode AlgoLoaderPathDict = dubboService.findOneselfDictCode("windows遥感算法执行器路径");
	    		String AlgoLoaderPath = AlgoLoaderPathDict.getDictCode();
	            //String AlgoLoaderPath = PropertiesUtil.getString("htht.job.AlgoLoaderPath");
	            localExePath = AlgoLoaderPath + " " + noExtenName + ".dll";
	            commonParameter.setValue(localExePath);
	        }else if(AlgoTypeConstants.SH.equals(algoType)){
	        	localExePath += ".sh";
	        	commonParameter.setValue(localExePath);
	        }else if(AlgoTypeConstants.PY.equals(algoType)){
	        	localExePath += ".py";
	        	commonParameter.setValue("python "+localExePath);
	        }else if(AlgoTypeConstants.LN.equals(algoType)){
	            commonParameter.setValue(localExePath);
	        }
	        jobparameterlist.add(commonParameter);
	        String jsonString = JSON.toJSONString(jobparameterlist);
	        p.setFixedParameter(jsonString);
            atomicAlgorithmService.saveParameter(p);
			
            //CountDownLatch组件,多线程部署算法
            // 开始的倒数锁 
            final CountDownLatch begin = new CountDownLatch(1);  
            // 结束的倒数锁 
            final CountDownLatch end = new CountDownLatch(atomicAlgorithm.getNodeList().size());
        	// 线程池个数
        	final ExecutorService exec = Executors.newFixedThreadPool(atomicAlgorithm.getNodeList().size());
            for (String registryid : atomicAlgorithm.getNodeList()) {
        		Runnable run = new Runnable() {
        			public void run() {  
        				try {  
        					// 如果当前计数为零，则此方法立即返回。
        					// 等待
        					begin.await();  
        					if (!StringUtils.isNullOrEmpty(atomicAlgorithm.getAlgoPath())) {
        						//节点下载压缩包
        						Registry registry = dubboService.findRegistryById(registryid);
        						String address = registry.getRegistryIp();
        						System.out.println("执行器地址"+address);
        						try {
        							ExecutorBiz executorBiz = RealReference.getExecutorBiz(address);
        							ReturnT<String> os = executorBiz.getOs();
        							//上传文件名
        							String filename = atomicAlgorithm.getAlgoZipName();
        					        //获取算法上传根路径
        							String uploadPath = dubboService.getNodeSharePath(os);
        							String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
        							uploadPath = uploadPath + uploadSuffix;
        							//获取算法执行根路径
        							String executePath = dubboService.getExePath(os);
        							ReturnT<String> deployAlgo = executorBiz.deployAlgo(uploadPath,filename,executePath);
        							arrayList.add(deployAlgo);
        						} catch (Exception e) {
        							e.printStackTrace();
        							logger.error(e.getMessage(), e);
        						}
        					}
        				} catch (InterruptedException e) {  
        				} finally {  
        					// 每个选手到达终点时，end就减一
        					end.countDown();
        				}  
        			}  
        		};  
        		exec.submit(run);
            }
            System.out.println("Multi-thread Start");  
            // begin减一，开始
            begin.countDown();  
            // 等待end变为0，即所有线程执行完毕
            try {
    			end.await();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}  
            System.out.println("Multi-thread Over");  
            exec.shutdown();  
        }
        
        for (ReturnT<String> result : arrayList) {
			if(result.getCode() != 200){
				return result;
			}
		}
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    @RequestMapping(value = "/updateForTree", method = RequestMethod.POST)
    @ResponseBody
    public ReturnT<String> updateForTree(String id,String treeId) {
    	boolean b = atomicAlgorithmService.updateForTree(id,treeId);
    	if(!b) {
    		return ReturnT.FAIL;
    	}
    	return ReturnT.SUCCESS;
    }
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ReturnT<String> update(AtomicAlgorithm atomicAlgorithm) {
    	ArrayList<ReturnT<String>> arrayList = new ArrayList<>();
    	//校验是否部署到相同系统的节点
    	ArrayList<String> systemList = new ArrayList();
    	if(atomicAlgorithm.getNodeList().size() > 0){
    		for (String registryId : atomicAlgorithm.getNodeList()) {
    			Registry registry = dubboService.findRegistryById(registryId);
    			systemList.add(registry.getDeploySystem());
			}
    	}
    	for (String string : systemList) {
    		int frequency = Collections.frequency(systemList,string);
    		if(frequency == systemList.size()){
    			break;
    		}else{
    			return new ReturnT<>(406, "请选择部署系统相同的节点");
    		}
		}
    	
    	AtomicAlgorithm p =null;
    	if(StringUtils.isNullOrEmpty(atomicAlgorithm.getAlgoPath())){
    		//说明未上传算法包修改
    		 p = atomicAlgorithmService.saveParameter(atomicAlgorithm);
    		 if(!StringUtils.isNullOrEmpty(p.getId())){
    			//为修改算法调用,删除之前节点对应关系
     			dubboService.deleteRegistryAlgoByAlgoId(p.getId());
     			if (atomicAlgorithm.getNodeList().size() > 0) {
     				//重新赋值节点关系
     				RegistryAlgo registryAlgo = new RegistryAlgo();
     				for (String registryid : atomicAlgorithm.getNodeList()) {
     					Date date = new Date();
     					registryAlgo.setAlgoId(p.getId());
     					registryAlgo.setRegistryId(registryid);
     					registryAlgo.setCreateTime(date);
     					dubboService.saveRegistryAlgo(registryAlgo);
     				}
     			}		
    		 }
    	}else{
    		//建立算法和节点映射关系
    		if (!StringUtils.isNullOrEmpty(atomicAlgorithm.getId())) {
    			 p = atomicAlgorithmService.findParameterById(atomicAlgorithm.getId());
    			//为修改算法调用,删除之前节点对应关系
    			dubboService.deleteRegistryAlgoByAlgoId(atomicAlgorithm.getId());
    			if (atomicAlgorithm.getNodeList().size() > 0) {
    				//重新赋值节点关系
    				RegistryAlgo registryAlgo = new RegistryAlgo();
    				for (String registryid : atomicAlgorithm.getNodeList()) {
    					Date date = new Date();
    					registryAlgo.setAlgoId(p.getId());
    					registryAlgo.setRegistryId(registryid);
    					registryAlgo.setCreateTime(date);
    					dubboService.saveRegistryAlgo(registryAlgo);
    				}
    				//根据节点所属系统重新赋值算法固定参数
    				String registryId = atomicAlgorithm.getNodeList().get(0);
    				Registry registry = dubboService.findRegistryById(registryId);
    				ExecutorBiz executorBiz = RealReference.getExecutorBiz(registry.getRegistryIp());
    				ReturnT<String> os = executorBiz.getOs();
    				//获取算法执行根路径
    				String executePath = dubboService.getExePath(os);
    				//上传文件名
    				String filename = atomicAlgorithm.getAlgoZipName();
    				String noExtenName = ZipFileUtil.getFileNameNoEx(filename);
    				//获取算法上传根路径
    				String Os = System.getProperty("os.name");  
    				String uploadPath = dubboService.getMasterSharePath(Os);
    				String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
    				uploadPath = uploadPath + uploadSuffix;
    				String prefixPath = uploadPath + "/TEMP" + "/" + noExtenName + "/" + noExtenName;
    				//判断算法类型
    				String algoType = getAlgoType(prefixPath);
    				String localExePath = executePath + "/" + noExtenName + "/" + noExtenName;
    				List<CommonParameter> jobparameterlist = new ArrayList<CommonParameter>();
    				CommonParameter commonParameter = new CommonParameter();
    				commonParameter.setId("1");
    				commonParameter.setParameterDesc("执行路径");
    				commonParameter.setParameterType("string");
    				commonParameter.setParameterName("执行路径");
    				if (AlgoTypeConstants.JAR.equals(algoType)) {
    					localExePath += ".jar";
    					commonParameter.setValue(localExePath);
    				} else if (AlgoTypeConstants.EXE.equals(algoType)) {
    					localExePath += ".exe";
    					commonParameter.setValue(localExePath);
    				} else if (AlgoTypeConstants.CPP.equals(algoType)) {
    					//获取遥感算法执行路径
    					DictCode AlgoLoaderPathDict = dubboService.findOneselfDictCode("windows遥感算法执行器路径");
    					String AlgoLoaderPath = AlgoLoaderPathDict.getDictCode();
    					//String AlgoLoaderPath = PropertiesUtil.getString("htht.job.AlgoLoaderPath");
    					localExePath = AlgoLoaderPath + " " + noExtenName + ".dll";
    					commonParameter.setValue(localExePath);
    				}else if(AlgoTypeConstants.SH.equals(algoType)){
    					localExePath += ".sh";
    					commonParameter.setValue(localExePath);
    				}else if(AlgoTypeConstants.PY.equals(algoType)){
    					localExePath += ".py";
    					commonParameter.setValue("python "+localExePath);
    				}else if(AlgoTypeConstants.LN.equals(algoType)){
    					commonParameter.setValue(localExePath);
    				}
    				jobparameterlist.add(commonParameter);
    				String jsonString = JSON.toJSONString(jobparameterlist);
    				atomicAlgorithm.setFixedParameter(jsonString);
    				AtomicAlgorithm saveParameter = atomicAlgorithmService.saveParameter(atomicAlgorithm);
    				
    				
    				//CountDownLatch组件,多线程部署算法
    				// 开始的倒数锁 
    				final CountDownLatch begin = new CountDownLatch(1);  
    				// 结束的倒数锁 
    				final CountDownLatch end = new CountDownLatch(atomicAlgorithm.getNodeList().size());
    				// 线程池个数
    				final ExecutorService exec = Executors.newFixedThreadPool(atomicAlgorithm.getNodeList().size());
    				for (String registryid : atomicAlgorithm.getNodeList()) {
    					Runnable run = new Runnable() {
    						public void run() {  
    							try {
    								if(!StringUtils.isNullOrEmpty(saveParameter.getAlgoPath())){
    									//节点下载压缩包
    									Registry registry = dubboService.findRegistryById(registryid);
    									String address = registry.getRegistryIp();
    									ExecutorBiz executorBiz;
    									executorBiz = RealReference.getExecutorBiz(address);
    									ReturnT<String> os = executorBiz.getOs();
    									//获取算法上传根路径
    									String uploadPath = dubboService.getNodeSharePath(os);
    									String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
    									uploadPath = uploadPath + uploadSuffix;
    									//上传文件名
    									File algoFile = new File(saveParameter.getAlgoPath());
    									String filename = algoFile.getName();
    									//获取算法执行根路径
    									String executePath = dubboService.getExePath(os);
    									ReturnT<String> runResult = executorBiz.deployAlgo(uploadPath,filename,executePath);
    									arrayList.add(runResult);
    								}
    								RegistryAlgo registryAlgo = new RegistryAlgo();
    								Date date = new Date();
    								registryAlgo.setAlgoId(saveParameter.getId());
    								registryAlgo.setRegistryId(registryid);
    								registryAlgo.setCreateTime(date);
    								dubboService.saveRegistryAlgo(registryAlgo);
    							} catch (Exception e) {  
    								e.printStackTrace();
    							} finally {  
    								// 每个选手到达终点时，end就减一
    								end.countDown();
    							}  
    						}  
    					};  
    					exec.submit(run);
    				}
    				System.out.println("Multi-thread Start");  
    				// begin减一，开始
    				begin.countDown();  
    				// 等待end变为0，即所有线程执行完毕
    				try {
    					end.await();
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}  
    				System.out.println("Multi-thread Over");  
    				exec.shutdown(); 
    			}
    		}
    		for (ReturnT<String> result : arrayList) {
    			if(result.getCode() != 200){
    				return result;
    			}
    		}
    	}
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }


    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(Model model,@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length, AtomicAlgorithm atomicAlgorithm) {

        if (start != 0) {
            start = start / length;
        }
        return atomicAlgorithmService.pageList(start, length, atomicAlgorithm);
    }

    @RequestMapping("/deleteParameter")
    @ResponseBody
    public ReturnT<String> deleteParameter(String id) {
        return atomicAlgorithmService.deleteParameter(id);
    }

    @RequestMapping("/getRegistListByAlgoId")
    @ResponseBody
    public List<RegistryAlgo> getRegistListByAlgoId(@RequestParam(value = "algoId") String id) {
        List<RegistryAlgo> algoList = dubboService.getRegistListByAlgoId(id);
        return algoList;
    }

    public static void main(String[] args) {
        String filecode = null;
        try {

            filecode = getFileEncode("/zzj/git/RawProcess.xml");
            System.out.println(filecode);

            if (filecode.equals("UTF-8")) {
                convert("/zzj/git/RawProcess.xml", filecode, "/zzj/git/AA_QuickImgAndMetaTask.xml", "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void convert(String oldFile, String oldCharset,
                               String newFlie, String newCharset) {
        BufferedReader bin;
        FileOutputStream fos;
        StringBuffer content = new StringBuffer();
        try {
            bin = new BufferedReader(new InputStreamReader(new FileInputStream(
                    oldFile), oldCharset));
            String line = null;
            while ((line = bin.readLine()) != null) {
                // System.out.println("content:" + content);
                content.append(line);
                content.append(System.getProperty("line.separator"));
            }
            bin.close();
            File dir = new File(newFlie.substring(0, newFlie.lastIndexOf("/")));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fos = new FileOutputStream(newFlie);
            Writer out = new OutputStreamWriter(fos, newCharset);
            out.write(content.toString());
            out.close();
            fos.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getFileEncode(String filePath) {
        String charsetName = null;
        try {
            File file = new File(filePath);
            CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
            detector.add(new ParsingDetector(false));
            detector.add(JChardetFacade.getInstance());
            detector.add(ASCIIDetector.getInstance());
            detector.add(UnicodeDetector.getInstance());
            java.nio.charset.Charset charset = null;
            charset = detector.detectCodepage(file.toURI().toURL());
            if (charset != null) {
                charsetName = charset.name();
            } else {
                charsetName = "UTF-8";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return charsetName;
    }


}
