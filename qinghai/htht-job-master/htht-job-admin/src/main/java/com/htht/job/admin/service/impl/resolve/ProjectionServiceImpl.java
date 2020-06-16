package com.htht.job.admin.service.impl.resolve;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.htht.job.admin.core.model.FileParam;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.htht.job.admin.core.model.resolve.Level;
import com.htht.job.admin.core.model.resolve.ProjType;
import com.htht.job.admin.core.model.resolve.Wkt;
import com.htht.job.admin.service.ProjectionService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.util.CreateXmlUtils;

@Service("projectionService")
public class ProjectionServiceImpl implements ProjectionService {

	/**
	 * 解析PROJECTION.dat文件 封装成对象放到树中
	 */
	public List<Object> resolveProjectionDat() {
		SAXReader sax = new SAXReader();// 创建一个SAXReader对象
		// PROJECTION.dat
		String file = this.getClass().getClassLoader().getResource("PROJECTION.dat").getFile();
		File xmlFile = new File(file);// 根据指定的路径创建file对象
		Document document = null;
		try {
			document = sax.read(xmlFile);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		// 获取document对象,如果文档无节点，则会抛出Exception提前结束
		Element root = document.getRootElement();// 获取根节点

		List<Element> listElement = root.elements();
		List<ProjType> proJTypeList = new ArrayList<ProjType>();
		for (Element projTypeElement : listElement) {
			// 一级节点
			ProjType projType = new ProjType();
			// String textTrim = projTypeElement.getTextTrim();
			String textTrim = projTypeElement.attributeValue("Name");
			if (textTrim.equals("Domestic Common Coordinates")) {
				textTrim = "国内常用坐标";
			}
			if (textTrim.equals("Geographic Coordinates")) {
				textTrim = "地理坐标";
			}
			if (textTrim.equals("Projection Coordinates")) {
				textTrim = "投影坐标";
			}
			String projTypeId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
			projType.setProjType(projTypeElement.getTextTrim());
			projType.setName(textTrim);
			projType.setId(projTypeId);
			// 二级节点
			List<Element> eLevelList1 = projTypeElement.elements();
			ArrayList<Level> levelList = new ArrayList<Level>();
			for (Element elementLevel : eLevelList1) {
				Level level = new Level();
				String levelId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
				level.setLevel(elementLevel.getTextTrim());
				level.setId(levelId);
				level.setpId(projTypeId);

				String attributeValue = elementLevel.attributeValue("Name");
				if (attributeValue.equals("Common Geographic Coordinates")) {
					attributeValue = "常用地理坐标";
				}
				if (attributeValue.equals("Common Projection Coordinates")) {
					attributeValue = "常用投影坐标";
				}
				level.setName(attributeValue);
				levelList.add(level);
				// 三级节点
				List<Element> elements = elementLevel.elements();
				ArrayList<Level> childrenLevelList = new ArrayList<Level>();
				List<Wkt> wktList4 = new ArrayList<Wkt>();
				for (Element element : elements) {
					if (element.getName().equals("Level")) {
						Level childrenLevel = new Level();
						String clevelId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
						childrenLevel.setLevel(element.getTextTrim());
						childrenLevel.setId(clevelId);
						childrenLevel.setpId(levelId);
						childrenLevel.setName(element.attributeValue("Name"));
						childrenLevelList.add(childrenLevel);
						// 四级节点
						List<Element> wktList1 = element.elements();
						List<Wkt> wktList2 = new ArrayList<Wkt>();
						for (Element elementWkt : wktList1) {
							Wkt wkt = new Wkt();
							String wktId2 = UUID.randomUUID().toString().replace("-", "").toLowerCase();
							String wktStr = elementWkt.attributeValue("WKT_STR");
							// String subGeogcs = subGeogcs(wktStr);
							wkt.setWktName(elementWkt.attributeValue("WKT_NAME"));
							wkt.setWktStr(elementWkt.attributeValue("WKT_STR"));
							// wkt.setGeogcs(subGeogcs);
							wkt.setId(wktId2);
							wkt.setpId(clevelId);
							wkt.setName(elementWkt.attributeValue("WKT_NAME"));
							wktList2.add(wkt);
						}
						childrenLevel.setWktList(wktList2);
					} else {
						Wkt wkt = new Wkt();
						String wktStr = element.attributeValue("WKT_STR");
						// String subGeogcs = subGeogcs(wktStr);
						String wktId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
						wkt.setWktName(element.attributeValue("WKT_NAME"));
						wkt.setWktStr(element.attributeValue("WKT_STR"));
						// wkt.setGeogcs(subGeogcs);
						wkt.setId(wktId);
						wkt.setpId(levelId);
						wkt.setName(element.attributeValue("WKT_NAME"));
						wktList4.add(wkt);

					}
				}
				level.setWktList(wktList4);
				level.setChildrenLevelList(childrenLevelList);
			}

			projType.setLevelList(levelList);

			// this.getNodes(element,projType);// 从一级点开始遍历所有节点

			proJTypeList.add(projType);
		}
		// System.out.println(proJTypeList);
		List<Object> allTree = allTree(proJTypeList);
		return allTree;

	}

	public String subGeogcs(String wktStr) {
		int indexOf1 = wktStr.indexOf("GEOGCS");
		int indexOf2 = wktStr.indexOf("PROJECTION");
		// String GEOGCS = wktStr.substring(indexOf1, indexOf2 - 1);
		// System.out.println(substring);
		return wktStr.substring(indexOf1, indexOf2 - 1);
	}

	public List<Object> allTree(List<ProjType> p) {
		ArrayList<Object> arrayList = new ArrayList<>();
		for (ProjType projType : p) {
			arrayList.add(projType);
			List<Level> levelList = projType.getLevelList();
			for (Level level : levelList) {
				arrayList.add(level);
				List<Wkt> wktList = level.getWktList();
				for (Wkt wkt : wktList) {
					arrayList.add(wkt);
				}
				List<Level> childrenLevelList = level.getChildrenLevelList();
				for (Level level2 : childrenLevelList) {
					arrayList.add(level2);
					List<Wkt> wktList2 = level2.getWktList();
					for (Wkt wkt : wktList2) {
						arrayList.add(wkt);
					}
				}
			}
		}
		return arrayList;
	}

	/**
	 * 存放pan、mss的路径获取
	 * 
	 * @param xmlPath
	 * @return
	 */
	@Override
	public String getPath(String xmlPath) {

		int indexOf = xmlPath.indexOf("\\");
		int indexOf2 = xmlPath.indexOf("\\", indexOf + 1);
		String substring = "";
		if (indexOf2 > 0) {
			substring = xmlPath.substring(0, indexOf2);
			return substring;
		}

		return xmlPath;
	}

	/*
	 * 全色文件夹扫描
	 */
	@Override
	public ArrayList<String> getPanList(File rootPanPath,ArrayList<String> panList) {
		File[] childFiles = rootPanPath.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {

				this.getPanList(childFile,panList);

			} else {
				String name = childFile.getName();
				String upperCaseName = name.toUpperCase();
				if (upperCaseName.contains("PAN") && upperCaseName.endsWith(".TIFF")) {
					// 获取文件绝对路径
					String absolutePath = childFile.getAbsolutePath();
					panList.add(absolutePath);
				}
			}
		}
		return panList;
	}

