package com.htht.job.admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.admin.core.model.resolve.ProjType;
import com.htht.job.admin.service.ProjectionService;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.CreateXmlUtils;
import com.htht.job.core.utilbean.UploadAlgoEntity;
import com.htht.job.executor.model.dictionary.DictCode;
import com.mysql.jdbc.StringUtils;

/**
 * 
 * @author lianxiaomo
 *
 */
@Controller
@RequestMapping("/pieorthoplugin")
public class PieorthoPlugInController {
	
    @Autowired
    public DubboService dubboService;

	@Autowired
	private ProjectionService projectionService;

	/**
	 * 文件夹扫描/文件选取 方式获得全色影像
	 * 
	 * @param pan_xml_path
	 * @return
	 */
	@RequestMapping("/pan_xml_path1")
	@ResponseBody
	public ReturnT<String> createPan_xml_path1(String pan_xml_path,String pan_xml_folder) {
		// 存储全色影像路径list
		ArrayList<String> arrayList = new ArrayList<>();
		File rootPanPath = new File(pan_xml_path);
		String os = System.getProperty("os.name");  
		String pan_path = dubboService.getMasterSharePath(os);
		if (rootPanPath.exists() && rootPanPath.isDirectory()) {
			//ArrayList<String> panList = projectionService.getPanList(rootPanPath, arrayList);
			//	if(!StringUtils.isNullOrEmpty(pan_xml_folder)) {
			//		pan_path += "/tiffXml/"+pan_xml_folder;
			//		if(!new File(pan_path).exists()) {
			//			new File(pan_path).mkdirs();
			//		}
			//	}
				// 将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
			//	String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("pan_path.xml", pan_path, arrayList);
				return new ReturnT<>(200, rootPanPath.getAbsolutePath());
		} else {
			String[] arrayPan = pan_xml_path.split(",");
			ArrayList<String> panList = projectionService.getPanList1(arrayPan, arrayList);
			if (null != panList && panList.size() > 0) {

			//	File file = new File(panList.get(0));
			//	String parentFile = file.getPath();
			//	String pan_path = projectionService.getPath(parentFile);
				if(!StringUtils.isNullOrEmpty(pan_xml_folder)) {
					pan_path += "/tiffXml/"+pan_xml_folder;
					if(!new File(pan_path).exists()) {
						new File(pan_path).mkdirs();
					}
				}
				String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("pan_path.xml", pan_path, arrayList);
				return new ReturnT<>(200, testCreateMssPathXml);
			}
			return new ReturnT<>(500, "文件夹路径不存在");
		}
	}

	/**
	 * 扫描文件夹/选取文件 方式获得多光谱影像
	 * @param mss_xml_path
	 * @return
	 */
	@RequestMapping("/mss_xml_path")
	@ResponseBody
	public ReturnT<String> createMss_xml_path(String mss_xml_path,String mss_xml_folder) {
		// 存储全色影像路径list
		ArrayList<String> arrayList = new ArrayList<>();

		File rootPanPath = new File(mss_xml_path);
		String os = System.getProperty("os.name");  
		String mss_path = dubboService.getMasterSharePath(os);
		if (rootPanPath.exists() && rootPanPath.isDirectory()) {
//			ArrayList<String> mssList = projectionService.getMssList(rootPanPath, arrayList);
//				if(!StringUtils.isNullOrEmpty(mss_xml_folder)) {
//					mss_path += "/tiffXml/"+mss_xml_folder;
//					if(!new File(mss_path).exists()) {
//						new File(mss_path).mkdirs();
//					}
//				}
				// 将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
			//	String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("mss_path.xml", mss_path, arrayList);
				return new ReturnT<>(200, rootPanPath.getAbsolutePath());
		} else {
			String[] arrayMss = mss_xml_path.split(",");
			ArrayList<String> mssList = projectionService.getMssList1(arrayMss, arrayList);
			if (null != mssList && mssList.size() > 0) {

			//	File file = new File(mssList.get(0));
			//	String parentFile = file.getPath();
			//	String mss_path = projectionService.getPath(parentFile);
				if(!StringUtils.isNullOrEmpty(mss_xml_folder)) {
					mss_path += "/tiffXml/"+mss_xml_folder;
					if(!new File(mss_path).exists()) {
						new File(mss_path).mkdirs();
					}
				}
				// 将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
				String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("mss_path.xml", mss_path, arrayList);
				return new ReturnT<>(200, testCreateMssPathXml);
			}
			return new ReturnT<>(500, "文件夹路径不存在");
		}
	}
	
	/**
	 * 文件夹路径/选取文件 方式获得基准影像
	 * @param mss_ref_path
	 * @return
	 */
	@RequestMapping("/ref_xml_path")
	@ResponseBody
	public ReturnT<String> createRef_xml_path(String ref_xml_path,String ref_xml_folder) {
		// 存储基准影像路径list
		ArrayList<String> arrayList = new ArrayList<>();
		
		File rootPanPath = new File(ref_xml_path);
		String os = System.getProperty("os.name");  
		String ref_path = dubboService.getMasterSharePath(os);
		if (rootPanPath.exists() && rootPanPath.isDirectory()) {
			return new ReturnT<>(200, rootPanPath.getAbsolutePath());
		} else {
			String[] arrayRef = ref_xml_path.split(",");
			ArrayList<String> mssList = projectionService.getRefList1(arrayRef, arrayList);
			if (null != mssList && mssList.size() > 0) {
				
				//	File file = new File(mssList.get(0));
				//	String parentFile = file.getPath();
				//	String mss_path = projectionService.getPath(parentFile);
				if(!StringUtils.isNullOrEmpty(ref_xml_folder)) {
					ref_path += "/tiffXml/"+ref_xml_folder;
					if(!new File(ref_path).exists()) {
						new File(ref_path).mkdirs();
					}
				}
				// 将list集合中的全色影像路径生成xml文件,保存到pan_xml_path文件夹下
				String testCreateMssPathXml = CreateXmlUtils.testCreateMssPathXml("ref_path.xml", ref_path, arrayList);
				return new ReturnT<>(200, testCreateMssPathXml);
			}
			return new ReturnT<>(500, "文件夹路径不存在");
		}
	}


	@RequestMapping(value = "/getoutwkt", method = RequestMethod.POST, produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public List<Object> getOutWkt() {
		List<Object> pTList = projectionService.resolveProjectionDat();
		return pTList;
	}

}
