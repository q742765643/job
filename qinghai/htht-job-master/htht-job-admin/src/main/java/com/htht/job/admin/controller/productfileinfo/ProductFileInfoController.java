package com.htht.job.admin.controller.productfileinfo;

import com.htht.job.admin.core.util.WriteToHtml;
import com.htht.job.core.api.DubboService;
import com.htht.job.core.biz.model.ReturnT;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.fileinfo.FileInfo;
import com.htht.job.executor.model.productfileinfo.ProductFileInfo;
import com.htht.job.executor.model.productinfo.ProductInfo;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzj on 2018/1/3.
 */
@Controller
@RequestMapping("/productfileinfo")
public class ProductFileInfoController {
	@Autowired
	private DubboService dubboService;

	@RequestMapping
	public String index(Model model) {
		return "/productfileinfo/productfileinfo.index";
	}

	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(
			@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length,
			ProductFileInfo productFileInfo) {
		if (start != 0) {
			start = start / length;
		}
		return dubboService.pageListProductFileInfo(start, length,
				productFileInfo);
	}

	@RequestMapping("/pageLists")
	@ResponseBody
	public Map<String, Object> pageLists(
			@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length,
			String id) {
		if (start != 0) {
			start = start / length;
		}
		return dubboService.pageListProductFileInfos(start, length, id);
	}

	@RequestMapping("/getFileUrls")
	@ResponseBody
	public DictCode getFileUrls(String dictName) {
		// 字典中获取ip:端口 其中关键字为fileUrl
		return dubboService.findOneselfDictCode(dictName);
	}

	@RequestMapping("/pageListProductInfo")
	@ResponseBody
	public Map<String, Object> pageList(
			@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length,
			String menuId, String productType, String issue) {
		if (start != 0) {
			start = start / length;
		}

		if (StringUtils.isEmpty(menuId)) {
			return dubboService.pageListProductInfo(start, length, "");
		} else {
			if (ObjectUtils.isEmpty(dubboService.findByTreeId(menuId))) {
				Map<String, Object> maps = new HashMap<String, Object>();
				maps.put("recordsTotal", 0); // 总记录数
				maps.put("recordsFiltered", 0); // 过滤后的总记录数
				maps.put("data", new ArrayList<ProductInfo>()); // 分页列表
				return maps;
			} else {
				return dubboService.pageListProductInfo(start, length,
						dubboService.findByTreeId(menuId).getId());
			}
		}
	}

	@RequestMapping("/pageListProductFileInfo")
	@ResponseBody
	public Map<String, Object> pageListProductFileInfo(
			@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length,
			String id) {
		if (start != 0) {
			start = start / length;
		}
		return dubboService.pageListProductFileInfos(start, length, id);
	}

	@RequestMapping("/saveProductFileInfo")
	@ResponseBody
	public ReturnT<String> saveProductFileInfo(ProductFileInfo productFileInfo) {
		ProductFileInfo p = dubboService.saveProductFileInfo(productFileInfo);
		if (null != p.getId()) {
			return ReturnT.SUCCESS;
		} else {
			return ReturnT.FAIL;
		}
	}

	@RequestMapping("/deleteProductFileInfo")
	@ResponseBody
	public ReturnT<String> deleteProductFileInfo(String id) {
		dubboService.deleteProductFileInfo(id);
		return ReturnT.SUCCESS;

	}

	@RequestMapping("/deleteProductInfo")
	@ResponseBody
	public ReturnT<String> deleteProductInfo(String id) {
		
		ProductInfo productInfo = dubboService.findProductInfoById(id);
		List<ProductInfo> productInfoList = new ArrayList<>();
		if ("QHS".equals(productInfo.getRegionId())) {
			productInfoList = dubboService.findProductInfoListByIssueRange(productInfo.getProductId(), 
					productInfo.getIssue(), productInfo.getIssue());
		} else {
			productInfoList.add(productInfo); 
		}
		for (ProductInfo productInfo2 : productInfoList) {
			dubboService.deleteProductFileInfo(productInfo2.getId());
			dubboService.deleteProductInfo(productInfo2.getId());
		}
		
		return ReturnT.SUCCESS;
	}

	@RequestMapping("/findFileInfoByWhere/{id}")
	@ResponseBody
	public List<FileInfo> findFileInfoByWhere(@PathVariable String id) {
		List<FileInfo> fileInfos = dubboService.findFileInfoByWhere(id);
		return fileInfos;
	}

	@RequestMapping("/htmlToStr" )
	@ResponseBody
	public Map<String,String> testSendMessage(String filepath) throws Exception  {
		Map<String,String> map=new HashMap<String,String>();
		InputStream is;
		try {
			is = new FileInputStream(filepath.replace("doc","html"));
			String htmlStr = IOUtils.toString(is,"utf-8");
			htmlStr.replaceAll("", "");
			String c = "<body style=\"max-width:600px; margin: 0 auto;\">";
			htmlStr=htmlStr.substring(htmlStr.indexOf("<body style=\"max-width:600px; margin: 0 auto;\">")+c.length(),htmlStr.lastIndexOf("</body>"));
			DictCode dic = dubboService.findOneselfDictCode("productionPath");
			DictCode dict = dubboService.findOneselfDictCode("fileUrl");
			htmlStr = htmlStr.replace(dic.getDictCode(),dict.getDictCode());
			map.put("html", htmlStr);
//			System.out.println(htmlStr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping("/HtmlToWord" )
	@ResponseBody
	public Map<String,String> StringtoHtml(HttpServletRequest req, HttpServletResponse resp,String filepath,String data) throws Exception  {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html;charset=utf-8");//设置传过去的页面显示的编码
		filepath = filepath.replace("doc","html");
		File file = new File(filepath);
		Map<String,String> map=new HashMap<String,String>();
//		System.out.println(filepath);
		WriteToHtml.writeToHtml(data,filepath);
		String name = file.getName();
		String docName=WriteToHtml.saveHtmlToWord(file.getParent()+"\\", name, name.replace("html","doc"));
		map.put("docName", docName);
		return map;
	}
}
