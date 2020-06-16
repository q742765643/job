package com.htht.job.admin.controller.exceldata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.htht.job.core.api.DubboService;
import com.htht.job.executor.model.dictionary.DictCode;
import com.htht.job.executor.model.exceldata.ExcelDataPageInfo;
import com.htht.job.executor.model.exceldata.ExcelElementInfoView;
import com.xuxueli.poi.excel.ExcelExportUtil;

@Controller
@RequestMapping("/ywsjgl")
public class DataManageController {
	
	
	@RequestMapping
    public String index(Model model) {

        return "/ywsjgl/ywsjgl.index";
    }

	
	
	@Autowired
	private DubboService dubboService;
		
	@RequestMapping("/findAllByName")
	@ResponseBody
	public ExcelDataPageInfo findDataByName( 
			@RequestParam(value = "pageNum") int pageNum,
			@RequestParam(value = "pageSize") int pageSize, 
			@RequestParam(value = "name") String name,
			@RequestParam(value = "params") String[] params){
		
		return dubboService.findDataByexcelName(name,pageNum,pageSize,params);
		
	}
	
	@RequestMapping("/exportData")
	public void orderExport(HttpServletRequest request, @RequestParam(value = "name") String name,
//			@RequestParam(value = "pageNum") int pageNum, @RequestParam(value = "pageSize") int pageSize, 
			@RequestParam(value = "params") String[] params, HttpServletResponse response) throws IOException {

		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/x-download");
		DictCode productPath = dubboService.findOneselfDictCode("excelPath");
		String tempPath = productPath.getDictCode();
		File tempDir = new File(tempPath);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		int pageNum = -1;
		int pageSize = -1;
		ExcelDataPageInfo excelDataPageInfo = dubboService.findDataByexcelName(name,pageNum,pageSize,params);
		String fileName = excelDataPageInfo.getExcelName();
		response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
		
		String tempFilePath = tempPath + fileName;
		File tempFile = new File(tempFilePath);
		if (!tempFile.exists()) {
			if (null != excelDataPageInfo.getDataList()) {
				ExcelExportUtil.exportToFile(excelDataPageInfo.getDataList(),tempFilePath);
			}	
		}
		
		try {
			if (tempFile.exists()) {
				InputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(tempFilePath));
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
				tempFile.delete();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/findAllElement")
	@ResponseBody
	public List<ExcelElementInfoView> findAllElement(){
		
		List<ExcelElementInfoView> elements = dubboService.findAllElement();
		
		return elements;
	}
	
	
	@RequestMapping("/findStationsByName")
	@ResponseBody
	public List<String> findStationsByName(String name){
		
		List<String> stations = dubboService.findStationByName(name);
		
		return stations;
		
	}
	
}
