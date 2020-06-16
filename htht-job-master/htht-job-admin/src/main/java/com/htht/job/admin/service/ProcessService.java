package com.htht.job.admin.service;

import java.io.File;

import org.dom4j.Attribute;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.algorithm.AtomicAlgorithmDTO;
import com.htht.job.executor.model.algorithm.CommonParameter;

public interface ProcessService {

	void convert(String oldFile, String oldCharset,String newFlie, String newCharset);
	
	String getFileEncode(String filePath);
	
	void storageXml(AtomicAlgorithmDTO atomicAlgorithmDTO, File xmlFile);
	
	//读取子节点元素属性赋值给CommonParameter
	CommonParameter setParameter(CommonParameter parameter, Attribute attr);
	
	String setFixedParameter(String algoType,String localExePath,String noExtenName);
	
	String getAlgoType(String prefixName);
	
	ReturnT<String> updateAtomicAlgorithmDTO(AtomicAlgorithmDTO atomicAlgorithmDTO);
	
	ReturnT<String> saveAtomicAlgorithmDTO(AtomicAlgorithmDTO atomicAlgorithmDTO);
	
	void setBootstrapParameter(AtomicAlgorithmDTO atomicAlgorithmDTO);
	
}
