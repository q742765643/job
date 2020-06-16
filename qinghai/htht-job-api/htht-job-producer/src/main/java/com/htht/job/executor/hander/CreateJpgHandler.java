package com.htht.job.executor.hander;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.core.biz.model.TriggerParam;
import com.htht.job.core.handler.IJobHandler;
import com.htht.job.core.handler.annotation.JobHandler;
import com.htht.job.core.util.FileUtil;
import com.htht.job.core.util.ResultUtil;

@JobHandler(value = "createJpgHandler")
@Service
public class CreateJpgHandler extends IJobHandler {
	@Override
	public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
		ResultUtil<String> result = new ResultUtil();
		LinkedHashMap dymap = triggerParam.getDynamicParameter();
		String type = (String) dymap.get("type");
		List<String> list = new ArrayList<>();
		//获取封装后的inputxml，projectPath
		getActualParameters(dymap);
		String inputxml = (String) dymap.get("inputxml");
		String projectPath = (String) dymap.get("projectPath");
		// 获取输入文件路径
		parsingXml(inputxml, result, list);
		if (!result.isSuccess()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		// 原始xml,jpg文件转移至指定路径
		for (String fromPath : list) {
			copyTootherFolders(fromPath, projectPath, type, result);
		}
		if (!result.isSuccess()) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, result.toString());
		}
		return ReturnT.SUCCESS;
	}

	public ResultUtil<String> parsingXml(String inputxml, ResultUtil<String> result, List<String> list) {
		try {
			File f = new File(inputxml);
			if (!f.exists() || f.length() == 0) {
				result.setErrorMessage("outxml文件不存在");
				return result;
			}
			// 创建SAXReader对象
			SAXReader reader = new SAXReader();
			// 读取文件 转换成Document
			Document document = reader.read(f);
			// 获取根节点元素对象
			Element root = document.getRootElement();
			if (root == null) {
				result.setErrorMessage("outxml根节点获取错误");
				return result;
			}

			Iterator<Element> imgs = root.elementIterator("IMG");
			while (imgs.hasNext()) {
				Element e = imgs.next();
				list.add(e.elementText("IMG_PATH"));

			}

		} catch (Exception e) {
			result.setErrorMessage("解析outputxml出错");

			throw new RuntimeException();
		}
		return result;
	}

	private ResultUtil<String> copyTootherFolders(String fromPath, String toPath, String type,
			ResultUtil<String> result) {
		try {
			File startTempFile = new File(toPath);
			String startPath = startTempFile.getParent() + File.separator + FileUtil.getFileName(startTempFile);
			
			File fromFile = new File(fromPath);
			String[] fromPathMs = { fromFile.getParent() + File.separator + FileUtil.getFileName(fromFile) + ".xml",
					fromFile.getParent() + File.separator + FileUtil.getFileName(fromFile) + ".jpg" };
			String[] fromPathPs = { fromFile.getParent() + File.separator + FileUtil.getFileName(fromFile).replace("MSS", "PAN") + ".xml",
					fromFile.getParent() + File.separator + FileUtil.getFileName(fromFile).replace("MSS", "PAN") + ".jpg" };
			ArrayList<String> endPath = new ArrayList<>();
			if ("fuse".equals(type)) {
				endPath.add(startPath + File.separator + "DOM" + File.separator +"fuse" +File.separator +FileUtil.getFileName(fromPath).replace("MSS", "PAN") + "_ortho_fuse");
				endPath.add(startPath + File.separator + "DOM" + File.separator +"mult" +File.separator +FileUtil.getFileName(fromPath)+ "_ortho");
				endPath.add(startPath + File.separator + "DOM" + File.separator +"pan" +File.separator +FileUtil.getFileName(fromPath).replace("MSS", "PAN") + "_ortho");
			} else {
				endPath.add(startPath + File.separator + "DOM" + File.separator +"mult" +File.separator +FileUtil.getFileName(fromPath)+ "_ortho");

			}
			for (String endString : endPath) {
				
				String[] fromPaths = null;
				if (endString.contains("PAN") && !endString.contains("fuse")) {
					fromPaths = fromPathPs;
				}else {
					fromPaths = fromPathMs;
					
				}
				for (String from : fromPaths) {
					BufferedInputStream bin = new BufferedInputStream(new FileInputStream(from));
					// 输出目录
					BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(endString +FileUtil.getFileExtensionName(from)));
					int c;
					while ((c = bin.read()) != -1)
						bout.write(c);
					bin.close();
					bout.close();
					System.out.println(endString +FileUtil.getFileExtensionName(from)+"生产成功");
				}
				File successFlag = new File(endString + ".ok");
				if (!successFlag.exists()) {
					successFlag.createNewFile();
				}
			}
			return result;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			result.setErrorMessage("xml或者jpg生产失败");
			return result;
		}
	}

	private void getActualParameters(LinkedHashMap dymap) {
		String inputxml = (String) dymap.get("inputxml");
		String projectPath = (String) dymap.get("projectPath");
		// 获取实际inputxml
		File projectPathFile = new File(projectPath);
		String projectName = FileUtil.getFileName(projectPathFile);
		String projectNamePrefix = projectName.substring(0, projectName.length() - 14);
		String projectNameSuffix = projectName.substring(projectName.length() - 14);
		inputxml = inputxml + File.separator + "tiffXml" + File.separator + projectNamePrefix + File.separator
				+ projectNameSuffix + File.separator + "mss_path.xml";
		// proj路径
		projectPath = projectPathFile.getParent() + File.separator + "PIEPrj" + File.separator
				+ projectPathFile.getName();
		dymap.put("inputxml", inputxml);
		dymap.put("projectPath", projectPath);
	}
}
