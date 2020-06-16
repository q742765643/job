package com.htht.job.admin.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.htht.job.admin.core.model.resolve.ProjType;

/**
 * @date:2018年6月25日上午9:27:08
 * @author:yss
 */
public interface ProjectionService {

	/**
	 * @return
	 */
	List<Object> resolveProjectionDat();

	/**
	 * @param parentFile
	 * @return
	 */
	String getPath(String parentFile);

	/**
	 * @param rootPanPath
	 * @param panList 
	 * @return
	 */
	ArrayList<String> getPanList(File rootPanPath, ArrayList<String> panList);

	/**
	 * @param arrayPan
	 * @param arrayList
	 * @return
	 */
	ArrayList<String> getPanList1(String[] arrayPan, ArrayList<String> arrayList);

	/**
	 * @param rootPanPath
	 * @param arrayList
	 * @return
	 */
	ArrayList<String> getMssList(File rootPanPath, ArrayList<String> arrayList);

	/**
	 * @param arrayMss
	 * @param arrayList
	 * @return
	 */
	ArrayList<String> getMssList1(String[] arrayMss, ArrayList<String> arrayList);

	ArrayList<String> getRefList1(String[] arrayRef, ArrayList<String> arrayList);


}
