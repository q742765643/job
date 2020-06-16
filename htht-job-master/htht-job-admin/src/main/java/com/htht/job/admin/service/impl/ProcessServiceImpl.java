package com.htht.job.admin.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.htht.job.admin.core.rpc.RealReference;
import com.htht.job.admin.core.util.PropertiesFileUtil;
import com.htht.job.admin.service.ProcessService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.api.algorithm.AtomicAlgorithmService;
import com.htht.job.core.biz.ExecutorBiz;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.constant.JobConstant;
import com.htht.job.core.util.ZipFileUtil;
import com.htht.job.executor.model.algorithm.AlgoTypeConstants;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;
import com.htht.job.executor.model.algorithm.OptionBean;
import com.htht.job.executor.model.algorithm.ParameterConstants;
import com.htht.job.executor.model.dictionary.DictCodeDTO;
import com.htht.job.executor.model.registry.RegistryDTO;
import com.htht.job.executor.model.registryalgo.RegistryAlgoDTO;
import com.mysql.jdbc.StringUtils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

@Service
public class ProcessServiceImpl implements ProcessService {
    private static Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);
	
    @Resource
    private DubboService dubboService;
    @Autowired
    private AtomicAlgorithmService atomicAlgorithmService;

	@Override
	public void convert(String oldFile, String oldCharset, String newFlie, String newCharset) {

        try (
            BufferedReader bin  = new BufferedReader(new InputStreamReader(new FileInputStream(oldFile), oldCharset));
        	){
        	File dir = new File(newFlie.substring(0, newFlie.lastIndexOf('/')));
        	if (!dir.exists()) {
        		dir.mkdirs();
        	}
        	StringBuffer content = new StringBuffer();
        	String line = null;
        	while ((line = bin.readLine()) != null) {
        		content.append(line);
        		content.append(System.getProperty("line.separator"));
        	}
        	try(Writer out = new OutputStreamWriter(new FileOutputStream(newFlie), newCharset);){
        		out.write(content.toString());
        	}
        } catch (IOException e) {
        	logger.error(e.toString());
        }
    
		
	}

	@Override
	public String getFileEncode(String filePath) {
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
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
        return charsetName;
    }

	@Override
	public void storageXml(AtomicAlgorithmDTO atomicAlgorithmDTO, File xmlFile) {
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
                        if (ParameterConstants.TYPE.equals(attribute.getName()) && "infolder".equals(attribute.getValue())) {
                                parameter.setParameterType(name + attribute.getValue());
                        }
                        //设置inputparamfile
                        if (ParameterConstants.TYPE.equals(attribute.getName())&&"paramfile".equals(attribute.getValue())) {
                                parameter.setParameterType(name + attribute.getValue());
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
                        if (ParameterConstants.TYPE.equals(attribute.getName())&&JobConstant.STRING.equals(attribute.getValue())) {
                                parameter.setParameterType(name + attribute.getValue());
                        }
                    }
                    arrayList.add(parameter);
                }
                String jsonString = JSON.toJSONString(arrayList, SerializerFeature.DisableCircularReferenceDetect);
                atomicAlgorithmDTO.setDynamicParameter(jsonString);

                if ("ModelIdentification".equals(name)) {
                    String defaultValue = child.getStringValue();
                    atomicAlgorithmDTO.setModelIdentify(defaultValue);
                }
            }
        } catch (DocumentException e) {
        	 logger.error(e.toString());
        }
    }

	@Override
	public CommonParameter setParameter(CommonParameter parameter, Attribute attr) {
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
            }
            String jsonString = JSON.toJSONString(arrayList);
            jsonString = jsonString.replace("\"", "'");
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
            if ("int".equals(attr.getValue())) {
                parameter.setDataType("int");
            } else if ("float".equals(attr.getValue())) {
                parameter.setDataType("float");
            } else if ("double".equals(attr.getValue())) {
                parameter.setDataType("double");
            } else if ("array".equals(attr.getValue())) {
                parameter.setDataType("array");
            } else if ("long".equals(attr.getValue())) {
                parameter.setDataType("long");
            } else {
                parameter.setDataType(JobConstant.STRING);
            }
        }
        if (ParameterConstants.EXPANDEDNAME.equals(attr.getName())) {
            parameter.setExpandedname(attr.getValue());
        }
        if (ParameterConstants.DIALOGTYPE.equals(attr.getName())) {
            parameter.setDialogType(attr.getValue());
        }
        if (ParameterConstants.DISPLAY.equals(attr.getName())) {
            parameter.setDisplay(attr.getValue());
        }
        if (ParameterConstants.IDENTIFY.equals(attr.getName())) {
            parameter.setParameterName(attr.getValue());
        }
        return parameter;
    }

	@Override
	public String setFixedParameter(String algoType, String localExePath,String noExtenName) {
        List<CommonParameter> jobparameterlist = new ArrayList<>();
        CommonParameter commonParameter = new CommonParameter();
        commonParameter.setId("1");
        commonParameter.setParameterDesc("执行路径");
        commonParameter.setParameterType(JobConstant.STRING);
        commonParameter.setParameterName("执行路径");
        if (AlgoTypeConstants.JAR.equals(algoType)) {
            localExePath += ".jar";
            commonParameter.setValue(localExePath);
        } else if (AlgoTypeConstants.EXE.equals(algoType)) {
            localExePath += ".exe";
            commonParameter.setValue(localExePath);
        } else if (AlgoTypeConstants.CPP.equals(algoType)) {
            //获取遥感算法执行路径
            DictCodeDTO algoLoaderPathDict = dubboService.findOneselfDictCode(JobConstant.WINDOWS_ALGOLOADER_PATH);
            String algoLoaderPath = algoLoaderPathDict.getDictCode();
            localExePath = algoLoaderPath + " " + noExtenName + ".dll";
            commonParameter.setValue(localExePath);
        } else if (AlgoTypeConstants.SH.equals(algoType)) {
            localExePath += ".sh";
            commonParameter.setValue(localExePath);
        } else if (AlgoTypeConstants.PY.equals(algoType)) {
            localExePath += ".py";
            commonParameter.setValue(JobConstant.PYTHON + localExePath);
        } else if (AlgoTypeConstants.LN.equals(algoType)) {
            commonParameter.setValue(localExePath);
        }
        jobparameterlist.add(commonParameter);
        return JSON.toJSONString(jobparameterlist);
        
	}

	@Override
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
        if (sh.exists()) {
            return AlgoTypeConstants.SH;
        }
        File py = new File(prefixName + "." + AlgoTypeConstants.PY);
        if (py.exists()) {
            return AlgoTypeConstants.PY;
        }
        File ln = new File(prefixName);
        if (ln.exists()) {
            return AlgoTypeConstants.LN;
        }
        return AlgoTypeConstants.CPP;
    }

	@Override
	public ReturnT<String> updateAtomicAlgorithmDTO(AtomicAlgorithmDTO atomicAlgorithmDTO) {
        ArrayList<ReturnT<String>> arrayList = new ArrayList<>();
        AtomicAlgorithmDTO p = null;
        if (StringUtils.isNullOrEmpty(atomicAlgorithmDTO.getAlgoPath())) {
            //说明未上传算法包修改
            p = atomicAlgorithmService.saveParameter(atomicAlgorithmDTO);
            if (!StringUtils.isNullOrEmpty(p.getId())) {
                //为修改算法调用,删除之前节点对应关系
                dubboService.deleteRegistryAlgoByAlgoId(p.getId());
                if (!atomicAlgorithmDTO.getNodeList().isEmpty()) {
                    //重新赋值节点关系
                    RegistryAlgoDTO registryAlgoDTO = new RegistryAlgoDTO();
                    for (String registryid : atomicAlgorithmDTO.getNodeList()) {
                        Date date = new Date();
                        registryAlgoDTO.setAlgoId(p.getId());
                        registryAlgoDTO.setRegistryId(registryid);
                        registryAlgoDTO.setCreateTime(date);
                        dubboService.saveRegistryAlgo(registryAlgoDTO);
                    }
                }
            }
        } else {
            //建立算法和节点映射关系
            if (!StringUtils.isNullOrEmpty(atomicAlgorithmDTO.getId())) {
                p = atomicAlgorithmService.findParameterById(atomicAlgorithmDTO.getId());
                //为修改算法调用,删除之前节点对应关系
                dubboService.deleteRegistryAlgoByAlgoId(atomicAlgorithmDTO.getId());
                if (!atomicAlgorithmDTO.getNodeList().isEmpty()) {
                    //重新赋值节点关系
                    RegistryAlgoDTO registryAlgoDTO = new RegistryAlgoDTO();
                    for (String registryid : atomicAlgorithmDTO.getNodeList()) {
                        Date date = new Date();
                        registryAlgoDTO.setAlgoId(p.getId());
                        registryAlgoDTO.setRegistryId(registryid);
                        registryAlgoDTO.setCreateTime(date);
                        dubboService.saveRegistryAlgo(registryAlgoDTO);
                    }
                    //根据节点所属系统重新赋值算法固定参数
                    String registryId = atomicAlgorithmDTO.getNodeList().get(0);
                    RegistryDTO registryDTO = dubboService.findRegistryById(registryId);
                    ExecutorBiz executorBiz = RealReference.getExecutorBiz(registryDTO.getRegistryIp());
                    ReturnT<String> os = executorBiz.getOs();
                    //获取算法执行根路径
                    String executePath = dubboService.getExePath(os);
                    //上传文件名
                    String filename = atomicAlgorithmDTO.getAlgoZipName();
                    String noExtenName = ZipFileUtil.getFileNameNoEx(filename);
                    //获取算法上传根路径
                    String oS = System.getProperty("os.name");
                    String uploadPath = dubboService.getMasterSharePath(oS);
                    String uploadSuffix = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
                    uploadPath = uploadPath + uploadSuffix;
                    String prefixPath = uploadPath + "/TEMP" + '/' + noExtenName + '/' + noExtenName;
                    //判断算法类型
                    String algoType = getAlgoType(prefixPath);
                    String localExePath = executePath + '/' + noExtenName + '/' + noExtenName;
                    List<CommonParameter> jobparameterlist = new ArrayList<>();
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
                        DictCodeDTO algoLoaderPathDict = dubboService.findOneselfDictCode("windows遥感算法执行器路径");
                        String algoLoaderPath = algoLoaderPathDict.getDictCode();
                        localExePath = algoLoaderPath + " " + noExtenName + ".dll";
                        commonParameter.setValue(localExePath);
                    } else if (AlgoTypeConstants.SH.equals(algoType)) {
                        localExePath += ".sh";
                        commonParameter.setValue(localExePath);
                    } else if (AlgoTypeConstants.PY.equals(algoType)) {
                        localExePath += ".py";
                        commonParameter.setValue("python " + localExePath);
                    } else if (AlgoTypeConstants.LN.equals(algoType)) {
                        commonParameter.setValue(localExePath);
                    }
                    jobparameterlist.add(commonParameter);
                    String jsonString = JSON.toJSONString(jobparameterlist);
                    atomicAlgorithmDTO.setFixedParameter(jsonString);
                    AtomicAlgorithmDTO saveParameter = atomicAlgorithmService.saveParameter(atomicAlgorithmDTO);


                    //CountDownLatch组件,多线程部署算法
                    // 开始的倒数锁
                    final CountDownLatch begin = new CountDownLatch(1);
                    // 结束的倒数锁
                    final CountDownLatch end = new CountDownLatch(atomicAlgorithmDTO.getNodeList().size());
                    // 线程池个数
                    final ExecutorService exec = Executors.newFixedThreadPool(atomicAlgorithmDTO.getNodeList().size());
                    for (String registryid : atomicAlgorithmDTO.getNodeList()) {
                        Runnable run = () -> {
                                try {
                                	begin.await();
                                    if (!StringUtils.isNullOrEmpty(saveParameter.getAlgoPath())) {
                                        //节点下载压缩包
                                        RegistryDTO registryDTO1 = dubboService.findRegistryById(registryid);
                                        String address = registryDTO1.getRegistryIp();
                                        ExecutorBiz executorBiz1;
                                        executorBiz1 = RealReference.getExecutorBiz(address);
                                        ReturnT<String> os1 = executorBiz1.getOs();
                                        //获取算法上传根路径
                                        String uploadPath1 = dubboService.getNodeSharePath(os1);
                                        String uploadSuffix1 = PropertiesFileUtil.getInstance("config").get("htht.job.algo.uploadPath");
                                        uploadPath1 = uploadPath1 + uploadSuffix1;
                                        //上传文件名
                                        File algoFile = new File(saveParameter.getAlgoPath());
                                        String filename1 = algoFile.getName();
                                        //获取算法执行根路径
                                        String executePath1 = dubboService.getExePath(os1);
                                        ReturnT<String> runResult = executorBiz1.deployAlgo(uploadPath1, filename1, executePath1);
                                        arrayList.add(runResult);
                                    }
                                    RegistryAlgoDTO registryAlgoDTO1 = new RegistryAlgoDTO();
                                    Date date = new Date();
                                    registryAlgoDTO1.setAlgoId(saveParameter.getId());
                                    registryAlgoDTO1.setRegistryId(registryid);
                                    registryAlgoDTO1.setCreateTime(date);
                                    dubboService.saveRegistryAlgo(registryAlgoDTO1);
                                } catch (Exception e) {
                                	 logger.error(e.toString());
                                } finally {
                                    // 每个选手到达终点时，end就减一
                                    end.countDown();
                                }
                            
                        };
                        exec.submit(run);
                    }
                    logger.info("Multi-thread Start");
                    // begin减一，开始
                    begin.countDown();
                    // 等待end变为0，即所有线程执行完毕
                    try {
                        end.await();
                    } catch (InterruptedException e) {
                    	 logger.error(e.toString());
                        Thread.currentThread().interrupt();
                    }
                    logger.info("Multi-thread Over");
                    exec.shutdown();
                }
            }
            for (ReturnT<String> result : arrayList) {
                if (result.getCode() != 200) {
                    return result;
                }
            }
        }
        if (null != p) {
        	return ReturnT.SUCCESS;
        } else {
        	return ReturnT.FAIL;
        }
	}

	@Override
	public ReturnT<String> saveAtomicAlgorithmDTO(AtomicAlgorithmDTO atomicAlgorithmDTO) {
		ArrayList<ReturnT<String>> arrayList = new ArrayList<>();
        AtomicAlgorithmDTO p = atomicAlgorithmService.saveParameter(atomicAlgorithmDTO);
        //建立算法和节点映射关系
        if (!atomicAlgorithmDTO.getNodeList().isEmpty()) {
            if (!StringUtils.isNullOrEmpty(atomicAlgorithmDTO.getId())) {
                //为修改算法调用,删除之前节点对应关系
                dubboService.deleteRegistryAlgoByAlgoId(atomicAlgorithmDTO.getId());
            }
            //保存节点算法绑定关系
            for (String registryid : atomicAlgorithmDTO.getNodeList()) {
                RegistryAlgoDTO registryAlgoDTO = new RegistryAlgoDTO();
                Date date = new Date();
                registryAlgoDTO.setAlgoId(p.getId());
                registryAlgoDTO.setRegistryId(registryid);
                registryAlgoDTO.setCreateTime(date);
                dubboService.saveRegistryAlgo(registryAlgoDTO);
            }
            //根据节点所属系统重新赋值算法固定参数
            String registryId = atomicAlgorithmDTO.getNodeList().get(0);
            RegistryDTO registryDTO = dubboService.findRegistryById(registryId);
            ExecutorBiz executorBiz = RealReference.getExecutorBiz(registryDTO.getRegistryIp());
            ReturnT<String> os = executorBiz.getOs();
            //获取算法执行根路径
            String executePath = dubboService.getExePath(os);
            //上传文件名
            String filename = atomicAlgorithmDTO.getAlgoZipName();
            String noExtenName = ZipFileUtil.getFileNameNoEx(filename);
            //获取算法上传根路径
            String oS = System.getProperty("os.name");
            String uploadPath = dubboService.getMasterSharePath(oS);
            String uploadSuffix = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get(JobConstant.HTHT_JOB_ALGO_UPLOADPATH);
            uploadPath = uploadPath + uploadSuffix;
            String prefixPath = uploadPath + "/TEMP" + '/' + noExtenName + '/' + noExtenName;
            //判断算法类型
            String algoType = getAlgoType(prefixPath);
            String localExePath = executePath + '/' + noExtenName + '/' + noExtenName;
            List<CommonParameter> jobparameterlist = new ArrayList<>();
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
                DictCodeDTO algoLoaderPathDict = dubboService.findOneselfDictCode("windows遥感算法执行器路径");
                String algoLoaderPath = algoLoaderPathDict.getDictCode();
                localExePath = algoLoaderPath + " " + noExtenName + ".dll";
                commonParameter.setValue(localExePath);
            } else if (AlgoTypeConstants.SH.equals(algoType)) {
                localExePath += ".sh";
                commonParameter.setValue(localExePath);
            } else if (AlgoTypeConstants.PY.equals(algoType)) {
                localExePath += ".py";
                commonParameter.setValue("python " + localExePath);
            } else if (AlgoTypeConstants.LN.equals(algoType)) {
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
            final CountDownLatch end = new CountDownLatch(atomicAlgorithmDTO.getNodeList().size());
            // 线程池个数
            final ExecutorService exec = Executors.newFixedThreadPool(atomicAlgorithmDTO.getNodeList().size());
            for (String registryid : atomicAlgorithmDTO.getNodeList()) {
                Runnable run = () -> {
                        try {
                            // 如果当前计数为零，则此方法立即返回。
                            // 等待鸣枪
                            begin.await();
                            if (!StringUtils.isNullOrEmpty(atomicAlgorithmDTO.getAlgoPath())) {
                                //节点下载压缩包
                                RegistryDTO registryDTO1 = dubboService.findRegistryById(registryid);
                                String address = registryDTO1.getRegistryIp();
                                    ExecutorBiz executorBiz1 = RealReference.getExecutorBiz(address);
                                    ReturnT<String> os1 = executorBiz1.getOs();
                                    //上传文件名
                                    String filename1 = atomicAlgorithmDTO.getAlgoZipName();
                                    //获取算法上传根路径
                                    String uploadPath1 = dubboService.getNodeSharePath(os1);
                                    String uploadSuffix1 = PropertiesFileUtil.getInstance(JobConstant.CONFIG).get(JobConstant.HTHT_JOB_ALGO_UPLOADPATH);
                                    uploadPath1 = uploadPath1 + uploadSuffix1;
                                    //获取算法执行根路径
                                    String executePath1 = dubboService.getExePath(os1);
                                    ReturnT<String> deployAlgo = executorBiz1.deployAlgo(uploadPath1, filename1, executePath1);
                                    arrayList.add(deployAlgo);
                            }
                        } catch (InterruptedException e) {
                        	logger.error(e.toString(),e);
                        	Thread.currentThread().interrupt();
                        } finally {
                            // 每个选手到达终点时，end就减一
                            end.countDown();
                        }
                    };
                exec.submit(run);
            }
            logger.info("Multi-thread Start");
            // begin减一，开始
            begin.countDown();
            // 等待end变为0，即所有线程执行完毕
            try {
                end.await();
            } catch (InterruptedException e) {
            	logger.error(e.toString());
            	Thread.currentThread().interrupt();
            }
            logger.info("Multi-thread Over");
            exec.shutdown();
        }

        for (ReturnT<String> result : arrayList) {
            if (result.getCode() != 200) {
                return result;
            }
        }
        if (null != p.getId()) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
	}

	@Override
	public void setBootstrapParameter(AtomicAlgorithmDTO atomicAlgorithmDTO) {
        if (!StringUtils.isNullOrEmpty(atomicAlgorithmDTO.getFixedParameter())) {
            Map map = new HashMap(20);
            map.put("parameterType", "0");
            List<CommonParameter>  parameterlist = JSON.parseArray(atomicAlgorithmDTO.getFixedParameter(),
                    CommonParameter.class);
            List<CommonParameter> list = new ArrayList<>();
            list.addAll(parameterlist);
            map.put("list", list);
            String jsonString2 = JSON.toJSONString(map);
            atomicAlgorithmDTO.setFixedParameter(jsonString2);
            logger.info(atomicAlgorithmDTO.getFixedParameter());
        }
        if (!StringUtils.isNullOrEmpty(atomicAlgorithmDTO.getDynamicParameter())) {
            Map map = new HashMap(20);
            map.put("parameterType", "0");
            List<CommonParameter> parameterlist = JSON.parseArray(atomicAlgorithmDTO.getDynamicParameter(),
                    CommonParameter.class);
            List<CommonParameter> list = new ArrayList<>();
            list.addAll(parameterlist);
            map.put("list", list);
            String jsonString2 = JSON.toJSONString(map);
            atomicAlgorithmDTO.setDynamicParameter(jsonString2);
            logger.info(atomicAlgorithmDTO.getDynamicParameter());
        }
	}

    
}
