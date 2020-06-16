package com.htht.job.uus.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.uus.common.Consts;
import com.htht.job.uus.model.DictCode;
import com.htht.job.uus.model.HSFire;
import com.htht.job.uus.model.LyyInfo;
import com.htht.job.uus.model.Product;
import com.htht.job.uus.model.ProductCycle;
import com.htht.job.uus.model.ProductFileInfo;
import com.htht.job.uus.model.ProductInfo;
import com.htht.job.uus.model.ProductTree;
import com.htht.job.uus.model.RegionInfo;
import com.htht.job.uus.model.viewModel.HSFireView;
import com.htht.job.uus.service.DictCodeService;
import com.htht.job.uus.service.HSFireService;
import com.htht.job.uus.service.LyyService;
import com.htht.job.uus.service.ProductFileInfoService;
import com.htht.job.uus.service.ProductInfoService;
import com.htht.job.uus.service.ProductTreeService;
import com.htht.job.uus.service.RegionInfoService;
import com.htht.job.uus.util.FileUtil;
import com.htht.job.uus.util.ResponseModel;
import com.htht.job.uus.util.TreeBuilder;

/**
 * 
 * @author yuguoqing
 * @Date 2018年5月9日 上午11:31:12
 *
 *
 */
@Controller
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private ProductTreeService productTreeService;
	@Autowired
	private RegionInfoService regionInfoService;
	@Autowired
	private ProductFileInfoService productFileInfoService;
	
	@Autowired
	private ProductInfoService productInfoService;
	@Autowired
	private DictCodeService dictCodeService;
	@Autowired
	private HSFireService hsFireService;
	@Autowired
	private LyyService lyyService;

	/**
	 *  @Description: 产品目录树查询
	 *  @param 
	 *  @return ResponseModel 
	 *  @throws
	 * 
	 */
	@RequestMapping("/productTree")
	@ResponseBody
	public ResponseModel productTree(@RequestParam("userId") String userId) {
		ResponseModel response = new ResponseModel();
		List<ProductTree> productTreeList = productTreeService.findProductTreeByUserId(userId);	
		List<ProductTree> prodTree = TreeBuilder.buildByRecursive(productTreeList);
		response.setData(prodTree);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setCode(Consts.ResponseCode.CODE_OK);
		return response;
	}
	
	/**
	 * @Description: 根据产品Id查询当前产品的周期类型
	 * @param id 产品id
	 * @return ResponseModel
	 * @throws 
	 * 
	 */
	@RequestMapping("/productCycle")
	@ResponseBody
	public ResponseModel findCycleById(@RequestParam("id") String id) {
		ResponseModel response = new ResponseModel();
		List<ProductCycle> cycleList = new ArrayList<>();
		cycleList = productTreeService.findCycleById(id);
		response.setData(cycleList);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setCode(Consts.ResponseCode.CODE_OK);
		return response;
	}

	/**
	 *  @Description: 根据行政区域等级和父类行政区域Id查询行政区域
	 *  @param level 行政区域等级 
	 *  @param parentId 父类行政区域Id 
	 *  @return ResponseModel 
	 *  @throws
	 * 
	 */
	@RequestMapping("/getRegionByPidAndLevel")
	@ResponseBody
	public ResponseModel findRegionInfoByRegionLevelAndParentId(@RequestParam("level") String level,
			@RequestParam("parentRegionIds") String parentRegionIds) {
		ResponseModel response = new ResponseModel();
		List<RegionInfo> regionInfoList = new ArrayList<RegionInfo>();

		regionInfoList = regionInfoService.findRegionInfosByParentId(parentRegionIds);
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setData(regionInfoList);
		return response;
	}
	
	/**
	 *  @Description: 根据行政区域Id查询行政区域信息
	 *  @param regionId 行政区域Id 
	 *  @return ResponseModel 
	 *  @throws
	 * 
	 */
	@RequestMapping("/getRegionByRegionId")
	@ResponseBody
	public ResponseModel findRegionInfoByRegionId(@RequestParam("regionId") String regionId) {
		ResponseModel response = new ResponseModel();
		RegionInfo regionInfo = new RegionInfo();

		regionInfo = regionInfoService.findRegionInfoByRegionId(regionId);
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setData(regionInfo);
		return response;
	}
	
	/**
	 * @Description: 查询同一期号产品文件清单列表 
	 * @param cycle 周期 
	 * @param productId 产品Id 
	 * @param regionId 区域Id 
	 * @param issue 期号 
	 * @param pageNum 页码 
	 * @param pageSize 每页显示产品文件数目
	 * @return ResponseModel 
	 * @throws
	 * 
	 */
	@RequestMapping("/getProductFileByIssueAndCycle")
	@ResponseBody
	public ResponseModel findProductFileInfoByIssueAndCycle(@RequestParam("cycle") String cycle,
			@RequestParam("productId") String productInfoId, @RequestParam("regionIds") String[] regionIds,
			@RequestParam("issue") String issue, @RequestParam("pageNum") int pageNum,
			@RequestParam("pageSize") int pageSize) {
		ResponseModel response = new ResponseModel();
		Map<String, Object> productFileInfoMap = null;
		productFileInfoMap = productFileInfoService.findProductFileInfoByIssueAndCycle(cycle, productInfoId, regionIds,
				issue, pageNum, pageSize);
		
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setData(productFileInfoMap);
		return response;
	}

	/**
	 * 查询某个期次的火情详情
	 * @param productInfoId
	 * @param issue
	 * @return
	 */
	@RequestMapping("/findProductFireByIssueAndProductInfoId")
	@ResponseBody
	public ResponseModel findProductFireByIssueAndProductInfoId(@RequestParam("productInfoId") String productInfoId, 
			@RequestParam("issue") String issue) {
		ResponseModel response = new ResponseModel();
		List<HSFire> hsFireList = hsFireService.findHSFireByIssueAndProductInfoId(issue,productInfoId);
		
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setData(hsFireList);
		return response;
	}
	
	/**
	 * 查询某个期次的连阴雨详情
	 * @param productInfoId
	 * @param issue
	 * @return
	 */
	@RequestMapping("/findProductLyyByIssueAndProductInfoId")
	@ResponseBody
	public ResponseModel findProductLyyByIssueAndProductInfoId(@RequestParam("productInfoId") String productInfoId, 
			@RequestParam("issue") String issue) {
		ResponseModel response = new ResponseModel();
		List<LyyInfo> lyyInfoList = lyyService.findLyyByIssueAndProductInfoId(issue,productInfoId);
		
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setData(lyyInfoList);
		return response;
	}

	
	
	/**
	 * @Description: 查询不同期号产品列表
	 * @param beginTime	起始时间
	 * @param endTime	结束时间
	 * @param cycle		周期
	 * @param productId	产品Id
	 * @param regionIds	区域Id
	 * @param pageNum	页码
	 * @param pageSize	每页显示产品文件数目
	 * @return ResponseModel
	 * @throws 
	 * 
	 */
	@RequestMapping("/getProductInfoDistinct")
	@ResponseBody
	public ResponseModel findProductInfoDistinct(
			@RequestParam("beginTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date beginTime,
			@RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endTime,
			@RequestParam("cycle") String cycle, @RequestParam("productId") String productId,
			@RequestParam("regionIds") String[] regionIds, @RequestParam("pageNum") int pageNum,
			@RequestParam("pageSize") int pageSize) {
		ResponseModel response = new ResponseModel();
		Map<String, Object> productInfoMap = productInfoService.findProductInfoDistinct(beginTime, endTime, cycle,
				productId, regionIds, pageNum, pageSize);
		response.setCode(Consts.ResponseCode.CODE_OK);
		response.setStatus(Consts.ResposeStatus.STATUS_OK);
		response.setData(productInfoMap);
		return response;
	}
	
	/**
	 * @Description: 下载同一期号文件
	 * @param issue 	期号
	 * @param cycle		周期
	 * @param productId	产品Id
	 * @param regionId	区域Id
	 * @param request	
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/downLoadProductIssueFile")
	public void downLoadProductIssueFile(@RequestParam("issue") String issue, @RequestParam("cycle") String cycle,
			@RequestParam("productInfoId") String productInfoId, @RequestParam("regionId") String regionId,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		String zipFileName = issue + ".zip";
		response.addHeader("Content-Disposition", "attachment;fileName=" + new String(zipFileName.getBytes(), "UTF-8"));

		List<File> fileList = new ArrayList<File>();
		System.out.println("ready ...");
		fileList = getFileList(issue, cycle, productInfoId, regionId);
		downLoadFile(fileList, zipFileName, request, response);
	}

	/**
	 * @Description: 下载单个产品文件
	 * @param fileName	文件名
	 * @param filePath	文件路径
	 * @param request
	 * @param response
	 * @return 
	 * @throws IOException
	 * 
	 */
	@RequestMapping("/downLoadProductFile")
	public void downLoadProductFile(@RequestParam("fileName") String fileName, @RequestParam("filePath") String filePath,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
		String os = System.getProperty("os.name");
		//如果是windows环境，替换路径
		if(!os.toLowerCase().startsWith("win")){  
			DictCode prePathDict = dictCodeService.getDictCodeByName("oldProductPath");
			DictCode newPrePathDict = dictCodeService.getDictCodeByName("newProductPath");
			String newPrePath = "";
			if (null != newPrePathDict) {
				newPrePath = newPrePathDict.getDictCode();
			}
			filePath = filePath.replace("//", "/").replace(prePathDict.getDictCode(), newPrePath);
		}
		System.out.println("filePath 2:" + filePath);
		File fileTemp = new File(filePath);
		if (fileTemp.exists()) {
			InputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			byte[] buffer = new byte[1024 * 1024 * 10];
			int i = -1;
			while ((i = bufferedInputStream.read(buffer)) != -1) {
				bufferedOutputStream.write(buffer, 0, i);
			}
			bufferedInputStream.close();
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
		}
	}

	/**
	 * @Description: 下载文件 
	 * 		1. 将文件列表压缩成zip格式的文件 
	 * 		2. 下载压缩后的zip文件 
	 * 		3. 删除压缩后的zip文件 
	 * @param srcFilePath 
	 * @param targetFilePath 
	 * @return
	 * @throws IOException
	 * 
	 */
	public void downLoadFile(List<File> fileList, String fileName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		DictCode tempPath = dictCodeService.getDictCodeByName("tempPath");
		File zip = FileUtil.zipFiles(fileList, fileName,tempPath.getDictCode());
		response.addHeader("Content-Length", "" + zip.length());
		// 以流的形式下载文 
		if (zip.exists()) {
			InputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(zip));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			byte[] buffer = new byte[1024 * 1024 * 10];
			int i = -1;
			while ((i = bufferedInputStream.read(buffer)) != -1) {
				bufferedOutputStream.write(buffer, 0, i);
			}
			bufferedInputStream.close();
			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			// try
			// {
			// response.wait();
			zip.delete(); // 将生成的服务器端文件删除
			// } catch (InterruptedException e)
			// {
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * @Description: 查询文件信息列表
	 * 		1.	根据期号、周期、产品Id、区域Id 查询产品文件信息列表
	 * 		2.	将产品文件信息放在同一个集合里，并返回
	 * @param issue 
	 * @param cycle
	 * @param productId
	 * @param regionId
	 * @return List<File>
	 * @throws 
	 */
	@SuppressWarnings("unchecked")
	private List<File> getFileList(String issue, String cycle, String productInfoId, String regionId) {
		
		int startNum = -1;
		int num = -1;
		List<ProductFileInfo> productFileInfoList = null;
		String[] regionIds = {regionId};
		if (regionId==null || "".equals(regionId)) {
			regionIds = null;
		}
		System.out.println("finding files...");
		Map<String, Object> productFileInfoMap = productFileInfoService.findProductFileInfoByIssueAndCycle(cycle,
				productInfoId, regionIds, issue, startNum, num);
		List<File> fileList = new ArrayList<>();
		productFileInfoList =  (List<ProductFileInfo>) productFileInfoMap.get("productFileInfoList");
		DictCode prePath = dictCodeService.getDictCodeByName("newProductPath");
		DictCode oldSymbol = dictCodeService.getDictCodeByName("oldSymbol");
		DictCode newSymbol = dictCodeService.getDictCodeByName("newSymbol");
		System.out.println("prePath :" + prePath.getDictCode());
		String os = System.getProperty("os.name");
		for (ProductFileInfo productFileInfo : productFileInfoList) {
			
			String relivePath =new String( productFileInfo.getRelativePath());
			System.out.println("symbolPath :"+relivePath.replace(oldSymbol.getDictCode(), newSymbol.getDictCode()));
			String filePath = productFileInfo.getFilePath();
			//如果是windows环境，替换路径
			if(!os.toLowerCase().startsWith("win")){  
				filePath = prePath.getDictCode() + relivePath.replace(oldSymbol.getDictCode(), newSymbol.getDictCode());
			}
			System.out.println("relivePath :" + relivePath);
			System.out.println("filePath :" + filePath);
			File file = new File(filePath);
			if (file.exists() && file.isFile()) {
				fileList.add(file);
			} else {
				List<File> subFiles = FileUtil.getSubFiles(file);
				if (subFiles.size() <= 0) {
					continue;
				} else {
					for (File subFile : subFiles) {
						fileList.add(subFile);
					}
				}
			}
		}
		return fileList;
	}

}