	@Override
	public ArrayList<String> getPanList1(String[] arrayPan, ArrayList<String> arrayList) {
		for (String string : arrayPan) {
			File childFile = new File(string);
			String name = childFile.getName();
			String upperCaseName = name.toUpperCase();
			//upperCaseName.contains("PAN") &&
			if (upperCaseName.endsWith(".TIFF")) {
				// 获取文件绝对路径
				String absolutePath = childFile.getAbsolutePath();
				arrayList.add(absolutePath);
			}
		}
		
		return arrayList;
	}

	/* 
	 * 多光谱文件夹扫描
	 */
	@Override
	public ArrayList<String> getMssList(File rootPanPath, ArrayList<String> mssList) {
		File[] childFiles = rootPanPath.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {

				this.getMssList(childFile,mssList);

			} else {
				String name = childFile.getName();
				String upperCaseName = name.toUpperCase();
				String[] split = upperCaseName.split("\\.");
				if (upperCaseName.contains("MSS") && upperCaseName.endsWith(".TIFF")) {
					// 获取文件绝对路径
					String absolutePath = childFile.getAbsolutePath();
					mssList.add(absolutePath);
				}else if(upperCaseName.contains("GF5_AHSI") && split.length ==4 && upperCaseName.endsWith(".TIFF")) {
					// 获取文件绝对路径
					String absolutePath = childFile.getAbsolutePath();
					mssList.add(absolutePath);
				}


			}
		}
		return mssList;
	}

	/* 
	 * 多光谱文件扫描
	 */
	@Override
	public ArrayList<String> getMssList1(String[] arrayMss, ArrayList<String> arrayList) {
		for (String string : arrayMss) {
			File childFile = new File(string);
			String name = childFile.getName();
			String upperCaseName = name.toUpperCase();
			//upperCaseName.contains("MSS") &&
			if (upperCaseName.endsWith(".TIFF")) {
				// 获取文件绝对路径
				String absolutePath = childFile.getAbsolutePath();
				arrayList.add(absolutePath);
			}
		}
		return arrayList;
	}
	
	/* 
	 * 基准文件扫描
	 */
	@Override
	public ArrayList<String> getRefList1(String[] arrayRef, ArrayList<String> arrayList) {
		for (String string : arrayRef) {
			File childFile = new File(string);
			String name = childFile.getName();
			String upperCaseName = name.toUpperCase();
			if (upperCaseName.endsWith(".IMG")) {
				// 获取文件绝对路径
				String absolutePath = childFile.getAbsolutePath();
				arrayList.add(absolutePath);
			}
		}
		return arrayList;
	}
}
